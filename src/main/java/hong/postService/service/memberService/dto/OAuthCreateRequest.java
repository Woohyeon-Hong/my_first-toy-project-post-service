package hong.postService.service.memberService.dto;

import hong.postService.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthCreateRequest {


    @NotBlank
    private String username;

    private String email;
}
