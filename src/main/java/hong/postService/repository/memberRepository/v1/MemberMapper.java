//package hong.postService.repository.memberRepository;
//
//import hong.postService.domain.Member;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//
//import java.util.List;
//import java.util.Optional;
//
//@Mapper
//public interface MemberMapper {
//
//    //create
//    void save(Member member);
//
//    //read
//    Optional<Member> findById(Long id);
//    Optional<Member> findByLoginId(String loginId);
//    List<Member> findAll();
//
//    //update
//    void update(@Param("id") Long id, @Param("updateParam") MemberUpdateDto updateParam);
//
//    //delete
//    void delete(Long id);
//}
