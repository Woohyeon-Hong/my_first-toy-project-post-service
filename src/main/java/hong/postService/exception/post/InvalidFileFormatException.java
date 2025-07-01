package hong.postService.exception.post;

public class InvalidFileFormatException extends RuntimeException{
    public InvalidFileFormatException(String message) {
        super(message);
    }
}
