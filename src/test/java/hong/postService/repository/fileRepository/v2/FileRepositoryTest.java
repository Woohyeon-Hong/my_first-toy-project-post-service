//package hong.postService.repository.fileRepository.v2;
//
//import hong.postService.TestSecurityConfig;
//import hong.postService.domain.File;
//import hong.postService.domain.Member;
//import hong.postService.domain.Post;
//import hong.postService.repository.memberRepository.v2.MemberRepository;
//import hong.postService.repository.postRepository.v2.PostRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Transactional
//@Import(TestSecurityConfig.class)
//@SpringBootTest
//class FileRepositoryTest {
//
//    @Autowired
//    FileRepository fileRepository;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    PostRepository postRepository;
//
//
//    @Test
//    void findByIdAndIsRemovedFalse_File이_삭제되지_않았으면_정상반환() {
//        //given
//        Member m = Member.createNewMember("user", "pw", "e@e.com", "nick");
//        memberRepository.save(m);
//
//        Post p = m.writeNewPost("title1", "content1");
//        postRepository.save(p);
//
//        File file1 = p.addNewFile("file1.txt");
//        File file2 = p.addNewFile("file2.txt");
//
//        fileRepository.save(file1);
//        fileRepository.save(file2);
//
//        file2.remove();
//
//        //when
//        Optional<File> result1 = fileRepository.findByIdAndIsRemovedFalse(file1.getId());
//        Optional<File> result2 = fileRepository.findByIdAndIsRemovedFalse(file2.getId());
//
//        //then
//        assertThat(result1).isPresent();
//        assertThat(result2).isEmpty();
//    }
//
//    @Test
//    void findByS3KeyAndIsRemovedFalse_File이_삭제되지_않았으면_정상반환() {
//
//        //given
//        Member m = Member.createNewMember("user", "pw", "e@e.com", "nick");
//        memberRepository.save(m);
//
//        Post p = m.writeNewPost("title1", "content1");
//        postRepository.save(p);
//
//        File file1 = p.addNewFile("file1.txt");
//        File file2 = p.addNewFile("file2.txt");
//
//        fileRepository.save(file1);
//        fileRepository.save(file2);
//
//        file2.remove();
//
//        //when
//        Optional<File> result1 = fileRepository.findByS3KeyAndIsRemovedFalse(file1.getS3Key());
//        Optional<File> result2 = fileRepository.findByS3KeyAndIsRemovedFalse(file2.getS3Key());
//
//        //then
//        assertThat(result1).isPresent();
//        assertThat(result2).isEmpty();
//    }
//
//    @Test
//    void findByStoredFileNameAndIsRemovedFalse_File이_삭제되지_않았으면_정상반환() {
//
//        //given
//        Member m = Member.createNewMember("user", "pw", "e@e.com", "nick");
//        memberRepository.save(m);
//
//        Post p = m.writeNewPost("title1", "content1");
//        postRepository.save(p);
//
//        File file1 = p.addNewFile("file1.txt");
//        File file2 = p.addNewFile("file2.txt");
//
//        fileRepository.save(file1);
//        fileRepository.save(file2);
//
//        file2.remove();
//
//        //when
//        Optional<File> result1 = fileRepository.findByStoredFileNameAndIsRemovedFalse(file1.getStoredFileName());
//        Optional<File> result2 = fileRepository.findByStoredFileNameAndIsRemovedFalse(file2.getStoredFileName());
//
//        //then
//        assertThat(result1).isPresent();
//        assertThat(result2).isEmpty();
//    }
//
//    @Test
//    void findAllByPostIdAndIsRemovedFalse_삭제되지_않은_모든_File_반환() {
//
//        //given
//        Member m = Member.createNewMember("user", "pw", "e@e.com", "nick");
//        memberRepository.save(m);
//
//        Post p1 = m.writeNewPost("title1", "content1");
//        Post p2 = m.writeNewPost("title2", "content2");
//        postRepository.save(p1);
//        postRepository.save(p2);
//
//        for (int i = 1; i <= 6; i++) {
//            File file = (i % 2 == 0) ? p1.addNewFile("file" + i + ".txt") : p2.addNewFile("file" + i + ".txt");
//            fileRepository.save(file);
//
//            if (i == 5 || i == 6) file.remove();
//        }
//
//        //when
//        List<File> result1 = fileRepository.findAllByPostIdAndIsRemovedFalse(p1);
//        List<File> result2 = fileRepository.findAllByPostIdAndIsRemovedFalse(p2);
//
//        //then
//        assertThat(result1.size()).isEqualTo(2);
//        assertThat(result2.size()).isEqualTo(2);
//    }
// }