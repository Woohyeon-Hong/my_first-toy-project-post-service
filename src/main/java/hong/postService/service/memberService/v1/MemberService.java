package hong.postService.service.memberService.v1;

import hong.postService.domain.Member;
import hong.postService.service.memberService.v2.MemberUpdateDto;

public interface MemberService {

    Member signUp(Member member);

    void unregister(Long id);

    Member logIn(String loginId, String password);

    Member updateInfo(Long id, MemberUpdateDto updateParam);

    Member updatePassword(Long id, String newPassword);
}
