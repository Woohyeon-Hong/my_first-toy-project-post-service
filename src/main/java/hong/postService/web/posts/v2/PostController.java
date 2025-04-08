package hong.postService.web.posts.v2;

import hong.postService.repository.postRepository.v2.SearchCond;
import hong.postService.service.postService.dto.PostDetailResponse;
import hong.postService.service.postService.dto.PostSummaryResponse;
import hong.postService.service.postService.v2.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public ResponseEntity<Page<PostSummaryResponse>> getPosts(Pageable pageable) {
        Page<PostSummaryResponse> posts = postService.getPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostSummaryResponse>> search(
            @Valid @RequestBody SearchCond cond,
            Pageable pageable) {
        Page<PostSummaryResponse> posts = postService.search(cond, pageable);
        return ResponseEntity.ok(posts);
    }


}
