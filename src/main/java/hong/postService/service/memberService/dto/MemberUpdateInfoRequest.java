package hong.postService.service.memberService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class MemberUpdateInfoRequest {

    @NotBlank(message = "username은 필수입니다.")
    @Size(min = 3, max = 20, message = "username은 3 ~ 20자여야 합니다.")
    private String username;

    @Email(message = "email 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "nickname은 필수입니다.")
    private String nickname;
}
