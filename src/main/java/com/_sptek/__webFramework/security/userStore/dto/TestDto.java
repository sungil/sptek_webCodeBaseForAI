package com._sptek.__webFramework.security.userStore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA repository 예제에서 key/value 테스트 데이터를 전달하기 위한 DTO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestDto {
    private long id;
    private String key;
    private String value;
}
