package hong.postService.web.members.v2;

import hong.postService.domain.UserRole;
import hong.postService.exception.ErrorResponse;
import hong.postService.service.memberService.dto.PasswordUpdateRequest;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.service.memberService.dto.MemberUpdateInfoRequest;
import hong.postService.service.memberService.dto.UserCreateRequest;
import hong.postService.service.postService.dto.PostCreateRequest;
import hong.postService.service.postService.dto.PostDetailResponse;
import hong.postService.service.postService.dto.PostSummaryResponse;
import hong.postService.service.postService.v2.PostService;
import hong.postService.service.userDetailsService.dto.CustomUserDetails;
import hong.postService.web.posts.v2.PostController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "User", description = "회원 관련 API")
@RestController
@RequestMapping("/v2/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PostService postService;


    /**
     *UserCreateRequest
     *       Service에서 사용하는 DTO를 그대로 사용
     *      UserCreateRequest와 Member의 업데이트 주기가 거의 같을 것이기 때문
     */

    @Operation(summary = "회원 가입",
            description = "일반 회원을 생성한다."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "회원 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "입력값이 유효하지 않음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "중복된 필드 존재 (username/email 등)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<Void> signUp(@Valid @RequestBody UserCreateRequest request) {

        request.setRole(UserRole.USER);

        Long userId = memberService.signUp(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(userId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "어드민 회원 가입",
            description = "어드민 회원을 생성한다."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "회원 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "입력값이 유효하지 않음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "중복된 필드 존재 (username/email 등)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/admin")
    public ResponseEntity<Void> signUpAdmin(@Valid @RequestBody UserCreateRequest request) {

        request.setRole(UserRole.ADMIN);

        Long userId = memberService.signUpAdmin(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(userId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    /**
     * SecurityContext에 담겨있는 UserDetails를 사용하여 user 식별 및 인증
     */

    @Operation(summary = "회원 게시글 목록 조회 (실용적 버젼)",
            description = "회원이 작성한 전체 게시글 목록을 조회한다. 삭제된 게시글은 제외된다."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "회원 게시글 목록 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 회원 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/me/posts-simple")
    public ResponseEntity<Page<PostSummaryResponse>> getMyPostsSimple(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {

        Page<PostSummaryResponse> posts = postService.getMemberPosts(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(posts);
    }


    @Operation(
            summary = "회원 게시글 목록 조회 (RESTful 버젼)",
            description = "회원이 작성한 전체 게시글 목록을 HATEOAS 형식으로 반환한다. 삭제된 게시글은 제외된다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 회원 ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/me/posts-standard")
    public ResponseEntity<PagedModel<EntityModel<PostSummaryResponse>>> getMyPostsStandard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable,
            PagedResourcesAssembler<PostSummaryResponse> assembler
    ) {

        Page<PostSummaryResponse> memberPosts = postService.getMemberPosts(userDetails.getUserId(), pageable);

        PagedModel<EntityModel<PostSummaryResponse>> pagedModel = assembler.toModel(
                memberPosts,
                post -> EntityModel.of(post,
                        linkTo(methodOn(PostController.class).getPost(post.getId())).withSelfRel()
                )
        );

        return ResponseEntity.ok(pagedModel);
    }


    /**
     *MemberUpdateInfoRequest
     *      Service에서 반환하는 DTO를 그대로 사용
     *      MemberUpdateInfoRequest와 Member의 업데이트 주기가 거의 같을 것이기 때문.
     */
    @Operation(
            summary = "회원 정보 업데이트",
            description = "회원의 username, email, nickname 정보를 업데이트 한다."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "회원 정보 업데이트 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 회원 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "회원의 필드가 Null",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "회원의 필드가 중복됨",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/me")
    public ResponseEntity<Void> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberUpdateInfoRequest updateParam
    ) {

        memberService.updateInfoOfNotOAuthMember(userDetails.getUserId(), updateParam);

        return ResponseEntity.noContent().build();
    }



    /**
     *PasswordUpdateRequest
     *      Service에서 사용하는 DTO를 그대로 사용
     *      MemberUpdateInfoRequest와 Member의 업데이트 주기가 거의 같을 것이기 때문.
     *      currentPassword와 newPassword를 모두 받아 비밀번호 확인 및 검증을 모두 처리.
     */
    @Operation(
            summary = "회원 비밀번호 업데이트",
            description = "회원의 비밀번호를 업데이트한다."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "회원 비밀번호 업데이트 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 회원 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "회원의 필드가 Null",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "현재 비밀번호가 일치하지 않음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "회원의 필드가 중복됨",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/me/password")
    public ResponseEntity<Void> updateMyPassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordUpdateRequest request
    ) {

        memberService.updatePasswordOfNotOAuthMember(userDetails.getUserId(), request);

        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "회원 탈퇴",
            description = "회원을 탈퇴한다."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 회원 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/me")
    public ResponseEntity<Void> unregister(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        memberService.unregister(userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }

    /**
     *PostCreateRequest
     *      Service에서 사용하는 DTO를 그대로 사용
     *
     */
    @Operation(
            summary = "회원 게시글 작성",
            description = "회원이 게시글을 작성한다."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "게시긒 작성 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않거나 이미 삭제된 회원 ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 게시글 작성 요청",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/me/posts")
    public ResponseEntity<PostDetailResponse> writePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostCreateRequest request
    ) {

        Long postId = postService.write(userDetails.getUserId(), request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/v2/posts/{postId}")
                .buildAndExpand(postId)
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
