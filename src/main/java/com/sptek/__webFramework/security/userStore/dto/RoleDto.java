package com.sptek.__webFramework.security.userStore.dto;

import com.sptek.__webFramework.security.authorization.AuthorityEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * role 이름과 연결된 authority 정보를 화면/API 계층으로 전달하기 위한 DTO.
 *
 * <p>권한 DTO 목록과 enum 목록을 모두 지원해 role 관리 화면과 Spring Security authority 변환에서 함께 사용한다.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDto {
    private Long id;

    @Size(min = 2, max = 20, message = "role Name 은 2자 이상 20자 이하로 입력해 주세요.")
    @Schema(description = "롤 이름", example = "ROLE_USER")
    private String roleName;

    private List<AuthorityDto> authorities;

    @Setter(AccessLevel.NONE)
    private List<AuthorityEnum> authorityEnums;

    /**
     * authority DTO 목록을 Spring Security에서 사용하는 AuthorityEnum 목록으로 변환한다.
     */
    public List<AuthorityEnum> getAuthorityEnums() {
        return Optional.ofNullable(authorities).orElseGet(Collections::emptyList)
                .stream().map(AuthorityDto::getAuthority).collect(Collectors.toList());
    }
}
