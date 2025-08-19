package hong.postService.service.fileService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class UploadUrlResponse {

    List<Item> results;


    @Getter
    @AllArgsConstructor
    public static class Item{
        private String originalFileName;
        private String s3Key;
        private String storedFileName;
        private String uploadUrl;
        private final Instant expiresAt;
    }
}

