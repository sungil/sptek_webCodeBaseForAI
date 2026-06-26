# AGENTS.md

이 문서는 이 저장소에서 Codex를 포함한 AI 에이전트가 안전하고 일관되게 작업하기 위한 기본 지침이다. 저장소 루트와 모든 하위 경로에 적용한다. 하위 디렉터리에 더 구체적인 `AGENTS.md`가 생기면 해당 범위에서는 하위 문서가 우선한다.

## 1. 프로젝트 기준

- 이 저장소는 SPT Framework Web Core 기반의 단일 모듈 Spring Boot 웹 애플리케이션이다.
- 빌드 기준은 Java 17, Spring Boot 3.2.5, Gradle Wrapper 7.6.1이다. 로컬 JDK는 17 이상을 사용할 수 있지만 생성되는 코드는 Java 17과 호환되어야 한다.
- 주요 기술은 Spring MVC, Thymeleaf, Spring Security, JPA, MyBatis, H2/MySQL/MariaDB, Redis, SpringDoc, Lombok이다.
- 기본 활성 프로파일은 `local`이다. 프로파일은 `local`, `dev`, `stg`, `prd` 네 종류를 기준으로 한다.
- 프로젝트 파일을 근거로 답변할 때는 첫 문장에서 `SPT Framework 를 기준으로한 설명입니다`라고 밝힌다. 저장소에서 근거를 찾을 수 없어 일반론으로 답할 때는 `일반적인 사항에 대한 설명 입니다`라고 구분한다.

## 2. 코드와 리소스의 역할

- `src/main/java/com/sptek/_frameworkWebCore`: 애노테이션, 필터, 인터셉터, 예외·응답 처리, 보안, 데이터소스 등 기반 프레임워크 코드다. 명시적인 프레임워크 변경 요청이 없으면 수정하지 않는다.
- `src/main/java/com/sptek/_frameworkWebCore/_example`: 프레임워크 기능의 사용 예제다. 참고 자료로만 사용하고 신규 업무 코드를 이 패키지에 추가하지 않는다.
- `src/main/java/com/sptek/_projectCommon`: 프로젝트 전역 공통 확장 지점이다. 공통 보안 정책, 필터, 인터셉터, 이벤트, 스케줄러처럼 여러 도메인에서 공유하는 코드만 둔다.
- `src/main/java/com/sptek/projectName/domainName`: 현재 업무 코드용 자리표시자 패키지다. 실제 기능을 구현할 때는 요구된 프로젝트·도메인 이름으로 패키지를 구체화하고 계층을 도메인 안에 둔다.
- `src/main/resources/_frameworkWebCoreResources`: 프레임워크 설정, 로그백, keystore가 위치한다. 프레임워크 설정 변경 요청이 없으면 보존한다.
- `src/main/resources/_projectCommonResources`: 프로젝트 공통 설정, i18n, MyBatis 설정이 위치한다.
- `src/main/resources/templates/thymeleaf`와 `static`: 서버 렌더링 뷰와 정적 자원이다. 외부 라이브러리의 minified 파일은 직접 편집하지 않는다.
- `infra`: 로컬 인프라 보조 파일이다. 애플리케이션 코드 변경과 함께 무조건 실행하거나 수정하지 않는다.
- `http-client`: IntelliJ HTTP 요청 예제와 환경 파일이다. API 변경 시 관련 공개 예제는 갱신하되 private 환경 파일은 보호한다.

## 3. 작업 시작 전 확인 절차

1. `git status --short`로 사용자의 기존 변경을 확인하고 관련 없는 변경을 보존한다.
2. 요청과 직접 관련된 클래스, 호출자, 설정, 테스트 또는 예제를 `rg`로 찾는다. 클래스명이나 프레임워크 기능에 관해 답할 때 기억에 의존하지 말고 현재 파일을 다시 읽는다.
3. 프레임워크 기능을 사용하는 작업은 다음 연결 지점을 함께 확인한다.
   - 커스텀 `@Enable_*` 애노테이션과 그 조건·등록 코드
   - `SptWfwApplication`의 활성화 애노테이션
   - `_frameworkWebCoreResources/_frameworkApplicationProperties`
   - `_projectCommonResources/_projectApplicationProperties`
   - `_projectCommon`의 실제 확장 예제
4. 보안 경로나 API URL을 바꿀 때 `_projectCommon/springSecurity/SecurityFilterChainConfig.java`와 프레임워크 필터 체인의 matcher 및 순서를 확인한다.
5. 로그백 질문·변경은 `_frameworkWebCoreResources/logbackConfig`, 다국어 작업은 `_projectCommonResources/i18n`을 함께 확인한다.
6. 요구가 불명확하더라도 기존 구조에서 안전하게 결정할 수 있는 범위는 진행한다. 데이터 모델, 인증 정책, 외부 계약처럼 결과가 크게 달라지는 선택만 사용자에게 확인한다.

## 4. 구현 규칙

- 변경 범위를 작게 유지한다. 요청과 무관한 리팩터링, 포맷 정리, 의존성 업그레이드를 섞지 않는다.
- 기존 코드의 생성자 주입 방식과 Lombok 관례(`@RequiredArgsConstructor`, `@Slf4j`)를 우선한다. 필드 주입을 새로 추가하지 않는다.
- 신규 Java 타입은 표준 PascalCase, 메서드·필드는 camelCase를 사용한다. 기존의 비표준 이름은 별도 요청 없이 대규모로 변경하지 않는다.
- API 컨트롤러는 기존 공통 응답과 예외 정책이 필요한지 먼저 판단하고, 필요하면 다음 애노테이션 관례를 따른다.
  - `@Enable_ResponseOfApiCommonSuccess_At_RestController`
  - `@Enable_ResponseOfApiGlobalException_At_RestController`
- 비즈니스 규칙 실패는 기존 `ServiceException`과 프로젝트 오류 코드 체계를 우선 사용한다. 임의의 응답 포맷이나 중복 전역 예외 처리기를 만들지 않는다.
- 보안, XSS, CORS, 암호화, 비동기, 중복 요청 방지는 기존 필터·인터셉터·커스텀 애노테이션을 우선 재사용한다. 동일 기능을 별도 구현하기 전에 기존 동작 순서와 적용 조건을 확인한다.
- JPA와 MyBatis가 함께 구성되어 있다. 영속화 방식을 임의로 혼합하지 말고 해당 도메인의 기존 방식을 따른다. 트랜잭션 경계는 서비스 계층에 명시한다.
- deprecated 또는 `DEPRECATED_`, `DPRECATED_`, `deplicated` 경로의 코드는 호환성 참고용이다. 신규 코드에서 의존하지 않는다.
- `_example` 코드를 복사할 때 예제 URL, 테스트용 권한 완화, 주석 처리된 임시 설정을 운영 코드로 가져오지 않는다.
- 기존 코드를 답변에 인용할 때는 필요한 부분만 제시하고 import 및 무관한 주석은 생략한다.

## 5. 설정과 프로파일

- 공통 기본값은 `application.yml`, 환경별 값과 import는 `application-{profile}.yml`에서 관리한다.
- 프로파일별 설정을 추가하거나 키 구조를 바꿀 때 `local/dev/stg/prd` 파일을 모두 검토한다. 값이 환경마다 달라야 하면 구조만 동기화하고 실제 운영 값을 추측하지 않는다.
- `spring.config.import`는 프로파일별 리소스를 불러오는 현재의 `optional:classpath:` 패턴을 유지한다. 파일 이동이나 이름 변경 시 모든 import를 함께 확인한다.
- 데이터소스 전환은 `SptWfwApplication`의 `@Enable_DatasourceOf*` 선택, Gradle DB 의존성, 해당 프로파일 설정을 한 묶음으로 검토한다.
- 신규 비밀값, 토큰, 실제 비밀번호, 개인 경로를 커밋하지 않는다. 환경 변수나 별도 비공개 설정으로 주입한다.
- `http-client/http-client.private.env.json`과 `src/main/resources/_frameworkWebCoreResources/keystore/keystore.p12`는 Git에 추적 중인 민감 가능 파일이다. 명시적 요청 없이 열람 내용을 출력하거나 수정·교체하지 않는다.
- 암호화된 값은 복호화하거나 평문으로 치환하지 않는다. `prd`와 `stg` 설정은 명시적 요청 없이 실제 값으로 채우지 않는다.

## 6. 테스트와 검증

Gradle wrapper에 실행 비트가 없으므로 Unix 계열에서는 `bash ./gradlew`를 사용한다.

```bash
# 빠른 컴파일 확인
bash ./gradlew compileJava

# 전체 자동 테스트와 빌드
bash ./gradlew clean test
bash ./gradlew build

# 로컬 프로파일 실행이 필요한 경우
bash ./gradlew bootRun --args='--spring.profiles.active=local'
```

- 현재 `src/test` 테스트 소스가 없다. 신규 동작이나 버그 수정에는 가능하면 `src/test/java`에 회귀 테스트를 추가한다.
- 단위 테스트는 외부 MySQL, Redis, 네트워크에 의존하지 않도록 한다. 통합 테스트가 필요하면 H2, mock 또는 명시적인 테스트 프로파일을 사용한다.
- Java 변경은 최소 `compileJava`, 로직 변경은 관련 테스트와 `test`, 의존성·설정·패키징 변경은 `build`까지 수행한다.
- 보안 필터, 데이터소스, 프로파일 import, 서버 시작 과정 변경은 가능하면 `local` 프로파일 기동도 확인한다. 장시간 실행 프로세스는 시작 성공을 확인한 뒤 종료한다.
- 검증 실패가 기존 환경, 네트워크, 비밀 설정 부재 때문이면 이를 숨기지 말고 실행한 명령, 핵심 오류, 미검증 범위를 완료 보고에 기록한다.

## 7. 안전한 작업 원칙

- 사용자 변경을 되돌리거나 덮어쓰지 않는다. `git reset --hard`, 무단 파일 삭제, 광범위한 자동 포맷을 수행하지 않는다.
- 생성 파일(`build/`, `.gradle/`, IDE 메타데이터)을 소스 변경으로 취급하지 않는다.
- DB 초기화 SQL과 `infra/mysql-replication`은 데이터 손실 가능성이 있다. 명시적 요청과 영향 확인 없이 실행하지 않는다.
- 보안 설정에 현재 테스트 편의를 위한 CSRF/CORS 완화 TODO가 존재한다. 이를 다른 경로로 확대하지 않으며, 운영 보안 정책으로 간주하지 않는다.
- 외부 라이브러리를 추가하기 전에 Spring 또는 현재 의존성으로 해결 가능한지 확인한다. 추가가 필요하면 목적과 버전 호환성을 설명하고 최소 범위로 반영한다.
- 공개 API 계약을 바꾸면 컨트롤러, DTO validation, 공통 응답 래핑, 보안 matcher, HTTP 예제, 관련 문서를 함께 검토한다.

## 8. 완료 보고

- 먼저 구현 결과를 짧게 설명하고, 변경한 파일과 핵심 설계 결정을 밝힌다.
- 실행한 검증 명령과 결과를 정확히 기록한다. 실행하지 못한 검증은 이유와 함께 구분한다.
- 남은 위험, TODO, 사용자 결정이 필요한 사항이 있을 때만 후속 항목으로 제시한다.
- 프로젝트 근거와 일반적인 권고를 섞지 말고 어느 쪽인지 명확히 구분한다.
