package hong.postService.repository.postRepository.v2;

import hong.postService.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SearchCond {

    private String username;
    private String title;
}
