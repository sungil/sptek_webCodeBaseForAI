package com._sptek._webFrameworkExample.unit.authentication.userStore.entity;

import jakarta.persistence.*;
import lombok.*;

//todo: Entity는 setter를 막는것을 지향하는데 그러면 매번 DTO->Entity 변환을 Mapper를 사용하지 못하고 Builder로 해야하는데 이게 맞을까?
/**
 * 사용자 주소 유형과 주소 값을 저장하는 JPA entity.
 *
 * <p>User entity의 일대다 관계에서 관리되며, user_id join column은 User 쪽 매핑으로 생성된다.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "USER_ADDRESS")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String addressType;
    private String address;



}
