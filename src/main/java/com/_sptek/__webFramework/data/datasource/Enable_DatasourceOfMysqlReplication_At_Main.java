package com._sptek.__webFramework.data.datasource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 MySQL replication datasource 설정 Bean을 활성화하는 애노테이션.
 *
 * <p>{@code DataSourceConfigForMysqlReplication}이 이 애노테이션을 조건으로 등록된다.
 * datasource 전환 시에는 애노테이션, DB 의존성, 프로파일별 datasource 설정을 한 묶음으로 검토한다.</p>
 */
public @interface Enable_DatasourceOfMysqlReplication_At_Main {
}
