package hong.postService.web.posts.v2;

import hong.postService.domain.Post;
import hong.postService.repository.postRepository.v2.SearchCond;
import hong.postService.service.commentService.dto.CommentResponse;
import hong.postService.service.commentService.v2.CommentService;
import hong.postService.service.postService.dto.PostDetailResponse;
import hong.postService.service.postService.dto.PostSummaryResponse;
import hong.postService.service.postService.dto.PostUpdateRequest;
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
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<Page<PostSummaryResponse>> getPosts(Pageable pageable) {
        Page<PostSummaryResponse> posts = postService.getPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable("postId") Long postId) {
        PostDetailResponse post = postService.getPostDetailResponse(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostSummaryResponse>> search(
            @Valid @RequestBody SearchCond cond,
            Pageable pageable) {
        Page<PostSummaryResponse> posts = postService.search(cond, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Page<CommentResponse>> getPostComments(
            @PathVariable("postId") Long postId,
            Pageable pageable
    ) {
        Page<CommentResponse> comments = commentService.getCommentsByPost(postId, pageable);

        return ResponseEntity
                .ok(comments);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> editPost(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostUpdateRequest request
            ) {
        postService.update(postId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> deletePost(
            @PathVariable("postId") Long postId
    ) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }
}
