package hong.postService.service.commentService.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.exception.comment.CommentNotFoundException;
import hong.postService.exception.post.PostNotFoundException;
import hong.postService.repository.commentRepository.v2.CommentRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
import hong.postService.service.commentService.dto.CommentResponse;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.service.postService.v2.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MemberService memberService;
    private final PostService postService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long write(Long postId, Long memberId, String content) {

        Member writer = memberService.findMember(memberId);

        Post post = postService.getPost(postId);

        Comment comment = post.writeComment(content, writer);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public Long writeReply(Long commentId, Long memberId, String content) {

        Member writer = memberService.findMember(memberId);

        Comment comment = getComment(commentId);

        Comment reply = comment.writeReply(content, writer);

        return commentRepository.save(reply).getId();
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findByIdAndIsRemovedFalse(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable) {
        return commentRepository
                .findAllByPostAndIsRemovedFalse(postService.getPost(postId), pageable)
                .map(CommentResponse::from);
    }

    public Page<CommentResponse> getCommentsByParentComment(Long parentCommentId, Pageable pageable) {
        return commentRepository
                .findAllByParentCommentAndIsRemovedFalse(getComment(parentCommentId), pageable)
                .map(CommentResponse::from);
    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = getComment(commentId);
        comment.remove();
    }
}
