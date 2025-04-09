package hong.postService.repository.postRepository.v2;

import hong.postService.domain.Member;
import hong.postService.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

    Page<Post> findAllByWriterAndIsRemovedFalse(Member writer, Pageable pageable);
    List<Post> findAllByWriterAndIsRemovedFalse(Member writer);

    Page<Post> findAllByIsRemovedFalse(Pageable pageable);
    List<Post> findAllByIsRemovedFalse();

}
