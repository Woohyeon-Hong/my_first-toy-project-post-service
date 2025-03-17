package hong.postService.domain;

import hong.postService.domain.baseEntity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private String password;

    private String email;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();
}
