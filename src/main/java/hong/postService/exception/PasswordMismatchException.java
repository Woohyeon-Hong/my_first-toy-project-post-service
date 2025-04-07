package hong.postService.exception;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException() {
        super("현재 비밀번호가 일치하지 않습니다.");
    }
}
