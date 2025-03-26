package hong.postService.repository.postRepository.v2;

import hong.postService.domain.Member;
import hong.postService.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 게시물 검색
 * Create
 *     Post save(post) - 글 작성 시
 *
 * Read
 *     Post findById(id)
 *
 *     Page<Post> findMemberPosts(member, pageable) - 회원이 작성한 글 페이징 조회 시
 *     List<Post> findMemberPosts(member) - 회원이 작성한 글 단순 조회
 *
 *     Page<Post> findAll(pageable) - 전체 게시글 목록 페이징 조회 시
 *     List<Post> findAll() - 모든 게시글 단순 조회 시
 *
 *     Page<Post> searchPosts(searchCond, pageable) - 게시물 검색 시 (with paging)
 *     List<Post> searchPosts(searchCond) - 게시물 단순 검색 시
 *
 * Delete
 *     void delete(post) - 게시글 삭제 시
 */

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Page<Post> findPostsByWriter(Member member, Pageable pageable);
    List<Post> findPostsByWriter(Member member);
}
