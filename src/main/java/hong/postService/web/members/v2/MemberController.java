package hong.postService.web.members.v2;

import hong.postService.domain.Member;
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
import hong.postService.web.members.dto.MemberResponse;
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
            description = "일반 회원 또는 어드민 회원을 생성한다."
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
    public ResponseEntity<MemberResponse> signUp(@Valid @RequestBody UserCreateRequest request) {

        UserRole role = request.getRole();
        Long userId;

        if (role == UserRole.ADMIN) userId = memberService.signUpAdmin(request);
        else userId = memberService.signUp(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(userId)
                .toUri();

        return ResponseEntity.created(location).build();
    }


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
    @GetMapping("/{userId}/posts-simple")
    public ResponseEntity<Page<PostSummaryResponse>> getMemberPostsSimple(
            @PathVariable Long userId,
            Pageable pageable
    ) {
        Page<PostSummaryResponse> posts = postService.getMemberPosts(userId, pageable);
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
    @PatchMapping("/{userId}")
    public ResponseEntity<Void> updateInfo(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody MemberUpdateInfoRequest updateParam
    ) {
        memberService.updateInfo(userId, updateParam);

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
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody PasswordUpdateRequest request
    ) {
        memberService.updatePassword(userId, request);

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
    @DeleteMapping("/{userId}")
    public  ResponseEntity<Void> unregister(@PathVariable("userId") Long userId) {
        memberService.unregister(userId);
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
                     @ApiResponse(responseCode = "400", description = "게시글 필드가 Null",
                             content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                     @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                             content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
             }
    )
    @PostMapping("/{userId}/posts")
    public ResponseEntity<PostDetailResponse> writePost(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody PostCreateRequest request) {
        Long postId = postService.write(userId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{postId}")
                .buildAndExpand(postId)
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
