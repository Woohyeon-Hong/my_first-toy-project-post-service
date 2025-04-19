package hong.postService.service.userDetailsService.dto;

import hong.postService.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    /*
        - userid가 담겨 있어야, 회원정보를 수정해도 로그인 지속 가능
        - 만약 이 필드가 없다면, username으로 회원을 조회해야하는데, username을 수정하면 user 식별 불가능
     */
    private final Long userId;
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

    public Long getUserId() {return userId;}

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
