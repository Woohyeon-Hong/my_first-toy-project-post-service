package hong.postService.web.members.dto;

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

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Email
    private String email;
    @NotBlank
    private  String nickname;
    @NotNull
    private UserRole role;
}
