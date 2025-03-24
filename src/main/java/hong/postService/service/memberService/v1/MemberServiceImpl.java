//package hong.postService.service.memberService;
//
//import hong.postService.domain.Member;
//import hong.postService.service.memberService.v2.MemberUpdateDto;
//import hong.postService.repository.memberRepository.UsersRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//
//@Service
//@RequiredArgsConstructor
//public class MemberServiceImpl implements MemberService{
//
//    private final UsersRepository usersRepository;
//
//    @Override
//    public Member signUp(Member member) {
//        String loginId = member.getLoginId();
//
//        if(usersRepository.findAll().stream().
//                anyMatch((ordinary) -> ordinary.getLoginId().equals(loginId))) {
//            throw new IllegalArgumentException();
//        } else {
//            return usersRepository.save(member);
//        }
//    }
//
//    @Override
//    public void unregister(Long id) {
//        if (isIdExisted(id)) {
//            usersRepository.delete(id);
//        } else {
//            throw new NoSuchElementException();
//        }
//    }
//
//    private boolean isIdExisted(Long id) {
//        List<Member> members = usersRepository.findAll();
//        return members.stream().anyMatch(member -> member.getId() == id);
//    }
//
//
//    @Override
//    public Member logIn(String loginId, String password) {
//        Member findMember = usersRepository.findByLoginId(loginId).orElseThrow();
//        String findMemberPassword = findMember.getPassword();
//
//        if (findMemberPassword.equals(password)) {
//            return findMember;
//        } else {
//            throw new IllegalArgumentException();
//        }
//    }
//
//    @Override
//    public Member updateInfo(Long id, MemberUpdateDto updateParam) {
//        Member findMember = usersRepository.findById(id).orElseThrow();
//        updateParam.setPassword(findMember.getPassword());
//
//        usersRepository.update(id, updateParam);
//
//        return usersRepository.findById(id).get();
//
//    }
//
//    @Override
//    public Member updatePassword(Long id, String newPassword) {
//        Member findMember = usersRepository.findById(id).orElseThrow();
//        MemberUpdateDto updateParam = new MemberUpdateDto(newPassword);
//        updateParam.setName(findMember.getName());
//        updateParam.setLoginId(findMember.getLoginId());
//
//        usersRepository.update(id, updateParam);
//
//        return usersRepository.findById(id).get();
//    }
//}
