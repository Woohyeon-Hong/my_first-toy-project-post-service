package hong.postService.web.comment.v2;

import hong.postService.service.commentService.dto.CommentCreateRequest;
import hong.postService.service.commentService.dto.CommentResponse;
import hong.postService.service.commentService.dto.CommentUpdateRequest;
import hong.postService.service.commentService.v2.CommentService;
import hong.postService.service.userDetailsService.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v2/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{commentId}/replies")
    public ResponseEntity<Void> writeReply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("commentid") Long commentId,
            @Valid @RequestBody CommentCreateRequest request
            ) {

        Long replyId = commentService.writeReply(commentId, userDetails.getUserId(), request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{replyId}")
                .buildAndExpand(replyId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

//    @PatchMapping("/{commentId}")
//    public ResponseEntity<CommentResponse> editComment(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @PathVariable("commentId") Long commentId,
//            @Valid @RequestBody CommentUpdateRequest request
//    ) {
//
//    }
}
