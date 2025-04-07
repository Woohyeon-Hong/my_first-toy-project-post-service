package hong.postService.exception;

public class InvalidPostFieldException extends RuntimeException {
    public InvalidPostFieldException(String message) {
        super(message);
    }
}
