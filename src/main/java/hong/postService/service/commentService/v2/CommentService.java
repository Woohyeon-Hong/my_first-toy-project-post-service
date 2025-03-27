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
        return commentRepository.findAllByPost(post, pageable);
    }

    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findAllByPost(post);
    }

    public Page<Comment> getCommentsByParentComment(Comment parentComment, Pageable pageable) {
        return commentRepository.findAllByParentComment(parentComment, pageable);
    }

    public List<Comment> getCommentsByParentComment(Comment parentComment) {
        return commentRepository.findAllByParentComment(parentComment);
    }
}
