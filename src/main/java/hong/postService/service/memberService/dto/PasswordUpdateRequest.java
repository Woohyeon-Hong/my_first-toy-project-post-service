package hong.postService.service.memberService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordUpdateRequest {

    @NotBlank(message = "기존 password는 필수입니다.")
    private String currentPassword;

    @NotBlank(message = "password는 필수입니다.")
    @Size(min = 6, message = "password 6자 이상이어야 합니다.")
    private String newPassword;
}
