package hong.postService.repository.fileRepository.v2;

import hong.postService.TestSecurityConfig;
import hong.postService.domain.File;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Import(TestSecurityConfig.class)
@SpringBootTest
class FileRepositoryTest {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;


    @Test
    void findByIdAndIsRemovedFalse() {
        //given
        Member m = Member.createNewMember("user", "pw", "e@e.com", "nick");
        memberRepository.save(m);

        Post p = m.writeNewPost("title1", "content1");
        postRepository.save(p);

        File file1 = p.addNewFile("file1.txt");
        File file2 = p.addNewFile("file2.txt");

        fileRepository.save(file1);
        fileRepository.save(file2);

        file2.remove();

        //when
        Optional<File> result1 = fileRepository.findByIdAndIsRemovedFalse(file1.getId());
        Optional<File> result2 = fileRepository.findByIdAndIsRemovedFalse(file2.getId());

        //then
        Assertions.assertThat(result1).isPresent();
        Assertions.assertThat(result2).isEmpty();
    }
}