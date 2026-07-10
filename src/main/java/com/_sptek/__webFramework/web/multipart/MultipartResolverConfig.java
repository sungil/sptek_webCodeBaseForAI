package com._sptek.__webFramework.web.multipart;


import jakarta.servlet.MultipartConfigElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Servlet multipart 요청 처리를 위한 resolver와 업로드 크기 제한을 등록하는 설정.
 *
 * <p>Spring MVC가 multipart/form-data 요청을 파일 업로드로 해석하게 하며,
 * 전역 max size는 넉넉하게 두고 업무 API 내부에서 별도 검증하는 현재 정책을 반영한다.</p>
 */
@Slf4j
@Configuration
public class MultipartResolverConfig implements WebMvcConfigurer {

    /**
     * 표준 Servlet multipart resolver를 Spring MVC의 multipartResolver Bean으로 등록한다.
     */
    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver multipartResolver() {
        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        return multipartResolver;
    }

    /**
     * Servlet multipart request/file 최대 크기 제한을 설정한다.
     *
     * <p>MaxUploadSizeExceededException이 공통 예외 응답으로 안정적으로 변환되지 않는 제약 때문에
     * 이 값은 상한 안전장치로 두고, 실제 업무 제한은 각 API 내부에서 검사한다.</p>
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        // max 초과시 발생하는 MaxUploadSizeExceededException 에 대해서 Ex 핸들러 에서 catch 가 안되는 문제가 있음 (DefaultHandlerExceptionResolver 에서 직접 처리고 response 커밋이 이루어 지는듯)
        // 그래서 MaxUploadSizeExceededException 에 대해 api 규격화된 json 구성이 어려움
        // 그래서 좋은 방법은 아니지만.. spring 단의 설정 값은 넉넉히 하고.. 각 api 내부 에서 최대값 처리를 하는 것으로.. 방향을 잡음 (ServiceErrorCodeEnum.MULTIPARTFILE_UPLOAD_ERROR)
        long maxUploadSizePerFile = 100 * 1024 * 1024; //M
        long maxUploadSize = 100 * 1024 * 1024; //M

        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxRequestSize(DataSize.ofBytes(maxUploadSize));
        factory.setMaxFileSize(DataSize.ofBytes(maxUploadSizePerFile));

        return factory.createMultipartConfig();
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
