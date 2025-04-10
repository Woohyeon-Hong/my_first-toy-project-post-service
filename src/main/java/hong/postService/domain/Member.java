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

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private String password;

    private String email;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "is_removed")
    private boolean isRemoved;

    @OneToMany(mappedBy = "writer")
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "writer")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public static Member createNewMember(String username, String password, String email, String nickname) {

        if (username == null) throw new InvalidMemberFieldException("createNewMember: username == null");
        if (password == null) throw new InvalidMemberFieldException("createNewMember: password == null");
        if (nickname == null) throw new InvalidMemberFieldException("createNewMember: nickname == null");
        if (!validateEmailFormat(email)) throw new IllegalEmailFormatException("createNewMember: email 형식이 잘못됨");

        return Member.builder()
                .username(username)
                .password(password)
                .email(email)
                .nickname(nickname)
                .role(UserRole.USER)
                .build();
    }

//생성---------------------------------------------------------------------------------------------------

    public static Member createNewAdmin(String username, String password, String email, String nickname) {

        if (username == null) throw new InvalidMemberFieldException("createNewAdmin: username == null");
        if (password == null) throw new InvalidMemberFieldException("createNewAdmin: password == null");
        if (nickname == null) throw new InvalidMemberFieldException("createNewAdmin: nickname == null");
        if (!validateEmailFormat(email)) throw new IllegalEmailFormatException("createNewAdmin: email 형식이 잘못됨");

        return Member.builder()
                .username(username)
                .password(password)
                .email(email)
                .nickname(nickname)
                .role(UserRole.ADMIN)
                .build();
    }

//검증---------------------------------------------------------------------------------------------------

    public static boolean validateEmailFormat(String email) {
        if (email == null) {
            return true;
        }
       return Pattern.compile(EMAIL_REGEX)
               .matcher(email)
                    .matches();
    }

//업데이트---------------------------------------------------------------------------------------------------


    public void changeUsername(String username) {
        validateMember();
        if (username == null) throw new InvalidMemberFieldException("changeUsername: username == null");
        this.username = username;
    }

    public void changePassword(String password) {
        validateMember();
        if (password == null) throw new InvalidMemberFieldException("changePassword: password == null");
        this.password = password;
    }

    public void changeEmail(String email) {
        validateMember();
        if (email == null) throw new InvalidMemberFieldException("changeEmail: email == null");
        this.email = email;
    }

    public void changeNickname(String nickname) {
        validateMember();
        if (nickname == null) throw new InvalidMemberFieldException("changeNickname: nickname == null");
        this.nickname = nickname;
    }

    public void remove() {
        validateMember();
        this.isRemoved = true;
    }

//Post 작성---------------------------------------------------------------------------------------------------


    public Post writeNewPost(String title, String content) {
        validateMember();
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

    private void validateMember() {
        if (this.isRemoved()) throw new MemberNotFoundException(this.getId());
    }
}
