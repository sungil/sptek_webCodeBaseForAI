package com._sptek._webFrameworkExample.unit.authentication.userStore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

//todo: Entity는 setter를 막는것을 지향하는데 그러면 매번 DTO->Entity 변환을 Mapper를 사용하지 못하고 Builder로 해야하는데 이게 맞을까?
/**
 * Spring Security role 이름과 연결 authority 목록을 저장하는 JPA entity.
 *
 * <p>User와는 다대다 역방향 관계이고, Authority와는 role_authority 조인 테이블로 연결된다.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ToString(exclude = "users") // 서로 참조로 인한 toStrig에서의 SOF 방지
@Table(name = "ROLE")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ROLE_AUTHORITY_MAP",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private List<Authority> authorities;

}
