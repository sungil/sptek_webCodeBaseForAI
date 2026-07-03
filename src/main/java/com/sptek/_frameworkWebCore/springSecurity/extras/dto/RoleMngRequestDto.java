package com.sptek._frameworkWebCore.springSecurity.extras.dto;

import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

/**
 * role 관리 화면/API에서 전체 role과 authority 목록을 함께 전달하기 위한 DTO.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleMngRequestDto {
    @Valid //내부 객체에 대해서도 Valid 기능을 동작하게 함
    private List<RoleDto> allRoles;

    @Valid
    private List<AuthorityDto> allAuthorities;
}
