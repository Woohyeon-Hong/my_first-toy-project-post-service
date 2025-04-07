package hong.postService.exception.member;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException() {
        super("현재 비밀번호가 일치하지 않습니다.");
    }
}
