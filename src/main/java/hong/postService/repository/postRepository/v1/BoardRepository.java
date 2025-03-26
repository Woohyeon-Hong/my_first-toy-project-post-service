package hong.postService.repository.postRepository.v1;

import hong.postService.domain.Post;
import hong.postService.service.postService.v2.PostUpdateDto;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    Post save(Post post);

    Optional<Post> findById(Long id);
    List<Post> findMemberPosts(Long memberId);
    List<Post> findAll();

    void update(Long id, PostUpdateDto updateParam);

    void delete(Long id);

    List<Post> findMemberPostsWithPaging(Long memberId, int limit, int offset);
    List<Post> findPostsWithPaging(int limit, int offset);

    List<Post> searchPosts(String title);
}
