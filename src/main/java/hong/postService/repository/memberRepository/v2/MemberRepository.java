package hong.postService.repository.memberRepository.v2;

import hong.postService.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


/**
 * 회원 Repository API
 *
 * Create
 *      Member save(member) - 회원/어드민 가입 시
 *
 * Read
 *      Member findByIdAndIsRemovedFalse(id)
 *      Member findByUsernameAndIsRemovedFalse(id)
 *
 *      List<Member> findAllByUsernameAndIsRemovedFalse(String username) - username 중복 조회 시
 *      List<Member> findAllByPasswordAndIsRemovedFalse(String password) - password 중복 조회 시
 *      List<Member> findAllByEmailAndIsRemovedFalse(String email) - email 중복 조회 시
 *      List<Member> findAllByNicknameAndIsRemovedFalse(String nickname) - nickname 중복 조회 시
 *
 *Delete
 *      void delete(member) - 회원 탈퇴 시
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByIdAndIsRemovedFalse(Long id);
    Optional<Member> findByUsernameAndIsRemovedFalse(String username);
    Optional<Member> findByEmailAndIsRemovedFalse(String email);
    Optional<Member> findByNicknameAndIsRemovedFalse(String nickname);

}
