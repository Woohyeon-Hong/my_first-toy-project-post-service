package hong.postService.service.postService.dto;

import hong.postService.service.fileService.dto.FileCreateRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "title은 필수입니다.")
    private String title;

    @NotNull
    private String content;

    private List<FileCreateRequest> files;
}
