package hong.postService.web.members.v1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberUpdateInfo {

    @NotBlank
    private String name;
    @NotBlank
    private String loginId;
}
