//package hong.postService.repository.memberRepository;
//
//import hong.postService.domain.Member;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//@RequiredArgsConstructor
//public class UsersRepositoryImpl implements UsersRepository{
//
//    private final MemberMapper memberMapper;
//
//    @Override
//    public Member save(Member member) {
//        memberMapper.save(member);
//        return member;
//    }
//
//    @Override
//    public Optional<Member> findById(Long id) {
//        return memberMapper.findById(id);
//    }
//
//    @Override
//    public Optional<Member> findByLoginId(String loginId) {
//        return memberMapper.findByLoginId(loginId);
//    }
//
//    @Override
//    public List<Member> findAll() {
//        return memberMapper.findAll();
//    }
//
//    @Override
//    public void update(Long id, MemberUpdateDto updateParam) {
//        memberMapper.update(id, updateParam);
//    }
//
//    @Override
//    public void delete(Long id) {
//        memberMapper.delete(id);
//    }
//}
