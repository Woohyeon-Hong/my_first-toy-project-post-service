package hong.postService.repository.memberRepository.v1;

import hong.postService.domain.Member;
import hong.postService.service.memberService.dto.MemberUpdateInfoRequest;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {

    Member save(Member member);

   Optional<Member> findById(Long id);
   Optional<Member> findByLoginId(String loginId);
    List<Member> findAll();

    void update(Long id, MemberUpdateInfoRequest updateParam);

    void delete(Long id);
}
