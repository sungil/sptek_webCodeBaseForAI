package com.sptek.__webFramework.view.thymeleaf;

import com.sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;


/**
 * Thymeleaf 템플릿에서 Spring Security dialect를 사용할 수 있게 하는 외부 라이브러리 설정.
 *
 * <p>메인 클래스에 {@link Enable_ThymeleafSpringSecurityDialect_At_Main}가 있을 때만
 * {@code sec:*} 속성 처리를 위한 {@link SpringSecurityDialect} Bean을 등록한다.</p>
 */
@Configuration
public class ThymeleafConfig {

    /**
     * Thymeleaf 템플릿에서 인증/인가 상태를 표현하는 {@code sec:*} dialect Bean을 등록한다.
     */
    @HasAnnotationOnMain_At_Bean(Enable_ThymeleafSpringSecurityDialect_At_Main.class)
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
}

/*
<div sec:authorize="isAuthenticated()">
    로그인한 사용자만 볼 수 있는 영역
</div>

<div sec:authorize="hasRole('ADMIN')">
    관리자만 볼 수 있는 영역
</div>

<p>안녕하세요, <span sec:authentication="name"></span> 님!</p>
*/
