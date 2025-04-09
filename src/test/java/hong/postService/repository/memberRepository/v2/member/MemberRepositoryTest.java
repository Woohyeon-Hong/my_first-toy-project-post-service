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
    void findByIdAndIsRemovedFalse() {
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
    void findByUsernameAndIsRemovedFalse() {
        // given
        Member m1 = memberRepository.save(Member.createNewMember("user", "pw", "e@e.com", "nick"));
        Member m2 = memberRepository.save(Member.createNewMember("user", "pw2", "e2@e.com", "nick2"));

        m2.remove();

        em();

        // when
        Optional<Member> result = memberRepository.findByUsernameAndIsRemovedFalse("user");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(m1.getId());
    }

    @Test
    void findAllByUsernameAndIsRemovedFalse() {
        // given
        Member m1 = Member.createNewMember("same", "pw", "a@a.com", "n1");
        Member m2 = Member.createNewMember("same", "pw2", "b@b.com", "n2");

        memberRepository.save(m1);
        memberRepository.save(m2);

        m2.remove();

        // when
        List<Member> members = memberRepository.findAllByUsernameAndIsRemovedFalse("same");

        // then
        assertThat(members).containsExactly(m1);
    }

    @Test
    void findAllByPasswordAndIsRemovedFalse() {
        // given
        Member m1 = Member.createNewMember("u1", "1234", "a@a.com", "n1");
        Member m2 = Member.createNewMember("u2", "1234", "b@b.com", "n2");
        Member m3 = Member.createNewMember("u3", "0000", "c@c.com", "n3");

        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        m2.remove();

        // when
        List<Member> members = memberRepository.findAllByPasswordAndIsRemovedFalse("1234");

        // then
        assertThat(members).containsExactly(m1);
    }

    @Test
    void findAllByEmailAndIsRemovedFalse() {
        // given
        Member m1 = Member.createNewMember("u1", "pw", "same@naver.com", "n1");
        Member m2 = Member.createNewMember("u2", "pw", "same@naver.com", "n2");

        memberRepository.save(m1);
        memberRepository.save(m2);

        m2.remove();

        // when
        List<Member> members = memberRepository.findAllByEmailAndIsRemovedFalse("same@naver.com");

        // then
        assertThat(members).containsExactly(m1);
    }

    @Test
    void findAllByNicknameAndIsRemovedFalse() {
        // given
        Member m1 = Member.createNewMember("u1", "pw", "a@a.com", "동일닉");
        Member m2 = Member.createNewMember("u2", "pw", "b@b.com", "동일닉");

        memberRepository.save(m1);
        memberRepository.save(m2
        );
        m2.remove();

        // when
        List<Member> members = memberRepository.findAllByNicknameAndIsRemovedFalse("동일닉");

        // then
        assertThat(members).containsExactly(m1);
    }

    private void em() {
        em.flush();
        em.clear();
    }
}
