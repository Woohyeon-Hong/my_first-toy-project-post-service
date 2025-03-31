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
}