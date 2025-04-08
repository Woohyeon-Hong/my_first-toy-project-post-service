package hong.postService.service.postService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "title은 필수입니다.")
    private String title;

    @NotNull
    private String content;
}
