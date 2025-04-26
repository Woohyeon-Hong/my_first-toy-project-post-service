package hong.postService.service.userDetailsService.dto;

import hong.postService.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final Long userId;
    private final String username;
    private final String name;
    private final UserRole role;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role.getRoleName();
            }
        });

        return collection;
    }

    public Long getUserId() {return userId;}
    @Override
    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}
