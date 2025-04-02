package hong.postService.web.members.v2;

import hong.postService.domain.Member;
import hong.postService.domain.UserRole;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.web.members.dto.UserCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v2/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Member> signUp(@Valid @RequestBody UserCreateRequest request) {

        UserRole role = request.getRole();
        Long id;

        if (role == UserRole.USER) {
            id  = memberService.signUp(request.getUsername(),
                    request.getPassword(),
                    request.getEmail(),
                    request.getNickname());
        } else {
            id = memberService.signUpAdmin(request.getUsername(),
                    request.getPassword(),
                    request.getEmail(),
                    request.getNickname());
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
