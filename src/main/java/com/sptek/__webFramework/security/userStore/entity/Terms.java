package com.sptek.__webFramework.security.userStore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

//todo: Entity는 setter를 막는것을 지향하는데 그러면 매번 DTO->Entity 변환을 Mapper를 사용하지 못하고 Builder로 해야하는데 이게 맞을까?
/**
 * 사용자가 동의해야 하는 약관 이름을 저장하는 JPA entity.
 *
 * <p>User와는 다대다 역방향 관계로 연결되어 가입/수정 시 선택된 약관을 표현한다.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ToString(exclude = "users") // 서로 참조로 인한 toStrig에서의 SOF 방지
@Table(name = "TERMS")
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String termsName;

    @ManyToMany(mappedBy = "terms")
    private List<User> users;  // User 엔티티와의 다대다 관계

}
