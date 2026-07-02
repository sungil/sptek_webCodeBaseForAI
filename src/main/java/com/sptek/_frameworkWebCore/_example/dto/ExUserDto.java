package com.sptek._frameworkWebCore._example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Controller 요청/응답에서 사용하는 DTO 필드 계약을 애노테이션으로 표현하는 예제 DTO.
 *
 * <p>필드의 필수 여부, 검증 메시지, Swagger/OpenAPI 설명, 문서 노출 여부처럼
 * validation 또는 API 문서화에서 소비되는 정보는 가능하면 DTO 필드에 선언형 애노테이션으로 둔다.
 * 컨트롤러나 서비스에서 같은 의미를 별도 문자열, 조건문, 문서 전용 코드로 중복 관리하지 않기 위한 기준 예제이다.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExUserDto {

    @Schema(description = "사용자 ID", example = "sungilry")
    @NotBlank(message = "Id를 입력해 주세요")
    private String id;

    @Schema(description = "사용자 이름", example = "이성일")
    @NotBlank(message = "이름을 입력해 주세요")
    private String name;

    @Schema(description = "사용자 타입", example = "customer")
    private UserType type;

    @Schema(hidden = true)
    private String displayName;

    public enum UserType {
        customer, manager, admin, anonymous
    }
}
