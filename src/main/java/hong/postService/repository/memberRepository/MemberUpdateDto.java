package hong.postService.repository.memberRepository;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MemberUpdateDto {

    @NotBlank
    private String name;
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;

    public MemberUpdateDto() {
    }

    public MemberUpdateDto(String name, String loginId) {
        this.name = name;
        this.loginId = loginId;
    }

    public MemberUpdateDto(String password) {
        this.password = password;
    }

    public MemberUpdateDto(String name, String loginId, String password) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
    }
}
