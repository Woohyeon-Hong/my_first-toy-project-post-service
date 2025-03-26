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

        Post post = Post.writeNewPost("title", "content", member);

        //when
        postRepository.save(post);

        //then
        Post findPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(findPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(findPost.getContent()).isEqualTo(post.getContent());
        assertThat(member.getPosts()).contains(post);

        assertThatThrownBy(() -> Post.writeNewPost("t", "c", null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void findMemberPostWithPaging() {
        //given
        Member memberA = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(memberA);
        Member memberB = Member.createNewMember("userB", "pB", "userB@naver.com", "nicknameB");
        memberRepository.save(memberB);

        for (int i = 1; i <= 100; i++) {
            Member writer = memberA;
            if (i % 2 == 0) writer = memberB;
            Post post = Post.writeNewPost("title" + i, "content" + i, writer);
            postRepository.save(post);
        }

        PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        List<Post> postsOfMemberA = postRepository.findPostsByWriter(memberA, pageable).getContent();
        List<Post> postsOfMemberB = postRepository.findPostsByWriter(memberB, pageable).getContent();

        //then
        for (int i = 1; i <= 9; i = i + 2) {
            assertThat(postsOfMemberA.get((i - 1) / 2).getTitle()).isEqualTo("title" + i);
            assertThat(postsOfMemberB.get((i - 1) / 2).getTitle()).isEqualTo("title" + (i + 1));
        }
    }

    @Test
    void findAllWithoutPaging() {
        //given
        Member memberA = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(memberA);

        for (int i = 1; i <= 10; i++) {
            Post post = Post.writeNewPost("title" + i, "content" + i, memberA);
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
            Post post = Post.writeNewPost("title" + i, "content" + i, memberA);
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
    void findMemberPostWithoutPaging() {
        //given
        Member memberA = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(memberA);
        Member memberB = Member.createNewMember("userB", "pB", "userB@naver.com", "nicknameB");
        memberRepository.save(memberB);

        for (int i = 1; i <= 10; i++) {
            Member writer = memberA;
            if (i % 2 == 0) writer = memberB;
            Post post = Post.writeNewPost("title" + i, "content" + i, writer);
            postRepository.save(post);
        }

        //when
        List<Post> postsOfMemberA = postRepository.findPostsByWriter(memberA);
        List<Post> postsOfMemberB = postRepository.findPostsByWriter(memberB);

        //then
        for (int i = 0; i < 5; i++) {
            assertThat(postsOfMemberA.get(i).getTitle()).isEqualTo("title" + (2 * i + 1));
            assertThat(postsOfMemberB.get(i).getTitle()).isEqualTo("title" + (2 * i + 2));
        }
    }

    @Test
    void searchPostsWithPaging() {
        //given
        Member member = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(member);

        Member member2 = Member.createNewMember("userB", "pB", "userB@naver.com", "nicknameB");
        memberRepository.save(member2);

        for (int i = 1; i <= 100; i++) {
            Member writer = member;
            if (i % 2 == 0) writer = member2;
            Post post = Post.writeNewPost("title" + i, "content" + i, writer);
            postRepository.save(post);
        }

        SearchCond usernameCond = SearchCond.builder()
                .username("user")
                .build();

        SearchCond usernameCond2 = SearchCond.builder()
                .username("userA")
                .build();

        SearchCond titleCond = SearchCond.builder()
                .title("title")
                .build();

        SearchCond titleCond2 = SearchCond.builder()
                .title("title9")
                .build();

        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));

        //when
        List<Post> postsWithUsernameCond = postRepository.searchPosts(usernameCond, pageable).getContent();
        List<Post> postsWithUsernameCond2 = postRepository.searchPosts(usernameCond2, pageable).getContent();

        List<Post> postsWithTitleCond = postRepository.searchPosts(titleCond, pageable).getContent();
        List<Post> postsWithTitleCond2 = postRepository.searchPosts(titleCond2, pageable).getContent();

        //then
        assertThat(postsWithUsernameCond.size()).isEqualTo(10);
        assertThat(postsWithUsernameCond2.size()).isEqualTo(10);
        assertThat(postsWithTitleCond.size()).isEqualTo(10);
        assertThat(postsWithTitleCond2.size()).isEqualTo(10);
        System.out.println(postsWithTitleCond.get(0).getWriter().getUsername());

    }

    @Test
    void searchPostsWithoutPaging() {
        //given
        Member member = Member.createNewMember("userA", "pA", "userA@naver.com", "nicknameA");
        memberRepository.save(member);

        Member member2 = Member.createNewMember("userB", "pB", "userB@naver.com", "nicknameB");
        memberRepository.save(member2);

        for (int i = 1; i <= 100; i++) {
            Member writer = member;
            if (i % 2 == 0) writer = member2;
            Post post = Post.writeNewPost("title" + i, "content" + i, writer);
            postRepository.save(post);
        }

        SearchCond usernameCond = SearchCond.builder()
                .username("user")
                .build();

        SearchCond usernameCond2 = SearchCond.builder()
                .username("userA")
                .build();

        SearchCond titleCond = SearchCond.builder()
                .title("title")
                .build();

        SearchCond titleCond2 = SearchCond.builder()
                .title("title99")
                .build();

        //when
        List<Post> postsWithUsernameCond = postRepository.searchPosts(usernameCond);
        List<Post> postsWithUsernameCond2 = postRepository.searchPosts(usernameCond2);

        List<Post> postsWithTitleCond = postRepository.searchPosts(titleCond);
        List<Post> postsWithTitleCond2 = postRepository.searchPosts(titleCond2);

        //then
        assertThat(postsWithUsernameCond.size()).isEqualTo(100);
        assertThat(postsWithUsernameCond2.size()).isEqualTo(50);
        assertThat(postsWithTitleCond.size()).isEqualTo(100);
        assertThat(postsWithTitleCond2.size()).isEqualTo(1);
        System.out.println(postsWithTitleCond.get(0).getWriter().getUsername());
    }
}