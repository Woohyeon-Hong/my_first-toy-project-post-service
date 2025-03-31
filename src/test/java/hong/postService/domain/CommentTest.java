package hong.postService.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void updateContent() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");

        Post post = Post.builder()
                .title("title")
                .content("content")
                .writer(member)
                .build();

        Comment comment = post.writeComment("old", member);
        String newContent = "new";

        //when
        comment.updateContent(newContent);

        //then
        assertThat(comment.getContent()).isEqualTo("new");
        assertThatThrownBy(() -> comment.updateContent(null));

    }

    @Test
    void remove() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");

        Post post = Post.builder()
                .title("title")
                .content("content")
                .writer(member)
                .build();

        Comment comment = post.writeComment("content", member);
        Comment deletedComment = post.writeComment("content", member);
        deletedComment.remove();

        //when
        comment.remove();

        //then
        assertThat(comment.isRemoved()).isTrue();
        assertThatThrownBy(() -> deletedComment.remove()).isInstanceOf(IllegalStateException.class);
    }
}