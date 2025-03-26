package hong.postService.repository.postRepository.v2;

import hong.postService.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {

    Page<Post> searchPosts(SearchCond cond, Pageable pageable);
    List<Post> searchPosts(SearchCond cond);
}
