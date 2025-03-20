package hong.postService.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

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
        Comment createdComment = Comment.writeComment(comment.getContent(), comment.getWriter(), comment.getPost());

        //then
        assertThat(createdComment.getContent()).isEqualTo(comment.getContent());
        assertThat(createdComment.isRemoved()).isEqualTo(comment.isRemoved());
        assertThat(createdComment.getWriter()).isEqualTo(comment.getWriter());
        assertThat(createdComment.getPost()).isEqualTo(comment.getPost());

        assertThatThrownBy(() -> Comment.writeComment(commentWithoutWriter.getContent(), commentWithoutWriter.getWriter(), commentWithoutWriter.getPost()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Comment.writeComment(commentWithoutPost.getContent(), commentWithoutPost.getWriter(), commentWithoutPost.getPost()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Comment.writeComment(commentWithoutWriter.getContent(), commentWithoutWriter.getWriter(), commentWithoutWriter.getPost()))
                .isInstanceOf(NullPointerException.class);
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

        Comment comment = Comment.writeComment("old", member, post);
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

        Comment comment = Comment.writeComment("content", member, post);
        Comment deletedComment = Comment.writeComment("content", member, post);
        deletedComment.remove();

        //when
        comment.remove();

        //then
        assertThat(comment.isRemoved()).isTrue();
        assertThatThrownBy(() -> deletedComment.remove()).isInstanceOf(IllegalStateException.class);
    }
}