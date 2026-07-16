package com._sptek._webFrameworkExample;

import com._sptek.__webFramework.data.datasource.Enable_DatasourceOfH2_At_Main;
import com._sptek.__webFramework.data.jpa.Enable_JpaHybrid_At_Main;
import com._sptek.__webFramework.observability.logging.Enable_GlobalEnvLog_At_Main;
import com._sptek.__webFramework.observability.logging.Enable_OutboundSupportDetailLog_At_Main;
import com._sptek.__webFramework.observability.logging.Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod;
import com._sptek.__webFramework.observability.logging.Enable_VisitHistoryLog_At_Main;
import com._sptek.__webFramework.observability.mdc.Enable_MdcTagging_At_Main;
import com._sptek.__webFramework.observability.monitoring.Enable_HikariDataSourceMonitoring_At_Main;
import com._sptek.__webFramework.observability.monitoring.Enable_HttpConnectorWorkerMonitoring_At_Main;
import com._sptek.__webFramework.observability.monitoring.Enable_OutboundSupportMonitoring_At_Main;
import com._sptek.__webFramework.observability.timing.Enable_RequestTimestampLog_At_Main;
import com._sptek.__webFramework.security.crypto.Enable_EncryptorJasypt_At_Main;
import com._sptek.__webFramework.view.model.Enable_UserAuthenticationToModelAttribute_At_Main;
import com._sptek.__webFramework.web.cors.Enable_CorsPolicyFilter_At_Main;
import com._sptek.__webFramework.web.error.Enable_ResponseOfApplicationGlobalException_At_Main;
import com._sptek.__webFramework.web.filter.Enable_MinorRequestOptimization_At_Main;
import com._sptek.__webFramework.web.publicResourceCache.Enable_HttpCachePublicForStaticResource_At_Main;
import com._sptek.__webFramework.web.xss.Enable_XssProtectForApi_At_Main;
import com._sptek.__webFramework.bootstrap.testSupport.TestAnnotation_At_All;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/* Spring */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {
		"com._sptek.__webFramework",
		"com._sptek._webFrameworkExample"
})
@ServletComponentScan(basePackages = {
		"com._sptek.__webFramework",
		"com._sptek._webFrameworkExample"
}) //필터쪽에 @WebFilter 를 사용하기 위해 필요함
@EnableJpaRepositories(basePackages = {
		"com._sptek.__webFramework.security.userStore.repository"
})
@EntityScan(basePackages = {
		"com._sptek.__webFramework.security.userStore.entity"
})

/* TEST and CHECK */
@TestAnnotation_At_All("")

/* EXCEPTION */
@Enable_ResponseOfApplicationGlobalException_At_Main

/* MONITORING */
@Enable_HttpConnectorWorkerMonitoring_At_Main("")
@Enable_OutboundSupportMonitoring_At_Main("->noConsole")
@Enable_HikariDataSourceMonitoring_At_Main("->noConsole")

/* LOGGING */
@Enable_MdcTagging_At_Main
@Enable_VisitHistoryLog_At_Main("->noConsole")
@Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod("")
@Enable_OutboundSupportDetailLog_At_Main("->noConsole")
@Enable_GlobalEnvLog_At_Main("GlobalEnv file->FW_START_LOG ->noConsole")

/* DATABASE */
//-----------------------------
@Enable_DatasourceOfH2_At_Main // 활성 비활성시 gradle의 runtimeOnly 'com.h2database:h2' 활성/비활성 필요
//@Enable_DatasourceOfMysqlReplication_At_Main
//@Enable_DatasourceOfMysqlReplicationWithJndi_At_Main
//-----------------------------
@Enable_JpaHybrid_At_Main

/* SECURETY */
@Enable_EncryptorJasypt_At_Main
@Enable_CorsPolicyFilter_At_Main
@Enable_XssProtectForApi_At_Main
//Enable_XssProtectForApi_At_Main 가 적용된 경우 Enable_XssProtectForApi_At_ControllerMethod 는 동작하지 않는다.
@Enable_UserAuthenticationToModelAttribute_At_Main

/* UTIL */
@Enable_MinorRequestOptimization_At_Main //minor 한 request 에 대해 session 생성 방지 (세션 관리 효율)
@Enable_HttpCachePublicForStaticResource_At_Main
@Enable_RequestTimestampLog_At_Main

public class SptWfwApplication{
	public static void main(String[] args) {
		new SpringApplicationBuilder(SptWfwApplication.class)
				.properties(Map.of(
						"spring.config.location",
						"optional:classpath:/_sptek/_webFrameworkExample/"
				))
				.run(args);

		//아래와 같은 방법으로 프로파일을 지정(추가)할 수 있지만 환경변수 또는 실행파람의 프로파일에 비해 우선순위가 낮다.
		//다시말해 app.setAdditionalProfiles("dev, stg") 같이 dev, stg 를 추가 하여도 프로파일에 목록에는 올라가지만 실제 환경변수에 다른 프로파일이 있다면 해당 프로파일의 프로퍼티파일을 로딩한다.
		//환경변수에 별도 프로파일이 없으면 dev, stg가 프로파일로 올라가지만 프로퍼티 파일은 stg로 올라간다(순서상 뒷쪽)
		//그럼에도 여러 프로파일을 사용하는 이유는 프로파일에 따라서 특정 Bean을 생성할지 말지를 선택적으로 적용할수 있기 때문이다.
		//SpringApplication app = new SpringApplication(SptWfwApplication.class);
		//app.setAdditionalProfiles("dev, stg");
		//app.run(args);
	}
}


