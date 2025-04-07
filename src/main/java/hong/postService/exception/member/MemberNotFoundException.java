package hong.postService.exception.member;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(Long id) {
        super("해당 Id의 회원이 존재하지 않습니다. (id = " + id + ")");
    }
}
