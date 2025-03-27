package hong.postService.service.commentService.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Post;
import hong.postService.repository.commentRepository.v2.CommentRepository;
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

    private final CommentRepository commentRepository;

    @Transactional
    public Long write(Comment comment) {
        return commentRepository.save(comment).getId();
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
