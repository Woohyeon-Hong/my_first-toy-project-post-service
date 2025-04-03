package hong.postService.web.members.v2;

import hong.postService.domain.Member;
import hong.postService.domain.UserRole;
import hong.postService.service.memberService.dto.PasswordUpdateRequest;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.service.memberService.dto.MemberUpdateInfoRequest;
import hong.postService.service.memberService.dto.UserCreateRequest;
import hong.postService.service.postService.dto.PostSummaryResponse;
import hong.postService.service.postService.v2.PostService;
import hong.postService.web.members.dto.MemberResponse;
import hong.postService.web.posts.v2.PostController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/v2/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PostService postService;


    /**
     *Request: UserCreateRequest
     *                  Service에서 반환하는 DTO를 그대로 사용
     *                  UserCreateRequest와 Member의 업데이트 주기가 거의 같을 것이기 때문
     * Response: ResponseEntity
     *                  생성된 엔티티의 URI만 ResponseEntity에 추가하여 201 created 반환
     */
    @PostMapping
    public ResponseEntity<MemberResponse> signUp(@Valid @RequestBody UserCreateRequest request) {

        UserRole role = request.getRole();
        Long id;

        if (role == UserRole.USER) id = memberService.signUp(request);
        else id = memberService.signUpAdmin(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{userId}/posts-simple")
    public ResponseEntity<Page<PostSummaryResponse>> getMemberPostsSimple(
            @PathVariable Long userId,
            Pageable pageable
    ) {
        Page<PostSummaryResponse> posts = postService.getMemberPosts(userId, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{userId}/posts-standard")
    public ResponseEntity<PagedModel<EntityModel<PostSummaryResponse>>> getMemberPostsStandard(
            @PathVariable("userId") Long userId,
            Pageable pageable,
            PagedResourcesAssembler<PostSummaryResponse> assembler
    ) {
        Page<PostSummaryResponse> memberPosts = postService.getMemberPosts(userId, pageable);

        PagedModel<EntityModel<PostSummaryResponse>> pagedModel = assembler.toModel(
                memberPosts,
                post -> EntityModel.of(post,
                        linkTo(methodOn(PostController.class).getPost(post.getId())).withSelfRel()
                )
        );

        return ResponseEntity.ok(pagedModel);
    }

    /**
     *Request: MemberUpdateInfoRequest
     *                  Service에서 반환하는 DTO를 그대로 사용
     *                  MemberUpdateInfoRequest와 Member의 업데이트 주기가 거의 같을 것이기 때문.
     *Response: ResponseEntity
     *                  204 No Content 반환
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<MemberResponse> updateInfo(
            @PathVariable("userId") Long userId,
            MemberUpdateInfoRequest updateParam) {
        memberService.updateInfo(userId, updateParam);

        return ResponseEntity.noContent().build();
    }

    /**
     *Request: PasswordUpdateRequest
     *                  Service에서 반환하는 DTO를 그대로 사용
     *                  MemberUpdateInfoRequest와 Member의 업데이트 주기가 거의 같을 것이기 때문.
     *                  currentPassword와 newPassword를 모두 받아 비밀번호 확인 및 검증을 모두 처리.
     *Response: ResponseEntity
     *                  204 No Content 반환
     */
    @PatchMapping("/{userId}/password")
    public ResponseEntity<MemberResponse> updatePaassword(
            @PathVariable("userId") Long userId,
            PasswordUpdateRequest request
    ) {
        memberService.updatePassword(userId, request);

        return ResponseEntity.noContent().build();
    }

    /**
     *Response: ResponseEntity
     *                  204 No Content 반환
     */
    @DeleteMapping("/{userId}")
    public  ResponseEntity<MemberResponse> unregister(@PathVariable("userId") Long userId) {
        memberService.unregister(userId);
        return ResponseEntity.noContent().build();
    }

}
