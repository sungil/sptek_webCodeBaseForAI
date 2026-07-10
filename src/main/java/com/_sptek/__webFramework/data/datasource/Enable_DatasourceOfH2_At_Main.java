package com._sptek.__webFramework.data.datasource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 로컬 H2 datasource 설정 Bean을 활성화하는 애노테이션.
 *
 * <p>{@code DataSourceConfigForH2}가 이 애노테이션을 조건으로 등록된다.
 * datasource 전환 시에는 이 애노테이션뿐 아니라 Gradle DB 의존성과 프로파일별 datasource 설정을 함께 맞춘다.</p>
 */
public @interface Enable_DatasourceOfH2_At_Main {
}
