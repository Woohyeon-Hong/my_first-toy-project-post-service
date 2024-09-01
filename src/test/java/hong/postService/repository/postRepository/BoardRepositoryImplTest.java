package hong.postService.repository.postRepository;

import hong.postService.domain.Post;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardRepositoryImplTest {

    @Autowired BoardRepository boardRepository;

    @Test
    void save() {
        //given
        Post post = new Post("title", "cotent", LocalDateTime.now(), 1L);

        //when
        Post saved = boardRepository.save(post);

        //then
        Post findPost = boardRepository.findById(saved.getId()).get();
        assertThat(findPost).isEqualTo(saved);
    }

    @Test
    void findMemberPosts() {
        //given
        Post post1 = new Post("title1", "content1", LocalDateTime.now(), 1L);
        Post post2 = new Post("title2", "content2", LocalDateTime.now(), 1L);
        Post post3 = new Post("title3", "content3", LocalDateTime.now(), 2L);
        Post post4 = new Post("title4", "content4", LocalDateTime.now(), 3L);

        Post saved1 = boardRepository.save(post1);
        Post saved2 = boardRepository.save(post2);
        Post saved3 = boardRepository.save(post3);
        Post saved4 = boardRepository.save(post4);

        //when
        List<Post> posts = boardRepository.findMemberPosts(1L);

        //then
        assertThat(posts).contains(saved1, saved2);
        assertThat(posts).doesNotContain(saved3, saved4);
    }

    @Test
    void findAll() {
        //given
        Post post1 = new Post("title1", "content1", LocalDateTime.now(), 1L);
        Post post2 = new Post("title2", "content2", LocalDateTime.now(), 2L);
        Post post3 = new Post("title3", "content3", LocalDateTime.now(), 3L);

        Post saved1 = boardRepository.save(post1);
        Post saved2 = boardRepository.save(post2);
        Post saved3 = boardRepository.save(post3);

        //when
        List<Post> posts = boardRepository.findAll();

        //then
        assertThat(posts).contains(saved1, saved2, saved3);
    }

    @Test
    void update() throws InterruptedException {
        //given
        Post post = new Post("title", "cotent", LocalDateTime.now(), 1L);
        Post saved = boardRepository.save(post);

        PostUpdateDto updateParam = new PostUpdateDto("new_title", "new_content", LocalDateTime.now());

        //when
        boardRepository.update(saved.getId(), updateParam);

        //then
        saved.setTitle(updateParam.getTitle());
        saved.setContent(updateParam.getContent());
        saved.setModifiedDate(updateParam.getModifiedDate());

        assertThat(boardRepository.findById(saved.getId()).get()).isEqualTo(saved);
    }

    @Test
    void delete() {
        //given
        Post post = new Post("title", "cotent", LocalDateTime.now(), 1L);
        Post saved = boardRepository.save(post);

        //when
        boardRepository.delete(saved.getId());

        //then
        List<Post> posts = boardRepository.findAll();
        assertThat(posts).doesNotContain(saved);
    }
}
