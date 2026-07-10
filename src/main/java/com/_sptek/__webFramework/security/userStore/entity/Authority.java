package com._sptek.__webFramework.security.userStore.entity;

import com._sptek.__webFramework.security.authorization.AuthorityEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

//todo: Entity는 setter를 막는것을 지향하는데 그러면 매번 DTO->Entity 변환을 Mapper를 사용하지 못하고 Builder로 해야하는데 이게 맞을까?
/**
 * Spring Security authority enum과 DB 저장 정보를 연결하는 JPA entity.
 *
 * <p>authority enum을 저장하면 persist/update 직전에 code, alias, description 파생 필드를 동기화한다.
 * Role과는 다대다 관계로 연결된다.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "AUTHORITY")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private AuthorityEnum authority;

    @Column(unique = true)
    private String code;

    @Column(unique = true)
    private String alias;

    private String description;
    private String status; //해당 권한의 상태(사용함/사용안함 등)

    @ManyToMany(mappedBy = "authorities")
    private List<Role> roles;

    //@PostLoad
    //@PostPersist
    /**
     * AuthorityEnum의 보조 식별자 필드를 entity 컬럼에 동기화한다.
     */
    @PrePersist
    @PreUpdate
    private void initializeDerivedFields() {
        //authority 객체를 기준으로 나머지 값들을 저장하기 위해
        this.code = authority.getCode();
        this.alias = authority.getAlias();
        this.description = authority.getDescription();
    }
}
