package hong.postService.service.postService.v2;

import hong.postService.TestSecurityConfig;
import hong.postService.domain.Post;
import hong.postService.domain.UserRole;
import hong.postService.exception.file.InvalidFileFieldException;
import hong.postService.exception.member.MemberNotFoundException;
import hong.postService.exception.post.InvalidPostFieldException;
import hong.postService.exception.post.PostNotFoundException;
import hong.postService.repository.postRepository.v2.PostRepository;
import hong.postService.repository.postRepository.v2.SearchCond;
import hong.postService.service.fileService.dto.FileCreateRequest;
import hong.postService.service.memberService.dto.UserCreateRequest;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.service.postService.dto.PostCreateRequest;
import hong.postService.service.postService.dto.PostDetailResponse;
import hong.postService.service.postService.dto.PostSummaryResponse;
import hong.postService.service.postService.dto.PostUpdateRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Import(TestSecurityConfig.class)
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
    void write_회원조회_후_회원이_있고_request가_null이_아니면_정상_수행() {
        //given
        Long memberId1 = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));
        Long memberId2 = memberService.signUp(new UserCreateRequest("user2", "p2", "e2@naver.com", "nickname2", UserRole.USER));

        memberService.findMember(memberId2).remove();

        //when
        Long postId = postService.write(memberId1, new PostCreateRequest("title1", "content1", null));
        flushAndClear();

        //then
        Post findPost = postService.getPost(postId);

        assertThat(findPost.getTitle()).isEqualTo("title1");
        assertThat(findPost.getContent()).isEqualTo("content1");
        assertThat(findPost.getWriter().getId()).isEqualTo(memberId1);

        assertThatThrownBy(() -> postService.write(memberId2, new PostCreateRequest("title2", "content2", null)))
                .isInstanceOf(MemberNotFoundException.class);

        assertThatThrownBy(() -> postService.write(memberId1, new PostCreateRequest(null, null, null)))
                .isInstanceOf(InvalidPostFieldException.class);
    }

    @Test
    void write_파일_첨부_s3Key_중복_x() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        ArrayList<FileCreateRequest> files = new ArrayList<>();

        FileCreateRequest request = new FileCreateRequest("example.txt", "post/1/abc123.txt");
        files.add(request);

        FileCreateRequest originalFileNameNull = new FileCreateRequest(null, "post/1/abc123.txt");
        FileCreateRequest s3KeyNull = new FileCreateRequest("example.txt", null);
        FileCreateRequest wrongOriginalFileName = new FileCreateRequest("example", "post/1/abc123.txt");
        FileCreateRequest wrongS3Key = new FileCreateRequest("example.txt", "post/abc123.txt");

        //when
        Long postId = postService.write(memberId, new PostCreateRequest("title1", "content1", files));
        flushAndClear();

        //then
        Post findPost = postService.getPost(postId);
        assertThat(findPost.getFiles().size()).isEqualTo(1);

        files.add(originalFileNameNull);
        assertThatThrownBy(() -> postService.write(memberId, new PostCreateRequest("title1", "content1", files)))
                .isInstanceOf(InvalidFileFieldException.class);
        files.remove(originalFileNameNull);

        files.add(s3KeyNull);
        assertThatThrownBy(() -> postService.write(memberId, new PostCreateRequest("title1", "content1", files)))
                .isInstanceOf(InvalidFileFieldException.class);
        files.remove(s3KeyNull);

        files.add(wrongOriginalFileName);
        assertThatThrownBy(() -> postService.write(memberId, new PostCreateRequest("title1", "content1", files)))
                .isInstanceOf(InvalidFileFieldException.class);
        files.remove(wrongOriginalFileName);

        files.add(wrongS3Key);
        assertThatThrownBy(() -> postService.write(memberId, new PostCreateRequest("title1", "content1", files)))
                .isInstanceOf(InvalidFileFieldException.class);
        files.remove(wrongS3Key);
    }

    @Test
    void write_s3Key_중복() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        ArrayList<FileCreateRequest> fileCreateRequests = new ArrayList<>();

        fileCreateRequests.add(new FileCreateRequest("example1.txt", "post/1/example.txt"));
        fileCreateRequests.add(new FileCreateRequest("example1.txt", "post/1/example.txt"));

        //when & then
        assertThatThrownBy(() -> postService.write(memberId, new PostCreateRequest("title", "content", fileCreateRequests)))
                .isInstanceOf(InvalidFileFieldException.class);
    }
    @Test
    void getPost_post가_존재하면_정상_반환() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        ArrayList<FileCreateRequest> files = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            files.add(new FileCreateRequest("example" + i + ".txt", "post/" + i + "/file-" + "-" + UUID.randomUUID() + ".txt"));
        }

        Long postId1 = postService.write(memberId, new PostCreateRequest("title1", "content1", files));
        Long postId2 = postService.write(memberId, new PostCreateRequest("title2", "content2", null));

        postService.delete(postId2);
        flushAndClear();

        //when
        Post post = postService.getPost(postId1);

        //then
        assertThat(post.getTitle()).isEqualTo("title1");
        assertThat(post.getContent()).isEqualTo("content1");
        assertThat(post.getFiles().size()).isEqualTo(5);

        assertThatThrownBy(() -> postService.getPost(postId2))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void getPostDetailResponse_post가_존재하면_정상_반환() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        ArrayList<FileCreateRequest> files = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            files.add(new FileCreateRequest("example" + i + ".txt", "post/" + i + "/file-" + "-" + UUID.randomUUID() + ".txt"));
        }

        Long postId1 = postService.write(memberId, new PostCreateRequest("title1", "content1", files));
        Long postId2 = postService.write(memberId, new PostCreateRequest("title2", "content2", null));

        postService.delete(postId2);

        //when
        PostDetailResponse post = postService.getPostDetailResponse(postId1);

        //then
        assertThat(post.getTitle()).isEqualTo("title1");
        assertThat(post.getContent()).isEqualTo("content1");
        assertThat(post.getFiles().get(0).getOriginalFileName()).isEqualTo("example1.txt");

        assertThatThrownBy(() -> postService.getPostDetailResponse(postId2))
                .isInstanceOf(PostNotFoundException.class);
    }


    @Test
    void getPosts_삭제된_글_제외하고_모두_반환() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        for (int i = 1; i <= 50; i++) {
            Long postId;
            if (i % 2 != 0) {
                List<FileCreateRequest> files = new ArrayList<>();
                for (int j = 1; j <= 5; j++) {
                    files.add(new FileCreateRequest(
                            "example" + j + ".txt",
                            "post/" + i + "/file-" + j + "-" + UUID.randomUUID() + ".txt"
                    ));
                }

                postId = postService.write(memberId, new PostCreateRequest("title" + i, "content" + i, files));
            } else postId = postService.write(memberId, new PostCreateRequest("title" + i, "content" + i, null));

            if (i == 50) postService.delete(postId);
        }

        flushAndClear();

        PageRequest pageRequest1 = PageRequest.of(0, 25, Sort.by(Sort.Direction.ASC, "createdDate"));
        PageRequest pageRequest2 = PageRequest.of(1, 25, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<PostSummaryResponse> result1 = postService.getPosts(pageRequest1);
        Page<PostSummaryResponse> result2 = postService.getPosts(pageRequest2);

        //then
        assertThat(result1.getSize()).isEqualTo(25);
        assertThat(result2.getSize()).isEqualTo(25);

        assertThat(result1.getTotalPages()).isEqualTo(2);
        assertThat(result1.getTotalElements()).isEqualTo(49);

        assertThat(result1.getContent().get(0).isIncludingFile()).isTrue();
        assertThat(result1.getContent().get(1).isIncludingFile()).isFalse();
    }

    @Test
    void search_작성자_닉네임만() {
        //given
        Long memberId1 = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname1", UserRole.USER));
        Long memberId2 = memberService.signUp(new UserCreateRequest("user2", "p2", "e2@naver.com", "nickname2", UserRole.USER));

        Long postId;
        for (int i = 1; i <= 100; i++) {
            if (i % 2 != 0) postId = postService.write(memberId1, new PostCreateRequest("title" + i, "content" + i, null));
            else postId =  postService.write(memberId2, new PostCreateRequest("title" + i, "content" + i, null));

            if (i == 99) postService.delete(postId);
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
            if (i % 2 != 0) postId =  postService.write(memberId1, new PostCreateRequest("title" + i, "content" + i, null));
            else postId = postService.write(memberId2, new PostCreateRequest("title" + i, "content" + i, null));
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
            if (i % 2 != 0) postService.write(memberId1, new PostCreateRequest("title" + i, "content" + i, null));
            else postService.write(memberId2, new PostCreateRequest("title" + i, "content" + i, null));
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
            if (i % 2 != 0) postId = postService.write(memberId1, new PostCreateRequest("title" + i, "content" + i, null));
            else postId = postService.write(memberId2, new PostCreateRequest("title" + i, "content" + i, null));
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
    void search_File_첨부() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname1", UserRole.USER));

        for (int i = 1; i <= 50; i++) {
            if (i % 2 != 0) {
                List<FileCreateRequest> files = new ArrayList<>();
                for (int j = 1; j <= 5; j++) {
                    files.add(new FileCreateRequest(
                            "example" + j + ".txt",
                            "post/" + i + "/file-" + j + "-" + UUID.randomUUID() + ".txt"
                    ));
                }

                postService.write(memberId, new PostCreateRequest("title" + i, "content" + i, files));
            } else postService.write(memberId, new PostCreateRequest("title" + i, "content" + i, null));
        }

        flushAndClear();

        SearchCond cond = SearchCond.builder().build();
        PageRequest pageRequest1 = PageRequest.of(0, 25, Sort.by(Sort.Direction.ASC, "createdDate"));

        //when
        Page<PostSummaryResponse> posts = postService.search(cond, pageRequest1);

        //then
        assertThat(posts.getContent().get(0).isIncludingFile()).isTrue();
        assertThat(posts.getContent().get(1).isIncludingFile()).isFalse();
    }

    @Test
    void getMemberPosts_회원이_작성한_글_중에서_삭제된_글_제외하고_모두_반환() {
        //given
        Long memberId1 = memberService.signUp(new UserCreateRequest("userA", "pA", "userA@naver.com", "nicknameA", UserRole.USER));
        Long memberId2 = memberService.signUp(new UserCreateRequest("userB", "pB", "userB@naver.com", "nicknameB", UserRole.USER));

        Long postId;
        for (int i = 1; i <= 100; i++) {
            if (i % 2 != 0) {
                List<FileCreateRequest> files = new ArrayList<>();
                for (int j = 1; j <= 5; j++) {
                    files.add(new FileCreateRequest(
                            "example" + j + ".txt",
                            "post/" + i + "/file-" + j + "-" + UUID.randomUUID() + ".txt"
                    ));
                }

                postId = postService.write(memberId1, new PostCreateRequest("title" + i, "content" + i, files));
            } else postId = postService.write(memberId2, new PostCreateRequest("title" + i, "content" + i, null));

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

        assertThat(posts1.getContent().get(0).isIncludingFile()).isTrue();
        assertThat(posts2.getContent().get(0).isIncludingFile()).isFalse();
    }

    @Test
    void update_postId가_있으면_정상_수행() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId1 = postService.write(memberId, new PostCreateRequest("title1", "content1", null));

        Long postId2 = postService.write(memberId, new PostCreateRequest("title2", "content2", null));
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

    @Test
    void deletePost_postId가_존재하면_정상_삭제() {
        //given
        Long memberId = memberService.signUp(new UserCreateRequest("user", "p", "e@naver.com", "nickname", UserRole.USER));

        Long postId = postService.write(memberId, new PostCreateRequest("title1", "content1", null));
        Post post = postService.getPost(postId);

        //when
        postService.delete(postId);
        flushAndClear();

        //then
        Page<Post> posts = postRepository.findAllByIsRemovedFalse(PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "createdDate")));
        assertThat(posts).doesNotContain(post);

        assertThatThrownBy(() -> postService.delete(10000L))
                .isInstanceOf(PostNotFoundException.class);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}