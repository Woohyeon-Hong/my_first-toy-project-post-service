package hong.postService.service.postService.dto;

import hong.postService.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostSummaryResponse {

    private Long id;
    private String title;
    private String writerNickname;
    private LocalDateTime createdDate;
    private int commentCount;

    public static PostSummaryResponse from(Post post) {
        return new PostSummaryResponse(post.getId(),
                post.getTitle(),
                post.getWriter().getNickname(),
                post.getCreatedDate(),
                post.getComments().size());
    }
}
