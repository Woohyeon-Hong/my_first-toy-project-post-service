package hong.postService.web.members.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class UserInfoUpdateResponse {

    private Long id;
    private String username;
    private String email;
    private String nickname;
    private LocalDateTime lastModifiedDate;
}
