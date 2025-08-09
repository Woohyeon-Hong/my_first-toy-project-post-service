package hong.postService.service.postService.dto;

import hong.postService.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostSummaryResponse {

    private Long id;
    private String title;
    private String writerNickname;
    private LocalDateTime createdDate;
    private int commentCount;

    private boolean includingFile;

    public PostSummaryResponse(Long id, String title, String writerNickname,
                               LocalDateTime createdDate, Long commentCount, Boolean includingFile) {
        this.id = id;
        this.title = title;
        this.writerNickname = writerNickname;
        this.createdDate = createdDate;
        this.commentCount = commentCount == null ? 0 : commentCount.intValue();
        this.includingFile = includingFile != null && includingFile;
    }

    public static PostSummaryResponse from(Post post) {


        return new PostSummaryResponse(post.getId(),
                post.getTitle(),
                post.getWriter().getNickname(),
                post.getCreatedDate(),
                post.getComments().size(),
                false
        );
    }
}
