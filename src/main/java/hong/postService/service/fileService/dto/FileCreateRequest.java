package hong.postService.service.fileService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileCreateRequest {

    @NotBlank(message = "originalFileName은 필수입니다.")
    private String originalFileName;
    @NotBlank(message = "s3Key는 필수입니다.")
    private String s3Key;
}
