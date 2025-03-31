package hong.postService.service.commentService.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.repository.commentRepository.v2.CommentRepository;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
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

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long write(Long postId, Long memberId, String content) {

        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("write: 해당 memberId가 없음."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("write: 해당 postId가 없음."));

        Comment comment = post.writeComment(content, writer);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public Long writeReply(Long commentId, Long memberId, String content) {

        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("write: 해당 memberId가 없음."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("write: 해당 commentId가 없음."));

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
                -> new IllegalArgumentException("delete: 해당 id가 없음."));

        comment.remove();
    }
}
