package hong.postService.web.posts.v1.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Form {

    @NotEmpty
    @Size(max = 30)
    private String title;
    @NotNull
    private String content;
}
