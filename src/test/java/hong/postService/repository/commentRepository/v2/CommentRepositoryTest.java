package hong.postService.repository.commentRepository.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
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

        Post post1 = member.writeNewPost("title1", "content1");
        postRepository.save(post1);

        Comment comment = post1.writeComment("content", member);
        Comment reply = comment.writeReply("content", member);

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
    void findByPostAndIsRemovedFalseWithoutPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post1 = member.writeNewPost("title1", "content1");
        Post post2 = member.writeNewPost("title2", "content2");
        postRepository.save(post1);
        postRepository.save(post2);

        for (int i = 1; i <= 50; i++) {
            Post post = post1;
            if (i % 2 == 0) post = post2;

            Comment comment = post.writeComment("title" + i, member);
            commentRepository.save(comment);

            if (i == 25 || i == 50) comment.remove();
        }

        //when
        List<Comment> commentsWithPost1 = commentRepository.findByPostAndIsRemovedFalse(post1);
        List<Comment> commentsWithPost2 = commentRepository.findByPostAndIsRemovedFalse(post2);

        //then
        assertThat(commentsWithPost1.size()).isEqualTo(24);
        assertThat(commentsWithPost2.size()).isEqualTo(24);

        assertThat(commentsWithPost1.get(0).getContent()).isEqualTo("title1");
        assertThat(commentsWithPost2.get(0).getContent()).isEqualTo("title2");
    }

    @Test
    void findByPostAndIsRemovedFalseWitPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post1 = member.writeNewPost("title1", "content1");
        Post post2 = member.writeNewPost("title2", "content2");
        postRepository.save(post1);
        postRepository.save(post2);

        for (int i = 1; i <= 50; i++) {
            Post post = post1;
            if (i % 2 == 0) post = post2;

            Comment comment = post.writeComment("content" + i, member);
            commentRepository.save(comment);
        }

        PageRequest pageable1 = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Comment> commentsWithPost1 = commentRepository.findByPostAndIsRemovedFalse(post1, pageable1);
        Page<Comment> commentsWithPost2 = commentRepository.findByPostAndIsRemovedFalse(post1, pageable2);
        Page<Comment> commentsWithPost3 = commentRepository.findByPostAndIsRemovedFalse(post2, pageable1);
        Page<Comment> commentsWithPost4 = commentRepository.findByPostAndIsRemovedFalse(post2, pageable2);

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
    void findAllByParentCommentAndIsRemovedFalseWithoutPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post = member.writeNewPost("title", "content");
        postRepository.save(post);

        Comment comment1 = post.writeComment("content", member);
        commentRepository.save(comment1);

        Comment comment2 = post.writeComment("content2", member);
        commentRepository.save(comment2);

        for (int i = 1; i <= 50; i++) {

            Comment comment = comment1;
            if (i % 2 == 0) comment = comment2;

            Comment reply = comment.writeReply("content" + i, member);
            commentRepository.save(reply);

            if (i == 25 || i == 50) reply.remove();
        }

        //when
        List<Comment> replies1 = commentRepository.findAllByParentCommentAndIsRemovedFalse(comment1);
        List<Comment> replies2 = commentRepository.findAllByParentCommentAndIsRemovedFalse(comment2);

        //then
        assertThat(replies1.size()).isEqualTo(24);
        assertThat(replies2.size()).isEqualTo(24);

        assertThat(replies1.get(0).getContent()).isEqualTo("content1");
        assertThat(replies2.get(0).getContent()).isEqualTo("content2");

        assertThat(replies1.get(0).getParentComment()).isEqualTo(comment1);
        assertThat(replies2.get(0).getParentComment()).isEqualTo(comment2);
    }

    @Test
    void findAllByParentCommentAndIsRemovedFalseWithPaging() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post = member.writeNewPost("title", "content");
        postRepository.save(post);

        Comment comment1 = post.writeComment("content", member);
        commentRepository.save(comment1);

        Comment comment2 = post.writeComment("content2", member);
        commentRepository.save(comment2);

        for (int i = 1; i <= 50; i++) {

            Comment comment = comment1;
            if (i % 2 == 0) comment = comment2;

            Comment reply = comment.writeReply("content" + i, member);
            commentRepository.save(reply);
        }

        PageRequest pageable1 = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Comment> replies1 = commentRepository.findAllByParentCommentAndIsRemovedFalse(comment1, pageable1);
        Page<Comment> replies2 = commentRepository.findAllByParentCommentAndIsRemovedFalse(comment1, pageable2);
        Page<Comment> replies3 = commentRepository.findAllByParentCommentAndIsRemovedFalse(comment2, pageable1);
        Page<Comment> replies4 = commentRepository.findAllByParentCommentAndIsRemovedFalse(comment2, pageable2);

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