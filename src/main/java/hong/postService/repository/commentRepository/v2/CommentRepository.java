package hong.postService.repository.commentRepository.v2;

import hong.postService.domain.Comment;
import hong.postService.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
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

    @Query(value = "select c from Comment c left join fetch c.writer where c.post = :post and c.isRemoved = false",
            countQuery = "select count(c) from Comment c where c.post = :post and c.isRemoved = false")
    Page<Comment> findAllByPostAndIsRemovedFalse(@Param("post") Post post, Pageable pageable);

    @Query(value = "select c from Comment c left join fetch c.writer where c.parentComment = :parentComment and c.isRemoved = false",
            countQuery = "select count(c) from Comment c where c.parentComment = :parentComment and c.isRemoved = false")
    Page<Comment> findAllByParentCommentAndIsRemovedFalse(@Param("parentComment") Comment parentComment, Pageable pageable);
}
