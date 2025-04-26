package hong.postService.domain;

import hong.postService.exception.comment.CommentNotFoundException;
import hong.postService.exception.comment.InvalidCommentFieldException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CommentTest {

    @Test
    void writeReply_정상적으로_대댓글을_생성하고_유효하지_않으면_예외가_발생한다() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");
        Comment comment = post.writeComment("댓글", member);

        // when
        Comment reply = comment.writeReply("대댓글", member);

        // then
        assertThat(reply.getContent()).isEqualTo("대댓글");
        assertThat(reply.getParentComment()).isEqualTo(comment);
        assertThat(reply.getPost()).isEqualTo(post);

        // when & then
        assertThatThrownBy(() -> comment.writeReply(null, member))
                .isInstanceOf(InvalidCommentFieldException.class);
        assertThatThrownBy(() -> comment.writeReply("대댓글", null))
                .isInstanceOf(InvalidCommentFieldException.class);
    }

    @Test
    void writeReply_댓글이_삭제된_상태() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");
        Comment comment = post.writeComment("댓글", member);
        comment.remove();

        // when & then
        assertThatThrownBy(() -> comment.writeReply("대댓글", member))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void updateContent_댓글내용을_수정하고_null이면_예외가_발생한다() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");
        Comment comment = post.writeComment("댓글", member);

        // when
        comment.updateContent("수정됨");

        // then
        assertThat(comment.getContent()).isEqualTo("수정됨");

        // when & then
        assertThatThrownBy(() -> comment.updateContent(null))
                .isInstanceOf(InvalidCommentFieldException.class);
    }

    @Test
    void updateContent_댓글이_삭제된_상태() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");
        Comment comment = post.writeComment("댓글", member);
        comment.remove();

        // when & then
        assertThatThrownBy(() -> comment.updateContent("new"))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void remove_대댓글이_여러단계일때_모두_함께_삭제된다() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");

        Comment c1 = post.writeComment("댓글", member);
        Comment c2 = c1.writeReply("대댓글", member);
        Comment c3 = c2.writeReply("대대댓글", member);

        // when
        c1.remove();

        // then
        assertThat(c1.isRemoved()).isTrue();
        assertThat(c1.getContent()).isEqualTo("");

        assertThat(c2.isRemoved()).isTrue();
        assertThat(c2.getContent()).isEqualTo("");

        assertThat(c3.isRemoved()).isTrue();
        assertThat(c3.getContent()).isEqualTo("");
    }

    @Test
    void remove_이미_삭제된_댓글이면_예외가_발생한다() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");
        Comment comment = post.writeComment("댓글", member);
        comment.remove();

        // when & then
        assertThatThrownBy(comment::remove)
                .isInstanceOf(CommentNotFoundException.class);
    }
}
