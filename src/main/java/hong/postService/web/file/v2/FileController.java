package hong.postService.web.file.v2;

import hong.postService.service.fileService.v2.FileService;
import hong.postService.service.fileService.dto.UploadUrlRequest;
import hong.postService.service.fileService.dto.UploadUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
