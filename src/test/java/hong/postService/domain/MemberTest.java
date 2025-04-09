package hong.postService.domain;

import hong.postService.exception.member.InvalidMemberFieldException;
import hong.postService.exception.member.MemberNotFoundException;
import hong.postService.exception.post.InvalidPostFieldException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberTest {

    @Test
    void validateEmailFormat_이메일형식이_유효하면_true_그외_false() {
        // given
        String validEmail = "true@naver.com";
        String[] invalidEmails = {
                "fjdkfkfjkf", "@naver.com", "fjdkfkfjkf@", "@naver", "@.com"
        };

        // when & then
        assertThat(Member.validateEmailFormat(validEmail)).isTrue();

        for (String email : invalidEmails) {
            assertThat(Member.validateEmailFormat(email)).isFalse();
        }

        assertThat(Member.validateEmailFormat(null)).isTrue(); // null은 true 처리
    }

    @Test
    void createNewMember_정상적으로_생성되고_유효하지않으면_예외발생() {
        // given
        String username = "user";
        String password = "password";
        String email = "email@naver.com";
        String nickname = "nickname";

        // when
        Member member = Member.createNewMember(username, password, email, nickname);

        // then
        assertThat(member.getUsername()).isEqualTo(username);
        assertThat(member.getPassword()).isEqualTo(password);
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getRole()).isEqualTo(UserRole.USER);
        assertThat(member.isRemoved()).isFalse();

        // 예외 검증
        assertThatThrownBy(() -> Member.createNewMember(null, password, email, nickname))
                .isInstanceOf(InvalidMemberFieldException.class);

        assertThatThrownBy(() -> Member.createNewMember(username, null, email, nickname))
                .isInstanceOf(InvalidMemberFieldException.class);

        assertThatThrownBy(() -> Member.createNewMember(username, password, email, null))
                .isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void createNewAdmin_정상적으로_생성되고_유효하지않으면_예외발생() {
        // given
        String username = "admin";
        String password = "adminpass";
        String email = "admin@naver.com";
        String nickname = "관리자";

        // when
        Member admin = Member.createNewAdmin(username, password, email, nickname);

        // then
        assertThat(admin.getUsername()).isEqualTo(username);
        assertThat(admin.getRole()).isEqualTo(UserRole.ADMIN);

        // 예외 검증
        assertThatThrownBy(() -> Member.createNewAdmin(null, password, email, nickname))
                .isInstanceOf(InvalidMemberFieldException.class);

        assertThatThrownBy(() -> Member.createNewAdmin(username, null, email, nickname))
                .isInstanceOf(InvalidMemberFieldException.class);

        assertThatThrownBy(() -> Member.createNewAdmin(username, password, email, null))
                .isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void changeUsername_정상수행되고_null이면_예외발생() {
        Member member = Member.createNewMember("old", "pw", null, "nick");
        member.changeUsername("new");

        assertThat(member.getUsername()).isEqualTo("new");

        assertThatThrownBy(() -> member.changeUsername(null))
                .isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void changePassword_정상수행되고_null이면_예외발생() {
        Member member = Member.createNewMember("user", "pw", null, "nick");
        member.changePassword("newpw");

        assertThat(member.getPassword()).isEqualTo("newpw");

        assertThatThrownBy(() -> member.changePassword(null))
                .isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void changeEmail_정상수행되고_null이면_예외발생() {
        Member member = Member.createNewMember("user", "pw", "email@naver.com", "nick");
        member.changeEmail("new@email.com");

        assertThat(member.getEmail()).isEqualTo("new@email.com");

        assertThatThrownBy(() -> member.changeEmail(null))
                .isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void changeNickname_정상수행되고_null이면_예외발생() {
        Member member = Member.createNewMember("user", "pw", "email@naver.com", "nick");
        member.changeNickname("newnick");

        assertThat(member.getNickname()).isEqualTo("newnick");

        assertThatThrownBy(() -> member.changeNickname(null))
                .isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void writeNewPost_정상적으로_게시글을_작성하고_유효하지않으면_예외발생() {
        Member member = Member.createNewMember("user", "pw", null, "nick");

        Post post = member.writeNewPost("제목", "내용");

        assertThat(post.getTitle()).isEqualTo("제목");
        assertThat(post.getContent()).isEqualTo("내용");
        assertThat(post.getWriter()).isEqualTo(member);

        assertThatThrownBy(() -> member.writeNewPost(null, "내용"))
                .isInstanceOf(InvalidPostFieldException.class);

        assertThatThrownBy(() -> member.writeNewPost("제목", null))
                .isInstanceOf(InvalidPostFieldException.class);
    }

    @Test
    void remove_회원이_탈퇴하면_isRemoved가_true로_변경된다() {
        Member member = Member.createNewMember("user", "pw", "email@naver.com", "nick");

        member.remove();

        assertThat(member.isRemoved()).isTrue();
    }

    @Test
    void remove_이미_탈퇴된_회원이면_예외가_발생한다() {
        Member member = Member.createNewMember("user", "pw", "email@naver.com", "nick");
        member.remove();

        assertThatThrownBy(member::remove)
                .isInstanceOf(MemberNotFoundException.class);
    }
}
