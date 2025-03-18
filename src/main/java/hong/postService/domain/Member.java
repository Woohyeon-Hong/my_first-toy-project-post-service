package hong.postService.domain;

import hong.postService.domain.baseEntity.BaseTimeEntity;
import hong.postService.exception.IllegalEmailFormatException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private String password;

    private String email;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "writer")
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    public static Member createNewMember(String username, String password, String email, String nickname) {
        if (validateEmailFormat(email)) {
            return Member.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .nickname(nickname)
                    .role(UserRole.USER)
                    .build();
        } else {
            throw new IllegalEmailFormatException("회원가입에서 email 형식이 잘못됨");
        }
    }

    public static Member createNewAdmin(String username, String password, String email, String nickname) {

        if (username == null) throw new NullPointerException("username이 비어있음.");
        if (password == null) throw new NullPointerException("password가 비어있음.");
        if (nickname == null) throw new NullPointerException("username이 비어있음.");

        if (validateEmailFormat(email)) {
            return Member.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .nickname(nickname)
                    .role(UserRole.ADMIN)
                    .build();
        } else {
            throw new IllegalEmailFormatException("Admin 회원가입에서 email 형식이 잘못됨");
        }
    }

    public static boolean validateEmailFormat(String email) {
        if (email == null) {
            return true;
        }
       return Pattern.compile(EMAIL_REGEX)
               .matcher(email)
                    .matches();
    }

    public void changeUsername(String username) {
        this.username = username;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
