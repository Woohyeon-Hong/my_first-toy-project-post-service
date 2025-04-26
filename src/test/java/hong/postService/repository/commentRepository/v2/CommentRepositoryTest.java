package hong.postService.repository.commentRepository.v2;

import hong.postService.TestSecurityConfig;
import hong.postService.domain.Comment;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.exception.post.PostNotFoundException;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestSecurityConfig.class)
@Transactional
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    EntityManager em;

    @Test
    void findByIdAndIsRemovedFalse_Optional로_감싸서_반환() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post = member.writeNewPost("title1", "content1");
        postRepository.save(post);

        Comment comment = post.writeComment("comment", member);
        commentRepository.save(comment);

        Comment reply = comment.writeReply("reply", member);
        commentRepository.save(reply);

        reply.remove();

        flushAndClear();

        //when
        Optional<Comment> result1 = commentRepository.findByIdAndIsRemovedFalse(comment.getId());
        Optional<Comment> result2 = commentRepository.findByIdAndIsRemovedFalse(reply.getId());

        //then
        assertThat(result1).isPresent();
        assertThat(result2).isEmpty();
    }

    @Test
    void findAllByPostAndIsRemovedFalse_정상_반환() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post1 = member.writeNewPost("title1", "content1");
        Post post2 = member.writeNewPost("title2", "content2");
        postRepository.save(post1);
        postRepository.save(post2);

        for (int i = 1; i <= 50; i++) {
            Comment comment;
            if (i % 2 != 0) comment = post1.writeComment("comment" + i, member);
            else comment = post2.writeComment("comment" + i, member);
            commentRepository.save(comment);

            Comment reply = comment.writeReply("reply" + i, member);
            commentRepository.save(reply);

            if (i == 50) reply.remove();
        }

        flushAndClear();

        PageRequest pageable1 = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Comment> result1 = commentRepository.findAllByPostAndIsRemovedFalse(post1, pageable1);
        Page<Comment> result2 = commentRepository.findAllByPostAndIsRemovedFalse(post1, pageable2);
        Page<Comment> result3 = commentRepository.findAllByPostAndIsRemovedFalse(post2, pageable1);

        //then
        assertThat(result1.getSize()).isEqualTo(5);
        assertThat(result2.getSize()).isEqualTo(5);

        assertThat(result1.getTotalPages()).isEqualTo(10);
        assertThat(result3.getTotalPages()).isEqualTo(10);

        assertThat(result1.getTotalElements()).isEqualTo(50);
        assertThat(result3.getTotalElements()).isEqualTo(49);
    }

    @Test
    void findAllByPostAndIsRemovedFalse_빈_배열_반환() {
        //given
        Member member = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(member);

        Post post = member.writeNewPost("title1", "content1");
        postRepository.save(post);

        Comment comment = post.writeComment("comment", member);

        comment.remove();

        flushAndClear();

        PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Comment> comments = commentRepository.findAllByPostAndIsRemovedFalse(post, pageable);

        //then
        assertThat(comments.getContent()).isEmpty();
    }

    @Test
    void findAllByParentCommentAndIsRemovedFalse_정상_반환() {
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
            Comment reply;
            if (i % 2 != 0) reply = comment1.writeReply("content" + i, member);
            else reply = comment2.writeReply("content" + i, member);
            commentRepository.save(reply);

            if (i == 50) reply.remove();
        }

        flushAndClear();

        PageRequest pageable1 = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Comment> result1 = commentRepository.findAllByParentCommentAndIsRemovedFalse(comment1, pageable1);
        Page<Comment> result2 = commentRepository.findAllByParentCommentAndIsRemovedFalse(comment1, pageable2);
        Page<Comment> result3 = commentRepository.findAllByParentCommentAndIsRemovedFalse(comment2, pageable1);

        //then
        assertThat(result1.getSize()).isEqualTo(5);
        assertThat(result2.getSize()).isEqualTo(5);

        assertThat(result1.getTotalPages()).isEqualTo(5);
        assertThat(result3.getTotalPages()).isEqualTo(5);

        assertThat(result1.getTotalElements()).isEqualTo(25);
        assertThat(result3.getTotalElements()).isEqualTo(24);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}