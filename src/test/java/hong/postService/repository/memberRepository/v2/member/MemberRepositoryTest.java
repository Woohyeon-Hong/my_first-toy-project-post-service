package hong.postService.repository.memberRepository.v2.member;

import hong.postService.domain.Member;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.service.memberService.dto.MemberUpdateInfoRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    void findByIdAndIsRemovedFalse_Optional로_감싸서_반환() {
        // given
        Member m1 = Member.createNewMember("user", "pw", "e@e.com", "nick");
        Member m2 = Member.createNewMember("user2", "pw", "e2@e.com", "nick2");

        memberRepository.save(m1);
        memberRepository.save(m2);

        m2.remove();

        em();

        // when
        Optional<Member> result1 = memberRepository.findByIdAndIsRemovedFalse(m1.getId());
        Optional<Member> result2 = memberRepository.findByIdAndIsRemovedFalse(m2.getId());

        // then
        assertThat(result1).isPresent();
        assertThat(result2).isEmpty();
    }

    @Test
    void findByUsernameAndIsRemovedFalse_Optional로_감싸서_반환() {
        // given
        Member m1 = memberRepository.save(Member.createNewMember("user", "pw", "e@e.com", "nick"));
        Member m2 = memberRepository.save(Member.createNewMember("user2", "pw2", "e2@e.com", "nick2"));

        m2.remove();

        em();

        // when
        Optional<Member> result1 = memberRepository.findByUsernameAndIsRemovedFalse("user");
        Optional<Member> result2 = memberRepository.findByUsernameAndIsRemovedFalse("user2");

        // then
        assertThat(result1).isPresent();
        assertThat(result2).isEmpty();

        assertThat(result1.get().getId()).isEqualTo(m1.getId());
    }

    @Test
    void findByEmailAndIsRemovedFalse_Optional로_감싸서_반환() {
        // given
        Member m1 = memberRepository.save(Member.createNewMember("user", "pw", "e@e.com", "nick"));
        Member m2 = memberRepository.save(Member.createNewMember("user2", "pw2", "e2@e.com", "nick2"));

        m2.remove();

        em();

        // when
        Optional<Member> result1 = memberRepository.findByEmailAndIsRemovedFalse("e@e.com");
        Optional<Member> result2 = memberRepository.findByEmailAndIsRemovedFalse("e2@e.com");

        // then
        assertThat(result1).isPresent();
        assertThat(result2).isEmpty();

        assertThat(result1.get().getId()).isEqualTo(m1.getId());
    }

    @Test
    void findByNicknameAndIsRemovedFalse_Optional로_감싸서_반환() {
        // given
        Member m1 = memberRepository.save(Member.createNewMember("user", "pw", "e@e.com", "nick"));
        Member m2 = memberRepository.save(Member.createNewMember("user2", "pw2", "e2@e.com", "nick2"));

        m2.remove();

        em();

        // when
        Optional<Member> result1 = memberRepository.findByNicknameAndIsRemovedFalse("nick");
        Optional<Member> result2 = memberRepository.findByNicknameAndIsRemovedFalse("nick2");

        // then
        assertThat(result1).isPresent();
        assertThat(result2).isEmpty();

        assertThat(result1.get().getId()).isEqualTo(m1.getId());
    }

    private void em() {
        em.flush();
        em.clear();
    }
}
