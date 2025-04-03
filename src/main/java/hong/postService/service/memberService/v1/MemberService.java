package hong.postService.service.memberService.v1;

import hong.postService.domain.Member;
import hong.postService.service.memberService.dto.MemberUpdateInfoRequest;

public interface MemberService {

    Member signUp(Member member);

    void unregister(Long id);

    Member logIn(String loginId, String password);

    Member updateInfo(Long id, MemberUpdateInfoRequest updateParam);

    Member updatePassword(Long id, String newPassword);
}
