package hong.postService.repository.memberRepository.v2.member;

import hong.postService.domain.Member;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.service.memberService.v2.MemberUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void save() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Member admin = Member.createNewAdmin("username2", "password2", null, "nickname2");

        //when
        memberRepository.save(member);
        memberRepository.save(admin);

        //then
        Member findMember = memberRepository.findById(member.getId()).orElseThrow();
        Member findAdmin = memberRepository.findById(admin.getId()).orElseThrow();

        assertThat(findMember).isEqualTo(member);
        assertThat(findAdmin).isEqualTo(admin);
    }

    @Test
    void findAllByUsername() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Member member2 = Member.createNewMember("username", "password2", null, "nickname2");
        Member member3 = Member.createNewMember("username2", "password3", null, "nickname3");
        memberRepository.save(member);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        List<Member> members = memberRepository.findAllByUsername("username");

        //then
        assertThat(members).containsExactly(member, member2);
    }

    @Test
    void findAllByPassword() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Member member2 = Member.createNewMember("usernam2", "password", null, "nickname2");
        Member member3 = Member.createNewMember("username3", "password2", null, "nickname3");
        memberRepository.save(member);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        List<Member> members = memberRepository.findAllByPassword("password");

        //then
        assertThat(members).containsExactly(member, member2);
    }

    @Test
    void findAllByEmail() {
        //given
        Member member = Member.createNewMember("username", "password", "hong@naver.com", "nickname");
        Member member2 = Member.createNewMember("username2", "password2", "hong@naver.com", "nickname2");
        Member member3 = Member.createNewMember("username3", "password3", "woohyeon@naver.com", "nickname3");
        memberRepository.save(member);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        List<Member> members = memberRepository.findAllByEmail("hong@naver.com");

        //then
        assertThat(members).containsExactly(member, member2);
    }

    @Test
    void findAllByNickname() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        Member member2 = Member.createNewMember("username", "password2", null, "nickname");
        Member member3 = Member.createNewMember("username2", "password3", null, "nickname2");
        memberRepository.save(member);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        List<Member> members = memberRepository.findAllByNickname("nickname");

        //then
        assertThat(members).containsExactly(member, member2);
    }

    @Test
    void updateInfo() {
        //given
        Member member = Member.createNewMember("oldUsername", "oldPassword", "old@naver.com", "oldNickname");
        memberRepository.save(member);

        MemberUpdateDto updateParam = MemberUpdateDto.builder()
                .username("newUsername")
                .email("new@naver.com")
                .nickname("newNickname")
                .build();


        //when
        Member findMember = memberRepository.findById(member.getId()).orElseThrow();
        findMember.changeUsername(updateParam.getUsername());
        findMember.changeEmail(updateParam.getEmail());
        findMember.changeNickname(updateParam.getNickname());

        //then
        Member changedMember = memberRepository.findById(findMember.getId()).orElseThrow();
        assertThat(changedMember.getUsername()).isEqualTo(updateParam.getUsername());
        assertThat(changedMember.getEmail()).isEqualTo(updateParam.getEmail());
        assertThat(changedMember.getNickname()).isEqualTo(updateParam.getNickname());
    }

    @Test
    void updatePassword() {
        //given
        Member member = Member.createNewMember("oldUsername", "oldPassword", "old@naver.com", "oldNickname");
        memberRepository.save(member);

        String newPassword = "newPassword";
        String nullPassword = null;

        //when
        Member findMember = memberRepository.findById(member.getId()).orElseThrow();
        findMember.changePassword(newPassword);

        //then
        Member changedMember = memberRepository.findById(findMember.getId()).orElseThrow();
        assertThat(changedMember.getPassword()).isEqualTo(newPassword);
        assertThatThrownBy(() -> findMember.changePassword(nullPassword)).isInstanceOf(NullPointerException.class);
    }


    @Test
    void delete() {
        //given
        Member member = Member.createNewMember("username", "password", null, "nickname");
        memberRepository.save(member);

        //when
        memberRepository.delete(member);

        //then
        List<Member> members = memberRepository.findAll();
        assertThat(members).doesNotContain(member);

        assertThatThrownBy(() -> memberRepository.delete(null)).isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

}