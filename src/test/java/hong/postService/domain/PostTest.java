package hong.postService.domain;

import hong.postService.exception.comment.InvalidCommentFieldException;
import hong.postService.exception.file.InvalidFileFieldException;
import hong.postService.exception.post.InvalidPostFieldException;
import hong.postService.exception.post.PostNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostTest {

    @Test
    void updateTitle_정상수행되고_null이면_예외가_발생한다() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("old", "content");

        // when
        post.updateTitle("new");

        // then
        assertThat(post.getTitle()).isEqualTo("new");

        // when & then
        assertThatThrownBy(() -> post.updateTitle(null))
                .isInstanceOf(InvalidPostFieldException.class);
    }

    @Test
    void updateTitle_글이_삭제된_상태() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("old", "content");

        post.remove();

        // when & then
        assertThatThrownBy(() -> post.updateTitle("new"))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void updateContent_정상수행되고_null이면_예외가_발생한다() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "old");

        // when
        post.updateContent("new");

        // then
        assertThat(post.getContent()).isEqualTo("new");

        // when & then
        assertThatThrownBy(() -> post.updateContent(null))
                .isInstanceOf(InvalidPostFieldException.class);
    }

    @Test
    void updateContent_글이_삭제된_상태() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "old");

        post.remove();

        // when & then
        assertThatThrownBy(() -> post.updateContent("new"))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void remove_댓글과_대댓글까지_모두_함께_삭제된다() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");
        Comment c1 = post.writeComment("댓글1", member);
        Comment c2 = post.writeComment("댓글2", member);
        Comment r1 = c1.writeReply("대댓글1", member);
        Comment r2 = c1.writeReply("대댓글2", member);
        Comment r3 = c2.writeReply("대댓글3", member);

        // when
        post.remove();

        // then
        assertThat(post.isRemoved()).isTrue();
        assertThat(post.getTitle()).isEqualTo("");
        assertThat(post.getContent()).isEqualTo("");

        assertThat(c1.isRemoved()).isTrue();
        assertThat(c2.isRemoved()).isTrue();
        assertThat(r1.isRemoved()).isTrue();
        assertThat(r2.isRemoved()).isTrue();
        assertThat(r3.isRemoved()).isTrue();
    }

    @Test
    void remove_이미_삭제된_게시글이면_예외가_발생한다() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");
        post.remove();

        // when & then
        assertThatThrownBy(post::remove)
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void writeComment_정상적으로_댓글을_생성하고_유효하지_않으면_예외가_발생한다() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");

        // when
        Comment comment = post.writeComment("댓글입니다", member);

        // then
        assertThat(comment.getPost()).isEqualTo(post);
        assertThat(comment.getWriter()).isEqualTo(member);
        assertThat(comment.getContent()).isEqualTo("댓글입니다");

        // when & then - 예외
        assertThatThrownBy(() -> post.writeComment(null, member))
                .isInstanceOf(InvalidCommentFieldException.class);
        assertThatThrownBy(() -> post.writeComment("content", null))
                .isInstanceOf(InvalidCommentFieldException.class);
    }

    @Test
    void writeComment_대댓글이_여러단계일때_정상적으로_연결된다() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");

        // when
        Comment parent = post.writeComment("댓글", member);
        Comment child = parent.writeReply("대댓글", member);
        Comment grandChild = child.writeReply("대대댓글", member);

        // then
        assertThat(child.getParentComment()).isEqualTo(parent);
        assertThat(grandChild.getParentComment()).isEqualTo(child);
        assertThat(grandChild.getPost()).isEqualTo(post);
    }

    @Test
    void writeComment_글이_삭제된_상태() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");

        post.remove();

        // when & then
        assertThatThrownBy(() ->  post.writeComment("댓글", member)).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void addNewFile() {
        //given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post1 = member.writeNewPost("title", "content");
        Post post2 = member.writeNewPost("title2", "content2");

        post2.remove();

        //when
        File file = post1.addNewFile("example.txt", "post/1/example-stored.txt");

        //then
        assertThat(post1.getFiles()).contains(file);
        assertThat(file.getPost()).isEqualTo(post1);

        assertThatThrownBy(() -> post1.addNewFile("example.txt", null)).isInstanceOf(InvalidFileFieldException.class);
        assertThatThrownBy(() -> post1.addNewFile(null, "post/1/example-stored.txt")).isInstanceOf(InvalidFileFieldException.class);

        assertThatThrownBy(() -> post1.addNewFile("example", "post/1/example-stored.txt")).isInstanceOf(InvalidFileFieldException.class);
        assertThatThrownBy(() -> post1.addNewFile("example.", "post/1/example-stored.txt")).isInstanceOf(InvalidFileFieldException.class);
        assertThatThrownBy(() -> post1.addNewFile(".txt", "post/1/example-stored.txt")).isInstanceOf(InvalidFileFieldException.class);

        assertThatThrownBy(() -> post2.addNewFile("example.txt", "post/1/example-stored.txt"))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void remove_파일까지_모두_함께_삭제된다() {
        // given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("title", "content");

        File file1 = post.addNewFile("example1.txt", "post/1/example1-stored.txt");
        File file2 =  post.addNewFile("example2.txt", "post/1/example2-stored.txt");
        File file3 =  post.addNewFile("example3.txt", "post/1/example3-stored.txt");
        File file4 =  post.addNewFile("example4.txt", "post/1/example4-stored.txt");
        File file5 =  post.addNewFile("example5.txt", "post/1/example5-stored.txt");

        // when
        post.remove();

        // then
        assertThat(file1.isRemoved()).isTrue();
        assertThat(file2.isRemoved()).isTrue();
        assertThat(file3.isRemoved()).isTrue();
        assertThat(file4.isRemoved()).isTrue();
        assertThat(file5.isRemoved()).isTrue();
    }
}
