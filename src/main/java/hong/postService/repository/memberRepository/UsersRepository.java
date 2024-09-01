package hong.postService.repository.memberRepository;

import hong.postService.domain.Member;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {

    Member save(Member member);

   Optional<Member> findById(Long id);
   Optional<Member> findByLoginId(String loginId);
    List<Member> findAll();

    void update(Long id, MemberUpdateDto updateParam);

    void delete(Long id);
}
