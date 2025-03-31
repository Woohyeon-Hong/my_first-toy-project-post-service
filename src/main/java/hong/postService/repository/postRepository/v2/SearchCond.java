package hong.postService.repository.postRepository.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SearchCond {

    private String writer;
    private String title;
}
