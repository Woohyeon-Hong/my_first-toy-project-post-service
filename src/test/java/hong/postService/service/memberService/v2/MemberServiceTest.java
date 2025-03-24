package hong.postService.service.memberService.v2;

import hong.postService.domain.Member;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        Member member1 = Member.createNewMember("user1", "p1", "u1@naver.com", "n1");
        Member member2 = Member.createNewMember("user2", "p2", "u2@naver.com", "n2");
        Member member3 = Member.createNewMember("user2", "p3", "u3@naver.com", "n3");

        //when
        memberService.signUp(member1);
        memberService.signUp(member2);

        //then
        List<Member> members = memberRepository.findAll();
        assertThat(members).containsExactly(member1, member2);
        assertThatThrownBy(() -> memberService.signUp(member3)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void unregister() {
        //given
        Member member1 = Member.createNewMember("user1", "p1", "u1@naver.com", "n1");
        Member member2 = Member.createNewMember("user2", "p2", "u2@naver.com", "n2");
        Member member3 = Member.createNewMember("user3", "p3", "u3@naver.com", "n3");
        memberService.signUp(member1);
        memberService.signUp(member2);
        memberService.signUp(member3);

        //when
        memberService.unregister(member1.getId());
        memberService.unregister(member2.getId());

        //then
        List<Member> members = memberRepository.findAll();
        assertThat(members).containsExactly(member3);
    }

    @Test
    void updateInfo() {
        //given
        Member member1 = Member.createNewMember("user1", "p1", "u1@naver.com", "n1");
        Member member2 = Member.createNewMember("user2", "p2", "u2@naver.com", "n2");
        memberService.signUp(member1);
        memberService.signUp(member2);

        MemberUpdateDto updateParam = MemberUpdateDto.builder()
                .username("newUsername")
                .email("newEmail")
                .nickname("newNickname")
                .build();

        MemberUpdateDto nullParam = MemberUpdateDto.builder()
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
    }

    @Test
    void updatePassword() {
        //given
        Member member1 = Member.createNewMember("user1", "p1", "u1@naver.com", "n1");
        Member member2 = Member.createNewMember("user2", "p2", "u2@naver.com", "n2");
        memberService.signUp(member1);
        memberService.signUp(member2);

        String p1 = "newPassword";
        String p2 = null;

        //when
        memberService.updatePassword(member1.getId(), p1);
        memberService.updatePassword(member2.getId(), p2);

        em.flush();
        em.clear();


        //then
        Member changedMember = memberRepository.findById(member1.getId()).orElseThrow();
        assertThat(changedMember.getPassword()).isEqualTo(p1);

        Member changedMember2 = memberRepository.findById(member2.getId()).orElseThrow();
        assertThat(changedMember2.getPassword()).isNotEqualTo(p2);
    }

    @Test
    void baseEntity() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        memberRepository.save(member);

        LocalDateTime oldCreatedDate = member.getCreatedDate();
        LocalDateTime oldLastModifiedDate = member.getLastModifiedDate();

        //when
        memberService.updatePassword(member.getId(), "newPassword");
        em.flush();
        em.clear();

        //then
        Member changedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(changedMember.getCreatedDate()).isEqualTo(oldCreatedDate);
        assertThat(changedMember.getLastModifiedDate()).isAfter(oldLastModifiedDate);

    }

}