package hong.postService.web.members.dto;

import hong.postService.domain.Member;
import hong.postService.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String username;
    private String email;
    private String nickname;

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(),
                member.getUsername(),
                member.getEmail(),
                member.getNickname()
        );
    }
}
