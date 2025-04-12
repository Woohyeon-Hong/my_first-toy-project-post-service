package hong.postService.service.postService.v2;

import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.exception.post.PostNotFoundException;
import hong.postService.repository.postRepository.v2.PostRepository;
import hong.postService.repository.postRepository.v2.SearchCond;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.service.postService.dto.PostCreateRequest;
import hong.postService.service.postService.dto.PostDetailResponse;
import hong.postService.service.postService.dto.PostSummaryResponse;
import hong.postService.service.postService.dto.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final MemberService memberService;
    private final PostRepository postRepository;

    @Transactional
    public Long write(Long memberId, PostCreateRequest request) {

        Member member = memberService.findMember(memberId);

        Post post = member.writeNewPost(request.getTitle(), request.getContent());
        Post saved = postRepository.save(post);

        return saved.getId();
    }

    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findByIdAndIsRemovedFalse(id).orElseThrow(() ->
                new PostNotFoundException(id));

        post.remove();
    }

    public Post getPost(Long postId) {
        return postRepository.findByIdAndIsRemovedFalse(postId).orElseThrow(() -> new PostNotFoundException(postId));
    }

    public PostDetailResponse getPostDetailResponse(Long postId) {
        return PostDetailResponse.from(getPost(postId));
    }


    public Page<PostSummaryResponse> getPosts(Pageable pageable) {
        return postRepository.findAllByIsRemovedFalse(pageable).map(PostSummaryResponse::from);
    }

    public Page<PostSummaryResponse> search(SearchCond cond, Pageable pageable) {
        return postRepository.searchPosts(cond, pageable).map(PostSummaryResponse::from);
    }

    public Page<PostSummaryResponse> getMemberPosts(Long memberId, Pageable pageable) {
        Member member = memberService.findMember(memberId);
        return postRepository.findAllByWriterAndIsRemovedFalse(member, pageable).map(PostSummaryResponse::from);
    }

    @Transactional
    public void update(Long id, PostUpdateRequest updateParam) {
        Post post = postRepository.findByIdAndIsRemovedFalse(id).orElseThrow(()
                -> new PostNotFoundException(id));

        String title = updateParam.getTitle();
        String content = updateParam.getContent();
        if (title != null) post.updateTitle(title);
        if (content != null) post.updateContent(content);
    }
}
