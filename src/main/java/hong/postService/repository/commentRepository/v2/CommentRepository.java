package hong.postService.repository.commentRepository.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 *
 * ### 5. **댓글 기능 추가**
 *
 * - 게시물에 댓글 작성 및 대댓글 기능 구현.
 * - 댓글 삭제 시 논리적 삭제 처리(`is_removed`).
 *
 * 댓글 Repository API
 *
 * Create
 *      Comment save (comment) - 댓글/대댓글 작성 시
 *
 * Read
 *      findByIdAndIsRemovedFalse(id)
 *
 *      Page<Comment> findByPostAndIsRemovedFalse(post, pageable) - 게시글 댓글 목록 페이징 조회
 *      List<Comment> findByPostAndIsRemovedFalse(post) - 게시글 댓글 목록 단순 조회
 *
 *      Page<Comment> findAllByParentCommentAndIsRemovedFalse(parentComment, pageable) - 대댓글 목록 페이징 조회
 *      List<Comment> findAllByParentCommentAndIsRemovedFalse(parentComment) - 대댓글 목록 단순 조회
 *
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndIsRemovedFalse(Long id);

    Page<Comment> findByPostAndIsRemovedFalse(Post post, Pageable pageable);
    List<Comment> findByPostAndIsRemovedFalse(Post post);

    Page<Comment> findAllByParentCommentAndIsRemovedFalse(Comment parentComment, Pageable pageable);
    List<Comment> findAllByParentCommentAndIsRemovedFalse(Comment parentComment);
}
