package hong.postService.exception.member;

public class InvalidMemberFieldException extends RuntimeException{

    public InvalidMemberFieldException(String message) {
        super(message);
    }
}
