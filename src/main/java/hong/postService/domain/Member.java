package hong.postService.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * ## Member (회원)
 *
 * - id (Long) - DB에서 자동 생성
 * - name (String)
 * - loginId (String)
 * - password (String)
 */
@Data
public class Member {

    private Long id;
    private String name;
    private String loginId;
    private String password;

    public Member() {
    }

    public Member(String name, String loginId, String password) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
    }
}
