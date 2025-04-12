package hong.postService.service.postService.v2;

import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.domain.UserRole;
import hong.postService.exception.post.InvalidPostFieldException;
import hong.postService.exception.post.PostNotFoundException;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
import hong.postService.repository.postRepository.v2.SearchCond;
import hong.postService.service.memberService.dto.UserCreateRequest;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.service.postService.dto.PostCreateRequest;
import hong.postService.service.postService.dto.PostSummaryResponse;
import hong.postService.service.postService.dto.PostUpdateRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostService postService;
    @Autowired
    EntityManager em;

    @Test
    void write_회원조회_후_회원이_있으면_정상_수행() {
        //given
        Long memberId1 = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));
        Long memberId2 = memberService.signUp(new UserCreateRequest("user2", "p2", "e2@naver.com", "nickname2", UserRole.USER));

        memberService.findMember(memberId2).remove();

        //when
        Long postId = postService.write(memberId1, new PostCreateRequest("title1", "content1"));

        //then
        Post findPost = postService.getPost(postId);

        assertThat(findPost.getTitle()).isEqualTo("title1");
        assertThat(findPost.getContent()).isEqualTo("content1");
        assertThat(findPost.getWriter().getId()).isEqualTo(memberId1);

        assertThatThrownBy(() -> postService.write(memberId2, new PostCreateRequest("title2", "content2")));
    }

    @Test
    void deletePost_postId가_존재하면_정상_삭제() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId = postService.write(memberId, new PostCreateRequest("title1", "content1"));
        Post post = postService.getPost(postId);

        //when
        postService.delete(postId);
        flushAndClear();

        //then
        Page<Post> posts = postRepository.findAllByIsRemovedFalse(PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate")));

        assertThat(posts).doesNotContain(post);
        assertThatThrownBy(() -> postService.delete(10000L)).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void getPosts_삭제된_글_제외하고_모두_반환() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId;
        for (int i = 1; i <= 50; i++) {
            postId = postService.write(memberId, new PostCreateRequest("title" + i, "content" + i));
            if (i == 50) postService.delete(postId);
        }

        flushAndClear();

        PageRequest pageRequest1 = PageRequest.of(0, 25, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageRequest2 = PageRequest.of(1, 25, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<PostSummaryResponse> posts1 = postService.getPosts(pageRequest1);
        Page<PostSummaryResponse> posts2 = postService.getPosts(pageRequest2);

        //then
        assertThat(posts1.getSize()).isEqualTo(25);
        assertThat(posts2.getSize()).isEqualTo(25);

        assertThat(posts1.getTotalPages()).isEqualTo(2);

        assertThat(posts1.getTotalElements()).isEqualTo(49);
    }

    @Test
    void getMemberPosts_회원이_작성한_글_중에서_삭제된_글_제외하고_모두_반환() {
        //given
        Long memberId1 = memberService.signUp(new UserCreateRequest("userA", "pA", "userA@naver.com", "nicknameA", UserRole.USER));
        Long memberId2 = memberService.signUp(new UserCreateRequest("userB", "pB", "userB@naver.com", "nicknameB", UserRole.USER));

        Long postId;
        for (int i = 1; i <= 100; i++) {
            if (i % 2 != 0) postId = postService.write(memberId1, new PostCreateRequest("title" + i, "content" + i));
            else postId = postService.write(memberId2, new PostCreateRequest("title" + i, "content" + i));

            if (i == 99 || i == 100) postService.delete(postId);
        }

        flushAndClear();

        PageRequest pageRequest = PageRequest.of(0, 25, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<PostSummaryResponse> posts1 = postService.getMemberPosts(memberId1, pageRequest);
        Page<PostSummaryResponse> posts2 = postService.getMemberPosts(memberId2, pageRequest);

        //then
        assertThat(posts1.getSize()).isEqualTo(25);
        assertThat(posts2.getSize()).isEqualTo(25);

        assertThat(posts1.getTotalPages()).isEqualTo(2);
        assertThat(posts2.getTotalPages()).isEqualTo(2);

        assertThat(posts1.getTotalElements()).isEqualTo(49);
        assertThat(posts2.getTotalElements()).isEqualTo(49);
    }

   @Test
   void search_작성자_닉네임만() {
        //given
       Long memberId1 = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname1", UserRole.USER));
       Long memberId2 = memberService.signUp(new UserCreateRequest("user2", "p2", "e2@naver.com", "nickname2", UserRole.USER));

       Long postId;
       for (int i = 1; i <= 100; i++) {
           if (i % 2 != 0) postId = postService.write(memberId1, new PostCreateRequest("title" + i, "content" + i));
           else postId =  postService.write(memberId2, new PostCreateRequest("title" + i, "content" + i));;

           if (postId == 99) postService.delete(postId);
       }

       flushAndClear();

       SearchCond cond = SearchCond.builder()
               .writer("nickname1")
               .build();

       PageRequest pageRequest1 = PageRequest.of(0, 25, Sort.by(Sort.Direction.ASC, "createdDate"));
       PageRequest pageRequest2 = PageRequest.of(1, 25, Sort.by(Sort.Direction.ASC, "createdDate"));

       //when
       Page<PostSummaryResponse> posts1 = postService.search(cond, pageRequest1);
       Page<PostSummaryResponse> posts2 = postService.search(cond, pageRequest2);

       //then
        assertThat(posts1.getSize()).isEqualTo(25);
        assertThat(posts2.getSize()).isEqualTo(25);

        assertThat(posts1.getTotalPages()).isEqualTo(2);
        assertThat(posts2.getTotalPages()).isEqualTo(2);

        assertThat(posts1.getTotalElements()).isEqualTo(49);
   }

    @Test
    void search_제목만() {
        //given
        Long memberId1 = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname1", UserRole.USER));
        Long memberId2 = memberService.signUp(new UserCreateRequest("user2", "p2", "e2@naver.com", "nickname2", UserRole.USER));

        Long postId;
        for (int i = 1; i <= 100; i++) {
            if (i % 2 != 0) postId =  postService.write(memberId1, new PostCreateRequest("title" + i, "content" + i));
            else postId = postService.write(memberId2, new PostCreateRequest("title" + i, "content" + i));;
            if (i == 99) postService.delete(postId);
        }

        flushAndClear();

        SearchCond cond = SearchCond.builder()
                .title("title9")
                .build();

        PageRequest pageRequest1 = PageRequest.of(0, 25, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<PostSummaryResponse> posts = postService.search(cond, pageRequest1);

        //then
        assertThat(posts.getTotalElements()).isEqualTo(10);
    }

    @Test
    void search_작성자_닉네임_제목_둘다() {
        //given
        Long memberId1 = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname1", UserRole.USER));
        Long memberId2 = memberService.signUp(new UserCreateRequest("user2", "p2", "e2@naver.com", "nickname2", UserRole.USER));

        for (int i = 1; i <= 100; i++) {
            if (i % 2 != 0) postService.write(memberId1, new PostCreateRequest("title" + i, "content" + i));
            else postService.write(memberId2, new PostCreateRequest("title" + i, "content" + i));;
        }

        flushAndClear();

        SearchCond cond = SearchCond.builder()
                .writer("nickname1")
                .title("title99")
                .build();

        PageRequest pageRequest1 = PageRequest.of(0, 25, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<PostSummaryResponse> posts = postService.search(cond, pageRequest1);

        //then
        assertThat(posts.getTotalElements()).isEqualTo(1);
    }

    @Test
    void search_결과x() {
        //given
        Long memberId1 = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname1", UserRole.USER));
        Long memberId2 = memberService.signUp(new UserCreateRequest("user2", "p2", "e2@naver.com", "nickname2", UserRole.USER));

        Long postId;
        for (int i = 1; i <= 100; i++) {
            if (i % 2 != 0) postId = postService.write(memberId1, new PostCreateRequest("title" + i, "content" + i));
            else postId = postService.write(memberId2, new PostCreateRequest("title" + i, "content" + i));;
            if (i == 99) postService.delete(postId);
        }

        flushAndClear();

        SearchCond cond = SearchCond.builder()
                .writer("존재하지 않는 닉네임")
                .title("존재한지 않는 제목")
                .build();

        PageRequest pageRequest1 = PageRequest.of(0, 25, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<PostSummaryResponse> posts = postService.search(cond, pageRequest1);

        //then
        assertThat(posts.getTotalElements()).isEqualTo(0);
    }


    @Test
    void update_postId가_있으면_정상_수행() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId1 = postService.write(memberId, new PostCreateRequest("title1", "content1"));

        Long postId2 = postService.write(memberId, new PostCreateRequest("title2", "content2"));
        postService.delete(postId2);

        flushAndClear();

        String newTitle = "newTitle";
        String newContent = "newContent";

        PostUpdateRequest updateParam = PostUpdateRequest.builder()
                .title(newTitle)
                .content(newContent)
                .build();

        //when
        postService.update(postId1, updateParam);

        flushAndClear();

        //then
        Post findPost = postService.getPost(postId1);

        assertThat(findPost.getTitle()).isEqualTo(newTitle);
        assertThat(findPost.getContent()).isEqualTo(newContent);

        assertThat(findPost.getLastModifiedDate()).isAfter(findPost.getCreatedDate());

        assertThatThrownBy(() -> postService.delete(postId2)).isInstanceOf(PostNotFoundException.class);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}