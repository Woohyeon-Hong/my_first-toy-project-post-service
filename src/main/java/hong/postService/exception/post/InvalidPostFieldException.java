package hong.postService.exception.post;

public class InvalidPostFieldException extends RuntimeException {
    public InvalidPostFieldException(String message) {
        super(message);
    }
}
