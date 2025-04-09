package hong.postService.domain;

import hong.postService.exception.comment.CommentNotFoundException;
import hong.postService.exception.comment.InvalidCommentFieldException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CommentTest {

    @Test
    void writeReply_정상적으로_대댓글을_생성하고_유효하지_않으면_예외가_발생한다() {
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = member.writeNewPost("title", "content");
        Comment comment = post.writeComment("댓글", member);

        Comment reply = comment.writeReply("대댓글", member);

        assertThat(reply.getContent()).isEqualTo("대댓글");
        assertThat(reply.getParentComment()).isEqualTo(comment);

        assertThatThrownBy(() -> comment.writeReply(null, member))
                .isInstanceOf(InvalidCommentFieldException.class);

        assertThatThrownBy(() -> comment.writeReply("대댓글", null))
                .isInstanceOf(InvalidCommentFieldException.class);
    }

    @Test
    void updateContent_댓글_내용을_수정하고_null이면_예외가_발생한다() {
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = member.writeNewPost("title", "content");
        Comment comment = post.writeComment("old", member);

        comment.updateContent("new");

        assertThat(comment.getContent()).isEqualTo("new");

        assertThatThrownBy(() -> comment.updateContent(null))
                .isInstanceOf(InvalidCommentFieldException.class);
    }

    @Test
    void remove_대댓글이_여러단계일때_모두_함께_삭제된다() {
        Member member = Member.createNewMember("user", "pass", null, "nick");
        Post post = member.writeNewPost("title", "content");

        Comment parent = post.writeComment("댓글", member);
        Comment child = parent.writeReply("대댓글", member);
        Comment grandChild = child.writeReply("대대댓글", member);

        parent.remove();

        assertThat(parent.isRemoved()).isTrue();
        assertThat(child.isRemoved()).isTrue();
        assertThat(grandChild.isRemoved()).isTrue();
    }

    @Test
    void remove_이미_삭제된_댓글이라면_예외가_발생한다() {
        Member member = Member.createNewMember("user", "pass", null, "nick");
        Post post = member.writeNewPost("title", "content");
        Comment comment = post.writeComment("댓글", member);

        comment.remove();

        assertThatThrownBy(comment::remove)
                .isInstanceOf(CommentNotFoundException.class);
    }
}