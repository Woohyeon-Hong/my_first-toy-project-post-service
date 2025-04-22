package hong.postService.web.comment.v2;

import hong.postService.domain.Comment;
import hong.postService.exception.ErrorResponse;
import hong.postService.service.commentService.dto.CommentCreateRequest;
import hong.postService.service.commentService.dto.CommentResponse;
import hong.postService.service.commentService.dto.CommentUpdateRequest;
import hong.postService.service.commentService.v2.CommentService;
import hong.postService.service.userDetailsService.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Comment", description = "댓글 관련 API")
@RestController
@RequestMapping("/v2/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 응답 Location 수정
     */
    @Operation(summary = "대댓글 작성",
    description = "대댓글을 작성한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "대댓글 작성 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 회원 또는 댓글 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "대댓글 필드가 null",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/{commentId}/replies")
    public ResponseEntity<Void> writeReply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentCreateRequest request
            ) {

        Long replyId = commentService.writeReply(commentId, userDetails.getUserId(), request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/v2/comments/{replyId}")
                .buildAndExpand(replyId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "댓글 상세 조회",
    description = "댓글을 상세 조회한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "댓글 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 댓글 ID"
                            ,content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable("commentId") Long commentId) {
        CommentResponse commentResponse = commentService.getCommentResponse(commentId);

        return ResponseEntity.ok(commentResponse);
    }

    @Operation(summary = "댓글 수정",
    description = "댓글 또는 대댓글을 수정한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "댓글 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 댓글 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "댓글 필드가 null",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> editComment(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {

        commentService.update(commentId, request);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "댓글 삭제",
    description = "댓글 또는 대댓글을 삭제한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 댓글 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment (
            @PathVariable("commentId") Long commentId
    ) {

        commentService.delete(commentId);

        return ResponseEntity.noContent().build();
    }
}
