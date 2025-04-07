package hong.postService.domain;

import hong.postService.domain.baseEntity.BaseTimeEntity;
import hong.postService.exception.comment.InvalidCommentFieldException;
import hong.postService.exception.post.InvalidPostFieldException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

//업데이트---------------------------------------------------------------------------------------------------

    public void updateTitle(String newTitle) {
        if (newTitle == null) throw new InvalidPostFieldException("updateTitle: newTitle == null");
        this.title = newTitle;
    }

    public void updateContent(String newContent) {
        if (newContent == null) throw new InvalidPostFieldException("updateContent: newContent == null");
        this.content = newContent;
    }

//Comment 작성---------------------------------------------------------------------------------------------------

    public  Comment writeComment(String content, Member writer) {

        if (content == null) throw new InvalidCommentFieldException("writeComment: content == null");
        if (writer == null) throw new InvalidCommentFieldException("writeComment: writer == null");

        Comment comment = Comment.builder()
                .content(content)
                .isRemoved(false)
                .writer(writer)
                .post(this)
                .parentComment(null)
                .build();

        writer.getComments().add(comment);
        this.getComments().add(comment);

        return comment;
    }
}
