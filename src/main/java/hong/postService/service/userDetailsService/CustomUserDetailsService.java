package hong.postService.service.userDetailsService;

import hong.postService.domain.Member;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.service.userDetailsService.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username){

        Optional<Member> member = memberRepository.findByUsernameAndIsRemovedFalse(username);

        if (member.isPresent()) {
            return new CustomUserDetails(member.get());
        } else {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다: " + username);
        }
    }
}
