package hong.postService.service.memberService.v2;

import hong.postService.domain.Member;
import hong.postService.domain.UserRole;
import hong.postService.exception.member.DuplicateMemberFieldException;
import hong.postService.exception.member.InvalidMemberFieldException;
import hong.postService.exception.member.MemberNotFoundException;
import hong.postService.exception.member.PasswordMismatchException;
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
    void usernameDuplicateCheck_기존_username들_중_일치하는게_있거나_null이_들어올_경우_예외_발생() {
        //given
        Member member1 = Member.createNewMember("user1", "p1", "u1@naver.com", "n1");
        memberRepository.save(member1);

        Member member2 = Member.createNewMember("user2", "p2", "u2@naver.com", "n2");
        memberRepository.save(member2);

        member2.remove();

        flushAndClear();

        String ok = "user2";
        String notOk = "user1";

        //when & then
        memberService.usernameDuplicateCheck(ok);

        assertThatThrownBy(() -> memberService.usernameDuplicateCheck(notOk)).isInstanceOf(DuplicateMemberFieldException.class);
        assertThatThrownBy(() -> memberService.usernameDuplicateCheck(null)).isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void emailDuplicateCheck_기존_email들_중_일치하는게_있거나_null이_들어올_경우_예외_발생() {
        //given
        Member member1 = Member.createNewMember("user1", "p1", "u1@naver.com", "n1");
        memberRepository.save(member1);

        Member member2 = Member.createNewMember("user2", "p2", "u2@naver.com", "n2");
        memberRepository.save(member2);

        member2.remove();

        flushAndClear();

        String ok = "u2@naver.com";
        String notOk = "u1@naver.com";

        //when & then
        memberService.emailDuplicateCheck(ok);

        assertThatThrownBy(() -> memberService.emailDuplicateCheck(notOk)).isInstanceOf(DuplicateMemberFieldException.class);
        assertThatThrownBy(() -> memberService.emailDuplicateCheck(null)).isInstanceOf(InvalidMemberFieldException.class);

    }

    @Test
    void nicknameDuplicateCheck_기존_nickname들_중_일치하는게_있거나_null이_들어올_경우_예외_발생() {
        //given
        Member member1 = Member.createNewMember("user1", "p1", "u1@naver.com", "n1");
        memberRepository.save(member1);

        Member member2 = Member.createNewMember("user2", "p2", "u2@naver.com", "n2");
        memberRepository.save(member2);

        member2.remove();

        flushAndClear();

        String ok = "n2";
        String notOk = "n1";

        //when & then
        memberService.nicknameDuplicateCheck(ok);

        assertThatThrownBy(() -> memberService.nicknameDuplicateCheck(notOk)).isInstanceOf(DuplicateMemberFieldException.class);
        assertThatThrownBy(() -> memberService.nicknameDuplicateCheck(null)).isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void signUpMember_정상_가입() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);

        //when
        Long id1 = memberService.signUp(request);
        Long id2 = memberService.signUp(request2);

        //then
        Member member1 = memberRepository.findByIdAndIsRemovedFalse(id1).orElseThrow();
        Member member2 = memberRepository.findByIdAndIsRemovedFalse(id2).orElseThrow();

        List<Member> members = memberRepository.findAll();

        assertThat(member1.getRole()).isEqualTo(UserRole.USER);
        assertThat(member2.getRole()).isEqualTo(UserRole.USER);

        assertThat(members).containsExactly(member1, member2);
    }

    @Test
    void signUpMember_null인_필드() {
        //given
        UserCreateRequest request = new UserCreateRequest(null, null, null, null, UserRole.USER);

        //when & then
        assertThatThrownBy(() -> memberService.signUp(request)).isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void signUpMember_중복된_필드() {
        //given
        UserCreateRequest request = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);

        memberService.signUp(request);

        //when & then
        assertThatThrownBy(() -> memberService.signUp(request2)).isInstanceOf(DuplicateMemberFieldException.class);
    }


    @Test
    void signUpAdmin_정상_가입() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);

        //when
        Long id1 = memberService.signUp(request);
        Long id2 = memberService.signUp(request2);

        //then
        Member member1 = memberRepository.findByIdAndIsRemovedFalse(id1).orElseThrow();
        Member member2 = memberRepository.findByIdAndIsRemovedFalse(id2).orElseThrow();

        List<Member> members = memberRepository.findAll();

        assertThat(member1.getRole()).isEqualTo(UserRole.USER);
        assertThat(member2.getRole()).isEqualTo(UserRole.USER);

        assertThat(members).containsExactly(member1, member2);
    }

    @Test
    void signUpAdmin_null인_필드() {
        //given
        UserCreateRequest request = new UserCreateRequest(null, null, null, null, UserRole.USER);

        //when & then
        assertThatThrownBy(() -> memberService.signUp(request)).isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void signUpAdmin_중복된_필드() {
        //given
        UserCreateRequest request = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);

        memberService.signUp(request);

        //when & then

        assertThatThrownBy(() -> memberService.signUp(request2)).isInstanceOf(DuplicateMemberFieldException.class);
    }


    @Test
    void unregister_회원ID가_없으면_예외_발생하고_아니면_정상_수행() {
        //given
        UserCreateRequest request1 = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);
        UserCreateRequest request3 = new UserCreateRequest("user3", "p3", "u3@naver.com", "n3", UserRole.USER);

        Long id1 = memberService.signUp(request1);
        Long id2 = memberService.signUp(request2);
        Long id3 = memberService.signUp(request3);

        Member member1 = memberRepository.findByIdAndIsRemovedFalse(id1).orElseThrow();
        Member member2 = memberRepository.findByIdAndIsRemovedFalse(id2).orElseThrow();
        Member member3 = memberRepository.findByIdAndIsRemovedFalse(id3).orElseThrow();


        //when
        memberService.unregister(member1.getId());
        memberService.unregister(member2.getId());

        //then
        List<Member> members = memberRepository.findAll();
        assertThat(members).containsExactly(member3);

        assertThatThrownBy(() -> memberService.unregister(member1.getId() + 1000))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void updateInfo_필드에_null_또는_새로운_값이_들어오면_정상_업데이트() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("newUsername", "p2", "new@naver.com", "newNickname", UserRole.USER);

        Long id = memberService.signUp(request);
        Long id2 = memberService.signUp(request2);

        memberService.unregister(id2);

        Member member = memberRepository.findByIdAndIsRemovedFalse(id).orElseThrow();

        MemberUpdateInfoRequest updateParam = MemberUpdateInfoRequest.builder()
                .username("newUsername")
                .email("new@naver.com")
                .build();

        //when
        memberService.updateInfo(member.getId(), updateParam);

        flushAndClear();

        //then
        Member changedMember = memberRepository.findByIdAndIsRemovedFalse(member.getId()).orElseThrow();

        assertThat(changedMember.getUsername()).isEqualTo(updateParam.getUsername());
        assertThat(changedMember.getEmail()).isEqualTo(updateParam.getEmail());

        assertThat(changedMember.getLastModifiedDate()).isAfter(changedMember.getCreatedDate());
    }

    @Test
    void updateInfo_기존_필드와_일치할_경우_아무런_작업_수행x() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);

        Long id = memberService.signUp(request);

        Member member = memberRepository.findByIdAndIsRemovedFalse(id).orElseThrow();

        MemberUpdateInfoRequest updateParam = MemberUpdateInfoRequest.builder()
                .username(member.getUsername())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();

        //when
        memberService.updateInfo(id, updateParam);

        //then
        Member changedMember = memberRepository.findByIdAndIsRemovedFalse(id).orElseThrow();
        assertThat(changedMember).isEqualTo(member);

        assertThat(changedMember.getLastModifiedDate()).isEqualTo(changedMember.getCreatedDate());
    }

    @Test
    void updateInfo_필드가_중복되면_예외_발생() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);

        Long id = memberService.signUp(request);
        Long id2 = memberService.signUp(request2);

        Member another = memberRepository.findByIdAndIsRemovedFalse(id2).orElseThrow();

        MemberUpdateInfoRequest updateParam = MemberUpdateInfoRequest.builder()
                .username(another.getUsername())
                .email(another.getEmail())
                .build();

        //when & then
        assertThatThrownBy(() -> memberService.updateInfo(id, updateParam)).isInstanceOf(DuplicateMemberFieldException.class);
    }

    @Test
    void updatePassword_기존_비번_확인_후_일치하면_업데이트() {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest userCreateRequest2 = new UserCreateRequest("user2", "new", "u2@naver.com", "n2", UserRole.USER);

        Long id = memberService.signUp(userCreateRequest);
        Long id2 = memberService.signUp(userCreateRequest2);

        memberService.unregister(id2);

        PasswordUpdateRequest request1 = new PasswordUpdateRequest("p1", "new");
        PasswordUpdateRequest request2 = new PasswordUpdateRequest("틀린 비번", "new");

        // when
        memberService.updatePassword(id, request1);
        flushAndClear();

        // then
        Member findMember = memberService.findMember(id);
        assertThat(findMember.getLastModifiedDate()).isAfter(findMember.getCreatedDate());

        assertThatThrownBy(() -> memberService.updatePassword(id, request2))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    void baseEntity_정상_작동_확인() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);

        Long id = memberService.signUp(request);

        Member member = memberRepository.findByIdAndIsRemovedFalse(id).orElseThrow();

        LocalDateTime oldCreatedDate = member.getCreatedDate();
        LocalDateTime oldLastModifiedDate = member.getLastModifiedDate();

        //when
        memberService.updatePassword(member.getId(), new PasswordUpdateRequest("p1", "newPassword"));
        flushAndClear();

        //then
        Member changedMember = memberRepository.findByIdAndIsRemovedFalse(member.getId()).orElseThrow();
        assertThat(changedMember.getCreatedDate()).isEqualTo(oldCreatedDate);
        assertThat(changedMember.getLastModifiedDate()).isAfter(oldLastModifiedDate);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

}