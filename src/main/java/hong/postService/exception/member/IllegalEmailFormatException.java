package hong.postService.exception.member;

public class IllegalEmailFormatException extends RuntimeException {


    public IllegalEmailFormatException() {
        super();
    }

    public IllegalEmailFormatException(String message) {
        super(message);
    }
}
