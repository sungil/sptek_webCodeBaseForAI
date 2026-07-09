package com.sptek.__webFramework.security.userStore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 약관 정보를 화면/API 계층으로 전달하기 위한 DTO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TermsDto {
    private Long id;
    private String termsName;
}
