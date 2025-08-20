package hong.postService.web.posts.v2;

import hong.postService.domain.Post;
import hong.postService.exception.ErrorResponse;
import hong.postService.repository.postRepository.v2.SearchCond;
import hong.postService.service.commentService.dto.CommentCreateRequest;
import hong.postService.service.commentService.dto.CommentResponse;
import hong.postService.service.commentService.v2.CommentService;
import hong.postService.service.postService.dto.PostDetailResponse;
import hong.postService.service.postService.dto.PostSummaryResponse;
import hong.postService.service.postService.dto.PostUpdateRequest;
import hong.postService.service.postService.v2.PostService;
import hong.postService.service.userDetailsService.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "post", description = "게시글 관련 API")
@RestController
@RequestMapping("/v2/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    /**
     * 로그인 안 해도 확인 가능하게
     */
    @Operation(summary = "전체 게시글 조회",
            description = "전체 게시글을 조회한다. 삭제된 게시글은 제외한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "전체 게시글 조회 성공"),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<Page<PostSummaryResponse>> getPosts(Pageable pageable) {

        Page<PostSummaryResponse> posts = postService.getPosts(pageable);

        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "게시글 상세 조회",
            description = "게시글 하나를 상세 조회한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 회원 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable("postId") Long postId) {

        PostDetailResponse post = postService.getPostDetailResponse(postId);

        return ResponseEntity.ok(post);
    }

    @Operation(summary = "게시글 검색",
            description = "회원 닉네임이나 게시글 제목을 입력받아 게시글을 조회한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "게시글 검색 성공"),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/search")
    public ResponseEntity<Page<PostSummaryResponse>> search(
            @ModelAttribute SearchCond cond,
            Pageable pageable) {

        Page<PostSummaryResponse> posts = postService.search(cond, pageable);

        return ResponseEntity.ok(posts);
    }


    @Operation(summary = "게시글 수정",
            description = "게시글의 제목이나 본문을 수정하고, 새로운 파일을 추가하거나 기존 파일을 삭제한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "게시글 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 게시글 또는 파일",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 파일 추가/삭제 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/{postId}")
    public ResponseEntity<Void> editPost(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {

        postService.update(postId, request);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 삭제",
            description = "게시글을 삭제한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 게시글 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("postId") Long postId
    ) {

        postService.delete(postId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "댓글 작성",
            description = "댓글을 작성한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "댓글 작성 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 회원 또는 게시글 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "댓글 필드가 null",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

            }
    )
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

    @Operation(summary = "게시글 전체 댓글 목록 조회",
            description = "게시글에 달린 전체 댓글 및 대댓글들을 조회한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "게시글 전체 댓글 목록 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 게시글 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),

            }
    )
    @GetMapping("{postId}/comments")
    public ResponseEntity<Page<CommentResponse>> getPostComments(
            @PathVariable("postId") Long postId,
            Pageable pageable
    ) {
        Page<CommentResponse> comments = commentService.getCommentsByPost(postId, pageable);

        return ResponseEntity
                .ok(comments);
    }
}
