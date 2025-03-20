package hong.postService.web.members.v1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberUpdatePassword {

    @NotBlank
    private String password;
}
