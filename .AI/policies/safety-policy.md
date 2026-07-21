# Safety Policy

이 문서는 사용자 변경 보존, 민감 정보, 로컬 데이터, 생성 파일 처리 기준을 정의한다.

## 사용자 변경 보존

- 사용자 변경을 되돌리거나 덮어쓰지 않는다.
- `git reset --hard`, 무단 파일 삭제, 광범위한 자동 포맷은 수행하지 않는다.
- 변경 중 내가 만들지 않은 수정이 보이면 사용자 변경으로 간주하고, 요청과 무관하면 그대로 둔다.
- staged 상태는 사용자가 명시적으로 요청하지 않으면 임의로 변경하지 않는다.

## 소스 변경으로 보지 않는 항목

- `build/`, `.gradle/`, IDE 메타데이터
- `.codex/run/`, `.codex/config.toml`
- `.AI/assets/.cache/`
- `log/`
- `infra/h2DB/*.db`

단, `.codex/skills/`의 repo-local adapter는 팀 공통 AI 작업 규칙으로 취급하므로 커밋 대상이다.

## 민감 파일과 값

- 신규 비밀값, 토큰, 실제 비밀번호, 개인 경로를 커밋하지 않는다.
- `http-client/http-client.private.env.json`과 `src/main/resources/**/keystore/*.p12`는 명시적 요청 없이 내용을 출력하거나 수정/교체하지 않는다.
- 암호화된 값은 복호화하거나 평문으로 치환하지 않는다.
- `prd`와 `stg` 설정은 명시적 요청 없이 실제 값으로 채우지 않는다.

## DB와 인프라

- DB 초기화 SQL, `infra/h2DB`, `infra/mysql-replication`은 데이터 손실 가능성이 있으므로 명시적 요청과 영향 확인 없이 실행하거나 수정하지 않는다.
- H2 file DB는 새 DB 파일이 생기지 않도록 본체 파일 존재 여부를 먼저 확인한다.
- DB 조회는 읽기 전용 절차 `.AI/procedures/tasks/readonly-database-query.md`를 따른다.

## 로그

- `log`와 `log/logback`은 로컬 실행 산출물이다. 원인 분석을 위해 읽을 수 있지만 소스 변경이나 커밋 대상으로 취급하지 않는다.
