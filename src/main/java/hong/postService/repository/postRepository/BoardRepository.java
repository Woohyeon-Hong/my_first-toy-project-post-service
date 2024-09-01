package hong.postService.repository.postRepository;

import hong.postService.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
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
