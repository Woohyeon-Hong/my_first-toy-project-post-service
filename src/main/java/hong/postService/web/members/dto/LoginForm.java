package hong.postService.web.members.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {

    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;
}
