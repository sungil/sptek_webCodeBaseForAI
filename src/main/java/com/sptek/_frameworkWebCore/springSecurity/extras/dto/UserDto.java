package com.sptek._frameworkWebCore.springSecurity.extras.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * 프레임워크 보안 사용자 정보를 화면/API와 UserDetails adapter로 전달하기 위한 DTO.
 *
 * <p>사용자 기본 정보와 주소, role, terms 관계를 함께 담는다. 인증 처리에서는 email을 username으로 사용한다.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private long id;
    private String name;
    private String email;
    private String password;
    private List<UserAddressDto> userAddresses;
    private Set<RoleDto> roles;
    private Set<TermsDto> terms;

}
