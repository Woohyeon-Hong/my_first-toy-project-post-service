package hong.postService.service.postService;

import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.repository.postRepository.BoardRepository;
import hong.postService.repository.postRepository.PostUpdateDto;
import hong.postService.service.memberService.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Slf4j
class PostServiceImplTest {

    @Autowired PostService postService;
    @Autowired BoardRepository boardRepository;
    @Autowired MemberService memberService;

    @Test
    void upload() {
        //given
        Post post = new Post("title", "content", 1L);

        //when
        Post uploaded = postService.upload(post);

        //then
        Post findPost = boardRepository.findById(uploaded.getId()).get();
        assertThat(findPost).isEqualTo(uploaded);
        log.info("post={}", post);
    }

    @Test
    void deletePost() {
        //given
        Post post1 = new Post("title", "content", 1L);
        Post uploaded1 = postService.upload(post1);

        Post post2 = new Post("title2", "content", 1L);
        Post uploaded2 = postService.upload(post2);

        Post post3 = new Post("title3", "content", 1L);
        Post uploaded3 = postService.upload(post3);

        //when
        postService.deletePost(uploaded1.getId());

        //then
        List<Post> posts = boardRepository.findAll();
        assertThat(posts).doesNotContain(uploaded1);
        assertThat(posts).contains(uploaded2, uploaded3);
    }


    @Test
    void showAllPosts() {
        //given
        Member member = new Member("name", "id", "pw");
        memberService.signUp(member);

        Post post1 = new Post("title", "content", 1L);
        Post uploaded1 = postService.upload(post1);

        Post post2 = new Post("title2", "content", 1L);
        Post uploaded2 = postService.upload(post2);

        Post post3 = new Post("title3", "content", 1L);
        Post uploaded3 = postService.upload(post3);

        //when
        List<Post> posts = postService.showAllPosts();

        //then
        assertThat(posts).contains(uploaded1, uploaded2, uploaded3);
    }

    @Test
    void showMemberPosts() {
        //given
        Member member = new Member("name", "id1", "pw");
        memberService.signUp(member);

        Member member2 = new Member("name", "id2", "pw");
        memberService.signUp(member2);

        Post post1 = new Post("title", "content", 1L);
        Post uploaded1 = postService.upload(post1);

        Post post2 = new Post("title2", "content", 1L);
        Post uploaded2 = postService.upload(post2);

        Post post3 = new Post("title3", "content", 1L);
        Post uploaded3 = postService.upload(post3);

        Post post4 = new Post("title", "content", 2L);
        Post uploaded4 = postService.upload(post4);

        Post post5 = new Post("title2", "content", 2L);
        Post uploaded5 = postService.upload(post5);

        Post post6 = new Post("title3", "content", 2L);
        Post uploaded6 = postService.upload(post6);


        //when
        List<Post> posts = postService.showMemberPosts(1L);

        //then
        assertThat(posts).contains(uploaded1, uploaded2, uploaded3);
        assertThat(posts).doesNotContain(uploaded4, uploaded5, uploaded6);
    }

    @Test
    void updatePost() {
        //given
        Post post = new Post("title", "content", 1L);
        Post uploaded = postService.upload(post);

        PostUpdateDto updateParam = new PostUpdateDto("new_title", "new_content");

        //when
        Post updated = postService.updatePost(uploaded.getId(), updateParam);

        //then
        uploaded.setTitle(updateParam.getTitle());
        uploaded.setContent(updated.getContent());
        uploaded.setModifiedDate(updated.getModifiedDate());

        assertThat(updated).isEqualTo(uploaded);
    }
}