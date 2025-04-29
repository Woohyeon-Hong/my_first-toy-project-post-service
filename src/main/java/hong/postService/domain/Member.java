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
import java.util.UUID;
import java.util.regex.Pattern;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    /**
     * 이메일 형식 검증을 위한 정규 표현식 상수
     */
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String username;
    //BCrypto 기준 암호 길이 60자 + a
    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 320) // 이메일 RFC 최대 길이 기준
    private String email;
    @Column(nullable = false, length = 30)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(name = "is_removed", nullable = false)
    private boolean isRemoved;
    @Column(name = "is_oauth_member", nullable = false, updatable = false)
    private boolean isOAuthMember;

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
                .isOAuthMember(false)
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
                .isOAuthMember(false)
                .build();
    }

    /**
     * username으로 서비스에서 회원을 조회하기 때문에 username은 필수
     * password는 oauth에서는 의미 없는 값이나 도메인에서는 인코딩이 불가하기 떄문에 서비스에서 임의값을 생성하고 인코딩하여 인자로 넘김
     * nickname은 oath 회원 가입 시에는 디폴트 값을 할당하는게 전략이기 때문에, 전달받는 인자에 Null을 명시하고 도메인에서 임의값 할당
     */
    public static Member createNewOAuthMember(String username, String password, String email, String nickname) {
        validateOAuthFields(username, password, email);

        nickname = createDefaultNickname(nickname);

        return Member.builder()
                .username(username)
                .password(password)
                .email(email)
                .nickname(nickname)
                .role(UserRole.USER)
                .isRemoved(false)
                .isOAuthMember(true)
                .build();
    }

//검증---------------------------------------------------------------------------------------------------

    private static void validateMemberFields(String username, String password, String email, String nickname) {
        if (username == null) throw new InvalidMemberFieldException("createNewMember: username == null");
        if (password == null) throw new InvalidMemberFieldException("createNewMember: password == null");
        if (nickname == null) throw new InvalidMemberFieldException("createNewMember: nickname == null");
        if (!validateEmailFormat(email)) throw new IllegalEmailFormatException("createNewMember: email 형식이 잘못됨");
    }

    private static void validateOAuthFields(String username, String password, String email) {
        if (username == null) throw new InvalidMemberFieldException("createOAuthMember: username == null");
        if (password == null) throw new InvalidMemberFieldException("createOAuthMember: password == null");
        if (!validateEmailFormat(email)) throw new IllegalEmailFormatException("createOAuthMember: email 형식이 잘못됨");
    }

    /**
     * Controller 단에서 @Email을 이용하여 Bean Validation을 하기보단, 직접 자바 코드로 구현해보기 위함
     */
    public static boolean validateEmailFormat(String email) {
        if (email == null) return true;
        return EMAIL_PATTERN.matcher(email).matches();
    }

//업데이트---------------------------------------------------------------------------------------------------
    public void changeUsername(String username) {
        checkIsOAuthMember();
        checkNotRemoved();
        if (username == null) throw new InvalidMemberFieldException("changeUsername: username == null");
        this.username = username;
    }

    public void changePassword(String password) {
        checkIsOAuthMember();
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

        this.password = "";
        this.isRemoved = true;
    }

//Post 작성---------------------------------------------------------------------------------------------------

    /**
     *  - 회원이 글을 작성하는 것이 자연스럽기 때문에, Member 도메인에 Post 생성 로직을 둠
     *  - 또한, Member에서 Post의 writer 할당도 담당
     *  - 즉, 연관관계 편의 메소드 위치
     *  - 이를 통해, Post의 writer에 항상 값이 할당 되도록 보장 가능
     */
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

//내부 메소드---------------------------------------------------------------------------------------------------

    private static String createDefaultNickname(String nickname) {
        if (nickname == null) {
            nickname = "user_" + UUID.randomUUID().toString().substring(0, 8);
        }
        return nickname;
    }

    private void checkNotRemoved() {
        if (this.isRemoved()) throw new MemberNotFoundException(this.getId());
    }

    private void checkIsOAuthMember() {
        if (this.isOAuthMember()) throw new InvalidMemberFieldException("changeUsername: isAOuthMember == true");
    }
}
