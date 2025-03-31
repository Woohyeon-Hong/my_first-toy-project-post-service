package hong.postService.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    //연관관계 편의 메소드가 정상 작동했는지에 대해서는 테스트하지 않는다.
    @Test
    void writeReplyWithoutJPA() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = member.writeNewPost("title", "content");
        Comment comment = post.writeComment("content2", member);

        //when
        Comment reply = comment.writeReply("content3", member);

        //then
        assertThat(reply.getContent()).isEqualTo("content3");
        assertThatThrownBy(() -> comment.writeReply(null, member)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> comment.writeReply("content4", null)).isInstanceOf(NullPointerException.class);

    }

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