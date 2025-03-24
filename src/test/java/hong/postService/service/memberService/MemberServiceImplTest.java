//package hong.postService.service.memberService;
//
//import hong.postService.domain.Member;
//import hong.postService.service.memberService.v2.MemberUpdateDto;
//import hong.postService.repository.memberRepository.UsersRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;;
//
//@Slf4j
//@SpringBootTest
//class MemberServiceImplTest {
//
//    @Autowired MemberService memberService;
//    @Autowired UsersRepository usersRepository;
//
//    @Test
//    void signUp() {
//        //given
//        Member member = new Member("signUp", "id", "pw");
//
//        //when
//        Member registered = memberService.signUp(member);
//
//        //then
//        member.setId(registered.getId());
//        assertThat(registered).isEqualTo(member);
//    }
//
//    @Test
//    void signUpIfLoginIdExist() {
//        //given
//        Member member1 = new Member("signUp", "id", "pw");
//        Member member2 = new Member("signUp", "id", "pw");
//
//        //when
//        Member registered = memberService.signUp(member1);
//        assertThatThrownBy(() ->
//                memberService.signUp(member2));
//    }
//
//    @Test
//    void unregister() {
//        //given
//        Member member1 = new Member("unregister", "id", "pw");
//        Member saved = memberService.signUp(member1);
//
//        Member member2 = new Member("unregister2", "id", "pw");
//        Member saved2 = memberService.signUp(member1);
//
//        Member member3 = new Member("unregister3", "id", "pw");
//        Member saved3 = memberService.signUp(member1);
//
//        //when
//        memberService.unregister(saved.getId());
//
//        //then
//        List<Member> members = usersRepository.findAll();
//        assertThat(members).doesNotContain(saved);
//
//        members.stream().forEach((eachMember)-> log.info("member={}", eachMember));
//    }
//
//    @Test
//     void login() {
//        //given
//        Member member = new Member("login", "id", "pw");
//        memberService.signUp(member);
//
//        //when
//        Member matchMember = memberService.logIn(member.getLoginId(), member.getPassword());
//
//        //then
//        assertThat(matchMember).isEqualTo(member);
//    }
//
//    @Test
//    void loginFail() {
//        //given
//        Member member = new Member("login", "id", "pw");
//        memberService.signUp(member);
//
//        //when
//        assertThatThrownBy(() -> memberService.logIn(member.getLoginId(), "fail"));
//    }
//
//    @Test
//    void updateInfo() {
//        //given
//        Member member = new Member("updateInfo", "id", "pw");
//        MemberUpdateDto updateParam = new MemberUpdateDto("update", "new_id");
//
//        Member saved = memberService.signUp(member);
//
//        member.setId(saved.getId());
//        member.setName(updateParam.getName());
//        member.setLoginId(updateParam.getLoginId());
//
//        //when
//        Member updated = memberService.updateInfo(saved.getId(), updateParam);
//
//        //then
//        assertThat(updated).isEqualTo(member);
//        log.info("member={}", usersRepository.findById(saved.getId()));
//    }
//
//    @Test
//    void updatePassword() {
//        //given
//        Member member = new Member("new_pw", "id", "pw");
//        MemberUpdateDto updateParam = new MemberUpdateDto("new_pw");
//
//        Member saved = usersRepository.save(member);
//
//        saved.setPassword(updateParam.getPassword());
//
//        //when
//        Member updated = memberService.updatePassword(saved.getId(), "new_pw");
//
//        //then
//        assertThat(updated).isEqualTo(saved);
//    }
//
//
//}