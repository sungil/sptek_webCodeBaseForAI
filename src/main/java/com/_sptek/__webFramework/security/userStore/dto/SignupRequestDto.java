package com._sptek.__webFramework.security.userStore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 회원 가입 요청과 가입 화면 선택 데이터를 함께 담는 DTO.
 *
 * <p>입력된 사용자 기본 정보, 주소, 선택 role/terms와 화면 표시용 전체 role/terms 목록을 함께 표현한다.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    @Size(min = 2, max = 20, message = "name은 2자 이상 20자 이하로 입력해 주세요.")
    @Schema(description = "사용자 이름", example = "이성일")
    private String name;

    @NotEmpty(message = "email을 입력해 주세요.")
    @Email(message = "email이 이메일 형식에 맞지 않습니다.")
    private String email;

    //최소6자, 최대20자, 숫자, 대문자, 특수문자가 각각 최소 1개 이상 들어가야함
    @Pattern(message = "비밀번호는 최소6자, 최대20자, 숫자, 대문자, 특수문자가 각각 최소 1개 이상 들어가야 합니다.", regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-])[가-힣0-9a-zA-Z!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-]{6,20}$")
    private String password;

    //주소
    private List<UserAddressDto> userAddresses;

    @NotNull(message = "하나 이상의 Role을 선택해 주세요.")
    private List<RoleDto> roles;

    @NotNull(message = "MEMBER_SHIP 약관은 필수 동의 사항 입니다.")
    //약관 동의
    private List<TermsDto> terms;

    private List<RoleDto> allRoles;
    private List<TermsDto> allTerms;

    private List<String> userRoleNames;

    /**
     * 선택된 role DTO 목록에서 role name만 추출해 반환한다.
     */
    public List<String> getUserRoleNames() {
        return Optional.ofNullable(roles).orElseGet(Collections::emptyList).stream().map(RoleDto::getRoleName).collect(Collectors.toList());
    }

    private List<String> userTermsNames;

    /**
     * 선택된 terms DTO 목록에서 terms name만 추출해 반환한다.
     */
    public List<String> getUserTermsNames() {
        return Optional.ofNullable(terms).orElseGet(Collections::emptyList).stream().map(TermsDto::getTermsName).collect(Collectors.toList());
    }
}
