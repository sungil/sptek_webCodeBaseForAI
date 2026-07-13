# Framework Custom Annotations

이 문서는 `src/main/java/com/sptek/__webFramework` 하위 기능 패키지에 함께 배치된 커스텀 애노테이션을 새 코드 개발 전에 재사용하기 위한 안내다.

## 먼저 볼 기준

- 기능 추가 요청을 받으면 새 Filter, Interceptor, Aspect, ControllerAdvice, ArgumentResolver, 응답 래퍼, 예외 처리기, datasource 설정을 만들기 전에 이 문서와 `_annotation` 패키지를 먼저 확인한다.
- 이름의 `At_*`는 적용 위치다. `At_Main`은 `SptWfwApplication`, `At_RestController`와 `At_ViewController`는 컨트롤러 클래스, `At_*Method`는 컨트롤러 메서드, `At_Param`은 메서드 파라미터, `At_DtoString`은 DTO `String` 필드에 적용한다.
- `At_Main` 애노테이션은 기능의 전역 활성화 스위치다. 실제 Bean 등록은 대개 `@HasAnnotationOnMain_At_Bean` 조건, `MainClassAnnotationRegister`, 또는 request mapping 애노테이션 레지스트리를 통해 연결된다.
- `String value()`가 있는 애노테이션의 값은 대부분 로그/모니터링 태그다. 기능 옵션으로 단정하지 말고 사용처를 확인한다.
- Base 프레임워크 동작을 새로 만들거나 바꾸기 전에 `__webFramework/example`, 현재 `SptWfwApplication` 활성화 상태, 관련 `.AI/procedures/tech/**` 문서를 함께 확인한다.

## 전역 활성화

| 애노테이션 | 목적 | 개발 시 재사용 기준 |
| --- | --- | --- |
| `@Enable_ResponseOfApplicationGlobalException_At_Main` | 애플리케이션 전역 예외 처리와 기본 오류 컨트롤러를 활성화한다. | 오류 페이지, 기본 오류 응답, 전역 예외 처리를 새로 만들기 전에 확인한다. |
| `@Enable_CorsPolicyFilter_At_Main` | 프레임워크 CORS 정책 필터를 등록한다. | CORS Filter나 WebMvc CORS 설정을 새로 만들기 전에 기존 CORS 리소스와 함께 확인한다. |
| `@Enable_MdcTagging_At_Main` | 요청 단위 MDC 태깅 필터를 등록한다. | request id, user, path 같은 로그 추적값을 직접 MDC에 넣는 코드를 만들기 전에 확인한다. |
| `@Enable_NoFilterAndSessionForMinorRequest_At_Main` | 정적 리소스 등 minor request의 세션 생성과 일부 필터 처리를 줄인다. | 정적 리소스, health, favicon 같은 경량 요청 예외 처리를 새로 추가하기 전에 확인한다. |
| `@Enable_HttpCachePublicForStaticResource_At_Main` | 정적 리소스 응답에 public cache 정책을 적용한다. | 정적 리소스 캐시 헤더를 별도 필터나 컨트롤러에서 처리하기 전에 확인한다. |
| `@Enable_PropertiesToModelAttribute_At_Main` | View 모델에 공통 properties 값을 주입한다. | Thymeleaf 공통 설정값을 매 컨트롤러에서 직접 모델에 넣기 전에 확인한다. |
| `@Enable_UserAuthenticationToModelAttribute_At_Main` | 현재 인증 사용자 정보를 View 모델에 주입한다. | 화면 컨트롤러마다 로그인 사용자 모델 속성을 직접 넣기 전에 확인한다. |
| `@Enable_ThymeleafSpringSecurityDialect_At_Main` | Thymeleaf Spring Security dialect Bean을 등록한다. | 템플릿에서 권한/인증 조건을 직접 구현하기 전에 dialect 사용 가능 여부를 확인한다. |
| `@Enable_ExecutionTimer_At_Main` | 프레임워크 실행 시간 측정 결과 노출을 활성화한다. | 임의의 실행 시간 측정 로그 유틸을 만들기 전에 기존 `ExecutionTimerSupport` 사용 여부를 확인한다. |
| `@Enable_EncryptorJasypt_At_Main` | Jasypt 문자열 암호화 Bean 구성을 활성화한다. | 설정값 암호화 Bean을 별도로 만들기 전에 환경별 암호화 설정과 함께 확인한다. |
| `@Enable_XssProtectForApi_At_Main` | API 응답 XSS 문자열 처리를 ObjectMapper 수준에서 일괄 적용한다. | API 응답 문자열 escape를 컨트롤러나 DTO마다 직접 처리하기 전에 확인한다. 활성화되어 있으면 메서드 단위 API XSS 애노테이션보다 우선한다. |

## 데이터소스와 영속성

| 애노테이션 | 목적 | 개발 시 재사용 기준 |
| --- | --- | --- |
| `@Enable_DatasourceOfH2_At_Main` | H2 datasource 설정 Bean을 활성화한다. | 로컬 DB 연결을 새로 구성하기 전에 Gradle H2 의존성, profile 설정과 함께 확인한다. |
| `@Enable_DatasourceOfMysqlReplication_At_Main` | MySQL replication datasource 설정 Bean을 활성화한다. | master/replica datasource 구성을 새로 만들기 전에 기존 replication 설정을 확인한다. |
| `@Enable_DatasourceOfMysqlReplicationWithJndi_At_Main` | JNDI 기반 MySQL replication datasource 설정 Bean을 활성화한다. | WAS/JNDI datasource 연결을 새로 만들기 전에 기존 JNDI replication 설정을 확인한다. |
| `@Enable_JpaHybrid_At_Main` | JPA와 MyBatis를 함께 사용하는 hybrid persistence 구성을 활성화한다. | Repository, EntityManager, MyBatis SqlSession 구성을 새로 만들기 전에 현재 hybrid 여부를 확인한다. |

Datasource 계열 `At_Main` 애노테이션은 애노테이션만 바꾸면 끝나지 않는다. DB 의존성, profile별 datasource 설정, JPA/MyBatis 설정을 한 묶음으로 검토한다.

## 로깅과 모니터링

| 애노테이션 | 목적 | 개발 시 재사용 기준 |
| --- | --- | --- |
| `@Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod` | 요청/응답 상세 로그 적용 범위를 메인, 컨트롤러, 메서드 단위로 표시한다. | 요청/응답 body, header, outbound 연계 로그를 새로 남기기 전에 기존 상세 로그 필터와 interceptor를 확인한다. |
| `@Enable_VisitHistoryLog_At_Main` | 방문 이력 로깅 interceptor를 활성화한다. | 화면/API 방문 이력 로그를 별도 interceptor로 만들기 전에 확인한다. |
| `@Enable_OutboundSupportDetailLog_At_Main` | `OutboundSupport` 외부 호출 상세 로그를 활성화한다. | 외부 API 호출 로그를 호출부마다 직접 남기기 전에 `OutboundSupport`와 함께 확인한다. |
| `@Enable_GlobalEnvLog_At_Main` | 애플리케이션 시작 시 전역 환경 정보 로그를 활성화한다. | 시작 로그나 환경 진단 로그를 새로 추가하기 전에 민감 정보 노출 정책과 함께 확인한다. |
| `@Enable_HttpConnectorWorkerMonitoring_At_Main` | HTTP connector worker 모니터링 scheduler를 활성화한다. | HTTP connector thread 상태 모니터링을 새로 만들기 전에 확인한다. |
| `@Enable_AsyncMonitoring_At_Main` | async executor 상태 모니터링 scheduler를 활성화한다. | async executor pool 모니터링을 새로 만들기 전에 확인한다. |
| `@Enable_OutboundSupportMonitoring_At_Main` | outbound support 모니터링 scheduler를 활성화한다. | 외부 호출 관리 상태 모니터링을 새로 만들기 전에 확인한다. |
| `@Enable_HikariDataSourceMonitoring_At_Main` | Hikari datasource 상태 모니터링 scheduler를 활성화한다. | 커넥션 풀 모니터링을 새로 만들기 전에 확인한다. |

## API와 View 컨트롤러

| 애노테이션 | 목적 | 개발 시 재사용 기준 |
| --- | --- | --- |
| `@Enable_ResponseOfApiCommonSuccess_At_RestController` | RestController 성공 응답을 공통 응답 구조로 감싼다. | API별 응답 wrapper를 직접 만들기 전에 컨트롤러 클래스에 적용한다. |
| `@Enable_ResponseOfApiGlobalException_At_RestController` | API 예외를 JSON 기반 공통 오류 응답으로 처리한다. | API 전용 `@ExceptionHandler`나 `@RestControllerAdvice`를 새로 만들기 전에 적용한다. |
| `@Enable_ResponseOfViewGlobalException_At_ViewController` | View 요청 예외를 공통 오류 페이지 흐름으로 처리한다. | 화면 컨트롤러별 예외 페이지 이동 로직을 직접 만들기 전에 적용한다. |
| `@Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod` | RestController 클래스나 메서드에 중복 요청 방지 대상을 표시한다. | 중복 클릭, 재전송, 멱등성 방어를 새 interceptor나 service lock으로 만들기 전에 확인한다. |
| `@Enable_AsyncController_At_RestControllerMethod` | 해당 API 메서드 응답을 프레임워크 비동기 컨트롤러 흐름으로 처리한다. | `Callable`, `DeferredResult`, executor 처리 코드를 직접 붙이기 전에 기존 async controller 예제를 확인한다. |
| `@Enable_XssProtectForApi_At_ControllerMethod` | 특정 API 메서드 응답에 선택적 XSS 문자열 처리를 적용한다. | 전역 API XSS가 꺼진 상태에서 일부 API만 escape해야 할 때 사용한다. |
| `@Enable_XssProtectForView_At_ControllerMethod` | View controller 메서드 요청 파라미터에 XSS 처리를 적용한다. | 화면 요청 파라미터 escape를 컨트롤러에서 직접 처리하기 전에 적용한다. |

API 컨트롤러를 새로 만들 때는 기본적으로 공통 성공 응답, API 전역 예외, validation, SpringDoc, 보안 matcher, HTTP 예제를 함께 검토한다.

## DTO와 ArgumentResolver

| 애노테이션 | 목적 | 개발 시 재사용 기준 |
| --- | --- | --- |
| `@Enable_ArgumentResolver_At_Param` | Controller 메서드 파라미터를 특정 `HandlerMethodArgumentResolver` 적용 대상으로 표시한다. | 같은 DTO 타입 중 일부 파라미터만 커스텀 바인딩해야 할 때 사용한다. |
| `@Enable_DecryptAuto_At_DtoString` | DTO의 암호화된 `String` 필드를 자동 복호화 대상으로 표시한다. | 암호화 문자열을 서비스나 컨트롤러에서 직접 복호화하기 전에 `GlobalEncryptor` 흐름을 확인한다. 일반 문자열에는 붙이지 않는다. |

## 내부 조건과 테스트용

| 애노테이션 | 목적 | 개발 시 재사용 기준 |
| --- | --- | --- |
| `@HasAnnotationOnMain_At_Bean` | Bean 클래스나 `@Bean` 메서드를 메인 클래스의 특정 애노테이션 존재 여부로 조건부 등록한다. | 신규 프레임워크 설정 Bean을 `At_Main` 스위치와 연결할 때 사용한다. 업무 코드에서 일반 기능 플래그처럼 남용하지 않는다. |
| `@TestAnnotation_At_All` | 애노테이션 탐지, Aspect pointcut, interceptor 조건 확인용 테스트 마커다. | 실제 기능 스위치로 사용하지 않는다. 예제, 진단, 실험 코드에서만 사용한다. |

