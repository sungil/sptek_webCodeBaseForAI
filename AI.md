# AI.md

## AI 에이전트 문서 사용 원칙

- 이 문서는 이 저장소에서 Codex, Claude를 포함한 AI 에이전트가 안전하고 일관되게 작업하기 위한 최상위 공통 지침이다. 저장소 구조, 코딩 규칙, 보안 원칙, 검증 절차는 루트의 `AI.md`를 기준으로 따른다.
- `AGENTS.md`, `CLAUDE.md`, `.github/copilot-instructions.md`, `.codex/skills`, `.claude/commands` 등 도구별 문서는 자동 발견과 실행 편의를 위한 진입점 또는 어댑터로만 사용한다. 특정 도구 전용 skill, command, MCP, plugin, connector가 있더라도 프로젝트 공통 규칙을 우회하지 않는다. 도구별 문서와 `AI.md`의 내용이 충돌하면 `AI.md`를 우선한다.
- 반복적으로 사용하는 분석·구현·검증 절차는 `.AI/workflows` 아래에 도구 독립적인 Markdown 문서로 작성한다. Codex skill이나 Claude command가 같은 절차를 사용해야 하면 각 도구별 파일에는 먼저 루트 `AI.md`를 읽고, 그 다음 관련 `.AI/workflows/**` 문서를 따르라는 최소한의 참조만 둔다. 같은 내용을 여러 도구별 폴더에 복사하지 않는다.
- 신규 Codex skill 또는 Claude command를 만들 때도 상세 절차와 재사용 스크립트는 먼저 `.AI/workflows/**`에 둔다. `.codex/skills/**`, `.claude/commands/**`에는 자동 발견을 위한 메타데이터와 `.AI/workflows/**` 참조, 필요한 경우 얇은 래퍼만 둔다.
- `.codex/skills`의 repo-local 스킬 진입점은 팀 공통 AI 작업 규칙으로 취급하므로 커밋 대상이다. 반면 `.codex/run`은 Codex 로컬 실행 로그와 임시 출력물이므로 소스 변경으로 취급하지 않고 커밋 대상에 포함하지 않는다.
- 작업공간에 구성된 Codex skill, Claude command, MCP, plugin, connector 또는 사용자가 추가한 기타 자동화 구성을 활용하는 경우, 작업에 앞서 `XXX를 이용해서 작업합니다.`처럼 어떤 기술 요소를 사용하는지 아주 간략하게 알린다.
- 이 문서는 저장소 루트와 모든 하위 경로에 적용한다. 하위 디렉터리에 더 구체적인 AI 지침 문서(`AI.md`, `AGENTS.md`, `CLAUDE.md` 등)가 생기면 해당 범위에서는 하위 문서가 우선하되, 루트 `AI.md`의 공통 원칙과 충돌하지 않아야 한다.

## Base 코드 사용 목적과 우선순위

이 저장소의 Base 코드는 개별 프로젝트에서 가장 짧거나 가장 효율적인 구현만을 목표로 하지 않는다. 
여러 프로젝트가 동일한 구조와 방식으로 코드를 생산하게 하여 전사적인 코드 일관성, 학습 비용 절감, 유지보수성, 운영 안정성을 지속적으로 유지하는 것이 더 큰 목적이다.
AI 에이전트는 신규 기능을 구현하거나 기존 기능을 수정할 때 다음 원칙을 따른다.
- 먼저 `_frameworkWebCore`, `_projectCommon`, `_example`의 기존 구조와 사용 예제를 확인하고, 가능한 한 Base 코드가 제공하는 패턴과 확장 지점을 사용한다.
- 더 간결하거나 더 효율적인 일반적인 구현 방법이 있더라도, 프로젝트 전체의 일관성을 해치면 임의로 도입하지 않는다.
- 컨트롤러, 서비스, DTO, 예외 처리, 공통 응답, 보안, 필터, 인터셉터, 설정 방식은 기존 Base 코드와 예제 코드의 형식을 우선 따른다.
- 신규 업무 코드는 Base 코드의 관례를 기반으로 실제 프로젝트·도메인 패키지 아래에 작성한다.
- 기존 Base 코드로 요구사항을 처리할 수 있으면 중복 구현체나 별도 공통 구조를 만들지 않는다.
- 요구사항을 처리하는 과정에서 Base 코드 자체를 개선하는 편이 장기적으로 더 일관적이거나 유지보수에 유리하다고 판단되면, 바로 수정하지 말고 먼저 사용자에게 변경 필요성, 영향 범위, 장단점을 설명하고 확인을 받는다.
- Base 코드 변경은 여러 프로젝트에 영향을 줄 수 있는 주요 변경으로 간주한다. 따라서 단일 기능 구현보다 더 보수적으로 판단하고, 관련 예제·설정·문서·검증 범위를 함께 고려한다.
- 단 단순 오타, 주석, 문서, 명백히 잘못된 예제 보정의 경우는 필요한 범위에서 바로 수정할 수 있다. 공통 API, 응답 형식, 보안 정책, 필터·인터셉터 순서, 데이터소스·프로파일 동작처럼 실행 동작에 영향을 주는 Base 코드 변경은 사전 확인 후 진행한다.

## 프로젝트 기준

- 이 저장소는 SPT(사명) Framework Web Core 기반의 단일 모듈 Spring Boot 웹 애플리케이션이다. 단순 샘플 앱이 아니라 여러 업무 프로젝트가 공통으로 가져갈 Base 코드, 프로젝트 공통 확장 코드, 업무 코드 작성 예시를 함께 담는 기준 저장소로 이해한다.
- Gradle 루트 프로젝트명은 `spt-webfw1`, group은 `com.sptek`이다. 빌드 기준은 Java 17, Spring Boot 3.2.5, Gradle Wrapper 7.6.1이다. 로컬 JDK는 17 이상을 사용할 수 있지만 생성되는 코드는 Java 17과 호환되어야 한다.
- 주요 기술은 Spring MVC, Thymeleaf, Spring Security, JPA, MyBatis, H2/MySQL/MariaDB, Redis, SpringDoc, Lombok이다. 의존성은 Spring Boot 3.2.5 BOM과 현재 `build.gradle`의 버전 조합을 기준으로 판단한다.
- 애플리케이션 진입점은 `src/main/java/com/sptek/SptWfwApplication.java`이다. 이 클래스의 커스텀 `@Enable_*` 애노테이션들이 프레임워크 기능의 실제 활성화 스위치 역할을 하므로, 기능 동작을 분석할 때는 코드 구현체만 보지 말고 메인 클래스의 활성화 상태를 함께 확인한다.
- 현재 메인 클래스 기준으로 H2 datasource와 JPA hybrid 구성이 활성화되어 있고, MySQL replication 및 JNDI replication datasource는 비활성 예시로 남아 있다. 데이터소스 전환은 애노테이션, Gradle DB 의존성, 프로파일별 datasource 설정을 함께 바꾸는 작업으로 취급한다.
- 기본 활성 프로파일은 `local`이다. 프로파일은 `local`, `dev`, `stg`, `prd` 네 종류를 기준으로 하며, 각 프로파일 파일은 `spring.config.import`로 `_frameworkWebCoreResources`와 `_projectCommonResources` 아래의 기능별 설정을 조합해 로딩한다.
- `local` 실행 설정은 개발 편의를 위한 기준이다. 현재 로컬 서버 포트는 443이고, H2 file DB는 `infra/h2DB` 아래에 생성되며, graceful shutdown과 devtools 설정이 포함된다. 이 값들을 운영 기본값으로 일반화하지 않는다.
- 코드 계층은 크게 세 구역으로 나뉜다. `_frameworkWebCore`는 Base 프레임워크, `_projectCommon`은 프로젝트 공통 확장 지점, `projectName/domainName`은 실제 업무 패키지 작성 방식을 보여주는 자리표시자이다.
- 신규 업무 기능은 `_frameworkWebCore`나 `_example`에 넣지 않는다. 실제 프로젝트명과 도메인명을 반영한 `com.sptek.{project}.{domain}` 형태의 패키지를 만들고, 기존 예제의 controller/service/repository/dto/entity 분리 방식을 참고한다.
- 이 저장소는 REST API와 Thymeleaf View를 모두 지원한다. API 컨트롤러는 공통 응답·예외 래핑 애노테이션과 SpringDoc 문서화 관례를 확인하고, View 컨트롤러는 Thymeleaf 템플릿, layout/fragment, view 전역 예외 처리 관례를 함께 확인한다.
- 보안은 Spring Security, JWT, 세션·권한·역할 기반 구조, 프로젝트 공통 `SecurityFilterChainConfig`가 함께 구성한다. URL 접근 정책, Swagger/H2/static 예외, 로그인/에러 페이지 동작은 보안 matcher와 프레임워크 유틸의 공통 경로 정의를 함께 확인한다.
- 영속성은 JPA와 MyBatis가 공존한다. JPA entity/repository 기반 코드와 MyBatis 공통 DAO/XML mapper 기반 코드가 모두 있으므로, 신규·수정 작업에서는 해당 도메인 또는 예제의 기존 방식을 먼저 따르고 임의로 혼합하지 않는다.
- 리소스 구조는 프레임워크 기본 리소스(`_frameworkWebCoreResources`), 프로젝트 공통 리소스(`_projectCommonResources`), 일반 웹 리소스(`static`, `templates`)로 구분한다. 설정·메시지·mapper·템플릿을 추가할 때 이 경계를 유지한다.
- `_example`, `projectName/domainName`, `example1` 설정은 사용법을 보여주기 위한 기준 자료이다. 예제를 업무 코드로 옮길 때는 URL, 권한 완화, 테스트 데이터, 임시 주석, placeholder 이름을 실제 프로젝트 기준으로 정리한다.
- 로그는 Logback 설정과 로컬 `log/logback` 출력 구조를 기준으로 생성된다. 로그 파일은 분석 자료로 읽을 수 있지만 소스 변경이나 커밋 대상으로 취급하지 않는다.

## 코드와 리소스의 역할

- `http-client`: IntelliJ HTTP Client용 요청 예제와 환경 파일을 보관한다. API 요청/응답 방식이 바뀌면 공개 예제 `.http`와 `http-client.env.json`은 함께 갱신하되, `http-client.private.env.json`은 명시적 요청 없이 내용을 출력하거나 수정하지 않는다.
- `http-client/unit`: 프레임워크/업무 기능 단위 테스트용 HTTP 요청 예제를 보관한다.
- `infra`: 로컬 개발 보조 인프라 파일을 보관한다. 애플리케이션 코드 변경과 함께 무조건 실행하거나 수정하지 않는다.
- `infra/h2DB`: 로컬 H2 file DB 파일이 생성되는 위치다. DB 파일은 실행 중 잠금이 걸릴 수 있으며, 초기화·삭제·교체는 데이터 손실 가능성이 있으므로 명시적 요청 없이 수행하지 않는다.
- `infra/mysql-replication`: MySQL replication 로컬 테스트용 Docker Compose 등 인프라 구성을 보관한다. 명시적 요청과 영향 확인 없이 실행하지 않는다.
- `infra/wsl`: WSL 관련 로컬 실행 보조 파일을 보관한다.
- `log`: 로컬 실행 중 생성되는 애플리케이션 로그 출력 위치다. 소스 변경으로 취급하지 않으며, 필요 시 원인 분석을 위해 읽을 수는 있지만 불필요하게 커밋 대상에 포함하지 않는다.
- `log/logback`: Logback 설정에 따라 생성되는 서비스/에러/프레임워크 로그를 보관한다.
- `log/logback/service`: 일반 서비스 로그를 보관한다.
- `log/logback/error`: 에러 로그를 보관한다.
- `log/logback/_FW_LOG`: 프레임워크 시작 로그와 기능별 진단 로그를 보관한다.
- `src/main/java/com/sptek/_frameworkWebCore`: 애노테이션, 필터, 인터셉터, 예외·응답 처리, 보안, 데이터소스 등 기반 프레임워크 코드다. 명시적인 프레임워크 변경 요청이 없으면 수정하지 않는다.
- `src/main/java/com/sptek/_frameworkWebCore/_annotation`: 메인 클래스, 컨트롤러, 메소드, DTO 필드 등에 적용하는 프레임워크 활성화·조건부 등록용 커스텀 애노테이션을 정의한다.
- `src/main/java/com/sptek/_frameworkWebCore/_example`: 프레임워크 기능 사용 예제 코드다. 참고용이며 신규 업무 코드를 이 패키지에 추가하지 않는다.
- `src/main/java/com/sptek/_frameworkWebCore/_systemController`: 서버명, 프로젝트 정보, 헬스체크, RSA 공개키, 지원 언어, 파일 스트림, 인덱스, 로그인, 에러 페이지 등 기본 시스템 지원 API와 페이지를 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/actuator`: Actuator 정보 노출과 커스텀 헬스체크 지표를 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/argumentResolver`: 컨트롤러 파라미터 바인딩 확장을 위한 `HandlerMethodArgumentResolver` 설정을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/aspect`: API 공통 응답 처리처럼 횡단 관심사를 적용하는 Aspect 구현체를 둔다.
- `src/main/java/com/sptek/_frameworkWebCore/async`: 비동기 컨트롤러 처리, async executor, 비동기 반환값 처리를 위한 설정과 구현체를 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/base`: 공통 응답 DTO, 성공·실패 코드, 예외 타입·핸들러, 전역 상수, 메인/요청 매핑 애노테이션 레지스트리 등 프레임워크 기반 구조를 정의한다.
- `src/main/java/com/sptek/_frameworkWebCore/commonObject`: 프레임워크 전역에서 공유하는 DTO와 설정 프로퍼티 VO를 둔다.
- `src/main/java/com/sptek/_frameworkWebCore/controllerAdvice`: API XSS 처리, 뷰 공통 모델 속성, 인증 사용자 정보 주입 등 전역 ControllerAdvice를 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/datasource`: H2, MySQL replication, JNDI replication 등 데이터소스 구성 클래스를 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/DEPRECATED_applicationContextInitializer`: deprecated 된 ApplicationContext 초기화 코드다. 신규 코드에서 의존하지 않으며, 호환성 확인이 필요한 경우에만 참고한다.
- `src/main/java/com/sptek/_frameworkWebCore/encryption`: AES, DES, RSA, Jasypt 등 암복호화 모듈과 전역 암호화 지원 기능을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/event`: 프레임워크 이벤트, 이벤트 발행기, 컨텍스트/세션 생명주기 리스너를 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/external`: Swagger/OpenAPI, Thymeleaf dialect 등 외부 라이브러리 연동 설정을 둔다.
- `src/main/java/com/sptek/_frameworkWebCore/filter`: CORS, MDC, 요청 시각, 세션 제외, 요청·응답 상세 로그 등 Servlet Filter와 필터 등록 설정을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/globalConfigurer`: 정적 리소스 핸들러와 기본 뷰 컨트롤러 등 Spring MVC 전역 설정을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/hash`: 비밀번호 해시 등 해시 관련 Bean 설정을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/httpConnector`: 외부 HTTP 호출에 사용하는 HttpClient 설정을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/interceptor`: 로케일 변경, 중복 요청 방지, 뷰 에러 로그, 뷰 XSS, 방문 이력 등 MVC Interceptor와 등록 설정을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/logging`: Logback appender와 로그 필터 등 프레임워크 로그 확장 기능을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/message`: Locale, Jackson ObjectMapper, 메시지 컨버터, XSS 문자열 처리 설정을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/modelMapper`: ModelMapper Bean과 매핑 설정을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/multipart`: Multipart 요청 처리를 위한 resolver 설정을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/persistence`: JPA/MyBatis 설정과 MyBatis 공통 DAO 등 영속성 계층 공통 기능을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/schedule`: async, Hikari, HTTP connector, outbound 지원 상태를 모니터링하는 스케줄러와 executor 설정을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/springSecurity`: Spring Security 필터 체인, 인증 성공/실패 처리, 사용자 상세정보, 권한·역할·약관·사용자 엔티티/DTO/Repository 등 보안 기반 기능을 제공한다.
- `src/main/java/com/sptek/_frameworkWebCore/support`: outbound 호출, MyBatis result handler, 페이징, XSS escape 등 프레임워크 보조 지원 클래스를 제공한다. deprecated 경로는 신규 코드에서 의존하지 않는다.
- `src/main/java/com/sptek/_frameworkWebCore/util`: 인증, 쿠키, 예외, 파일, 로케일, 로그, 요청·응답, 보안, Spring 컨텍스트 등 공통 유틸리티를 제공한다.

- `src/main/java/com/sptek/_projectCommon`: 프로젝트 전역 공통 확장 지점이다. 여러 도메인에서 공유하는 보안 정책, 필터, 인터셉터, 이벤트, 스케줄러, 공통 DTO·코드만 둔다.
- `src/main/java/com/sptek/_projectCommon/argumentResolver`: 프로젝트 공통 컨트롤러 파라미터 바인딩 확장을 위한 ArgumentResolver 구현체를 둔다.
- `src/main/java/com/sptek/_projectCommon/aspect`: 프로젝트 공통 횡단 관심사를 처리하는 Aspect 예제 및 확장 구현체를 둔다.
- `src/main/java/com/sptek/_projectCommon/commonObject`: 프로젝트 공통 오류 코드, 보안 파일 경로 타입, 게시글·업로드 파일 등 여러 도메인에서 공유할 DTO와 코드를 둔다.
- `src/main/java/com/sptek/_projectCommon/controllerAdvice`: 프로젝트 공통 응답 후처리나 컨트롤러 전역 처리를 위한 ControllerAdvice 확장 구현체를 둔다.
- `src/main/java/com/sptek/_projectCommon/event`: 프로젝트 공통 커스텀 이벤트, 컨텍스트/세션 생명주기 리스너, 이벤트별 리스너 구현체를 둔다.
- `src/main/java/com/sptek/_projectCommon/filter`: 프로젝트 공통 Servlet Filter 구현체를 둔다.
- `src/main/java/com/sptek/_projectCommon/interceptor`: 프로젝트 공통 MVC Interceptor와 인터셉터 등록 설정을 둔다.
- `src/main/java/com/sptek/_projectCommon/logging`: 프로젝트 공통 Logback 필터 등 로그 확장 구현체를 둔다.
- `src/main/java/com/sptek/_projectCommon/schedule`: 프로젝트 공통 스케줄러와 스케줄러 executor 설정을 둔다.
- `src/main/java/com/sptek/_projectCommon/smartLifecydleComponents`: 애플리케이션 생명주기에 맞춰 시작·종료되어야 하는 프로젝트 공통 SmartLifecycle 컴포넌트를 둔다.
- `src/main/java/com/sptek/_projectCommon/springSecurity`: 프로젝트 경로별 인가 정책과 SecurityFilterChain 커스터마이징을 둔다.

- `src/main/java/com/sptek/projectName/domainName`: 업무 코드 작성 위치를 안내하는 자리표시자 패키지다. 실제 기능 개발 시에는 이 패키지명을 그대로 사용하지 말고 `com.sptek.cesco.sale.controller`처럼 실제 프로젝트명과 업무 도메인명으로 패키지를 새로 구성하고, 그 아래에서 controller/service/repository/dto/entity 계층을 작성한다.

- `src/main/resources`: 애플리케이션 설정, 정적 자원, Thymeleaf 템플릿, SQL 초기화, 프레임워크/프로젝트 공통 리소스를 보관하는 리소스 루트다.
- `src/main/resources/application.yml`: 전체 공통 기본 설정과 기본 활성 프로파일을 관리한다.
- `src/main/resources/application-local.yml`: 로컬 실행용 설정과 로컬 전용 `spring.config.import` 구성을 관리한다.
- `src/main/resources/application-dev.yml`: 개발 환경용 설정과 개발 환경 전용 `spring.config.import` 구성을 관리한다.
- `src/main/resources/application-stg.yml`: 스테이징 환경용 설정과 스테이징 환경 전용 `spring.config.import` 구성을 관리한다.
- `src/main/resources/application-prd.yml`: 운영 환경용 설정과 운영 환경 전용 `spring.config.import` 구성을 관리한다. 실제 운영값이나 비밀값을 추측해 채우지 않는다.
- `src/main/resources/META-INF`: Spring Boot 자동 설정 및 초기화 확장을 위한 메타 정보를 보관한다.
- `src/main/resources/static`: CSS, JavaScript, 이미지, favicon 등 정적 웹 자원을 보관한다. 외부 라이브러리의 minified 파일은 직접 수정하지 않는다.
- `src/main/resources/templates`: Thymeleaf 레이아웃, 공통 fragment, 에러 페이지, 시스템/예제/업무 화면 템플릿을 보관한다.
- `src/main/resources/_autoSqlInitialize`: 로컬 H2 등 초기 DB 스키마와 시드 데이터 SQL 및 SQL init 설정을 보관한다. 반복 실행 가능한 형태로 작성하고 데이터 손실 가능 SQL은 주의한다.
- `src/main/resources/_frameworkWebCoreResources`: 프레임워크 기본 설정, logback 설정, SSL keystore, 프레임워크 기능별 프로파일 설정을 보관한다. 프레임워크 설정 변경 요청이 없으면 보존한다.
- `src/main/resources/_frameworkWebCoreResources/_frameworkApplicationProperties`: actuator, error, H2, pageHelper, SSL, Thymeleaf 등 프레임워크 기능별 설정을 프로파일별로 보관한다.
- `src/main/resources/_frameworkWebCoreResources/logbackConfig`: 로컬/서버용 Logback 설정을 보관한다. 로그 정책 변경 시 이 경로와 프로파일 import를 함께 확인한다.
- `src/main/resources/_frameworkWebCoreResources/keystore`: 로컬 HTTPS 등에 사용하는 keystore 파일을 보관한다. 명시적 요청 없이 내용을 출력하거나 교체하지 않는다.
- `src/main/resources/_projectCommonResources`: 프로젝트 공통 설정, i18n 메시지, MyBatis 설정과 mapper를 보관한다.
- `src/main/resources/_projectCommonResources/_projectApplicationProperties`: 프로젝트 공통 및 확장 기능 설정을 프로파일별로 보관한다. 설정 키를 추가할 때 local/dev/stg/prd 구조를 함께 검토한다.
- `src/main/resources/_projectCommonResources/i18n`: 다국어 메시지 번들을 보관한다. 문구 변경 시 지원 언어 파일의 동기화 여부를 함께 확인한다.
- `src/main/resources/_projectCommonResources/mybatis`: MyBatis 공통 설정과 mapper XML을 보관한다. 신규 업무 mapper는 실제 도메인명 기준 하위 경로에 둔다.

## 작업 실행 흐름

AI 에이전트는 작업을 시작할 때 다음 순서로 판단한다. 이 섹션은 “무엇을 먼저 확인할지”를 정한다.

1. `git status --short`로 사용자의 기존 변경을 확인하고, 요청과 무관한 변경은 보존한다.
2. Windows에서 PowerShell 명령을 실행할 때는 가능하면 PowerShell 7 이상인 `pwsh`를 우선 사용한다. 한글 주석이나 문서를 읽을 때는 `Get-Content -Encoding UTF8`처럼 UTF-8을 명시한다.
3. 요청 대상을 식별한다: 클래스, 메서드, URL, 애노테이션, 설정 키, 템플릿, SQL, 로그 라인, 테스트, HTTP 예제.
4. `rg`로 정의, 호출자, 설정, 테스트, 예제를 먼저 찾는다. 클래스명이나 기능명은 기억에 의존하지 말고 현재 파일을 다시 읽는다.
5. 프레임워크 기능과 연결되면 필요한 연결 지점만 추가로 추적한다.
   - `SptWfwApplication`의 활성화 `@Enable_*` 애노테이션
   - 커스텀 `@Enable_*` 애노테이션과 조건·등록 코드
   - `_frameworkWebCoreResources/_frameworkApplicationProperties`
   - `_projectCommonResources/_projectApplicationProperties`
   - `_projectCommon` 확장 구현체와 `_example` 사용 예제
6. 보안 경로, API URL, 공통 응답, 필터, 인터셉터, 데이터소스, 프로파일 동작처럼 실행 동작이 바뀌는 작업은 영향 범위를 먼저 설명하고 필요한 경우 사용자 확인을 받는다.
7. 요구가 불명확해도 기존 구조에서 안전하게 결정할 수 있는 범위는 진행한다. 데이터 모델, 인증 정책, 외부 연동 방식처럼 결과가 크게 달라지는 선택만 사용자에게 확인한다.

## 변경 안전 기준

이 섹션은 “무엇을 하지 말아야 하는지”와 “변경 범위를 어디까지로 제한할지”를 정한다.

- 사용자 변경을 되돌리거나 덮어쓰지 않는다. `git reset --hard`, 무단 파일 삭제, 광범위한 자동 포맷을 수행하지 않는다.
- 변경 범위를 작게 유지한다. 요청과 무관한 리팩터링, 포맷 정리, 의존성 업그레이드를 섞지 않는다.
- 생성 파일(`build/`, `.gradle/`, IDE 메타데이터, `.codex/run/`, `log/`, `infra/h2DB/*.db`)을 소스 변경으로 취급하지 않는다. 단, `.codex/skills/`의 repo-local 공통 스킬은 커밋 대상이다.
- DB 초기화 SQL, `infra/h2DB`, `infra/mysql-replication`은 데이터 손실 가능성이 있으므로 명시적 요청과 영향 확인 없이 실행하거나 수정하지 않는다.
- 보안 설정의 테스트 편의용 CSRF/CORS 완화 TODO를 다른 경로로 확대하지 않으며, 운영 보안 정책으로 간주하지 않는다.
- 외부 라이브러리를 추가하기 전에 Spring 또는 현재 의존성으로 해결 가능한지 확인한다. 추가가 필요하면 목적과 버전 호환성을 설명하고 최소 범위로 반영한다.
- 공개 API 요청/응답 방식을 바꾸면 컨트롤러, DTO validation, 공통 응답 래핑, 보안 matcher, HTTP 예제, 관련 문서를 함께 검토한다.

## 구현 결정 기준

이 섹션은 “어떤 형식의 코드를 생성할지”를 정한다. 새 코드는 가장 먼저 주변 Base 코드와 예제를 따른다.

- 기존 코드의 생성자 주입 방식과 Lombok 관례(`@RequiredArgsConstructor`, `@Slf4j`)를 우선한다. 필드 주입을 새로 추가하지 않는다.
- 신규 Java 타입은 표준 PascalCase, 메서드·필드는 camelCase를 사용한다. 기존의 비표준 이름은 별도 요청 없이 대규모로 변경하지 않는다.
- deprecated 또는 `DEPRECATED_`, `DPRECATED_`, `deplicated` 경로의 코드는 호환성 참고용이다. 신규 코드에서 의존하지 않는다.
- 동일 기능을 새로 만들기 전에 기존 Base 코드의 확장 지점, 커스텀 애노테이션, 필터, 인터셉터, Aspect, ControllerAdvice, ArgumentResolver를 먼저 확인한다.
- Base 코드 변경은 실행 동작 변경인지, 문서/주석/예제 보정인지 구분한다. 공통 API, 응답 형식, 보안 정책, 필터·인터셉터 순서, 데이터소스·프로파일 동작 변경은 더 보수적으로 다룬다.
- 기존 코드를 답변에 인용할 때는 필요한 부분만 제시하고 import 및 무관한 주석은 생략한다.

## 계층별 구현 관례

- API 컨트롤러는 기존 공통 응답과 예외 정책이 필요한지 먼저 판단한다. 필요하면 `@Enable_ResponseOfApiCommonSuccess_At_RestController`, `@Enable_ResponseOfApiGlobalException_At_RestController` 관례를 따른다.
- View 컨트롤러는 Thymeleaf 템플릿, layout/fragment, view 전역 예외 처리 관례를 함께 확인한다.
- API 컨트롤러와 View 컨트롤러는 기존 예제 구조를 참고해 필요하면 `controller/api`, `controller/view`처럼 역할별로 분리한다.
- 비즈니스 규칙 실패는 기존 `ServiceException`과 프로젝트 오류 코드 체계를 우선 사용한다. 임의의 응답 포맷이나 중복 전역 예외 처리기를 만들지 않는다.
- 보안, XSS, CORS, 암호화, 비동기, 중복 요청 방지는 기존 필터·인터셉터·커스텀 애노테이션을 우선 재사용한다. 동일 기능을 별도 구현하기 전에 기존 동작 순서와 적용 조건을 확인한다.
- JPA와 MyBatis를 임의로 혼합하지 않는다. 해당 도메인이나 예제가 따르는 영속화 방식을 우선하고, 트랜잭션 경계는 서비스 계층에 명시한다.
- MyBatis mapper는 실제 도메인명 기준 하위 경로에 둔다. `projectName/domainName` 또는 `_example`의 placeholder 이름을 신규 업무 코드에 그대로 가져오지 않는다.
- `_example` 코드를 복사할 때 예제 URL, 테스트용 권한 완화, 주석 처리된 임시 설정을 운영 코드로 가져오지 않는다.

## 설정과 프로파일

- 공통 기본값은 `application.yml`, 환경별 값과 import는 `application-{profile}.yml`에서 관리한다.
- 프로파일별 설정을 추가하거나 키 구조를 바꿀 때 `local/dev/stg/prd` 파일을 모두 검토한다. 값이 환경마다 달라야 하면 구조만 동기화하고 실제 운영 값을 추측하지 않는다.
- `spring.config.import`는 프로파일별 리소스를 불러오는 현재의 `optional:classpath:` 패턴을 유지한다. 파일 이동이나 이름 변경 시 모든 import를 함께 확인한다.
- 데이터소스 전환은 `SptWfwApplication`의 `@Enable_DatasourceOf*` 선택, Gradle DB 의존성, 해당 프로파일 설정을 한 묶음으로 검토한다.
- 신규 비밀값, 토큰, 실제 비밀번호, 개인 경로를 커밋하지 않는다. 환경 변수나 별도 비공개 설정으로 주입한다.
- `http-client/http-client.private.env.json`과 `src/main/resources/_frameworkWebCoreResources/keystore/keystore.p12`는 Git에 추적 중인 민감 가능 파일이다. 명시적 요청 없이 열람 내용을 출력하거나 수정·교체하지 않는다.
- 암호화된 값은 복호화하거나 평문으로 치환하지 않는다. `prd`와 `stg` 설정은 명시적 요청 없이 실제 값으로 채우지 않는다.

## 검증과 보고

- 코드 변경을 완료하기 전에는 변경 유형에 맞는 검증을 수행한다. 상세 명령과 판단 기준은 `.AI/workflows/common/change-verification.md`를 따른다.
- 커밋 메시지 제안이나 완료 보고를 요청받거나 작업을 마무리할 때는 `.AI/workflows/common/commit-reporting.md`를 따른다.
- 검증을 실행하지 못했거나 실패한 경우에는 실행한 명령, 핵심 오류, 미검증 범위를 완료 보고에 명확히 기록한다.
