package hong.postService.service.postService.v1;

import hong.postService.domain.Post;
import hong.postService.repository.postRepository.v1.PostUpdateDto;

import java.util.List;

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
