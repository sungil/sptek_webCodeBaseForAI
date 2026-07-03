# Resource Map

이 문서는 `src/main/resources` 하위 리소스 구조와 profile 설정 기준을 요약한다.

## Profile 설정

- `application.yml`: 전체 공통 기본 설정과 기본 활성 profile
- `application-local.yml`: 로컬 실행용 설정과 local 전용 import
- `application-dev.yml`: 개발 환경용 설정과 dev 전용 import
- `application-stg.yml`: 스테이징 환경용 설정과 stg 전용 import
- `application-prd.yml`: 운영 환경용 설정과 prd 전용 import

프로파일은 `local`, `dev`, `stg`, `prd` 네 종류를 기준으로 한다. `spring.config.import`는 현재의 `optional:classpath:` 패턴을 유지한다.

## 리소스 경계

- `_frameworkWebCoreResources`: 프레임워크 기본 설정, logback 설정, SSL keystore, 프레임워크 기능별 profile 설정
- `_frameworkWebCoreResources/_frameworkApplicationProperties`: actuator, error, H2, pageHelper, SSL, Thymeleaf 등 프레임워크 설정
- `_frameworkWebCoreResources/logbackConfig`: local/server용 Logback 설정
- `_frameworkWebCoreResources/keystore`: 로컬 HTTPS 등에 사용하는 keystore. 명시적 요청 없이 출력하거나 교체하지 않는다.
- `_projectCommonResources`: 프로젝트 공통 설정, i18n 메시지, MyBatis 설정과 mapper
- `_projectCommonResources/_projectApplicationProperties`: 프로젝트 공통 및 확장 기능 설정
- `_projectCommonResources/i18n`: 다국어 메시지 번들. 문구 변경 시 지원 언어 파일 동기화를 검토한다.
- `_projectCommonResources/mybatis`: MyBatis 공통 설정과 mapper XML
- `_autoSqlInitialize`: 로컬 H2 등 초기 DB 스키마와 seed SQL
- `templates`: Thymeleaf layout, fragment, error page, system/example/business 화면
- `static`: CSS, JavaScript, 이미지, favicon 등 정적 웹 자원. 외부 라이브러리 minified 파일은 직접 수정하지 않는다.
- `META-INF`: Spring Boot 자동 설정 및 초기화 확장 메타 정보

## 설정 변경 기준

- profile별 설정 키를 추가하거나 구조를 바꿀 때 `local/dev/stg/prd` 파일을 모두 검토한다.
- 값이 환경마다 달라야 하면 구조만 동기화하고 실제 운영 값을 추측하지 않는다.
- datasource 전환은 `SptWfwApplication`의 `@Enable_DatasourceOf*`, Gradle DB 의존성, 해당 profile 설정을 한 묶음으로 본다.
- 신규 업무 MyBatis mapper는 실제 도메인명 기준 하위 경로에 둔다.
