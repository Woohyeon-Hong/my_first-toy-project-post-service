package hong.postService.web.posts.v2;

import hong.postService.service.postService.dto.PostDetailResponse;
import hong.postService.service.postService.v2.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable("postId") Long postId) {
        PostDetailResponse post = postService.getPost(postId);
        return ResponseEntity.ok(post);
    }
}
