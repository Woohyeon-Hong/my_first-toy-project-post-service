package hong.postService.service.memberService.dto;

import hong.postService.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserCreateRequest {

    private String username;
    private String password;
    private String email;
    private  String nickname;
    private UserRole role;
}
