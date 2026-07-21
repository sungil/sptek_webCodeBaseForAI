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

