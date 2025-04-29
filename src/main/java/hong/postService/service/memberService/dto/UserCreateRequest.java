package hong.postService.service.memberService.dto;

import hong.postService.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
가import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
public class UserCreateRequest {

    @NotBlank(message = "username은 필수입니다.")
    @Size(min = 3, max = 20, message = "username은 3 ~ 20자여야 합니다.")
    private String username;

    @NotBlank(message = "password는 필수입니다.")
    @Size(min = 6, message = "password 6자 이상이어야 합니다.")
    private String password;

    private String email;

    @NotBlank(message = "nickname은 필수입니다.")
    private  String nickname;

    private UserRole role;
}
