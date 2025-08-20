package hong.postService.web.file.v2;

import hong.postService.service.fileService.dto.DownloadUrlResponse;
import hong.postService.service.fileService.v2.FileService;
import hong.postService.service.fileService.dto.UploadUrlRequest;
import hong.postService.service.fileService.dto.UploadUrlResponse;
import hong.postService.service.userDetailsService.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload-urls")
    public ResponseEntity<UploadUrlResponse> issueUploadUrls(@Valid @RequestBody UploadUrlRequest request) {
        UploadUrlResponse uploadUrlResponse = fileService.getUploadUrls(request);
        return ResponseEntity.ok(uploadUrlResponse);
    }

    @PostMapping("/{fileId}/download-url")
    public ResponseEntity<DownloadUrlResponse> issueDownloadUrl(@PathVariable("fileId") Long fileId,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        DownloadUrlResponse downloadUrl = fileService.getDownloadUrl(fileId, userDetails.getUserId());
        return ResponseEntity.ok(downloadUrl);
    }
}
