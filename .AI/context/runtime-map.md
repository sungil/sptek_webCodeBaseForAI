# Runtime Map

이 문서는 애플리케이션 실행 기준과 기능 활성화 연결 지점을 요약한다.

## 진입점

- 애플리케이션 진입점은 `src/main/java/com/sptek/SptWfwApplication.java`이다.
- 이 클래스의 커스텀 `@Enable_*` 애노테이션들이 프레임워크 기능의 실제 활성화 스위치 역할을 한다.
- 기능 동작을 분석할 때 구현체만 보지 말고 메인 클래스의 활성화 상태를 함께 확인한다.

## 현재 실행 기준

- 기본 활성 profile은 `local`이다.
- 현재 메인 클래스 기준으로 H2 datasource와 JPA hybrid 구성이 활성화되어 있다.
- MySQL replication 및 JNDI replication datasource는 비활성 예시로 남아 있다.
- local 서버 포트는 443이고, H2 file DB는 `infra/h2DB` 아래에 생성된다.
- local 설정에는 graceful shutdown과 devtools 설정이 포함된다. 이 값을 운영 기본값으로 일반화하지 않는다.

## 기능 분석 연결 지점

프레임워크 기능이나 서버 시작 오류를 분석할 때는 필요한 항목만 추적한다.

- `SptWfwApplication`의 활성화 `@Enable_*`
- 커스텀 `@Enable_*` 애노테이션 정의
- `@Conditional`, profile, Bean 등록 조건
- `@Component`, `@Configuration`, `@Bean` 등록 방식
- filter, interceptor, AOP, ArgumentResolver, ControllerAdvice 적용 순서
- profile별 `spring.config.import`
- `_frameworkApplicationProperties`
- `_projectApplicationProperties`

## 주요 흐름

- REST API와 Thymeleaf View를 모두 지원한다.
- API 컨트롤러는 공통 응답/예외 래핑 애노테이션과 SpringDoc 문서화 관례를 함께 확인한다.
- View 컨트롤러는 Thymeleaf 템플릿, layout/fragment, view 전역 예외 처리 관례를 함께 확인한다.
- 보안은 Spring Security, JWT, 세션/권한/역할 기반 구조, 프로젝트 공통 `SecurityFilterChainConfig`가 함께 구성한다.
- URL 접근 정책, Swagger/H2/static 예외, 로그인/에러 페이지 동작은 보안 matcher와 프레임워크 유틸의 공통 경로 정의를 함께 확인한다.
- 영속성은 JPA와 MyBatis가 공존한다. 도메인이나 예제가 따르는 방식을 우선하고 임의로 혼합하지 않는다.
