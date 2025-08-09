package hong.postService.repository.postRepository.v2;

import hong.postService.domain.Post;
import hong.postService.service.postService.dto.PostSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {

    Page<PostSummaryResponse> searchPosts(SearchCond cond, Pageable pageable);
}
