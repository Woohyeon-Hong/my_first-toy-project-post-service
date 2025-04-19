package hong.postService.domain;

import hong.postService.domain.baseEntity.BaseTimeEntity;
import hong.postService.exception.member.IllegalEmailFormatException;
import hong.postService.exception.member.InvalidMemberFieldException;
import hong.postService.exception.member.MemberNotFoundException;
import hong.postService.exception.post.InvalidPostFieldException;
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

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String username;
    //BCrypto 기준 암호 길이 60자 + a
    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 320) // 이메일 RFC 최대 길이 기준
    private String email;
    @Column(nullable = false, length = 30, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;
    @Column(name = "is_removed", nullable = false)
    private boolean isRemoved;

    /*
     - @Builder 사용 시 List에 @Builder.Default 필수
     - @Builder가 리스트 초기화를 무시하기 때문
     */
    @OneToMany(mappedBy = "writer")
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "writer")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

//생성---------------------------------------------------------------------------------------------------

    public static Member createNewMember(String username, String password, String email, String nickname) {
        validateMemberFields(username, password, email, nickname);

        return Member.builder()
                .username(username)
                .password(password)
                .email(email)
                .nickname(nickname)
                .role(UserRole.USER)
                .isRemoved(false)
                .build();
    }

    public static Member createNewAdmin(String username, String password, String email, String nickname) {
        validateMemberFields(username, password, email, nickname);

        return Member.builder()
                .username(username)
                .password(password)
                .email(email)
                .nickname(nickname)
                .role(UserRole.ADMIN)
                .isRemoved(false)
                .build();
    }

//검증---------------------------------------------------------------------------------------------------

    private static void validateMemberFields(String username, String password, String email, String nickname) {
        if (username == null) throw new InvalidMemberFieldException("createNewMember: username == null");
        if (password == null) throw new InvalidMemberFieldException("createNewMember: password == null");
        if (nickname == null) throw new InvalidMemberFieldException("createNewMember: nickname == null");
        if (!validateEmailFormat(email)) throw new IllegalEmailFormatException("createNewMember: email 형식이 잘못됨");
    }

    public static boolean validateEmailFormat(String email) {
        if (email == null) return true;

        return EMAIL_PATTERN.matcher(email).matches();
    }

//업데이트---------------------------------------------------------------------------------------------------


    public void changeUsername(String username) {
        checkNotRemoved();

        if (username == null) throw new InvalidMemberFieldException("changeUsername: username == null");

        this.username = username;
    }

    public void changePassword(String password) {
        checkNotRemoved();

        if (password == null) throw new InvalidMemberFieldException("changePassword: password == null");

        this.password = password;
    }

    public void changeEmail(String email) {
        checkNotRemoved();

        if (email == null) throw new InvalidMemberFieldException("changeEmail: email == null");

        if (!validateEmailFormat(email)) throw new InvalidMemberFieldException("changeEmail: email 형식이 잘못됨");

        this.email = email;
    }

    public void changeNickname(String nickname) {
        checkNotRemoved();

        if (nickname == null) throw new InvalidMemberFieldException("changeNickname: nickname == null");

        this.nickname = nickname;
    }

    public void remove() {
        checkNotRemoved();
        this.isRemoved = true;
    }

//Post 작성---------------------------------------------------------------------------------------------------


    public Post writeNewPost(String title, String content) {

        checkNotRemoved();

        if (title == null) throw new InvalidPostFieldException("writeNewPost: title == null");
        if (content == null) throw new InvalidPostFieldException("writeNewPost: content == null");

        Post post = Post.builder()
                .title(title)
                .content(content)
                .writer(this)
                .build();

        this.getPosts().add(post);

        return post;
    }

    private void checkNotRemoved() {
        if (this.isRemoved()) throw new MemberNotFoundException(this.getId());
    }
}
