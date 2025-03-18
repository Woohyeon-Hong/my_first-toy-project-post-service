package hong.postService.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberTest {

    @Test
    void validateEmailFormat() {
        //given
        String email = "true@naver.com";

        String emailFailAtAll = "fjdkfkfjkf";
        String emailFailAfterAt = "fjdkfkfjkf@";
        String emailFailBeforeAt = "@naver.com";
        String emailFailAfterDomain = "@naver";
        String emailFailBeforeDotCom = "@.com";
        String nullEmail = null;

        //when
        boolean resultOfEmail = Member.validateEmailFormat(email);
        boolean resultOfEmailFailAfterAll = Member.validateEmailFormat(emailFailAtAll);
        boolean resultOfEmailFailAfterAt = Member.validateEmailFormat(emailFailAfterAt);
        boolean resultOfEmailFailBeforeAt = Member.validateEmailFormat(emailFailBeforeAt);
        boolean resultOfEmailFailAfterDomain = Member.validateEmailFormat(emailFailAfterDomain);
        boolean resultOfEmailFailBeforeDotCom = Member.validateEmailFormat(emailFailBeforeDotCom);
        boolean resultOfNull = Member.validateEmailFormat(nullEmail);

        //then
        assertThat(resultOfEmail).isTrue();
        assertThat(resultOfEmailFailAfterAll).isFalse();
        assertThat(resultOfEmailFailAfterAt).isFalse();
        assertThat(resultOfEmailFailBeforeAt).isFalse();
        assertThat(resultOfEmailFailAfterDomain).isFalse();
        assertThat(resultOfEmailFailBeforeDotCom).isFalse();
        assertThat(resultOfNull).isTrue();
    }

    @Test
    void createNewMember() {
        //given
        Member member = Member.builder()
                .username("user")
                .password("password")
                .email("email@naver.com")
                .role(UserRole.USER)
                .nickname("nickname")
                .build();

        //when
        Member createdMember = Member.createNewMember(member.getUsername(), member.getPassword(),
                member.getEmail(), member.getNickname());

        //then
        assertThat(createdMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(createdMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(createdMember.getEmail()).isEqualTo(member.getEmail());
        assertThat(createdMember.getRole()).isEqualTo(member.getRole());
        assertThat(createdMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(createdMember.getId()).isNull();
    }

    @Test
    void createNewAdmin() {
        //given
        Member member = Member.builder()
                .username("user")
                .password("password")
                .email("email@naver.com")
                .role(UserRole.ADMIN)
                .nickname("nickname")
                .build();

        Member newMember = Member.createNewMember(null, null, null, null);
        //when
        Member createdMember = Member.createNewAdmin(member.getUsername(), member.getPassword(),
                member.getEmail(), member.getNickname());

        //then
        assertThat(createdMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(createdMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(createdMember.getEmail()).isEqualTo(member.getEmail());
        assertThat(createdMember.getRole()).isEqualTo(member.getRole());
        assertThat(createdMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(createdMember.getId()).isNull();
    }

    @Test
    void changeUsername() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        String newUsername = "new";

        //when
        member.changeUsername(newUsername);

        //then
        assertThat(member.getUsername()).isEqualTo("new");
    }

    @Test
    void changePassword() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        String newPassword = "new";

        //when
        member.changePassword(newPassword);

        //then
        assertThat(member.getPassword()).isEqualTo("new");
    }
}