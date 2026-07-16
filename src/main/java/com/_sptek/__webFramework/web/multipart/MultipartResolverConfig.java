package com._sptek.__webFramework.web.multipart;


import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

/**
 * Servlet multipart 요청 처리를 위한 resolver와 업로드 제한 정책을 등록하는 설정.
 *
 * <p>Spring MVC가 multipart/form-data 요청을 파일 업로드로 해석하게 하며, 크기 제한과
 * 임시 저장 위치 같은 환경별 정책은 {@code spring.servlet.multipart.*} 설정을 따른다.</p>
 */
@Configuration
@EnableConfigurationProperties(MultipartProperties.class)
@ConditionalOnProperty(prefix = "spring.servlet.multipart", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MultipartResolverConfig {

    /**
     * 표준 Servlet multipart resolver를 Spring MVC의 multipartResolver Bean으로 등록한다.
     */
    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver multipartResolver(MultipartProperties multipartProperties) {
        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        multipartResolver.setResolveLazily(multipartProperties.isResolveLazily());
        return multipartResolver;
    }

    /**
     * Servlet multipart request/file 최대 크기와 임시 저장 위치를 설정한다.
     *
     * <p>Spring Boot의 {@code spring.servlet.multipart.*} 표준 설정을 사용해 환경별 업로드
     * 상한을 업무 프로젝트 프로퍼티에서 조정할 수 있게 한다.</p>
     */
    @Bean
    public MultipartConfigElement multipartConfigElement(MultipartProperties multipartProperties) {
        return multipartProperties.createMultipartConfig();
    }


//    // todo : MaxUploadSizeExceededException 이 발생 했을때 ex를 catch 하기 위해서 아래와 같이 시도 했으나.. CustomErrorController 로 진입 되지 않음
//    // todo : DefaultHandlerExceptionResolver 에서 http status 만 적용 해서 바로 response 커밋을 직접 하는 것으로 추측 함
//    @Bean
//    public ErrorPageRegistrar errorPageRegistrar() {
//        return registry -> registry.addErrorPages(
//                new ErrorPage(HttpStatus.PAYLOAD_TOO_LARGE, "/error")
//        );
//    }
}
