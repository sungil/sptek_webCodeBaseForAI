package com._sptek._webFrameworkExample.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 요청 DTO의 입력값 검증 조건과 API 문서 정보를 필드 애노테이션으로 표현하는 예제 DTO.
 *
 * <p>{@code @NotBlank}, {@code @Pattern}, {@code @Size}, {@code @Email} 같은 Bean Validation 애노테이션은
 * 컨트롤러 진입 전에 입력 계약을 검증하고, 검증 실패 시 공통 예외 처리 흐름에서 일관된 메시지를 사용할 수 있게 한다.
 * {@code @Schema} 같은 문서화 애노테이션도 같은 필드에 함께 두어 validation 조건과 Swagger/OpenAPI 설명이
 * 서로 다른 위치에서 중복 관리되지 않도록 한다.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidatedDto {


    //NotBlank은 NotNull, NotEmpty 기능을 모두 포함함.
    @NotBlank(message = "userId을 입력해 주세요") //message값은 Exception 발생시 Exception의 메시지 값으로 처리됨.
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$", message = "userId는 영문자와 숫자로만 입력해 주세요.")
    @Schema(description = "사용자 ID", example = "sungilry")
    private String userId;

    @NotBlank(message = "userName을 입력해 주세요")
    @Size(min=2, max=20, message = "userName은 2자 이상 20자 이하로 해주세요")
    @Schema(description = "사용자 이름", example = "이성일")
    private String userName;

    @NotNull(message = "age을 입력해 주세요")
    @Min(value = 0, message = "age은 0보다 커야 합니다.")
    @Max(value = Integer.MAX_VALUE, message = "age가 너무 큽니다.")
    @Schema(description = "사용자 나이", example = "20")
    private int age;

    @Email(message = "email이 이메일 형식에 맞지 않습니다.")
    @Schema(description = "사용자 이메일", example = "sungilry@sptek.co.kr")
    private String email;

    @Pattern(regexp = "010\\d{8}", message = "전화번호 형식에 맞지 않습니다.")
    @Schema(description = "사용자 전화번호", example = "01012345678")
    private String mobileNumber;

    /*
    @Valid //객체 내부 까지 벨리드 검사
    @URL //url 형식
    @Positive //양수
    @PositiveOrZero //0포함 양수
    @Negative //음수
    @NegativeOrZero //0포함 음수
    @AssertTrue //true만
    @AssertFalse //false만
    @Pattern(regexp = "^[가-힣0-9a-zA-Z]{1,20}$") //한글,숫자,영문(대소문) 20자
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{1,20}$") //영문,수자,특수문자최소1개인 20자

    //비밀번호 예시 (최소6자, 최대20자, 숫자, 대문자, 특수문자가 각각 최소 1개 이상 들어가야함
    @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-])[가-힣0-9a-zA-Z!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-]{6,20}$")
     */
}
