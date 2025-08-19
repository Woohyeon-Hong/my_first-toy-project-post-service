package hong.postService.service.fileService.v2;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import hong.postService.domain.File;
import hong.postService.exception.file.InvalidFileFieldException;
import hong.postService.web.file.dto.UploadUrlRequest;
import hong.postService.web.file.dto.UploadUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final Duration PRESIGN_TTL = Duration.ofMinutes(5);

    public UploadUrlResponse getUploadUrlResponse (UploadUrlRequest request) {
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

            URL url = amazonS3Client.generatePresignedUrl( new GeneratePresignedUrlRequest(bucket, s3Key, HttpMethod.PUT)
                    .withExpiration(Date.from(expiresAt))
            );

            results.add(new UploadUrlResponse.Item(originalFileName, s3Key, storedFileName, url.toString(), expiresAt));
        }

        return new UploadUrlResponse(results);
    }
}
