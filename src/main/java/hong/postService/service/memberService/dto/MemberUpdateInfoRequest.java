package hong.postService.service.memberService.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class MemberUpdateInfoRequest {

    private String username;
    private String email;
    private String nickname;
}
