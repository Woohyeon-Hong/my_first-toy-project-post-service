package hong.postService.service.postService.v1;

import hong.postService.domain.Post;
import hong.postService.service.postService.v2.PostUpdateRequest;

import java.util.List;

public interface PostService {

    Post upload(Post post);

    void deletePost(Long id);

    List<Post> showAllPosts();

    List<Post> showMemberPosts(Long memberId);

    List<Post> searchTitle(String title);

    Post updatePost(Long id, PostUpdateRequest updateParam);

    List<Post> showMemberPostsWithPaging (Long memberId, int page, int size);

    List<Post> showPostsWithPaging(int page, int size);
}
