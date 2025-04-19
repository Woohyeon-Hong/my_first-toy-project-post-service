package hong.postService.web.posts.v2;

import hong.postService.domain.Post;
import hong.postService.repository.postRepository.v2.SearchCond;
import hong.postService.service.commentService.dto.CommentCreateRequest;
import hong.postService.service.commentService.dto.CommentResponse;
import hong.postService.service.commentService.v2.CommentService;
import hong.postService.service.postService.dto.PostDetailResponse;
import hong.postService.service.postService.dto.PostSummaryResponse;
import hong.postService.service.postService.dto.PostUpdateRequest;
import hong.postService.service.postService.v2.PostService;
import hong.postService.service.userDetailsService.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


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
            @RequestParam SearchCond cond,
            Pageable pageable) {

        Page<PostSummaryResponse> posts = postService.search(cond, pageable);

        return ResponseEntity.ok(posts);
    }


    @PatchMapping("/{postId}")
    public ResponseEntity<Void> editPost(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostUpdateRequest request
            ) {

        postService.update(postId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("postId") Long postId
    ) {

        postService.delete(postId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Void> writeComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody CommentCreateRequest request
            ) {

        Long commentId = commentService.write(postId, userDetails.getUserId(), request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/v2/comments/{commentId}")
                .buildAndExpand(commentId)
                .toUri();

        return ResponseEntity.created(location).build();
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
}
