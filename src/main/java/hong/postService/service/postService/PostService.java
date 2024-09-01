package hong.postService.service.postService;

import hong.postService.domain.Post;
import hong.postService.repository.postRepository.PostUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PostService {

    Post upload(Post post);

    void deletePost(Long id);

    List<Post> showAllPosts();

    List<Post> showMemberPosts(Long memberId);

    List<Post> searchTitle(String title);

    Post updatePost(Long id, PostUpdateDto updateParam);

    List<Post> showMemberPostsWithPaging (Long memberId, int page, int size);

    List<Post> showPostsWithPaging(int page, int size);
}
