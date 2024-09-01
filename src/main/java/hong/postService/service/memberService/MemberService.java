package hong.postService.service.memberService;

import hong.postService.domain.Member;
import hong.postService.domain.Post;
import hong.postService.repository.memberRepository.MemberUpdateDto;

import java.util.List;

public interface MemberService {

    Member signUp(Member member);

    void unregister(Long id);

    Member logIn(String loginId, String password);

    Member updateInfo(Long id, MemberUpdateDto updateParam);

    Member updatePassword(Long id, String newPassword);
}
