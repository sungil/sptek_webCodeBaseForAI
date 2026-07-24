# 새 업무 영역 개발 시 Framework코드영역 참고 키워드

- 새 업무 영역 코드를 작성할 때는 임의 패턴으로 구현하지 말고, 아래 키워드가 관련되면 반드시 현재 코드에서 Framework코드영역과 예시 업무 프로젝트의 사용 방식을 먼저 검색해 참고한다.
- 기능 요소와 더불어 각 기능 요소의 코딩 스타일도 참고하여 새 업무 영역 코드를 작성한다.

## 업무 영역 배치
- `com.{companyName}._{domainName}`
- `com.{companyName}.__{companyName}Common`
- `src/main/resources/{companyName}/_{domainName}`
- `src/main/resources/{companyName}/__{companyName}Common`
- `application-{profile}.yml`
- `spring.config.import`

## 기능 활성화 패턴
- `@Enable_*_At_Main`
- `@Enable_*_At_RestController`
- `@Enable_*_At_ControllerMethod`
- `@Enable_*_At_Param`
- `@Enable_*_At_DtoString`
- `@HasAnnotationOnMain_At_Bean`
- `ConditionForHasAnnotationOnMain`

## Controller / API 응답
- `@RestController`
- `@Controller`
- `@Tag`
- `@Operation`
- `ApiCommonSuccessResponseDto`
- `ApiCommonErrorResponseDto`
- `ApiCommonResponseHelperAspect`
- `@Enable_ResponseOfApiCommonSuccess_At_RestController`
- `@Enable_ResponseOfApiGlobalException_At_RestController`
- `ResponseEntity`
- `HttpEntity`

## 예외 / 결과 코드
- `ServiceException`
- `BaseCode`
- `CommonErrorCodeEnum`
- `SuccessCodeEnum`
- `ApiGlobalExceptionHandler`
- `ViewGlobalExceptionHandler`
- `ApplicationGlobalExceptionHandler`
- `CustomErrorController`
- `RequestUtil.isApiRequest`

## Validation
- `@Validated`
- `@Valid`
- `BindingResult`
- `@RequestBody @Validated`
- `ValidatedDto`
- `@Schema`
- `@Parameter`
- `@ApiResponse`

## Security / 인증 / 권한
- `SecurityFilterChain`
- `FrameworkSecurityFilterChainConfig`
- `ExampleSecurityFilterChainConfig`
- `SecurityPathUtil`
- `CurrentAuthenticationUtil`
- `FrameworkUserDetails`
- `JwtAuthenticationFilter`
- `JwtApiAuthenticationEntryPoint`
- `JwtApiAccessDeniedHandler`
- `DomainAuthorityEnum`
- `formLogin`
- `session`
- `csrf`
- `stateless`

## Filter / Interceptor / Aspect
- `FrameworkFilterConfig`
- `FilterRegistrationBean`
- `InterceptorGlobalConfig`
- `HandlerMethod`
- `RequestContextFilter`
- `MakeRequestTimestampFilter`
- `MakeMdcFilter`
- `ReqResDetailLogFilter`
- `ReqResDetailLogDecisionInterceptor`
- `VisitHistoryLoggingInterceptor`
- `ViewErrorLogSupportInterceptor`
- `PreventDuplicateRequestInterceptor`

## 로그 / 추적
- `LoggingUtil`
- `MDC`
- `correlationId`
- `memberId`
- `sessionId`
- `ExecutionTimeSupport`
- `RequestTimestampDto`
- `Req/Res detail log`
- `web-framework.logging.req-res-detail.*`
- `related outbounds`

## Transaction
- `@Transactional(readOnly = true)`
- `DataSourceConfigForH2`
- `DataSourceConfigForMysqlReplication`
- `DataSourceConfigForMysqlReplicationWithJndi`
- `MybatisConfig`
- `MybatisProperties`
- `MyBatisCommonDao`
- `PageHelperSupport`
- `PageInfoSupport`
- `@Enable_JpaHybrid_At_Main`
- `JpaHybridConfig`
- `@EnableJpaRepositories`
- `@EntityScan`

## MyBatis / Mapper
- `mapper-location-patterns`
- `namespace`
- `selectOne`
- `selectList`
- `selectMap`
- `selectListWithPagination`
- `ResultHandler`
- profile별 mapper 경로

## 외부 HTTP 호출
- `OutboundSupport`
- `OutboundResponse`
- `OutboundHttpClientProperties`
- `PoolingHttpClientConnectionManager`
- `CloseableHttpClient`
- `web-framework.outbound.*`
- outbound 로그

## File / Multipart
- `MultipartResolverConfig`
- `spring.servlet.multipart.*`
- `MultipartFile`
- `@ModelAttribute`
- `MediaType.MULTIPART_FORM_DATA_VALUE`
- `FileUtil`
- `SecurityPathUtil.resolveStoragePath`
- `getSecuredFilePathFor*`

## CORS / Session / Minor Request
- `@Enable_CorsPolicyFilter_At_Main`
- `CorsPolicyFilter`
- `corsPolicy-*.yml`
- `@Enable_MinorRequestOptimization_At_Main`
- `MinorRequestSessionRepositorySkipFilter`
- 정적 리소스 제외 패턴

## JSON / MessageConverter / XSS
- `MessageConverterConfig`
- `ObjectMapperConfig`
- `ResponseEscapingMappingJackson2HttpMessageConverter`
- `@Enable_HtmlEntityEscapeForJsonResponse_At_RestControllerMethod`
- `@Enable_UnicodeEscapeForJsonResponse_At_RestControllerMethod`

## Locale / Message
- `@Enable_LocaleSupport_At_Main`
- `LocaleConfig`
- `CustomLocaleChangeInterceptor`
- `ReloadableResourceBundleMessageSource`
- `messages*.properties`

## View / Thymeleaf
- `templates/thymeleaf`
- layout / fragment
- `ControllerAdviceForPropertiesToModelAttribute`
- `ControllerAdviceForUserAuthenticationToModelAttribute`
- `SimpleViewRouteConfig`
- `formError.html`

## Monitoring / Actuator / Scheduler
- `@Enable_*Monitoring_At_Main`
- `web-framework.monitoring.*`
- `IndicatorForHealth`
- `CustomInfoContributor`
- `ThreadPoolTaskScheduler`
- scheduler bean name
- thread name prefix
- graceful shutdown

## Event / Lifecycle
- `SptBaseEvent`
- `SptEventPublisher`
- `ApplicationListener`
- `HttpSessionCreatedEventListenerFor`
- `HttpSessionDestroyedEventListenerFor`

## 암호화 / 설정 보호
- `@Enable_EncryptorJasypt_At_Main`
- `customJasyptStringEncryptor`
- `EncryptorRegistry`
- `ENC_sptTYPE(...)`
- `@Enable_DecryptAuto_At_DtoString`
