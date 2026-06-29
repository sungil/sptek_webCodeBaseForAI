---
name: spt-code-analysis
description: SPT Framework Web Core 저장소 코드를 일관된 방식으로 분석합니다. 컨트롤러, 서비스, 커스텀 @Enable_* 애노테이션, 필터, 인터셉터, 보안 matcher, 프로파일 설정, 리소스, 요청/응답 동작, 서버 시작 오류를 조사하거나 AGENT.md와 Base 코드 관례를 기준으로 코드를 설명할 때 사용합니다.
---

# SPT 코드 분석

SPT Framework Web Core 저장소의 소스 코드를 분석할 때 이 스킬을 사용한다.

## 핵심 규칙

1. 먼저 `AGENT.md`를 읽고 Base 코드 우선 원칙을 따른다.
2. `git status --short`로 현재 staged/unstaged 변경을 확인한다. 기본 설명은 현재 작업트리 파일 기준으로 하되, 사용자가 원본·staged diff·변경 전후 비교를 요청하면 그 기준을 명확히 구분한다.
3. 클래스, URL, 애노테이션, 프로퍼티, 호출자는 `rg`로 찾는다. 저장소 사실관계는 기억에 의존하지 않는다.
4. 결론은 코드 근거가 있는 사실, 명시적인 추론, 일반적인 권고를 구분한다.

## 분석 절차

1. 사용자가 언급한 표면을 먼저 식별한다: URL, 클래스, 메소드, 애노테이션, 프로파일 키, 템플릿, SQL, 로그 라인.
2. 1차 탐색은 `rg`로 직접 정의와 호출자를 찾는다. Java 심볼 관계, 오버로드, AOP/프록시, Bean 등록, 프로파일 조건처럼 문자열 검색만으로 부족한 경우에는 IDE/LSP, Gradle 컴파일, 로그, 설정 파일을 함께 확인한다.
3. 관련성이 높은 최소 파일을 UTF-8 인코딩으로 연다.
4. 결론을 내리기 전에 프레임워크 연결 지점을 추적한다.
   - `SptWfwApplication`의 활성화 애노테이션
   - 커스텀 `@Enable_*` 애노테이션과 조건
   - `_frameworkWebCoreResources/_frameworkApplicationProperties`
   - `_projectCommonResources/_projectApplicationProperties`
   - `_projectCommon` 확장 구현체
   - `_example` 사용 예제
5. 답변을 뒷받침하는 구체적인 파일과 라인 근거를 제시한다.

## 컨트롤러와 요청 분석

컨트롤러, 엔드포인트, 뷰를 분석할 때는 다음을 확인한다.

1. request mapping, HTTP method, produces media type을 확인한다.
2. 컨트롤러와 메소드의 `@Enable_*` 애노테이션을 확인한다.
3. API 공통 성공/에러 응답 래핑 여부를 확인한다.
4. `SecurityFilterChainConfig`와 프레임워크 보안 matcher 순서를 확인한다.
5. DTO validation과 binding 동작을 확인한다.
6. 엔드포인트가 view를 반환하면 관련 Thymeleaf 템플릿과 정적 자원을 확인한다.
7. API 계약이 바뀌면 `http-client` 예제를 확인한다.

## 설정 분석

설정이나 서버 시작 동작을 분석할 때는 다음을 확인한다.

1. `application.yml`과 관련된 모든 `application-{profile}.yml` 파일을 읽는다.
2. `spring.config.import`를 따라 `_frameworkWebCoreResources`와 `_projectCommonResources`를 확인한다.
3. 프로파일별 변경은 `local`, `dev`, `stg`, `prd` 구조를 함께 검토한다.
4. 운영이나 스테이징 비밀값을 추측하지 않는다.
5. 데이터소스 변경은 `SptWfwApplication`, Gradle DB 의존성, datasource 프로파일 설정을 함께 확인한다.

## 보안과 민감 파일

1. `http-client/http-client.private.env.json`의 내용을 출력하지 않는다.
2. `src/main/resources/_frameworkWebCoreResources/keystore/keystore.p12`의 내용을 출력하거나 교체하지 않는다.
3. `infra/h2DB`, DB 초기화 SQL, `infra/mysql-replication`은 데이터 손실 가능성이 있는 대상으로 취급한다.

## 답변 방식

답변은 간결하게 작성하고, 저장소 코드에 의존하는 결론은 파일 근거를 제시한다. 근거가 부족하면 확인한 내용과 아직 검증하지 못한 범위를 구분해서 밝힌다.
