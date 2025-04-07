package hong.postService.web;

import hong.postService.exception.ErrorResponse;
import hong.postService.exception.comment.CommentNotFoundException;
import hong.postService.exception.comment.InvalidCommentFieldException;
import hong.postService.exception.member.*;
import hong.postService.exception.post.InvalidPostFieldException;
import hong.postService.exception.post.PostNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "INTERNAL_SERVER_ERROR", e.getMessage()));
    }

//Member-----------------------------------------------------------------------------
    @ExceptionHandler(DuplicateMemberFieldException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateMember(DuplicateMemberFieldException e) {
        return ResponseEntity
                .status(CONFLICT)
                .body(new ErrorResponse(409, "DUPLICATE_MEMBER_FIELD", e.getMessage()));
    }

    @ExceptionHandler(IllegalEmailFormatException.class)
    public ResponseEntity<ErrorResponse> handleIllegalEmailFormat(IllegalEmailFormatException e) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ErrorResponse(400, "ILLEGAL_EMAIL_FORMAT", e.getMessage()));
    }

    @ExceptionHandler(InvalidMemberFieldException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMemberField(InvalidMemberFieldException e) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ErrorResponse(400, "INVALID_MEMBER_FIELD", e.getMessage()));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFound(MemberNotFoundException e) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(new ErrorResponse(404, "MEMBER_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatch(PasswordMismatchException e) {
        return ResponseEntity
                .status(FORBIDDEN)
                .body(new ErrorResponse(403, "PASSWORD_MISMATCH_EXCEPTION", e.getMessage()));
    }

//Post-----------------------------------------------------------------------------
    @ExceptionHandler(InvalidPostFieldException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPostField(InvalidPostFieldException e) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ErrorResponse(400, "INVALID_POST_FIELD_EXCEPTION", e.getMessage()));
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPostNotFound(PostNotFoundException e) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(new ErrorResponse(404, "INVALID_POST_FIELD_EXCEPTION", e.getMessage()));
    }

//Comment-----------------------------------------------------------------------------
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(CommentNotFoundException e) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(new ErrorResponse(404, "COMMENT_NOT_FOUND_EXCEPTION", e.getMessage()));
    }

    @ExceptionHandler(InvalidCommentFieldException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCommentField(InvalidCommentFieldException e) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ErrorResponse(400, "INVALID_COMMENT_FIELD_EXCEPTION", e.getMessage()));
    }
}
