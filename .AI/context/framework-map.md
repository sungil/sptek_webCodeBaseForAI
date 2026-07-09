# Framework Map

이 문서는 Base 프레임워크와 프로젝트 공통 확장 경로의 책임을 요약한다.

## `__webFramework`

Base 프레임워크 Java 코드는 `src/main/java/com/sptek/__webFramework` 아래에 두며, Spring 기술 종류보다 기능 축을 우선해 배치한다.

- `api`: API 요청/응답 기능. 공통 응답 DTO, API 예외 처리, API 응답 래핑 Aspect, 중복 요청 방지, OpenAPI 문서 설정을 포함한다.
- `bootstrap`: 메인 클래스 애노테이션 탐색, 요청 매핑 애노테이션 레지스트리, 조건부 Bean 등록 조건, 시작 로그/전역 임시값, 테스트용 애노테이션 등 프레임워크 기동 기반을 둔다.
- `data`: 데이터 접근 기능. `datasource`는 H2/MySQL/JNDI datasource 구성, `jpa`는 JPA hybrid 설정, `mybatis`는 MyBatis 설정, 공통 DAO, PageHelper 지원을 담당한다.
- `event`: 프레임워크 이벤트, 이벤트 발행기, 애플리케이션 컨텍스트/HTTP session 생명주기 리스너를 둔다.
- `example`: 프레임워크 기능 사용 예제. 참고용이며 신규 업무 코드를 추가하지 않는다.
- `core`: 프레임워크 공통 코드, 상수, 파일/모델매퍼/직렬화/Spring/타입 변환/예외 같은 기능 축을 가로지르는 기반 요소를 둔다.
- `integration`: 외부 시스템 연동. Apache HttpClient 기반 outbound 호출을 둔다. RestTemplate 호환 코드는 `legacy.integration.httpClient`에 둔다.
- `legacy`: 삭제 후보이거나 deprecated 성격의 호환 코드. 신규 코드에서 의존하지 않는다.
- `observability`: 관측 기능. Actuator, 요청/응답 로그, 방문 이력, Logback appender/filter, MDC, 모니터링 scheduler/executor, 처리시간 측정을 포함한다.
- `security`: 프레임워크 보안 기능. `authorization`, `authentication`, `jwt`, `config`, `crypto`, `password`, `userStore`, `util`로 나누어 권한, 인증, 토큰, 보안 체인, 암복호화, 테스트/기본 사용자 저장소, 인증/보안 유틸을 구분한다.
- `system`: 서버명, 프로젝트 정보 설정, 헬스체크, RSA 공개키, 지원 언어, 파일 스트림, 인덱스, 로그인, 에러 페이지 API/페이지 등 시스템 지원 기능을 둔다.
- `view`: Thymeleaf/View 기능. View 전역 예외, 모델 속성 주입, 기본 view routing, Thymeleaf dialect 설정을 둔다.
- `web`: Servlet/MVC 웹 기능. async response, argument binding, CORS, 전역 Web 예외, filter 등록, interceptor 등록, locale, message conversion, multipart, static cache, request/response/cookie util, XSS 처리를 둔다.

## `_projectCommon`

- `argumentResolver`: 프로젝트 공통 컨트롤러 파라미터 바인딩 확장
- `aspect`: 프로젝트 공통 횡단 관심사
- `commonObject`: 프로젝트 공통 오류 코드, 보안 파일 경로 타입, 게시글/업로드 파일 DTO
- `controllerAdvice`: 프로젝트 공통 응답 후처리나 컨트롤러 전역 처리
- `event`: 프로젝트 공통 이벤트와 생명주기 리스너
- `filter`: 프로젝트 공통 Servlet Filter
- `interceptor`: 프로젝트 공통 MVC Interceptor와 등록 설정
- `logging`: 프로젝트 공통 Logback 필터
- `schedule`: 프로젝트 공통 scheduler와 executor 설정
- `smartLifecydleComponents`: 애플리케이션 생명주기에 맞춰 시작/종료되는 SmartLifecycle 컴포넌트
- `springSecurity`: 프로젝트 경로별 인가 정책과 `SecurityFilterChain` 커스터마이징

## 사용 기준

- 동일 기능을 새로 만들기 전에 `__webFramework`의 기능 축, 커스텀 애노테이션, filter, interceptor, Aspect, ControllerAdvice, ArgumentResolver를 먼저 확인한다.
- CORS, XSS, 공통 응답/예외, 요청/응답 로그, 방문 이력, 중복 요청 방지, 비동기 API, datasource, 암복호화, View model attribute 기능은 `.AI/context/framework-annotations.md`의 기존 애노테이션을 먼저 검토한다.
- `example` 코드를 업무 코드로 옮길 때는 예제 URL, 테스트용 권한 완화, 테스트 데이터, 임시 주석, placeholder 이름을 실제 프로젝트 기준으로 정리한다.
