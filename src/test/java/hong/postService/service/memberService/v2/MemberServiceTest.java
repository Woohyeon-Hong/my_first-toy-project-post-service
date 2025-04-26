package hong.postService.service.memberService.v2;

import hong.postService.domain.Member;
import hong.postService.domain.UserRole;
import hong.postService.exception.member.DuplicateMemberFieldException;
import hong.postService.exception.member.InvalidMemberFieldException;
import hong.postService.exception.member.MemberNotFoundException;
import hong.postService.exception.member.PasswordMismatchException;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.service.memberService.dto.MemberUpdateInfoRequest;
import hong.postService.service.memberService.dto.OAuthCreateRequest;
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

        assertThatThrownBy(() -> memberService.usernameDuplicateCheck(notOk))
                .isInstanceOf(DuplicateMemberFieldException.class);

        assertThatThrownBy(() -> memberService.usernameDuplicateCheck(null))
                .isInstanceOf(InvalidMemberFieldException.class);
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

        assertThatThrownBy(() -> memberService.emailDuplicateCheck(notOk))
                .isInstanceOf(DuplicateMemberFieldException.class);

        assertThatThrownBy(() -> memberService.emailDuplicateCheck(null))
                .isInstanceOf(InvalidMemberFieldException.class);

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

        assertThatThrownBy(() -> memberService.nicknameDuplicateCheck(notOk))
                .isInstanceOf(DuplicateMemberFieldException.class);

        assertThatThrownBy(() -> memberService.nicknameDuplicateCheck(null))
                .isInstanceOf(InvalidMemberFieldException.class);
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
        Member member1 = memberService.findMember(id1);
        Member member2 = memberService.findMember(id2);

        List<Member> members = memberRepository.findAllByIsRemovedFalse();

        assertThat(member1.getRole()).isEqualTo(UserRole.USER);
        assertThat(member2.getRole()).isEqualTo(UserRole.USER);

        assertThat(members).containsExactly(member1, member2);
    }

    @Test
    void signUpMember_null인_필드() {
        //given
        UserCreateRequest request = new UserCreateRequest(null, null, null, null, UserRole.USER);

        //when & then
        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void signUpMember_중복된_필드() {
        //given
        UserCreateRequest request = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);

        memberService.signUp(request);

        //when & then
        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(DuplicateMemberFieldException.class);
    }


    @Test
    void signUpAdmin_정상_가입() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.ADMIN);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.ADMIN);

        //when
        Long id1 = memberService.signUpAdmin(request);
        Long id2 = memberService.signUpAdmin(request2);

        //then
        Member member1 = memberService.findMember(id1);
        Member member2 =  memberService.findMember(id2);

        List<Member> members = memberRepository.findAllByIsRemovedFalse();

        assertThat(member1.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(member2.getRole()).isEqualTo(UserRole.ADMIN);

        assertThat(members).containsExactly(member1, member2);
    }

    @Test
    void signUpAdmin_null인_필드() {
        //given
        UserCreateRequest request = new UserCreateRequest(null, null, null, null, UserRole.ADMIN);

        //when & then
        assertThatThrownBy(() -> memberService.signUpAdmin(request))
                .isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void signUpAdmin_중복된_필드() {
        //given
        UserCreateRequest request = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.ADMIN);

        memberService.signUpAdmin(request);

        //when & then
        assertThatThrownBy(() -> memberService.signUpAdmin(request))
                .isInstanceOf(DuplicateMemberFieldException.class);
    }

    @Test
    void signUpWithOAuth_username이_null이_아니면_정상_가입() {
        //given
        OAuthCreateRequest request1 = new OAuthCreateRequest("username1", "email1@gmail.com");
        OAuthCreateRequest request2 = new OAuthCreateRequest("username2", null);

        //when
        Long id1 = memberService.signUpWithOAuth(request1);
        Long id2 = memberService.signUpWithOAuth(request2);

        //then
        Member findMember1 = memberService.findMember(id1);
        Member findMember2 = memberService.findMember(id2);

        List<Member> members = memberRepository.findAllByIsRemovedFalse();

        assertThat(members).containsExactly(findMember1, findMember2);

        assertThatThrownBy(() -> memberService.signUpWithOAuth(new OAuthCreateRequest(null, null)))
                .isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void signUpWithOAuth_필드가_중복되면_예외_발생() {
        //given
        OAuthCreateRequest request1 = new OAuthCreateRequest("username", "email1@gmail.com");
        OAuthCreateRequest request2 = new OAuthCreateRequest("username", null);
        OAuthCreateRequest request3 = new OAuthCreateRequest("username2", "email1@gmail.com");

        memberService.signUpWithOAuth(request1);

        //when & then
        assertThatThrownBy(() -> memberService.signUpWithOAuth(request2))
                .isInstanceOf(DuplicateMemberFieldException.class);

        assertThatThrownBy(() -> memberService.signUpWithOAuth(request3))
                .isInstanceOf(DuplicateMemberFieldException.class);

    }


    @Test
    void unregister_회원ID가_존재하면_정상_수행() {
        //given
        UserCreateRequest request1 = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.ADMIN);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.ADMIN);
        UserCreateRequest request3 = new UserCreateRequest("user3", "p3", "u3@naver.com", "n3", UserRole.ADMIN);

        Long id1 = memberService.signUpAdmin(request1);
        Long id2 = memberService.signUpAdmin(request2);
        Long id3 = memberService.signUpAdmin(request3);

        Member member1 = memberService.findMember(id1);
        Member member2 = memberService.findMember(id2);
        Member member3 = memberService.findMember(id3);


        //when
        memberService.unregister(member1.getId());
        memberService.unregister(member2.getId());

        //then
        List<Member> members = memberRepository.findAllByIsRemovedFalse();
        assertThat(members).containsExactly(member3);

        assertThatThrownBy(() -> memberService.unregister(member1.getId() + 1000))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void updateInfoOfNotOAuthMember_필드에_null이_아닌_새로운_값이_들어오면_정상_업데이트() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("newUsername", "p2", "new@naver.com", "newNickname", UserRole.USER);

        Long id1 = memberService.signUp(request);
        Long id2 = memberService.signUp(request2);

        memberService.unregister(id2);

        MemberUpdateInfoRequest updateParam = MemberUpdateInfoRequest.builder()
                .username(request2.getUsername())
                .email(request2.getEmail())
                .build();

        //when
        memberService.updateInfoOfNotOAuthMember(id1, updateParam);
        flushAndClear();

        //then
        Member changedMember = memberService.findMember(id1);

        assertThat(changedMember.getUsername()).isEqualTo(updateParam.getUsername());
        assertThat(changedMember.getEmail()).isEqualTo(updateParam.getEmail());

        assertThat(changedMember.getLastModifiedDate()).isAfter(changedMember.getCreatedDate());
    }

    @Test
    void updateInfoOfNotOAuthMember_기존_필드와_일치할_경우_아무런_작업_수행x() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);

        Long id = memberService.signUp(request);

        Member member = memberService.findMember(id);

        MemberUpdateInfoRequest updateParam = MemberUpdateInfoRequest.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .nickname(request.getNickname())
                .build();

        //when
        memberService.updateInfoOfNotOAuthMember(id, updateParam);
        flushAndClear();

        //then
        Member changedMember = memberService.findMember(id);

        assertThat(changedMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(changedMember.getEmail()).isEqualTo(member.getEmail());
        assertThat(changedMember.getNickname()).isEqualTo(member.getNickname());

        assertThat(changedMember.getLastModifiedDate()).isEqualTo(changedMember.getCreatedDate());
    }

    @Test
    void updateInfoOfNotOAuthMember_필드가_중복되면_예외_발생() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest request2 = new UserCreateRequest("user2", "p2", "u2@naver.com", "n2", UserRole.USER);

        Long id1 = memberService.signUp(request);
        memberService.signUp(request2);

        MemberUpdateInfoRequest updateParam = MemberUpdateInfoRequest.builder()
                .username(request2.getUsername())
                .email(request2.getEmail())
                .build();

        //when & then
        assertThatThrownBy(() -> memberService.updateInfoOfNotOAuthMember(id1, updateParam))
                .isInstanceOf(DuplicateMemberFieldException.class);
    }

    @Test
    void updateInfoOfNotOAuthMember_OAuth회원이면_예외_발생() {
        //given
        Long userId = memberService.signUpWithOAuth(new OAuthCreateRequest("username", "email@naver.com"));

        MemberUpdateInfoRequest request = new MemberUpdateInfoRequest("newUsername", "new@naver.com", "newNickname");

        //when & then
        assertThatThrownBy(() -> memberService.updateInfoOfNotOAuthMember(userId, request))
                .isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void updateNickname_회원이_존재하면_정상_업데이트() {
        //given
        Long userId = memberService.signUpWithOAuth(new OAuthCreateRequest("username", "email@naver.com"));
        String newNickname = "newNick";

        //when
        memberService.updateNickname(userId, newNickname);

        //then
        flushAndClear();
        Member findMember = memberService.findMember(userId);

        assertThat(findMember.getNickname()).isEqualTo(newNickname);
    }

    @Test
    void updatePasswordOfNotOAuthMember_기존_비번_확인_후_일치하면_업데이트() {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);
        UserCreateRequest userCreateRequest2 = new UserCreateRequest("user2", "new", "u2@naver.com", "n2", UserRole.USER);

        Long id = memberService.signUp(userCreateRequest);
        Long id2 = memberService.signUp(userCreateRequest2);

        memberService.unregister(id2);

        PasswordUpdateRequest request1 = new PasswordUpdateRequest("p1", "new");
        PasswordUpdateRequest request2 = new PasswordUpdateRequest("틀린 비번", "new");

        // when
        memberService.updatePasswordOfNotOAuthMember(id, request1);
        flushAndClear();

        // then
        Member findMember = memberService.findMember(id);

        assertThat(findMember.getLastModifiedDate()).isAfter(findMember.getCreatedDate());

        assertThatThrownBy(() -> memberService.updatePasswordOfNotOAuthMember(id, request2))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    void updatePasswordoOfNotOAuthMember_OAuth회원이면_예외_발생() {
        //given
        Long userId = memberService.signUpWithOAuth(new OAuthCreateRequest("username", "email@naver.com"));
        Member member = memberService.findMember(userId);

        PasswordUpdateRequest request = new PasswordUpdateRequest(member.getPassword(), "newPassword");

        //when & then
        assertThatThrownBy(() -> memberService.updatePasswordOfNotOAuthMember(userId, request))
                .isInstanceOf(InvalidMemberFieldException.class);
    }

    @Test
    void baseEntity_정상_작동_확인() {
        //given
        UserCreateRequest request = new UserCreateRequest("user1", "p1", "u1@naver.com", "n1", UserRole.USER);

        Long id = memberService.signUp(request);

        Member member = memberService.findMember(id);

        LocalDateTime oldCreatedDate = member.getCreatedDate();
        LocalDateTime oldLastModifiedDate = member.getLastModifiedDate();

        //when
        memberService.updatePasswordOfNotOAuthMember(member.getId(), new PasswordUpdateRequest("p1", "newPassword"));
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