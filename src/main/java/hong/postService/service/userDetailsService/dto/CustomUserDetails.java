package hong.postService.service.userDetailsService.dto;

import hong.postService.domain.Member;
import hong.postService.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final UserRole role;


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

    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
