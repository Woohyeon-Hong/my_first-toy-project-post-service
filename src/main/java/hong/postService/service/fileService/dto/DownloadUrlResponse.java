package hong.postService.service.fileService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class DownloadUrlResponse {

    private String downloadUrl;
    private Instant expiresAt;
}
