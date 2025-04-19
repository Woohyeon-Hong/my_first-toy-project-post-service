package hong.postService.service.commentService.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.exception.comment.CommentNotFoundException;
import hong.postService.exception.comment.InvalidCommentFieldException;
import hong.postService.exception.member.MemberNotFoundException;
import hong.postService.exception.post.InvalidPostFieldException;
import hong.postService.exception.post.PostNotFoundException;
import hong.postService.repository.commentRepository.v2.CommentRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
import hong.postService.service.commentService.dto.CommentCreateRequest;
import hong.postService.service.commentService.dto.CommentResponse;
import hong.postService.service.commentService.dto.CommentUpdateRequest;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.service.postService.v2.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CommentService는 댓글에 대한 비즈니스 로직을 담당하는 서비스 계층입니다.
 *
 * 주요 기능:
 *      댓글 작성 (일반/대댓글)
 *      게시글에 달린 전체 댓글 목록 조회 (Paging)
 *      댓글에 달린 전체 대댓글 목록 조회 (Paging)
 *      댓글 삭제 (soft delete)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentRepository commentRepository;

    /**
     * 댓글을 작성합니다.
     *
     * @param postId 댓글을 작성할 게시글 ID
     * @param memberId 댓글을 작성할 회원 ID
     * @param request 댓글을 달 내용
     * @return 작성한 댓글 ID
     *
     * @throws MemberNotFoundException 존재하지 않거나 이미 삭제된 회원인 경우
     * @throws PostNotFoundException 존재하지 않거나 이미 삭제된 게시글인 경우
     * @throws InvalidCommentFieldException null 값인 경우
     */
    @Transactional
    public Long write(Long postId, Long memberId, CommentCreateRequest request) {

        Member writer = memberService.findMember(memberId);

        Post post = postService.getPost(postId);

        Comment comment = post.writeComment(request.getContent(), writer);

        return commentRepository.save(comment).getId();
    }

    /**
     * 대댓글을 작성합니다.
     *
     * @param commentId 대댓글을 작성할 댓글 ID
     * @param memberId 댓글을 작성할 회원 ID
     * @param request 대댓글을 달 내용
     * @return 작성한 대댓글 ID
     *
     * @throws MemberNotFoundException 존재하지 않거나 이미 삭제된 회원인 경우
     * @throws CommentNotFoundException  존재하지 않거나 이미 삭제된 댓글인 경우
     * @throws InvalidCommentFieldException null 값인 경우
     */
    @Transactional
    public Long writeReply(Long commentId, Long memberId, CommentCreateRequest request) {

        Member writer = memberService.findMember(memberId);

        Comment comment = getComment(commentId);

        Comment reply = comment.writeReply(request.getContent(), writer);

        return commentRepository.save(reply).getId();
    }

    /**
     * 댓글을 조회합니다.
     *
     * @param commentId 조회할 댓글 ID
     * @return 조회된 댓글 엔티티
     *
     * @throws CommentNotFoundException 존재하지 않거나 이미 삭제된 댓글인 경우
     */
    public Comment getComment(Long commentId) {
        return commentRepository.findByIdAndIsRemovedFalse(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    /**
     * 게시글에 달린 전체 댓글 목록을 조회합니다.
     *
     * @param postId 댓글을 조회할 게시글 ID
     * @param pageable 페이징 및 정렬 정보를 담은 객체
     * @return 게시글에 달린 댓글들의 페이징 결과 (댓글 응답 DTO로 매핑됨)
     */
    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable) {
        return commentRepository
                .findAllByPostAndIsRemovedFalse(postService.getPost(postId), pageable)
                .map(CommentResponse::from);
    }

    /**
     * 댓글에 달린 전체 대댓글 목록을 조회합니다.
     *
     * @param parentCommentId 대댓글을 조회할 댓글 ID
     * @param pageable 페이징 및 정렬 정보를 담은 객체
     * @return 댓글에 달린 대댓글들의 페이징 결과 (댓글 응답 DTO로 매핑됨)
     */
    public Page<CommentResponse> getCommentsByParentComment(Long parentCommentId, Pageable pageable) {
        return commentRepository
                .findAllByParentCommentAndIsRemovedFalse(getComment(parentCommentId), pageable)
                .map(CommentResponse::from);
    }

    public void update(Long commentId, CommentUpdateRequest updateParam) {
        Comment comment = getComment(commentId);

        String content = updateParam.getContent();

        if (content == null) throw new InvalidCommentFieldException("update: content == null");

        comment.updateContent(content);
    }

    /**
     * 댓글을 삭제합니다.
     *
     * @param commentId 삭제할 댓글 ID
     *
     * @throws CommentNotFoundException 존재하지 않거나 이미 삭제된 댓글인 경우
     */
    @Transactional
    public void delete(Long commentId) {
        Comment comment = getComment(commentId);
        comment.remove();
    }
}
