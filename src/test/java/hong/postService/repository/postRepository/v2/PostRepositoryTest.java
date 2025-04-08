package hong.postService.repository.postRepository.v2;

import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Test
    void save() {
        //given
        Member member = Member.createNewMember("user", "p", "user@naver.com", "nickname");
        memberRepository.save(member);

        Post post = member.writeNewPost("title", "content");

        //when
        postRepository.save(post);

        //then
        Post findPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(findPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(findPost.getContent()).isEqualTo(post.getContent());
        assertThat(member.getPosts()).contains(post);
    }

    @Test
    void findMemberPostWithoutPaging() {
        //given
        Member memberA = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(memberA);
        Member memberB = Member.createNewMember("userB", "pB", "userB@naver.com", "nicknameB");
        memberRepository.save(memberB);

        for (int i = 1; i <= 10; i++) {
            Post post;
            if (i % 2 != 0) post = memberA.writeNewPost("title" + i, "content" + i);
            else post = memberB.writeNewPost("title" + i, "content" + i);
            postRepository.save(post);
        }

        //when
        List<Post> postsOfMemberA = postRepository.findAllByWriter(memberA);
        List<Post> postsOfMemberB = postRepository.findAllByWriter(memberB);

        //then
        for (int i = 0; i < 5; i++) {
            assertThat(postsOfMemberA.get(i).getTitle()).isEqualTo("title" + (2 * i + 1));
            assertThat(postsOfMemberB.get(i).getTitle()).isEqualTo("title" + (2 * i + 2));
        }
    }

    @Test
    void findMemberPostWithPaging() {
        //given
        Member memberA = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(memberA);
        Member memberB = Member.createNewMember("userB", "pB", "userB@naver.com", "nicknameB");
        memberRepository.save(memberB);

        for (int i = 1; i <= 100; i++) {
            Post post;
            if (i % 2 != 0) post = memberA.writeNewPost("title" + i, "content" + i);
            else post = memberB.writeNewPost("title" + i, "content" + i);
            postRepository.save(post);
        }

        PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<Post> posts1 = postRepository.findAllByWriter(memberA, pageable);
        Page<Post> posts2 = postRepository.findAllByWriter(memberA, pageable2);
        Page<Post> posts3 = postRepository.findAllByWriter(memberB, pageable);
        Page<Post> posts4 = postRepository.findAllByWriter(memberB, pageable2);

        //then
        assertThat(posts1.getTotalPages()).isEqualTo(10);
        assertThat(posts2.getTotalPages()).isEqualTo(10);

        assertThat(posts1.getSize()).isEqualTo(5);
        assertThat(posts2.getSize()).isEqualTo(5);
        assertThat(posts3.getSize()).isEqualTo(5);
        assertThat(posts4.getSize()).isEqualTo(5);

        assertThat(posts1.getContent().get(0).getTitle()).isEqualTo("title1");
        assertThat(posts2.getContent().get(0).getTitle()).isEqualTo("title11");
        assertThat(posts3.getContent().get(0).getTitle()).isEqualTo("title2");
        assertThat(posts4.getContent().get(0).getTitle()).isEqualTo("title12");
    }

    @Test
    void findAllWithoutPaging() {
        //given
        Member memberA = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(memberA);

        for (int i = 1; i <= 10; i++) {
            Post post = memberA.writeNewPost("title" + i, "content" + i);
            postRepository.save(post);
        }

        //when
        List<Post> posts = postRepository.findAll();

        //then
        for (int i = 1; i <= 10; i++) {
            assertThat(posts.get(i - 1).getTitle()).isEqualTo("title" + i);
        }
    }

    @Test
    void findAllWithPaging() {
        //given
        Member memberA = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(memberA);

        for (int i = 1; i <= 10; i++) {
            Post post = memberA.writeNewPost("title" + i, "content" + i);
            postRepository.save(post);
        }

        PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        List<Post> posts = postRepository.findAll(pageable).getContent();
        List<Post> posts2= postRepository.findAll(pageable2).getContent();

        //then
        for (int i = 1; i <= 5; i++) {
            assertThat(posts.get(i - 1).getTitle()).isEqualTo("title" + i);
            assertThat(posts2.get(i - 1).getTitle()).isEqualTo("title" + (i + 5));
        }
    }

    @Test
    void searchPostsWithoutPaging() {
        //given
        Member member = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(member);

        Member member2 = Member.createNewMember("userB", "pB", "userB@naver.com", "nicknameB");
        memberRepository.save(member2);

        for (int i = 1; i <= 100; i++) {
            Post post;
            if (i % 2 != 0) post = member.writeNewPost("title" + i, "content" + i);
            else post = member2.writeNewPost("title" + i, "content" + i);
            postRepository.save(post);
        }

        SearchCond nicknameCond = SearchCond.builder()
                .writer("nickname")
                .build();

        SearchCond nicknameCond2 = SearchCond.builder()
                .writer("nicknameA")
                .build();

        SearchCond titleCond = SearchCond.builder()
                .title("title")
                .build();

        SearchCond titleCond2 = SearchCond.builder()
                .title("title99")
                .build();

        //when
        List<Post> postsWithUsernameCond = postRepository.searchPosts(nicknameCond);
        List<Post> postsWithUsernameCond2 = postRepository.searchPosts(nicknameCond2);

        List<Post> postsWithTitleCond = postRepository.searchPosts(titleCond);
        List<Post> postsWithTitleCond2 = postRepository.searchPosts(titleCond2);

        //then
        assertThat(postsWithUsernameCond.size()).isEqualTo(100);
        assertThat(postsWithUsernameCond2.size()).isEqualTo(50);
        assertThat(postsWithTitleCond.size()).isEqualTo(100);
        assertThat(postsWithTitleCond2.size()).isEqualTo(1);
        System.out.println(postsWithTitleCond.get(0).getWriter().getUsername());
    }

    @Test
    void searchPostsWithPaging() {
        //given
        Member member = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(member);

        Member member2 = Member.createNewMember("userB", "pB", "userB@naver.com", "nicknameB");
        memberRepository.save(member2);

        for (int i = 1; i <= 100; i++) {
            Post post;
            if (i % 2 != 0) post = member.writeNewPost("title" + i, "content" + i);
            else post = member2.writeNewPost("title" + i, "content" + i);
            postRepository.save(post);
        }

        SearchCond nicknameCond1 = SearchCond.builder()
                .writer("nickname")
                .build();

        SearchCond nicknameCond2 = SearchCond.builder()
                .writer("nicknameB")
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
        Page<Post> posts1 = postRepository.searchPosts(nicknameCond1, pageable);
        Page<Post> posts2 = postRepository.searchPosts(nicknameCond1, pageable2);

        Page<Post> posts3 = postRepository.searchPosts(nicknameCond2, pageable);
        Page<Post> posts4 = postRepository.searchPosts(nicknameCond2, pageable2);

        Page<Post> posts5 = postRepository.searchPosts(titleCond, pageable);
        Page<Post> posts6 = postRepository.searchPosts(titleCond, pageable2);

        Page<Post> posts7 = postRepository.searchPosts(titleCond2, pageable);

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
}