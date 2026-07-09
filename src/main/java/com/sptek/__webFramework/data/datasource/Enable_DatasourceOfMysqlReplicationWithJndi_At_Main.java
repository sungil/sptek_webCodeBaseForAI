package com.sptek.__webFramework.data.datasource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 JNDI 기반 MySQL replication datasource 설정 Bean을 활성화하는 애노테이션.
 *
 * <p>{@code DataSourceConfigForMysqlReplicationWithJndi}가 이 애노테이션을 조건으로 등록된다.
 * 일반 MySQL replication 구성과 JNDI 구성은 동시에 활성화하지 않도록 메인 클래스와 프로파일 설정을 함께 확인한다.</p>
 */
public @interface Enable_DatasourceOfMysqlReplicationWithJndi_At_Main {
}
