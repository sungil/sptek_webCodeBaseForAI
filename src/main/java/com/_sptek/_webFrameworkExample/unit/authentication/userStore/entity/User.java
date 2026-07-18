package com._sptek._webFrameworkExample.unit.authentication.userStore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
//Entity를 DTO 용도로 활용하지 말것(Entity는 TB와 연결되는 주채로 TB컬럼외 다른 필드를 가지 안도록 하고 그렇게 때문에 필요에 따라 DTO 처럼 임의 필드를 널수 없다)
//todo: Entity는 setter를 막는것을 지향하는데 그러면 매번 DTO->Entity 변환을 Mapper를 사용하지 못하고 Builder로 해야하는데 이게 맞을까?
/**
 * 프레임워크 보안 예제의 사용자 계정 JPA entity.
 *
 * <p>email을 로그인 username으로 사용하며, 주소는 일대다, role과 terms는 다대다 관계로 연결한다.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) //one쪽에 작업이 이루어지면(삭제등) many 쪽도 처리됨
    @JoinColumn(name = "user_id") //해당 컬림은 UserAddrress 테이블에 자동으로 생성됨(UserAddress Entity에 명시적으로 컬럼을 만들수 없음, JPA에서는 매핑을 위한 컬럼은 데이터로써의 의미를 두지 않음)
    private List<UserAddress> userAddresses;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_ROLE_MAP", //만들어낼 매핑 테이블 이름
            joinColumns = @JoinColumn(name = "user_id"), //각각의 테이블에서 사용될 조인 컬럼
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_TERMS_MAP", //만들어낼 매핑 테이블 이름
            joinColumns = @JoinColumn(name = "user_id"), //각각의 테이블에서 사용될 조인 컬럼
            inverseJoinColumns = @JoinColumn(name = "terms_id")
    )
    private List<Terms> terms;
}
