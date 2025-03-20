package hong.postService.domain;

import hong.postService.domain.baseEntity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member writer;

    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    public static Post writeNewPost(String title, String content, Member writer) {
        if (title == null) throw new NullPointerException("writeNewPost: title == null");
        if (content == null) throw new NullPointerException("writeNewPost: content == null");
        if (writer == null) throw new NullPointerException("writeNewPost: writer == null");

        Post post = Post.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .build();

        writer.getPosts().add(post);

        return post;
    }

    public void updateTitle(String newTitle) {
        if (newTitle == null) throw new NullPointerException("updateTitle: newTitle == null");
        this.title = newTitle;
    }

    public void updateContent(String newContent) {
        if (newContent == null) throw new NullPointerException("updateContent: newContent == null");
        this.content = newContent;
    }
}
