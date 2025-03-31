package hong.postService.service.commentService.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.repository.commentRepository.v2.CommentRepository;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.service.postService.v2.PostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentServiceTest {
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    PostService postService;

    @Test
    void write() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberService.signUp(member);

        Post post1 = member.writeNewPost("title1", "content1");
        postService.write(post1);

        Comment comment = Comment.writeComment("content", member, post1);
        Comment reply = comment.writeReply("content2");

        //when
        Long commentId = commentService.write(comment);
        Long replyId = commentService.write(reply);

        //then
        Comment findComment = commentRepository.findById(commentId).orElseThrow();
        Comment findReply = commentRepository.findById(replyId).orElseThrow();

        Assertions.assertThat(findComment).isEqualTo(comment);
        Assertions.assertThat(findReply).isEqualTo(reply);
    }

    @Test
    void getCommentsByPostWithoutPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberService.signUp(member);

        Post post1 = member.writeNewPost("title1", "content1");
        Post post2 = member.writeNewPost("title2", "content2");
        postService.write(post1);
        postService.write(post2);

        for (int i = 1; i <= 50; i++) {
            Post post = post1;
            if (i % 2 == 0) post = post2;

            Comment comment = Comment.writeComment("title" + i, member, post);
            commentService.write(comment);
        }

        //when
        List<Comment> commentsWithPost1 = commentService.getCommentsByPost(post1);
        List<Comment> commentsWithPost2 = commentService.getCommentsByPost(post2);

        //then
        assertThat(commentsWithPost1.size()).isEqualTo(25);
        assertThat(commentsWithPost2.size()).isEqualTo(25);

        assertThat(commentsWithPost1.get(0).getContent()).isEqualTo("title1");
        assertThat(commentsWithPost2.get(0).getContent()).isEqualTo("title2");
    }

    @Test
    void getCommentsByPostWithPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberService.signUp(member);

        Post post1 = member.writeNewPost("title1", "content1");
        Post post2 = member.writeNewPost("title2", "content2");
        postService.write(post1);
        postService.write(post2);

        for (int i = 1; i <= 50; i++) {
            Post post = post1;
            if (i % 2 == 0) post = post2;

            Comment comment = Comment.writeComment("content" + i, member, post);
            commentRepository.save(comment);
        }

        PageRequest pageable1 = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Comment> commentsWithPost1 = commentService.getCommentsByPost(post1, pageable1);
        Page<Comment> commentsWithPost2 = commentService.getCommentsByPost(post1, pageable2);
        Page<Comment> commentsWithPost3 = commentService.getCommentsByPost(post2, pageable1);
        Page<Comment> commentsWithPost4 = commentService.getCommentsByPost(post2, pageable2);

        //then
        assertThat(commentsWithPost1.getTotalPages()).isEqualTo(5);
        assertThat(commentsWithPost3.getTotalPages()).isEqualTo(5);

        assertThat(commentsWithPost1.getSize()).isEqualTo(5);
        assertThat(commentsWithPost2.getSize()).isEqualTo(5);
        assertThat(commentsWithPost3.getSize()).isEqualTo(5);
        assertThat(commentsWithPost4.getSize()).isEqualTo(5);

        assertThat(commentsWithPost1.getContent().get(0).getContent()).isEqualTo("content1");
        assertThat(commentsWithPost2.getContent().get(0).getContent()).isEqualTo("content11");
        assertThat(commentsWithPost3.getContent().get(0).getContent()).isEqualTo("content2");
        assertThat(commentsWithPost4.getContent().get(0).getContent()).isEqualTo("content12");
    }

    @Test
    void getCommentsByParentCommentWithoutPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberService.signUp(member);

        Post post = member.writeNewPost("title", "content");
        postService.write(post);

        Comment comment1 = Comment.writeComment("content", member, post);
        commentService.write(comment1);

        Comment comment2 = Comment.writeComment("content2", member, post);
        commentService.write(comment2);

        for (int i = 1; i <= 50; i++) {

            Comment comment = comment1;
            if (i % 2 == 0) comment = comment2;

            Comment reply = comment.writeReply("content" + i);
            commentService.write(reply);
        }

        //when
        List<Comment> replies1 = commentService.getCommentsByParentComment(comment1);
        List<Comment> replies2 = commentService.getCommentsByParentComment(comment2);

        //then
        assertThat(replies1.size()).isEqualTo(25);
        assertThat(replies2.size()).isEqualTo(25);

        assertThat(replies1.get(0).getContent()).isEqualTo("content1");
        assertThat(replies2.get(0).getContent()).isEqualTo("content2");

        assertThat(replies1.get(0).getParentComment()).isEqualTo(comment1);
        assertThat(replies2.get(0).getParentComment()).isEqualTo(comment2);
    }

    @Test
    void getCommentsByParentCommentWithPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberService.signUp(member);

        Post post = member.writeNewPost("title", "content");
        postService.write(post);

        Comment comment1 = Comment.writeComment("content", member, post);
        commentService.write(comment1);

        Comment comment2 = Comment.writeComment("content2", member, post);
        commentService.write(comment2);

        for (int i = 1; i <= 50; i++) {

            Comment comment = comment1;
            if (i % 2 == 0) comment = comment2;

            Comment reply = comment.writeReply("content" + i);
            commentService.write(reply);
        }

        PageRequest pageable1 = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Comment> replies1 = commentService.getCommentsByParentComment(comment1, pageable1);
        Page<Comment> replies2 = commentService.getCommentsByParentComment(comment1, pageable2);
        Page<Comment> replies3 = commentService.getCommentsByParentComment(comment2, pageable1);
        Page<Comment> replies4 = commentService.getCommentsByParentComment(comment2, pageable2);

        //then
        assertThat(replies1.getTotalPages()).isEqualTo(5);
        assertThat(replies3.getTotalPages()).isEqualTo(5);

        assertThat(replies1.getSize()).isEqualTo(5);
        assertThat(replies2.getSize()).isEqualTo(5);
        assertThat(replies3.getSize()).isEqualTo(5);
        assertThat(replies4.getSize()).isEqualTo(5);

        assertThat(replies1.getContent().get(0).getContent()).isEqualTo("content1");
        assertThat(replies2.getContent().get(0).getContent()).isEqualTo("content11");
        assertThat(replies3.getContent().get(0).getContent()).isEqualTo("content2");
        assertThat(replies4.getContent().get(0).getContent()).isEqualTo("content12");

        assertThat(replies1.getContent().get(0).getParentComment()).isEqualTo(comment1);
        assertThat(replies3.getContent().get(0).getParentComment()).isEqualTo(comment2);
    }

    @Test
    void delete() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberService.signUp(member);

        Post post1 = member.writeNewPost("title1", "content1");
        postService.write(post1);

        Comment comment = Comment.writeComment("content", member, post1);
        Comment reply = comment.writeReply("content2");

        commentService.write(comment);
        commentService.write(reply);

        //when
        comment.remove();
        reply.remove();

        //then
        List<Comment> comments = commentService.getCommentsByPost(post1);
        assertThat(comments).doesNotContain(comment);
        assertThat(comments).doesNotContain(reply);
    }
}