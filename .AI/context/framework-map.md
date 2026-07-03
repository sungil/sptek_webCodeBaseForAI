# Framework Map

이 문서는 Base 프레임워크와 프로젝트 공통 확장 경로의 책임을 요약한다.

## `_frameworkWebCore`

- `_annotation`: 메인 클래스, 컨트롤러, 메서드, DTO 필드 등에 적용하는 프레임워크 활성화/조건부 등록용 커스텀 애노테이션
- `_example`: 프레임워크 기능 사용 예제. 참고용이며 신규 업무 코드를 추가하지 않는다.
- `_systemController`: 서버명, 프로젝트 정보, 헬스체크, RSA 공개키, 지원 언어, 파일 스트림, 인덱스, 로그인, 에러 페이지 API/페이지
- `actuator`: Actuator 정보 노출과 커스텀 헬스체크 지표
- `argumentResolver`: 컨트롤러 파라미터 바인딩 확장을 위한 `HandlerMethodArgumentResolver` 설정
- `aspect`: API 공통 응답 처리 등 횡단 관심사
- `async`: 비동기 컨트롤러 처리, async executor, 비동기 반환값 처리
- `base`: 공통 응답 DTO, 성공/실패 코드, 예외 타입/핸들러, 전역 상수, 애노테이션 레지스트리
- `commonObject`: 전역 DTO와 설정 프로퍼티 VO
- `controllerAdvice`: API XSS 처리, View 공통 모델 속성, 인증 사용자 정보 주입
- `datasource`: H2, MySQL replication, JNDI replication 데이터소스 구성
- `DEPRECATED_applicationContextInitializer`: deprecated 초기화 코드. 신규 코드에서 의존하지 않는다.
- `encryption`: AES, DES, RSA, Jasypt 등 암복호화 모듈
- `event`: 프레임워크 이벤트, 이벤트 발행기, 컨텍스트/세션 생명주기 리스너
- `external`: Swagger/OpenAPI, Thymeleaf dialect 등 외부 라이브러리 연동
- `filter`: CORS, MDC, 요청 시각, 세션 제외, 요청/응답 상세 로그 등 Servlet Filter
- `globalConfigurer`: 정적 리소스 핸들러와 기본 View Controller
- `hash`: 비밀번호 해시 등 hash 관련 Bean
- `httpConnector`: 외부 HTTP 호출용 HttpClient 설정
- `interceptor`: Locale, 중복 요청 방지, View 에러 로그, View XSS, 방문 이력 등 MVC Interceptor
- `logging`: Logback appender와 로그 필터
- `message`: Locale, Jackson ObjectMapper, 메시지 컨버터, XSS 문자열 처리
- `modelMapper`: ModelMapper Bean과 매핑 설정
- `multipart`: Multipart 요청 처리 resolver
- `persistence`: JPA/MyBatis 설정과 MyBatis 공통 DAO
- `schedule`: async, Hikari, HTTP connector, outbound 상태 모니터링 scheduler/executor
- `springSecurity`: Security filter chain, 인증 성공/실패 처리, 사용자 상세정보, 권한/역할/약관/사용자 엔티티/DTO/Repository
- `support`: outbound 호출, MyBatis result handler, 페이징, XSS escape 등 보조 지원
- `util`: 인증, 쿠키, 예외, 파일, 로케일, 로그, 요청/응답, 보안, Spring 컨텍스트 유틸

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

- 동일 기능을 새로 만들기 전에 Base 코드의 확장 지점, 커스텀 애노테이션, 필터, 인터셉터, Aspect, ControllerAdvice, ArgumentResolver를 먼저 확인한다.
- `_example` 코드를 업무 코드로 옮길 때는 예제 URL, 테스트용 권한 완화, 테스트 데이터, 임시 주석, placeholder 이름을 실제 프로젝트 기준으로 정리한다.
