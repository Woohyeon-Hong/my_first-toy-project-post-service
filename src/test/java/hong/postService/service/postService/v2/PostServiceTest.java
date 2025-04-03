package hong.postService.service.postService.v2;

import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
import hong.postService.repository.postRepository.v2.SearchCond;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostService postService;
    @Autowired
    EntityManager em;

    @Test
    void write() {
        //given
        Member writer = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(writer);

        //when
        Long savedId = postService.write(writer.getId(), "title1", "content1");

        //then
        Post post = postRepository.findById(savedId).orElseThrow();

        assertThat(post.getTitle()).isEqualTo("title1");
        assertThat(post.getContent()).isEqualTo("content1");
        assertThat(post.getWriter()).isEqualTo(writer);
    }

    @Test
    void deletePost() {
        //given
        Member writer = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(writer);

        Long savedId = postService.write(writer.getId(), "title1", "content1");
        Post post = postRepository.findById(savedId).orElseThrow();

        //when
        postService.delete(savedId);

        //then
        List<Post> posts = postRepository.findAll();
        assertThat(posts).doesNotContain(post);
    }

    @Test
    void getPosts() {
        //given
        Member writer = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(writer);

        for (int i = 1; i <= 50; i++) {
            postService.write(writer.getId(), "title" + i, "content" + i);
        }

        PageRequest pageRequest1 = PageRequest.of(0, 25, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageRequest2 = PageRequest.of(1, 25, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Post> posts1 = postService.getPosts(pageRequest1);
        Page<Post> posts2 = postService.getPosts(pageRequest2);

        //then
        assertThat(posts1.getTotalPages()).isEqualTo(2);
        assertThat(posts1.getSize()).isEqualTo(25);
        assertThat(posts2.getSize()).isEqualTo(25);
        assertThat(posts1.getContent().get(0).getTitle()).isEqualTo("title1");
        assertThat(posts2.getContent().get(0).getTitle()).isEqualTo("title26");
    }

    @Test
    void getMemberPosts() {
        //given
        Member member = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(member);

        Member member2 = Member.createNewMember("userB", "pB", "userB@naver.com", "nicknameB");
        memberRepository.save(member2);

        for (int i = 1; i <= 100; i++) {
            if (i % 2 != 0) postService.write(member.getId(), "title" + i, "content" + i);
            else postService.write(member2.getId(), "title" + i, "content" + i);
        }

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Post> posts = postService.getMemberPosts(member.getId(), pageRequest);
        Page<Post> posts2 = postService.getMemberPosts(member2.getId(), pageRequest);

        //then
        assertThat(posts.getSize()).isEqualTo(10);
        assertThat(posts2.getSize()).isEqualTo(10);
        assertThat(posts.getTotalPages()).isEqualTo(5);
        assertThat(posts2.getTotalPages()).isEqualTo(5);
    }

    @Test
    void search() {
        //given
        Member member = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(member);

        Member member2 = Member.createNewMember("userB", "pB", "userB@naver.com", "nicknameB");
        memberRepository.save(member2);

        for (int i = 1; i <= 100; i++) {
            if (i % 2 != 0) postService.write(member.getId(), "title" + i, "content" + i);
            else postService.write(member2.getId(), "title" + i, "content" + i);
        }

        SearchCond usernameCond = SearchCond.builder()
                .writer("user")
                .build();

        SearchCond usernameCond2 = SearchCond.builder()
                .writer("userB")
                .build();

        SearchCond titleCond = SearchCond.builder()
                .title("title")
                .build();

        SearchCond titleCond2 = SearchCond.builder()
                .title("title99")
                .build();

        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Post> posts1 = postService.search(usernameCond, pageable);
        Page<Post> posts2 = postService.search(usernameCond, pageable2);

        Page<Post> posts3 = postService.search(usernameCond2, pageable);
        Page<Post> posts4 = postService.search(usernameCond2, pageable2);

        Page<Post> posts5 = postService.search(titleCond, pageable);
        Page<Post> posts6 = postService.search(titleCond, pageable2);

        Page<Post> posts7 = postService.search(titleCond2, pageable);

        //then
        assertThat(posts1.getTotalPages()).isEqualTo(10);
        assertThat(posts3.getTotalPages()).isEqualTo(5);
        assertThat(posts5.getTotalPages()).isEqualTo(10);
        assertThat(posts7.getTotalPages()).isEqualTo(1);

        assertThat(posts1.getSize()).isEqualTo(10);
        assertThat(posts2.getSize()).isEqualTo(10);
        assertThat(posts3.getSize()).isEqualTo(10);
        assertThat(posts4.getSize()).isEqualTo(10);
        assertThat(posts5.getSize()).isEqualTo(10);
        assertThat(posts6.getSize()).isEqualTo(10);

        assertThat(posts1.getContent().get(0).getTitle()).isEqualTo("title1");
        assertThat(posts2.getContent().get(0).getTitle()).isEqualTo("title11");
        assertThat(posts3.getContent().get(0).getTitle()).isEqualTo("title2");
        assertThat(posts4.getContent().get(0).getTitle()).isEqualTo("title22");
        assertThat(posts5.getContent().get(0).getTitle()).isEqualTo("title1");
        assertThat(posts6.getContent().get(0).getTitle()).isEqualTo("title11");
    }

    @Test
    void update() {
        //given
        Member writer = Member.createNewMember("user", "p", "e@naver.com", "nickname");
        memberRepository.save(writer);

        Long savedId = postService.write(writer.getId(), "title1", "content1");

        String newTitle = "newTitle";
        String newContent = "newContent";

        PostUpdateRequest updateParam = PostUpdateRequest.builder()
                .title(newTitle)
                .content(newContent)
                .build();

        //when
        postService.update(savedId, updateParam);
        em.flush();
        em.clear();

        //then
        Post findPost = postRepository.findById(savedId).orElseThrow();

        assertThat(findPost.getTitle()).isEqualTo(newTitle);
        assertThat(findPost.getContent()).isEqualTo(newContent);
        assertThat(findPost.getLastModifiedDate()).isAfter(findPost.getCreatedDate());
    }
}