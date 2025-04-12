package hong.postService.service.memberService.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class MemberUpdateInfoRequest {

    @Size(min = 3, max = 20, message = "username은 3 ~ 20자여야 합니다.")
    private String username;

    private String email;

    private String nickname;
}
