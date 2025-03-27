package hong.postService.domain;

import hong.postService.domain.baseEntity.BaseTimeEntity;
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

    public static Comment writeComment(String content, Member writer, Post post) {

        if (content == null) throw new NullPointerException("writeComment: content == null");
        if (writer == null) throw new NullPointerException("writeComment: writer == null");
        if (post == null) throw new NullPointerException("writeComment: post == null");


        Comment comment = Comment.builder()
                .content(content)
                .isRemoved(false)
                .writer(writer)
                .post(post)
                .parentComment(null)
                .build();

        writer.getComments().add(comment);
        post.getComments().add(comment);

        return comment;
    }

    public Comment writeReply(String content) {
        if (content == null) throw new NullPointerException("writeReply: content == null");

        Comment childComment = Comment.builder()
                .content(content)
                .isRemoved(false)
                .writer(this.writer)
                .post(this.post)
                .parentComment(this)
                .build();

        this.childComments.add(childComment);

        return  childComment;
    }

    public void updateContent(String newContent) {
        if (newContent == null) throw new NullPointerException("updateContent: newContent == null");
        this.content = newContent;
    }

    public void remove() {
        if (isRemoved == true) throw new IllegalStateException("remove: 이미 삭제되어 있음.");
        this.isRemoved = true;
    }
}
