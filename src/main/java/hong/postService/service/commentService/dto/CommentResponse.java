package hong.postService.service.commentService.dto;


import hong.postService.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private String writer;
    private LocalDateTime createdDate;
    private Long parentCommentId;

    public CommentResponse(Long id, String content, String writer, LocalDateTime createdDate) {
        this.id = id;
        this.content = content;
        this.writer = writer;
        this.createdDate = createdDate;
    }

    public static CommentResponse from (Comment comment) {

        if (comment.getParentComment() == null) {
            return new CommentResponse(comment.getId(),
                    comment.getContent(),
                    comment.getWriter().getNickname(),
                    comment.getCreatedDate());
        } else {
            return new CommentResponse(comment.getId(),
                    comment.getContent(),
                    comment.getWriter().getNickname(),
                    comment.getCreatedDate(),
                    comment.getParentComment().getId());
        }
    }
}
