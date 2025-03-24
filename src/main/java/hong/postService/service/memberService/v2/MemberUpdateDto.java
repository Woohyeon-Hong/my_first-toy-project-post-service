package hong.postService.service.memberService.v2;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class MemberUpdateDto {

    private String username;
    private String email;
    private String nickname;
}
