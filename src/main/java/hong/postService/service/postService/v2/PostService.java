package hong.postService.service.postService.v2;

import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.repository.postRepository.v2.PostRepository;
import hong.postService.repository.postRepository.v2.SearchCond;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long write(Long memberId, String title, String content) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("write: 해당 id가 없음."));

        Post post = member.writeNewPost(title, content);
        Post saved = postRepository.save(post);

        return saved.getId();
    }

    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("deletePost: 해당하는 id 없음."));

        postRepository.delete(post);
    }

    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> search(SearchCond cond, Pageable pageable) {
        return postRepository.searchPosts(cond, pageable);
    }

    public Page<Post> getMemberPosts(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("getMemberPosts: 해당 memberId가 없음."));
        return postRepository.findAllByWriter(member, pageable);
    }

    @Transactional
    public void update(Long id, PostUpdateRequest updateParam) {
        Post post = postRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("update: 해당하는 id가 없음."));

        post.updateTitle(updateParam.getTitle());
        post.updateContent(updateParam.getContent());
    }
}
