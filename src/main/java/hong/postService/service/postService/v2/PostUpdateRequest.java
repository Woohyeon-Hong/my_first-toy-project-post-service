package hong.postService.service.postService.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostUpdateRequest {

    private String title;
    private String content;
}
