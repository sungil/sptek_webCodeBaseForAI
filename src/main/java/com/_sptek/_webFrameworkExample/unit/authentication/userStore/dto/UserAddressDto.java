package com._sptek._webFrameworkExample.unit.authentication.userStore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 주소 유형과 주소 문자열을 화면/API 계층으로 전달하기 위한 DTO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddressDto {
    private Long id;
    private String addressType;
    private String address;
}
