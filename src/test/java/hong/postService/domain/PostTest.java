package hong.postService.domain;

import hong.postService.exception.comment.InvalidCommentFieldException;
import hong.postService.exception.post.InvalidPostFieldException;
import hong.postService.exception.post.PostNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostTest {
    @Test
    void remove_댓글과_대댓글까지_모두_함께_삭제된다() {
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = member.writeNewPost("title", "content");

        Comment comment1 = post.writeComment("댓글1", member);
        Comment comment2 = post.writeComment("댓글2", member);
        Comment reply1 = comment1.writeReply("대댓글1", member);
        Comment reply2 = comment1.writeReply("대댓글2", member);
        Comment reply3 = comment2.writeReply("대댓글3", member);

        post.remove();

        assertThat(post.isRemoved()).isTrue();
        assertThat(comment1.isRemoved()).isTrue();
        assertThat(comment2.isRemoved()).isTrue();
        assertThat(reply1.isRemoved()).isTrue();
        assertThat(reply2.isRemoved()).isTrue();
        assertThat(reply3.isRemoved()).isTrue();
    }

    @Test
    void remove_이미_삭제된_게시글이라면_예외가_발생한다() {
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = member.writeNewPost("title", "content");

        post.remove();

        assertThatThrownBy(post::remove)
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void updateTitle_제목을_수정하고_null이면_예외가_발생한다() {
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = member.writeNewPost("old", "content");

        post.updateTitle("new");

        assertThat(post.getTitle()).isEqualTo("new");

        assertThatThrownBy(() -> post.updateTitle(null))
                .isInstanceOf(InvalidPostFieldException.class);
    }

    @Test
    void updateContent_내용을_수정하고_null이면_예외가_발생한다() {
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = member.writeNewPost("title", "old");

        post.updateContent("new");

        assertThat(post.getContent()).isEqualTo("new");

        assertThatThrownBy(() -> post.updateContent(null))
                .isInstanceOf(InvalidPostFieldException.class);
    }

    @Test
    void writeComment_정상적으로_댓글을_생성하고_유효하지_않으면_예외가_발생한다() {
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Post post = member.writeNewPost("title", "content");

        Comment comment = post.writeComment("content", member);

        assertThat(comment.getContent()).isEqualTo("content");
        assertThat(comment.getPost()).isEqualTo(post);
        assertThat(comment.getWriter()).isEqualTo(member);
        assertThat(comment.isRemoved()).isFalse();

        assertThatThrownBy(() -> post.writeComment(null, member))
                .isInstanceOf(InvalidCommentFieldException.class);

        assertThatThrownBy(() -> post.writeComment("content", null))
                .isInstanceOf(InvalidCommentFieldException.class);
    }

    @Test
    void writeComment_대댓글이_여러단계일때_정상적으로_연결된다() {
        Member member = Member.createNewMember("user", "pass", null, "nick");
        Post post = member.writeNewPost("title", "content");

        Comment parent = post.writeComment("댓글", member);
        Comment child = parent.writeReply("대댓글", member);
        Comment grandChild = child.writeReply("대대댓글", member);

        assertThat(child.getParentComment()).isEqualTo(parent);
        assertThat(grandChild.getParentComment()).isEqualTo(child);
        assertThat(grandChild.getPost()).isEqualTo(post);
    }
}
