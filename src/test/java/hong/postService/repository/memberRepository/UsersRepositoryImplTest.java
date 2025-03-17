//package hong.postService.repository.memberRepository;
//
//import hong.postService.domain.Member;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//class UsersRepositoryImplTest {
//
//    @Autowired UsersRepository usersRepository;
//
//    @Test
//    void save() {
//        //given
//        Member member = new Member("save", "id", "pw");
//
//        //when
//        Member saved = usersRepository.save(member);
//
//        //then
//        Member findMember = usersRepository.findById(saved.getId()).get();
//        assertThat(findMember).isEqualTo(saved);
//
//    }
//
//    @Test
//    void findByLoginId() {
//        //given
//        Member member = new Member("findByLoginId", "id", "pw");
//        Member saved = usersRepository.save(member);
//
//        //when
//        Member findMember = usersRepository.findByLoginId(member.getLoginId()).get();
//
//        //then
//        assertThat(findMember).isEqualTo(saved);
//    }
//
//    @Test
//    void findAll() {
//        //given
//        Member member1 = new Member("findAll1", "id1", "pw1");
//        Member member2 = new Member("findAll2", "id2", "pw2");
//        Member member3 = new Member("findAll3", "id3", "pw3");
//        Member member4 = new Member("findAll4", "id4", "pw4");
//
//        Member saved1 = usersRepository.save(member1);
//        Member saved2 = usersRepository.save(member2);
//        Member saved3 = usersRepository.save(member3);
//        Member saved4 = usersRepository.save(member4);
//
//        //when
//        List<Member> allMembers = usersRepository.findAll();
//
//        //then
//        assertThat(allMembers).contains(saved1, saved2, saved3, saved4);
//    }
//
//    @Test
//    void update() {
//        //given
//        Member member = new Member("update", "id", "pw");
//        Member saved = usersRepository.save(member);
//
//        MemberUpdateDto updateParam = new MemberUpdateDto("new", "new_id", "new _pw");
//
//        //when
//        usersRepository.update(saved.getId(), updateParam);
//
//        //then
//        Member updatedMember = new Member(updateParam.getName(), updateParam.getLoginId(), updateParam.getPassword());
//        updatedMember.setId(saved.getId());
//
//        assertThat(usersRepository.findById(updatedMember.getId()).get()).isEqualTo(updatedMember);
//    }
//
//    @Test
//    void delete() {
//        //given
//        Member member = new Member("delete", "id", "pw");
//        Member saved = usersRepository.save(member);
//
//        //when
//        usersRepository.delete(saved.getId());
//
//        //then
//        List<Member> members = usersRepository.findAll();
//        assertThat(members).doesNotContain(saved);
//    }
//
//}