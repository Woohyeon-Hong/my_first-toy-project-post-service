package hong.postService.service.postService.v2;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import hong.postService.domain.File;
import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.exception.file.InvalidFileFieldException;
import hong.postService.exception.member.MemberNotFoundException;
import hong.postService.exception.post.InvalidPostFieldException;
import hong.postService.exception.post.PostNotFoundException;
import hong.postService.repository.postRepository.v2.PostRepository;
import hong.postService.repository.postRepository.v2.SearchCond;
import hong.postService.service.fileService.dto.FileCreateRequest;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.service.postService.dto.PostCreateRequest;
import hong.postService.service.postService.dto.PostDetailResponse;
import hong.postService.service.postService.dto.PostSummaryResponse;
import hong.postService.service.postService.dto.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PostService는 게시글에 대한 비즈니스 로직을 담당하는 서비스 계층입니다.
 *
 * 주요 기능:
 *      게시글 작성
 *      게시글 조회
 *      게시글 상세 조회
 *      전체 게시글 목록 조회 (Paging)
 *      게시글 검색 (writer, title)
 *      회원이 작성한 전체 게시글 목록 조회 (Paging)
 *      게시글 수정 (title, content)
 *      게시글 삭제 (soft delete)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final MemberService memberService;
    private final PostRepository postRepository;

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 게시글을 새로 작성합니다.
     *
     * @param memberId 게시글을 작성할 회원 ID
     * @param request title, content, List<FileCreateRequest를 포함한 생성 DTO
     * @return 작성된 게시글의 ID
     *
     * @throws MemberNotFoundException 존재하지 않거나 이미 삭제된 회원인 경우
     * @throws InvalidPostFieldException null 값이 경우
     * @throws InvalidFileFieldException File 생성 요청이 잘못된 경우
     */
    @Transactional
    public Long write(Long memberId, PostCreateRequest request) {

        Member member = memberService.findMember(memberId);

        if (request.getTitle() == null) throw new InvalidPostFieldException("write: title == null");
        if (request.getContent() == null) throw new InvalidPostFieldException("write: content == null");

        Post post = member.writeNewPost(request.getTitle(), request.getContent());

        if (request.getFiles() != null) {
            List<FileCreateRequest> files = request.getFiles();

            for (FileCreateRequest file : files) {
                if (file.getOriginalFileName() == null || file.getS3Key() == null) {
                    throw new InvalidFileFieldException("write: file 정보가 누락됨");
                }
                post.addNewFile(file.getOriginalFileName(), file.getS3Key());
            }
        }
        return postRepository.save(post).getId();
    }

    /**
     * 게시글을 조회한다.
     *
     * @param postId 조회할 게시글 ID
     * @return 조회한 게시글 엔티티
     *
     * @throws PostNotFoundException 존재하지 않거나 이미 삭제된 게시글의 경우
     */
    public Post getPost(Long postId) {
        return postRepository.findByIdAndIsRemovedFalse(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    /**
     * 게시글을 상세 조회합니다.
     *
     * @param postId 조회할 게시글의 ID
     * @return 조회된 게시글의 상세 확인 DTO
     */
    public PostDetailResponse getPostDetailResponse(Long postId) {
        return PostDetailResponse.from(getPost(postId));
    }


    /**
     *전체 게시글 목록 페이지 단위로 조회합니다.
     *
     * @param pageable 페이징 및 정렬 정보를 담은 객체
     * @return 삭제되지 않은 게시글들의 페이징 결과 (요약 응답 DTO로 매핑됨)
     */
    public Page<PostSummaryResponse> getPosts(Pageable pageable) {
        return postRepository.findAllByIsRemovedFalse(pageable)
                .map(PostSummaryResponse::from);
    }

    /**
     * 게시글을 검색 조건에 따라 조회합니다.
     *
     * @param cond 검색 조건을 담은 객체 (title, writer)
     * @param pageable 페이징 및 정렬 정보를 담은 객체
     * @return 조건에 부합하는 게시글들의 페이징 결과 (요약 응답 DTO로 매핑됨)
     */
    public Page<PostSummaryResponse> search(SearchCond cond, Pageable pageable) {
        return postRepository.searchPosts(cond, pageable)
                .map(PostSummaryResponse::from);
    }

    /**
     * 회원이 작성한 게시글들을 조회합니다.
     *
     * @param memberId 작성한 게시글들을 조회할 회원 ID
     * @param pageable 페이징 및 정렬 정보를 담은 객체
     * @return 회원이 작성한 게시글들의 페이징 결과 (요약 응답 DTO로 매핑됨)
     *
     * @throws MemberNotFoundException 존재하지 않거나 이미 삭제된 회원인 경우
     */
    public Page<PostSummaryResponse> getMemberPosts(Long memberId, Pageable pageable) {
        Member member = memberService.findMember(memberId);

        return postRepository.findAllByWriterAndIsRemovedFalse(member, pageable)
                .map(PostSummaryResponse::from);
    }

    /**
     * 게시글을 수정합니다.
     *
     * @param postId 수정할 게시글 ID
     * @param updateParam title, content를 포함한 수정 DTO
     *
     *@throws PostNotFoundException 존재하지 않거나 이미 삭제된 게시글의 경우
     * @throws InvalidPostFieldException null 값이 경우
     */
    @Transactional
    public void update(Long postId, PostUpdateRequest updateParam) {
        Post post = getPost(postId);

        String title = updateParam.getTitle();
        String content = updateParam.getContent();

        if (title != null) post.updateTitle(title);
        if (content != null) post.updateContent(content);
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param postId 삭제할 게시글의 ID
     *
     * @throws PostNotFoundException 존재하지 않거나 이미 삭제된 게시글의 경우
     */
    @Transactional
    public void delete(Long postId) {

        Post post = getPost(postId);

        post.remove();
    }



}
