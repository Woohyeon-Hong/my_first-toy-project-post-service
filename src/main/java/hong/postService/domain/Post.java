package hong.postService.domain;

import hong.postService.domain.baseEntity.BaseTimeEntity;
import hong.postService.exception.comment.InvalidCommentFieldException;
import hong.postService.exception.post.InvalidPostFieldException;
import hong.postService.exception.post.PostNotFoundException;
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

    @Column(nullable = false, length = 100)
    private String title;
    //긴글이라고 해도 수 MB 단위이기 때문에 @Lob 필요 x - @Lob은 주로 파일과 같은 큰 오브젝트에 사용해야 함
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_removed", nullable = false)
    private boolean isRemoved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member writer;

    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

//비즈니스 로직---------------------------------------------------------------------------------------------------

    public void updateTitle(String newTitle) {
        checkNotRemoved();

        if (newTitle == null) throw new InvalidPostFieldException("updateTitle: newTitle == null");

        this.title = newTitle;
    }

    public void updateContent(String newContent) {
        checkNotRemoved();

        if (newContent == null) throw new InvalidPostFieldException("updateContent: newContent == null");

        this.content = newContent;
    }

    public void remove() {
        checkNotRemoved();

        for (Comment comment : comments) {
            if (!comment.isRemoved()) {
                comment.remove();
            }
        }

        this.isRemoved = true;
    }

//Comment 작성---------------------------------------------------------------------------------------------------

    public  Comment writeComment(String content, Member writer) {
        checkNotRemoved();

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

//내부 로직---------------------------------------------------------------------------------------------------
    private void checkNotRemoved() {
        if (this.isRemoved()) throw new PostNotFoundException(this.getId());
    }
}
