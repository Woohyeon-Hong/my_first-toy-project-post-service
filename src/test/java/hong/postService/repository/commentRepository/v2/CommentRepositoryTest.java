package hong.postService.repository.commentRepository.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;

    @Test
    void save() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post1 = Post.writeNewPost("title1", "content1", member);
        postRepository.save(post1);

        Comment comment = Comment.writeComment("content", member, post1);
        Comment reply = comment.writeReply("content2");

        //when
        commentRepository.save(comment);
        commentRepository.save(reply);

        //then
        Comment findComment = commentRepository.findById(comment.getId()).orElseThrow();
        Comment findReply= commentRepository.findById(reply.getId()).orElseThrow();

        assertThat(findComment.getContent()).isEqualTo(comment.getContent());
        assertThat(findComment.getWriter()).isEqualTo(comment.getWriter());
        assertThat(findComment.getPost()).isEqualTo(comment.getPost());

        assertThat(findReply.getContent()).isEqualTo(reply.getContent());
        assertThat(findReply.getWriter()).isEqualTo(reply.getWriter());
        assertThat(findReply.getPost()).isEqualTo(reply.getPost());
    }

    @Test
    void findAllByPostWithoutPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post1 = Post.writeNewPost("title1", "content1", member);
        Post post2 = Post.writeNewPost("title2", "content2", member);
        postRepository.save(post1);
        postRepository.save(post2);

        for (int i = 1; i <= 50; i++) {
            Post post = post1;
            if (i % 2 == 0) post = post2;

            Comment comment = Comment.writeComment("title" + i, member, post);
            commentRepository.save(comment);
        }

        //when
        List<Comment> commentsWithPost1 = commentRepository.findAllByPost(post1);
        List<Comment> commentsWithPost2 = commentRepository.findAllByPost(post2);

        //then
        assertThat(commentsWithPost1.size()).isEqualTo(25);
        assertThat(commentsWithPost2.size()).isEqualTo(25);

        assertThat(commentsWithPost1.get(0).getContent()).isEqualTo("title1");
        assertThat(commentsWithPost2.get(0).getContent()).isEqualTo("title2");
    }

    @Test
    void findAllByPostWitPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post1 = Post.writeNewPost("title1", "content1", member);
        Post post2 = Post.writeNewPost("title2", "content2", member);
        postRepository.save(post1);
        postRepository.save(post2);

        for (int i = 1; i <= 50; i++) {
            Post post = post1;
            if (i % 2 == 0) post = post2;

            Comment comment = Comment.writeComment("content" + i, member, post);
            commentRepository.save(comment);
        }

        PageRequest pageable1 = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Comment> commentsWithPost1 = commentRepository.findAllByPost(post1, pageable1);
        Page<Comment> commentsWithPost2 = commentRepository.findAllByPost(post1, pageable2);
        Page<Comment> commentsWithPost3 = commentRepository.findAllByPost(post2, pageable1);
        Page<Comment> commentsWithPost4 = commentRepository.findAllByPost(post2, pageable2);

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
    void findAllByParentCommentWithoutPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post = Post.writeNewPost("title", "content", member);
        postRepository.save(post);

        Comment comment1 = Comment.writeComment("content", member, post);
        commentRepository.save(comment1);

        Comment comment2 = Comment.writeComment("content2", member, post);
        commentRepository.save(comment2);

        for (int i = 1; i <= 50; i++) {

            Comment comment = comment1;
            if (i % 2 == 0) comment = comment2;

            Comment reply = comment.writeReply("content" + i);
            commentRepository.save(reply);
        }

        //when
        List<Comment> replies1 = commentRepository.findAllByParentComment(comment1);
        List<Comment> replies2 = commentRepository.findAllByParentComment(comment2);

        //then
        assertThat(replies1.size()).isEqualTo(25);
        assertThat(replies2.size()).isEqualTo(25);

        assertThat(replies1.get(0).getContent()).isEqualTo("content1");
        assertThat(replies2.get(0).getContent()).isEqualTo("content2");

        assertThat(replies1.get(0).getParentComment()).isEqualTo(comment1);
        assertThat(replies2.get(0).getParentComment()).isEqualTo(comment2);
    }

    @Test
    void findAllByParentCommentWithPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post = Post.writeNewPost("title", "content", member);
        postRepository.save(post);

        Comment comment1 = Comment.writeComment("content", member, post);
        commentRepository.save(comment1);

        Comment comment2 = Comment.writeComment("content2", member, post);
        commentRepository.save(comment2);

        for (int i = 1; i <= 50; i++) {

            Comment comment = comment1;
            if (i % 2 == 0) comment = comment2;

            Comment reply = comment.writeReply("content" + i);
            commentRepository.save(reply);
        }

        PageRequest pageable1 = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Comment> replies1 = commentRepository.findAllByParentComment(comment1, pageable1);
        Page<Comment> replies2 = commentRepository.findAllByParentComment(comment1, pageable2);
        Page<Comment> replies3 = commentRepository.findAllByParentComment(comment2, pageable1);
        Page<Comment> replies4 = commentRepository.findAllByParentComment(comment2, pageable2);

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
}