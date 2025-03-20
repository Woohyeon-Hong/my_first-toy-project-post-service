package hong.postService.repository.memberRepository.v2.member;

import hong.postService.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * 필요한 APIs
 *
 * Create
 *      Member save(member) - 회원/어드민 가입 시
 *
 * Read
 *      Member findById(id)
 *      List<Member> findAllByUsername(String username) - username 중복 조회 시
 *      List<Member> findAllByPassword(String password) - password 중복 조회 시
 *      List<Member> findAllByEmail(String email) - email 중복 조회 시
 *      List<Member> findAllByNickname(String nickname) - nickname 중복 조회 시
 *
 *Delete
 *      void delete(member) - 회원 탈퇴 시
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByUsername(String username);
    List<Member> findAllByPassword(String password);
    List<Member> findAllByEmail(String email);
    List<Member> findAllByNickname(String nickname);

}
