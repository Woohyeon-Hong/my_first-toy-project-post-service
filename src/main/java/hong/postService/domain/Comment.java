package hong.postService.domain;

import hong.postService.domain.baseEntity.BaseTimeEntity;
import hong.postService.exception.CommentNotFoundException;
import hong.postService.exception.InvalidCommentFieldException;
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

    private String content;
    @Column(name = "is_removed")
    private boolean isRemoved;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Builder.Default
    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComments = new ArrayList<>();

//생성---------------------------------------------------------------------------------------------------
    public Comment writeReply(String content, Member writer) {
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
        if (newContent == null) throw new InvalidCommentFieldException("updateContent: newContent == null");
        this.content = newContent;
    }

    public void remove() {
        if (isRemoved) throw new CommentNotFoundException(this.id);
        this.isRemoved = true;
    }
}
