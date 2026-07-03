# Repository Map

이 문서는 루트 디렉터리와 주요 경로의 역할을 요약한다.

## 루트 경로

- `src/main/java`: 애플리케이션 Java 코드
- `src/main/resources`: 설정, 정적 자원, Thymeleaf 템플릿, SQL 초기화, 공통 리소스
- `http-client`: IntelliJ HTTP Client용 공개 요청 예제와 환경 파일
- `http-client/unit`: 프레임워크/업무 기능 단위 테스트용 HTTP 요청 예제
- `infra`: 로컬 개발 보조 인프라
- `log`: 로컬 실행 중 생성되는 애플리케이션 로그
- `.AI`: 제품 중립 AI 작업 기준
- `.codex`, `.github`, `.claude`: 도구별 adapter

## 주의 경로

- `http-client/http-client.private.env.json`: 명시적 요청 없이 출력하거나 수정하지 않는다.
- `infra/h2DB`: 로컬 H2 file DB 생성 위치다. 초기화, 삭제, 교체는 데이터 손실 가능성이 있으므로 명시적 요청 없이 하지 않는다.
- `infra/mysql-replication`: MySQL replication 로컬 테스트용 Docker Compose 등을 보관한다. 명시적 요청과 영향 확인 없이 실행하지 않는다.
- `log/logback`: Logback 설정에 따라 생성되는 서비스/에러/프레임워크 로그다. 분석 자료로 읽을 수 있으나 커밋 대상으로 취급하지 않는다.

## Java 주요 경로

- `src/main/java/com/sptek/SptWfwApplication.java`: 애플리케이션 진입점과 프레임워크 기능 활성화 기준
- `src/main/java/com/sptek/_frameworkWebCore`: Base 프레임워크 코드
- `src/main/java/com/sptek/_projectCommon`: 프로젝트 공통 확장 코드
- `src/main/java/com/sptek/projectName/domainName`: 업무 코드 작성 위치 예시

## 리소스 주요 경로

- `src/main/resources/application.yml`: 전체 공통 기본 설정과 기본 활성 profile
- `src/main/resources/application-{profile}.yml`: 환경별 설정과 `spring.config.import`
- `src/main/resources/_frameworkWebCoreResources`: 프레임워크 기본 설정과 기능별 profile 설정
- `src/main/resources/_projectCommonResources`: 프로젝트 공통 설정, i18n, MyBatis 설정과 mapper
- `src/main/resources/templates`: Thymeleaf 레이아웃, fragment, 시스템/예제/업무 화면 템플릿
- `src/main/resources/static`: CSS, JavaScript, 이미지, favicon 등 정적 자원
- `src/main/resources/_autoSqlInitialize`: 로컬 H2 등 초기 DB 스키마와 seed SQL
