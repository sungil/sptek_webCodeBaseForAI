package com._sptek.__webFramework.view.thymeleaf;

import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;


/**
 * Thymeleaf 템플릿에서 Spring Security dialect를 사용할 수 있게 하는 외부 라이브러리 설정.
 *
 * <p>메인 클래스에 {@link Enable_ThymeleafSpringSecurityDialect_At_Main}가 있을 때만
 * {@code sec:*} 속성 처리를 위한 {@link SpringSecurityDialect} Bean을 등록한다.</p>
 */
@Configuration
public class ThymeleafConfig {

    /**
     * 프레임워크가 제공하는 공통 fragment와 error view를 새 resource 구조에서 찾도록 등록한다.
     */
    @Bean
    public SpringResourceTemplateResolver webFrameworkTemplateResolver(ApplicationContext applicationContext) {
        return templateResolver(
                applicationContext,
                "webFrameworkTemplateResolver",
                "classpath:/_sptek/__webFramework/templates/thymeleaf/",
                50
        );
    }

    /**
     * 프로젝트 공통 UI fragment가 생기면 실행 프로젝트 템플릿보다 뒤, 프레임워크 템플릿보다 앞에서 찾는다.
     */
    @Bean
    public SpringResourceTemplateResolver projectsCommonTemplateResolver(ApplicationContext applicationContext) {
        return templateResolver(
                applicationContext,
                "projectsCommonTemplateResolver",
                "classpath:/cesco/__projectsCommon/templates/thymeleaf/",
                40
        );
    }

    /**
     * sales 프로젝트가 자체 view를 갖기 시작하면 같은 view name에서 프로젝트 리소스를 우선한다.
     */
    @Bean
    public SpringResourceTemplateResolver salesTemplateResolver(ApplicationContext applicationContext) {
        return templateResolver(
                applicationContext,
                "salesTemplateResolver",
                "classpath:/cesco/_sales/templates/thymeleaf/",
                20
        );
    }

    /**
     * marketing 프로젝트가 자체 view를 갖기 시작하면 같은 view name에서 프로젝트 리소스를 우선한다.
     */
    @Bean
    public SpringResourceTemplateResolver marketingTemplateResolver(ApplicationContext applicationContext) {
        return templateResolver(
                applicationContext,
                "marketingTemplateResolver",
                "classpath:/cesco/_marketing/templates/thymeleaf/",
                20
        );
    }

    /**
     * Thymeleaf 템플릿에서 인증/인가 상태를 표현하는 {@code sec:*} dialect Bean을 등록한다.
     */
    @HasAnnotationOnMain_At_Bean(Enable_ThymeleafSpringSecurityDialect_At_Main.class)
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    private SpringResourceTemplateResolver templateResolver(
            ApplicationContext applicationContext,
            String name,
            String prefix,
            int order
    ) {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setName(name);
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCheckExistence(true);
        templateResolver.setCacheable(false);
        templateResolver.setOrder(order);
        return templateResolver;
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
