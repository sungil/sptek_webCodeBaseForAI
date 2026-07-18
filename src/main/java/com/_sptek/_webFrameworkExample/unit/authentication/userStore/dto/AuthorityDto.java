package com._sptek._webFrameworkExample.unit.authentication.userStore.dto;

import com._sptek._webFrameworkExample.unit.authentication.authorization.AuthorityEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 권한 enum 값을 화면/API 계층으로 전달하기 위한 DTO.
 *
 * <p>DB entity의 식별자와 프레임워크 권한 enum을 함께 담아 role 관리 화면에서 사용한다.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDto {
    private Long id;

    @NotNull
    private AuthorityEnum authority;
}
