package hong.postService.service.commentService.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.exception.CommentNotFoundException;
import hong.postService.exception.PostNotFoundException;
import hong.postService.repository.commentRepository.v2.CommentRepository;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
import hong.postService.service.memberService.v2.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MemberService memberService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long write(Long postId, Long memberId, String content) {

        Member writer = memberService.findMember(memberId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        Comment comment = post.writeComment(content, writer);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public Long writeReply(Long commentId, Long memberId, String content) {

        Member writer = memberService.findMember(memberId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        Comment reply = comment.writeReply(content, writer);

        return commentRepository.save(reply).getId();
    }

    public Page<Comment> getCommentsByPost(Post post, Pageable pageable) {
        return commentRepository.findByPostAndIsRemovedFalse(post, pageable);
    }

    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPostAndIsRemovedFalse(post);
    }

    public Page<Comment> getCommentsByParentComment(Comment parentComment, Pageable pageable) {
        return commentRepository.findAllByParentCommentAndIsRemovedFalse(parentComment, pageable);
    }

    public List<Comment> getCommentsByParentComment(Comment parentComment) {
        return commentRepository.findAllByParentCommentAndIsRemovedFalse(parentComment);
    }

    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(()
                -> new CommentNotFoundException(id));

        comment.remove();
    }
}
