package hong.postService.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostTest {
    @Test
    void updateTitle() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = member.writeNewPost("old", "content");

        //when
        post.updateTitle("new");

        //then
        assertThat(post.getTitle()).isEqualTo("new");
        assertThatThrownBy(() -> post.updateTitle(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void updateContent() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = member.writeNewPost("title", "old");

        //when
        post.updateContent("new");

        //then
        assertThat(post.getContent()).isEqualTo("new");
        assertThatThrownBy(() -> post.updateContent(null)).isInstanceOf(NullPointerException.class);
    }

    //연관관계 편의 메소드가 정상 작동했는지에 대해서는 테스트하지 않는다.
    @Test
    void writerCommentWithoutJPA() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");

        Post post = Post.builder()
                .title("title")
                .content("content")
                .writer(member)
                .build();

        Comment comment = Comment.builder()
                .content("content")
                .isRemoved(false)
                .parentComment(null)
                .writer(member)
                .post(post)
                .build();

        Comment commentWithoutContent = Comment.builder()
                .content(null)
                .isRemoved(false)
                .parentComment(null)
                .writer(member)
                .post(post)
                .build();

        Comment commentWithoutWriter = Comment.builder()
                .content("content")
                .isRemoved(false)
                .parentComment(null)
                .writer(null)
                .post(post)
                .build();

        Comment commentWithoutPost = Comment.builder()
                .content("content")
                .isRemoved(false)
                .parentComment(null)
                .writer(member)
                .post(null)
                .build();

        //when
        Comment createdComment = post.writeComment(comment.getContent(), comment.getWriter());

        //then
        assertThat(createdComment.getContent()).isEqualTo(comment.getContent());
        assertThat(createdComment.isRemoved()).isEqualTo(comment.isRemoved());
        assertThat(createdComment.getWriter()).isEqualTo(comment.getWriter());
        assertThat(createdComment.getPost()).isEqualTo(comment.getPost());

        assertThatThrownBy(() -> post.writeComment(commentWithoutWriter.getContent(), commentWithoutWriter.getWriter()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> post.writeComment(commentWithoutWriter.getContent(), commentWithoutWriter.getWriter()))
                .isInstanceOf(NullPointerException.class);
    }
}