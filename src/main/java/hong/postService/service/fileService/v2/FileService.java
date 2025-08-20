package hong.postService.service.fileService.v2;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import hong.postService.domain.File;
import hong.postService.domain.Post;
import hong.postService.exception.file.FileNotFoundException;
import hong.postService.exception.file.InvalidFileFieldException;
import hong.postService.exception.member.InvalidMemberFieldException;
import hong.postService.exception.post.PostNotFoundException;
import hong.postService.repository.fileRepository.v2.FileRepository;
import hong.postService.service.fileService.dto.DownloadUrlResponse;
import hong.postService.service.fileService.dto.UploadUrlRequest;
import hong.postService.service.fileService.dto.UploadUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;

    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final Duration PRESIGN_TTL = Duration.ofMinutes(5);

    public UploadUrlResponse getUploadUrls(UploadUrlRequest request) {
        if (request == null || request.getOriginalFileNames().isEmpty()) throw new InvalidFileFieldException("createUploadUrls: request가 비어있음.");

        List<UploadUrlResponse.Item> results = new ArrayList<UploadUrlResponse.Item>();
        List<String> originalFileNames = request.getOriginalFileNames();

        for (String originalFileName : originalFileNames) {

            if (originalFileName == null) throw new InvalidFileFieldException("createUploadUrls: originalFileName == null");
            File.validateOriginalFileName(originalFileName);

            String storedFileName = File.generateStoredFileName(originalFileName);
            String s3Key = "post/tmp/" + storedFileName;

            //AWS SDK는 Date를 필요로 하고, Instant가 Date 타입을 반환
            Instant now = Instant.now();
            Instant expiresAt = now.plus(PRESIGN_TTL);

            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, s3Key, HttpMethod.PUT)
                    .withExpiration(Date.from(expiresAt));

            URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

            results.add(new UploadUrlResponse.Item(originalFileName, s3Key, storedFileName, url.toString(), expiresAt));
        }

        return new UploadUrlResponse(results);
    }

    public DownloadUrlResponse getDownloadUrl(Long fileId, Long requesterId) {
        File file = fileRepository.findByIdAndIsRemovedFalse(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        Post post = file.getPost();
        if (post.isRemoved()) throw new PostNotFoundException(post.getId());

        if (post.getWriter().getId() != requesterId) throw new InvalidMemberFieldException("getDownloadUrl: 권한이 없습니디.");

        String originalFileName = file.getOriginalFileName();
        String contentDisposition = buildContentDisposition(originalFileName);

        String s3Key = file.getS3Key();
        Instant expiresAt = Instant.now().plus(PRESIGN_TTL);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, s3Key, HttpMethod.GET)
                .withExpiration(java.util.Date.from(expiresAt));

        generatePresignedUrlRequest.addRequestParameter("response-content-disposition", contentDisposition);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return new DownloadUrlResponse(url.toString(), expiresAt);
    }

    private String buildContentDisposition(String filename) {
        // 기본: attachment; filename="name"
        // RFC 5987 대응(비ASCII) 위해 filename* 도 함께 제공
        try {
            String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            return "attachment; filename=\"" + filename.replace("\"","") + "\"; filename*=UTF-8''" + encoded;
        } catch (Exception e) {
            return "attachment; filename=\"" + filename.replace("\"","") + "\"";
        }
    }
}
