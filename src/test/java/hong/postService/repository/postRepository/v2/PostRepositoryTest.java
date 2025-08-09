package hong.postService.repository.postRepository.v2;

import hong.postService.TestSecurityConfig;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.service.postService.dto.PostSummaryResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestSecurityConfig.class)
@Transactional
class PostRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager em;

    @Test
    void findByIdAndIsRemovedFalse_Optional로_감싸서_반환() {
        //given
        Member m = Member.createNewMember("user", "pw", "e@e.com", "nick");
        memberRepository.save(m);

        Post p1 = m.writeNewPost("title1", "content1");
        Post p2 = m.writeNewPost("title2", "content2");

        postRepository.save(p1);
        postRepository.save(p2);

        p2.remove();

        flushAndClear();

        //when
        Optional<Post> result1 = postRepository.findByIdAndIsRemovedFalse(p1.getId());
        Optional<Post> result2 = postRepository.findByIdAndIsRemovedFalse(p2.getId());

        //then
        assertThat(result1).isPresent();
        assertThat(result2).isEmpty();
    }

    /**
     * getSize():	        요청한 페이지의 page size(요청 크기)	PageRequest.of(0, 5) → getSize()는 항상 5
     * getContent().size():	현재 페이지에 실제로 반환된 요소 개수	예: 마지막 페이지라면 4일 수도 있음
     */
    @Test
    void findAllByWriterAndIsRemovedFalse_정상_반환() {
        //given
        Member m1 = memberRepository.save(Member.createNewMember("userA", "pw", "a@a.com", "nickA"));
        Member m2 = memberRepository.save(Member.createNewMember("userB", "pw", "b@b.com", "nickB"));

        for (int i = 1; i <= 100; i++) {
            Post post = (i % 2 != 0)
                    ? m1.writeNewPost("title" + i, "content" + i)
                    : m2.writeNewPost("title" + i, "content" + i);
            postRepository.save(post);

            if (i == 99) post.remove();
        }

        flushAndClear();

        PageRequest pageable1 = PageRequest.of(0, 5, Sort.by("createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by("createdDate"));

        // when
        Page<Post> result1 = postRepository.findAllByWriterAndIsRemovedFalse(m1, pageable1);
        Page<Post> result2 = postRepository.findAllByWriterAndIsRemovedFalse(m1, pageable2);
        Page<Post> result3 = postRepository.findAllByWriterAndIsRemovedFalse(m2, pageable1);
        Page<Post> result4 = postRepository.findAllByWriterAndIsRemovedFalse(m2, pageable2);

        // then
        assertThat(result1.getSize()).isEqualTo(5);

        assertThat(result1.getTotalPages()).isEqualTo(10);
        assertThat(result3.getTotalPages()).isEqualTo(10);

        assertThat(result1.getTotalElements()).isEqualTo(49);
        assertThat(result3.getTotalElements()).isEqualTo(50);

        assertThat(result1.getContent().size()).isEqualTo(5);
        assertThat(result2.getContent().size()).isEqualTo(5);
        assertThat(result3.getContent().size()).isEqualTo(5);
        assertThat(result4.getContent().size()).isEqualTo(5);
    }

    @Test
    void findAllByWriterAndIsRemovedFalse_빈_배열_반환() {
        //given
        Member m1 = memberRepository.save(Member.createNewMember("userA", "pw", "a@a.com", "nickA"));

        Post post1 = m1.writeNewPost("title1", "content1");

        postRepository.save(post1);

        post1.remove();

        flushAndClear();

        PageRequest pageable = PageRequest.of(0, 5, Sort.by("createdDate"));

        // when
        Page<Post> result = postRepository.findAllByWriterAndIsRemovedFalse(m1, pageable);

        // then
        assertThat(result).isEmpty();

    }

    @Test
    void findAllByIsRemovedFalse_정상_반환() {
        //given
        Member member = memberRepository.save(Member.createNewMember("user", "pw", "e@e.com", "nick"));

        for (int i = 1; i <= 10; i++) {
            Post post = member.writeNewPost("title" + i, "content" + i);
            postRepository.save(post);
            if (i == 10) post.remove();
        }

        PageRequest pageable1 = PageRequest.of(0, 5, Sort.by("createdDate"));
        PageRequest pageable2 = PageRequest.of(1, 5, Sort.by("createdDate"));

        // when
        Page<Post> result1 = postRepository.findAllByIsRemovedFalse(pageable1);
        Page<Post> result2 = postRepository.findAllByIsRemovedFalse(pageable2);

        // then
        assertThat(result1.getSize()).isEqualTo(5);

        assertThat(result1.getTotalPages()).isEqualTo(2);

        assertThat(result1.getTotalElements()).isEqualTo(9);

        assertThat(result1.getContent().size()).isEqualTo(5);
        assertThat(result2.getContent().size()).isEqualTo(4);
    }

    @Test
    void findAllByIsRemovedFalse_빈_배열_반환() {
        //given
        Member member = memberRepository.save(Member.createNewMember("user", "pw", "e@e.com", "nick"));

        Post post = member.writeNewPost("title", "content");
        postRepository.save(post);

        post.remove();

        flushAndClear();

        PageRequest pageable = PageRequest.of(0, 5, Sort.by("createdDate"));

        // when
        Page<Post> result = postRepository.findAllByIsRemovedFalse(pageable);

        // then
        assertThat(result).isEmpty();
    }


    @Test
    void searchPosts_닉네임만() {
        //given
        Member m1 = memberRepository.save(Member.createNewMember("userA", "pw", "a@a.com", "nickA"));
        Member m2 = memberRepository.save(Member.createNewMember("userB", "pw", "b@b.com", "nickB"));


        for (int i = 1; i <= 50; i++) {
            Post p1 = m1.writeNewPost("title" + i, "content" + i);
            Post p2 = m2.writeNewPost("title" + i, "content" + i);
            if (i == 50) p2.remove();
            postRepository.saveAll(List.of(p1, p2));
        }

        flushAndClear();

        SearchCond cond = SearchCond.builder().writer("nickB").build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate"));

        //when
        Page<PostSummaryResponse> result = postRepository.searchPosts(cond, pageable);

        // then
        assertThat(result.getSize()).isEqualTo(10);

        assertThat(result.getTotalPages()).isEqualTo(5);

        assertThat(result.getTotalElements()).isEqualTo(49);

        assertThat(result.getContent().size()).isEqualTo(10);
    }

    @Test
    void searchPosts_제목만() {
        //given
        Member m1 = memberRepository.save(Member.createNewMember("userA", "pw", "a@a.com", "nickA"));
        Member m2 = memberRepository.save(Member.createNewMember("userB", "pw", "b@b.com", "nickB"));


        for (int i = 1; i <= 50; i++) {
            Post p1 = m1.writeNewPost("titleM1" + i, "content" + i);
            Post p2 = m2.writeNewPost("titleM2" + i, "content" + i);
            if (i == 50) p2.remove();
            postRepository.saveAll(List.of(p1, p2));
        }

        flushAndClear();

        SearchCond cond = SearchCond.builder().title("titleM2").build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate"));

        //when
        Page<PostSummaryResponse> result = postRepository.searchPosts(cond, pageable);

        // then
        assertThat(result.getSize()).isEqualTo(10);

        assertThat(result.getTotalPages()).isEqualTo(5);

        assertThat(result.getTotalElements()).isEqualTo(49);

        assertThat(result.getContent().size()).isEqualTo(10);
    }

    @Test
    void searchPosts_닉네임_제목_둘다() {
        //given
        Member m1 = memberRepository.save(Member.createNewMember("userA", "pw", "a@a.com", "nickA"));
        Member m2 = memberRepository.save(Member.createNewMember("userB", "pw", "b@b.com", "nickB"));


        for (int i = 1; i <= 50; i++) {
            Post p1 = m1.writeNewPost("title" + i, "content" + i);
            Post p2 = m2.writeNewPost("title" + i, "content" + i);
            if (i == 50) p2.remove();
            postRepository.saveAll(List.of(p1, p2));
        }

        flushAndClear();

        SearchCond cond = SearchCond.builder()
                .title("title25")
                .writer("nickB")
                .build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate"));

        //when
        Page<PostSummaryResponse> result = postRepository.searchPosts(cond, pageable);

        // then
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("title25");
        assertThat(result.getContent().get(0).getWriterNickname()).isEqualTo("nickB");
    }

    @Test
    void searchPosts_결과x() {
        //given
        Member m1 = memberRepository.save(Member.createNewMember("userA", "pw", "a@a.com", "nickA"));
        Member m2 = memberRepository.save(Member.createNewMember("userB", "pw", "b@b.com", "nickB"));


        for (int i = 1; i <= 50; i++) {
            Post p1 = m1.writeNewPost("titleM1" + i, "content" + i);
            Post p2 = m2.writeNewPost("titleM2" + i, "content" + i);
            postRepository.saveAll(List.of(p1, p2));
        }

        flushAndClear();

        SearchCond cond = SearchCond.builder()
                .title("존재하지않음")
                .writer("없는닉네임")
                .build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate"));

        //when
        Page<PostSummaryResponse> result = postRepository.searchPosts(cond, pageable);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void searchPosts_삭제된_게시물_반환_제외() {
        //given
        Member m1 = memberRepository.save(Member.createNewMember("userA", "pw", "a@a.com", "nickA"));
        Member m2 = memberRepository.save(Member.createNewMember("userB", "pw", "b@b.com", "nickB"));


        for (int i = 1; i <= 50; i++) {
            Post p1 = m1.writeNewPost("titleM1" + i, "content" + i);
            Post p2 = m2.writeNewPost("titleM2" + i, "content" + i);
            postRepository.saveAll(List.of(p1, p2));
            if (i == 50) p2.remove();
        }

        flushAndClear();

        SearchCond cond = SearchCond.builder()
                .writer("nickB")
                .title("title50")
                .build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate"));

        //when
        Page<PostSummaryResponse> result = postRepository.searchPosts(cond, pageable);

        // then
        assertThat(result).isEmpty();
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}