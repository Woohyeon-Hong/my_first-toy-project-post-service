package hong.postService.service.commentService.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.domain.UserRole;
import hong.postService.repository.commentRepository.v2.CommentRepository;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.service.postService.dto.PostCreateRequest;
import hong.postService.service.postService.v2.PostService;
import hong.postService.service.memberService.dto.UserCreateRequest;
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
    MemberRepository memberRepository;
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;

    @Test
    void write() {
        //given
        UserCreateRequest request = new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER);
        Long id = memberService.signUp(request);

        Long postId = postService.write(id, new PostCreateRequest("title1", "content1"));

        //when
        Long commentId = commentService.write(postId, id, "content");

        //then
        Comment comment = commentRepository.findById(commentId).orElseThrow();

        Assertions.assertThat(comment.getContent()).isEqualTo("content");
    }

    @Test
    void writeReply() {
        //given
        UserCreateRequest request = new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER);
        Long id = memberService.signUp(request);

        Long postId = postService.write(id, new PostCreateRequest("title1", "content1"));
        Long commentId = commentService.write(postId, id, "content");

        //when
        Long replyId = commentService.writeReply(commentId, id, "content");

        //then
        Comment reply = commentRepository.findById(replyId).orElseThrow();

        Assertions.assertThat(reply.getContent()).isEqualTo("content");
    }

    @Test
    void getCommentsByPostWithoutPaging() {
        //given
        UserCreateRequest request = new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER);
        Long id = memberService.signUp(request);

        Member member = memberRepository.findById(id).orElseThrow();

        Long postId = postService.write(id, new PostCreateRequest("title1", "content1"));
        Post post1 = postRepository.findById(postId).orElseThrow();
        Long postId2 = postService.write(id, new PostCreateRequest("title2", "content2"));
        Post post2 = postRepository.findById(postId2).orElseThrow();

        for (int i = 1; i <= 50; i++) {
            if (i % 2 != 0)  commentService.write(post1.getId(), member.getId(), "title" + i);
            else  commentService.write(post2.getId(), member.getId(), "title" + i);
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
        UserCreateRequest request = new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER);
        Long id = memberService.signUp(request);

        Member member = memberRepository.findById(id).orElseThrow();

        Long postId = postService.write(id, new PostCreateRequest("title1", "content1"));
        Post post1 = postRepository.findById(postId).orElseThrow();
        Long postId2 = postService.write(id, new PostCreateRequest("title2", "content2"));
        Post post2 = postRepository.findById(postId2).orElseThrow();

        for (int i = 1; i <= 50; i++) {
            if (i % 2 != 0)  commentService.write(post1.getId(), member.getId(), "content" + i);
            else  commentService.write(post2.getId(), member.getId(), "content" + i);
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
        UserCreateRequest request = new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER);
        Long id = memberService.signUp(request);

        Member member = memberRepository.findById(id).orElseThrow();

        Long postId = postService.write(id, new PostCreateRequest("title1", "content1"));
        Post post = postRepository.findById(postId).orElseThrow();

        Long commentId = commentService.write(postId, id, "content");
        Comment comment1 = commentRepository.findById(commentId).orElseThrow();

        Long commentId2 = commentService.write(postId, id, "content2");
        Comment comment2 = commentRepository.findById(commentId2).orElseThrow();

        for (int i = 1; i <= 50; i++) {
            if (i % 2 != 0) commentService.writeReply(commentId, id, "content" + i);
            else commentService.writeReply(commentId2, id, "content" + i);
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
        UserCreateRequest request = new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER);
        Long id = memberService.signUp(request);

        Member member = memberRepository.findById(id).orElseThrow();

        Long postId = postService.write(id, new PostCreateRequest("title1", "content1"));

        Long commentId1 = commentService.write(postId, id, "content");
        Comment comment1 = commentRepository.findById(commentId1).orElseThrow();

        Long commentId2 = commentService.write(postId, id, "content2");
        Comment comment2 = commentRepository.findById(commentId2).orElseThrow();

        for (int i = 1; i <= 50; i++) {
            if (i % 2 != 0) commentService.writeReply(commentId1, id, "content" + i);
            else commentService.writeReply(commentId2, id, "content" + i);
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
        UserCreateRequest request = new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER);
        Long id = memberService.signUp(request);

        Member member = memberRepository.findById(id).orElseThrow();

        Long postId = postService.write(id, new PostCreateRequest("title1", "content1"));
        Post post1 = postRepository.findById(postId).orElseThrow();


        Long commentId = commentService.write(postId, id, "content");
        Long replyId = commentService.writeReply(commentId, id, "content2");

        Comment comment = commentRepository.findById(commentId).orElseThrow();
        Comment reply = commentRepository.findById(replyId).orElseThrow();

        //when
        comment.remove();
        reply.remove();

        //then
        List<Comment> comments = commentService.getCommentsByPost(post1);
        assertThat(comments).doesNotContain(comment);
        assertThat(comments).doesNotContain(reply);
    }
}