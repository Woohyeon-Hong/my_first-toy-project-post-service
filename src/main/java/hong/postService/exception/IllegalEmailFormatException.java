package hong.postService.exception;

public class IllegalEmailFormatException extends RuntimeException {


    public IllegalEmailFormatException() {
        super();
    }

    public IllegalEmailFormatException(String message) {
        super(message);
    }
}
