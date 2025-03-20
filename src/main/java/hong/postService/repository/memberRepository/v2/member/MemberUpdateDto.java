package hong.postService.repository.memberRepository.v2.member;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class MemberUpdateDto {

    private String username;
    private String password;
    private String email;
    private String nickname;

}
