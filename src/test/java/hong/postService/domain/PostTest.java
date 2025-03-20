package hong.postService.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostTest {

    //연관관계 편의 메소드가 정상 작동했는지에 대해서는 테스트하지 않는다.
    @Test
    void writeNewPostWithoutJPA() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = Post.builder()
                .title("title")
                .content("content")
                .writer(member)
                .build();

        //when
        Post createdPost = Post.writeNewPost(post.getTitle(), post.getContent(), post.getWriter());

        //then
        assertThat(createdPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(createdPost.getContent()).isEqualTo(post.getContent());
        assertThat(createdPost.getWriter()).isEqualTo(post.getWriter());
        assertThat(createdPost.getId()).isNull();

        assertThatThrownBy(() -> Post.writeNewPost(null, "content", member)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Post.writeNewPost("title", null, member)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Post.writeNewPost("title", "content", null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void updateTitle() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = Post.writeNewPost("old", "content", member);

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
        Post post = Post.writeNewPost("title", "old", member);

        //when
        post.updateContent("new");

        //then
        assertThat(post.getContent()).isEqualTo("new");
        assertThatThrownBy(() -> post.updateContent(null)).isInstanceOf(NullPointerException.class);
    }
}