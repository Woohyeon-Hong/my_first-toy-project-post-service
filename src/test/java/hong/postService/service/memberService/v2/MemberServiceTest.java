package hong.postService.service.memberService.v2;

import hong.postService.domain.Member;
import hong.postService.domain.UserRole;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.service.memberService.dto.MemberUpdateInfoRequest;
import hong.postService.service.memberService.dto.PasswordUpdateRequest;
import hong.postService.service.memberService.dto.UserCreateRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    EntityManager em;

    @Test
    void usernameValidate() {
        //given
        Member member1 = Member.createNewMember("user1", "p1", "u1@naver.com", "n1");
        memberRepository.save(member1);

        String ok = "user";
        String notOk = "user1";

        //then
        memberService.usernameValidate(ok);
        assertThatThrownBy(() -> memberService.usernameValidate(notOk)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void passwordValidate() {
        //given
        Member member1 = Member.createNewMember("user1", "p1", "u1@naver.com", "n1");
        memberRepository.save(member1);

        String ok = "p";
        String notOk = "p1";

        //then
        memberService.passwordValidate(ok);
        assertThatThrownBy(() -> memberService.passwordValidate(notOk)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emailValidate() {
        //given
        Member member1 = Member.createNewMember("user1", "p1", "u1@naver.com", "n1");
        memberRepository.save(member1);

        String ok = "u@naver.com";
        String notOk = "u1@naver.com";

        //then
        memberService.emailValidate(ok);
        assertThatThrownBy(() -> memberService.emailValidate(notOk)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nicknameValidate() {
        //given
        Member member1 = Member.createNewMember("user1", "p1", "u1@naver.com", "n1");
        memberRepository.save(member1);

        String ok = "n";
        String notOk = "n1";

        //then
        memberService.nicknameValidate(ok);
        assertThatThrownBy(() -> memberService.nicknameValidate(notOk)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void signUpMember() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);
        UserCreateRequest request3 = new UserCreateRequest("user2", "p3", "u3@naver.com", "n3", UserRole.USER);

        //when
        Long id1 = memberService.signUp(request);
        Long id2 = memberService.signUp(request2);

        //then
        Member member1 = memberRepository.findById(id1).orElseThrow();
        Member member2 = memberRepository.findById(id2).orElseThrow();

        List<Member> members = memberRepository.findAll();

        assertThat(member1.getRole()).isEqualTo(UserRole.USER);
        assertThat(member2.getRole()).isEqualTo(UserRole.USER);

        assertThat(members).containsExactly(member1, member2);
        assertThatThrownBy(() -> memberService.signUp(request3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void signUpAdmin() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);
        UserCreateRequest request3 = new UserCreateRequest("user2", "p3", "u3@naver.com", "n3", UserRole.USER);


        //when
        Long id1 = memberService.signUpAdmin(request);
        Long id2 = memberService.signUpAdmin(request2);

        //then
        Member member1 = memberRepository.findById(id1).orElseThrow();
        Member member2 = memberRepository.findById(id2).orElseThrow();

        List<Member> members = memberRepository.findAll();

        assertThat(member1.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(member2.getRole()).isEqualTo(UserRole.ADMIN);

        assertThat(members).containsExactly(member1, member2);
        assertThatThrownBy(() -> memberService.signUpAdmin(request3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void unregister() {
        //given
        UserCreateRequest request1 = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);
        UserCreateRequest request3 = new UserCreateRequest("user3", "p3", "u3@naver.com", "n3", UserRole.USER);

        Long id1 = memberService.signUp(request1);
        Long id2 = memberService.signUp(request2);
        Long id3 = memberService.signUp(request3);

        Member member1 = memberRepository.findById(id1).orElseThrow();
        Member member2 = memberRepository.findById(id2).orElseThrow();
        Member member3 = memberRepository.findById(id3).orElseThrow();


        //when
        memberService.unregister(member1.getId());
        memberService.unregister(member2.getId());

        //then
        List<Member> members = memberRepository.findAll();
        assertThat(members).containsExactly(member3);

        assertThatThrownBy(() -> memberService.unregister(member1.getId() + 1000))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateInfo() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);

        Long id1 = memberService.signUp(request);
        Long id2 = memberService.signUp(request2);

        Member member1 = memberRepository.findById(id1).orElseThrow();
        Member member2 = memberRepository.findById(id2).orElseThrow();

        MemberUpdateInfoRequest updateParam = MemberUpdateInfoRequest.builder()
                .username("newUsername")
                .email("newEmail")
                .nickname("newNickname")
                .build();

        MemberUpdateInfoRequest nullParam = MemberUpdateInfoRequest.builder()
                .username(null)
                .email(null)
                .nickname(null)
                .build();

        //when
        memberService.updateInfo(member1.getId(), updateParam);
        memberService.updateInfo(member2.getId(), nullParam);

        em.flush();
        em.clear();


        //then
        Member changedMember = memberRepository.findById(member1.getId()).orElseThrow();
        assertThat(changedMember.getUsername()).isEqualTo(updateParam.getUsername());
        assertThat(changedMember.getEmail()).isEqualTo(updateParam.getEmail());
        assertThat(changedMember.getNickname()).isEqualTo(updateParam.getNickname());

        Member changedMember2 = memberRepository.findById(member2.getId()).orElseThrow();
        assertThat(changedMember2.getUsername()).isNotEqualTo(nullParam.getUsername());
        assertThat(changedMember2.getEmail()).isNotEqualTo(nullParam.getEmail());
        assertThat(changedMember2.getNickname()).isNotEqualTo(nullParam.getNickname());

        assertThatThrownBy(() -> memberService.updateInfo(member1.getId() + 1000, updateParam))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updatePassword() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);

        Long id = memberService.signUp(request);

        Member member = memberRepository.findById(id).orElseThrow();

        String p1 = "newPassword";
        String p2 = null;

        PasswordUpdateRequest pRequest1 = new PasswordUpdateRequest(member.getPassword(), p1);
        PasswordUpdateRequest pRequest2 = new PasswordUpdateRequest(member.getPassword() + "!", p1);
        PasswordUpdateRequest pRequest3 = new PasswordUpdateRequest(member.getPassword() , p2);

        //when
        memberService.updatePassword(member.getId(), pRequest1);

        em.flush();
        em.clear();

        //then
        Member changedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(changedMember.getPassword()).isEqualTo(pRequest1.getNewPassword());

        assertThatThrownBy(() -> memberService.updatePassword(id + 1000, pRequest1))
                .isInstanceOf(NoSuchElementException.class);

        assertThatThrownBy(() -> memberService.updatePassword(id, pRequest2))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> memberService.updatePassword(id, pRequest3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void baseEntity() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);

        Long id = memberService.signUp(request);

        Member member = memberRepository.findById(id).orElseThrow();

        LocalDateTime oldCreatedDate = member.getCreatedDate();
        LocalDateTime oldLastModifiedDate = member.getLastModifiedDate();

        //when
        memberService.updatePassword(member.getId(), new PasswordUpdateRequest(member.getPassword(), "newPassword"));
        em.flush();
        em.clear();

        //then
        Member changedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(changedMember.getCreatedDate()).isEqualTo(oldCreatedDate);
        assertThat(changedMember.getLastModifiedDate()).isAfter(oldLastModifiedDate);

    }

}