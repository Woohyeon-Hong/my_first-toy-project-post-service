package hong.postService.repository.postRepository.v2;

import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.service.postService.dto.PostSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 게시물 Repository API
 *
 * Create
 *     Post save(post) - 글 작성 시
 *
 * Read
 *     Post findByIdAndIsRemovedFalse(id)
 *
 *     Page<Post> findAllByWriterAndIsRemovedFalse(writer, pageable) - 회원이 작성한 글 페이징 조회 시
 *     List<Post>findAllByWriterAndIsRemovedFalse(writer) - 회원이 작성한 글 단순 조회
 *
 *     Page<Post> findAllByIsRemovedFalse(pageable) - 전체 게시글 목록 페이징 조회 시
 *     List<Post> findAllByIsRemovedFalse() - 모든 게시글 단순 조회 시
 *
 *     Page<Post> searchPosts(searchCond, pageable) - 게시물 검색 시 (with paging)
 *     List<Post> searchPosts(searchCond) - 게시물 단순 검색 시
 *
 * Delete
 *     void delete(post) - 게시글 삭제 시
 */

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Optional<Post> findByIdAndIsRemovedFalse(Long id);

    @Query(
            value = "select new hong.postService.service.postService.dto.PostSummaryResponse " +
                    "(p.id, p.title, p.writer.nickname, p.createdDate, " +
                    "(select count(c) from Comment c where c.post = p and c.isRemoved = false), " +
                    "case when exists (select  f.id from File f where f.post = p and f.isRemoved = false) then true else false end)" +
                    "from Post p where p.writer = :writer and p.isRemoved = false",
            countQuery = "select count(p) from Post p where p.writer = :writer and p.isRemoved = false"
    )
    Page<PostSummaryResponse> findAllByWriterAndIsRemovedFalse(Member writer, Pageable pageable);

    @Query(
            value = "select p from Post p left join fetch p.writer where p.isRemoved = false",
            countQuery = "select count(p) from Post p where p.isRemoved = false"
    )
    Page<Post> findAllByIsRemovedFalse(Pageable pageable);

    @Query(
            value = "select new hong.postService.service.postService.dto.PostSummaryResponse " +
                    "(p.id, p.title, p.writer.nickname, p.createdDate, " +
                    "(select count(c) from Comment c where c.post = p and c.isRemoved = false), " +
                    "case when exists (select  f.id from File f where f.post = p and f.isRemoved = false) then true else false end) " +
                    "from Post  p where p.isRemoved = false",
            countQuery = "select count(p) from Post p where p.isRemoved = false"
    )
    Page<PostSummaryResponse> findSummaries(Pageable pageable);


}
