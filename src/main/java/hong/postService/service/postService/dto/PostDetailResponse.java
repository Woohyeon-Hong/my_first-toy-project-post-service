package hong.postService.service.postService.dto;

import hong.postService.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDetailResponse {

    private Long id;
    private String title;
    private String content;
    private String writerNickname;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public static PostDetailResponse from(Post post) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getWriter().getNickname(),
                post.getCreatedDate(),
                post.getLastModifiedDate());
    }
}
