package hong.postService.service.memberService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordUpdateRequest {

    private String currentPassword;
    private String newPassword;
}
