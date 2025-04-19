package hong.postService.service.commentService.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.domain.UserRole;
import hong.postService.exception.comment.CommentNotFoundException;
import hong.postService.exception.member.MemberNotFoundException;
import hong.postService.exception.post.PostNotFoundException;
import hong.postService.repository.commentRepository.v2.CommentRepository;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
import hong.postService.service.commentService.dto.CommentCreateRequest;
import hong.postService.service.commentService.dto.CommentResponse;
import hong.postService.service.commentService.dto.CommentUpdateRequest;
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

import static org.assertj.core.api.Assertions.*;

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
    void write_memberId와_postId가_null이_아니면_정상_수행하고_id_반환() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId = postService.write(memberId, new PostCreateRequest("title", "content"));

        CommentCreateRequest commentCreateRequest = new CommentCreateRequest("comment");

        //when
        Long commentId = commentService.write(postId, memberId, commentCreateRequest);

        //then
        Comment comment = commentService.getComment(commentId);
        assertThat(comment.getContent()).isEqualTo("comment");

        assertThatThrownBy(() -> commentService.write(null, memberId, commentCreateRequest)).isInstanceOf(PostNotFoundException.class);
        assertThatThrownBy(() -> commentService.write(postId, null, commentCreateRequest)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void writeReply_memberId와_commentId가_null이_아니면_정상_수행하고_id_반환() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId = postService.write(memberId, new PostCreateRequest("title", "content"));

        Long commentId = commentService.write(postId, memberId, new CommentCreateRequest("comment"));

        //when
        Long replyId = commentService.writeReply(commentId, memberId, new CommentCreateRequest("reply"));

        //then
        Comment reply = commentService.getComment(replyId);
        assertThat(reply.getContent()).isEqualTo("reply");

        assertThatThrownBy(() -> commentService.writeReply(null, memberId, new CommentCreateRequest("comment"))).isInstanceOf(CommentNotFoundException.class);
        assertThatThrownBy(() -> commentService.writeReply(commentId, null, new CommentCreateRequest("comment"))).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void getCommentsByPost_postId가_null이_아니면_삭제된_댓글을_제외하고_모두_반환() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId1 = postService.write(memberId, new PostCreateRequest("title1", "content1"));
        Long postId2 = postService.write(memberId, new PostCreateRequest("title2", "content2"));

        for (int i = 1; i <= 50; i++) {
            Long commentId;
            if (i % 2 != 0) commentId = commentService.write(postId1, memberId, new CommentCreateRequest("comment" + i));
            else commentId = commentService.write(postId2, memberId, new CommentCreateRequest("comment" + i));
            Long replyId = commentService.writeReply(commentId, memberId, new CommentCreateRequest("reply" + i));

            if (i == 49) commentService.getComment(replyId).remove();
            if (i == 50) commentService.getComment(commentId).remove();
        }

        //when
        Page<CommentResponse> comments1 = commentService.getCommentsByPost(postId1, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate")));
        Page<CommentResponse> comments2 = commentService.getCommentsByPost(postId2, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate")));

        //then
        assertThat(comments1.getSize()).isEqualTo(10);
        assertThat(comments2.getSize()).isEqualTo(10);

        assertThat(comments1.getTotalPages()).isEqualTo(5);
        assertThat(comments2.getTotalPages()).isEqualTo(5);

        assertThat(comments1.getTotalElements()).isEqualTo(49);
        assertThat(comments2.getTotalElements()).isEqualTo(48);

        assertThatThrownBy(() -> commentService.getCommentsByPost(null, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate"))))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void getCommentsByParentComment_parentCommentId가_null이_아니면_삭제된_댓글을_제외하고_모두_반환() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId = postService.write(memberId, new PostCreateRequest("title1", "content1"));

        Long commentId1 = commentService.write(postId, memberId, new CommentCreateRequest("comment1"));
        Long commentId2 = commentService.write(postId, memberId, new CommentCreateRequest("comment2"));


        for (int i = 1; i <= 50; i++) {
            Long replyId;
            if (i % 2 != 0) replyId = commentService.writeReply(commentId1, memberId, new CommentCreateRequest("reply" + i));
            else replyId = commentService.writeReply(commentId2, memberId, new CommentCreateRequest("reply" + i));
            if (i == 50) commentService.getComment(replyId).remove();
        }

        //when
        Page<CommentResponse> comments1 = commentService.getCommentsByParentComment(commentId1, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate")));
        Page<CommentResponse> comments2 = commentService.getCommentsByParentComment(commentId2, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate")));

        //then
        assertThat(comments1.getSize()).isEqualTo(10);
        assertThat(comments2.getSize()).isEqualTo(10);

        assertThat(comments1.getTotalPages()).isEqualTo(3);
        assertThat(comments2.getTotalPages()).isEqualTo(3);

        assertThat(comments1.getTotalElements()).isEqualTo(25);
        assertThat(comments2.getTotalElements()).isEqualTo(24);

        assertThatThrownBy(() -> commentService.getCommentsByParentComment(null, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate"))))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void update_commentId가_null이_아니고_updateParam_이_null이_아니면_정상_수행() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId = postService.write(memberId, new PostCreateRequest("title1", "content1"));

        Long commentId = commentService.write(postId, memberId, new CommentCreateRequest("comment"));

        CommentUpdateRequest request = new CommentUpdateRequest("newComment");

        //when
        commentService.update(commentId, request);

        //then
        Comment findComment = commentService.getComment(commentId);

        assertThat(findComment.getId()).isEqualTo(commentId);
        assertThat(findComment.getContent()).isEqualTo(request.getContent());

    }

    @Test
    void delete_commentId가_null이_아니면_정상_수행() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId = postService.write(memberId, new PostCreateRequest("title1", "content1"));

        Long commentId = commentService.write(postId, memberId, new CommentCreateRequest("comment"));

        //when
        commentService.delete(commentId);

        //then
        assertThatThrownBy(() -> commentService.getComment(commentId))
                .isInstanceOf(CommentNotFoundException.class);

        assertThatThrownBy(() -> commentService.delete(null))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void delete_부모_댓글을_삭제하면_대댓글도_삭제됨() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId = postService.write(memberId, new PostCreateRequest("title1", "content1"));
        Post post = postService.getPost(postId);

        Long commentId = commentService.write(postId, memberId, new CommentCreateRequest("comment"));
        Long replyId = commentService.writeReply(commentId, memberId, new CommentCreateRequest("reply"));

        Comment comment = commentService.getComment(commentId);
        Comment reply = commentService.getComment(replyId);

        //when
        commentService.delete(commentId);

        //then
        List<Comment> comments = commentRepository.findAllByPostAndIsRemovedFalse(post, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate"))).getContent();

        assertThat(comments).doesNotContain(comment, reply);
    }
}