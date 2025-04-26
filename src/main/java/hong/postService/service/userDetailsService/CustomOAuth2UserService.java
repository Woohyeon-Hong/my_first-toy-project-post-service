package hong.postService.service.userDetailsService;

import hong.postService.domain.Member;
import hong.postService.domain.UserRole;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.service.memberService.dto.MemberUpdateInfoRequest;
import hong.postService.service.memberService.dto.OAuthCreateRequest;
import hong.postService.service.memberService.v2.MemberService;
import hong.postService.service.userDetailsService.dto.CustomOAuth2User;
import hong.postService.service.userDetailsService.dto.GoogleResponse;
import hong.postService.service.userDetailsService.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = switch (registrationId) {
            case "google" -> new GoogleResponse(oAuth2User.getAttributes());
            default -> throw new OAuth2AuthenticationException("지원하지 않는 OAuth Provider입니다: " + registrationId);
        };

        //우리 서비스에서 회원을 구분할 username
        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        String email = oAuth2Response.getEmail();

        String name = oAuth2Response.getName();

        Optional<Member> findMember = memberRepository.findByUsernameAndIsRemovedFalse(username);

        if (findMember.isEmpty()) {
            Long userId = memberService.signUpWithOAuth(new OAuthCreateRequest(username, email));

            return new CustomOAuth2User(userId, username, name, UserRole.USER);
        } else {
            Member user = findMember.get();

            return new CustomOAuth2User(user.getId(), username, name, UserRole.USER);
        }

    }
}
