package com._sptek.__webFramework.web.cors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 프레임워크 CORS 정책 필터 등록을 활성화하는 애노테이션.
 *
 * <p>{@code FilterConfigForFrameworkWebCore}가 이 애노테이션을 조건으로 {@code CorsPolicyFilter}를 등록한다.
 * 실제 허용 origin, method, header 정책은 프로젝트 공통 CORS 설정 리소스에서 읽는다.</p>
 */
public @interface Enable_CorsPolicyFilter_At_Main {
}
