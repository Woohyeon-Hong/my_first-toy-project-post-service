package hong.postService.exception;

public class InvalidCommentFieldException extends RuntimeException {

    public InvalidCommentFieldException(String message) {
        super(message);
    }
}
