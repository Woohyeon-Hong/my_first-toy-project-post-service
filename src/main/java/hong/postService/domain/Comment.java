package hong.postService.domain;

import hong.postService.domain.baseEntity.BaseTimeEntity;
import hong.postService.exception.comment.CommentNotFoundException;
import hong.postService.exception.comment.InvalidCommentFieldException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_removed", nullable = false)
    private boolean isRemoved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Builder.Default
    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComments = new ArrayList<>();

    //생성---------------------------------------------------------------------------------------------------
    public Comment writeReply(String content, Member writer) {
        validateComment();
        if (content == null) throw new InvalidCommentFieldException("writeReply: content == null");
        if (writer == null) throw new InvalidCommentFieldException("writeComment: writer == null");

        Comment childComment = Comment.builder()
                .content(content)
                .isRemoved(false)
                .writer(writer)
                .post(this.post)
                .parentComment(this)
                .build();

        this.childComments.add(childComment);
        writer.getComments().add(childComment);

        return  childComment;
    }

//업데이트---------------------------------------------------------------------------------------------------

    public void updateContent(String newContent) {
        validateComment();
        if (newContent == null) throw new InvalidCommentFieldException("updateContent: newContent == null");
        this.content = newContent;
    }

    public void remove() {
        validateComment();

        for (Comment child : childComments) {
            if (!child.isRemoved()) {
                child.remove();
            }
        }

        this.content = "";
        this.isRemoved = true;
    }

//검증---------------------------------------------------------------------------------------------------

    private void validateComment() {
        if (this.isRemoved()) throw new CommentNotFoundException(this.getId());
    }
}
