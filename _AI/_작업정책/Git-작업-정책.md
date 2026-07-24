## Git 작업 기본 정책

- 사용자 변경을 임의로 되돌리거나 덮어쓰지 않는다.
- git reset --hard, 무단 파일 삭제, 광범위한 자동 포맷을 수행하지 않는다.
- 작업 중 내가 생성하지 않은 수정이 발견되면 사용자 변경으로 간주한다.
- 요청과 무관한 기존 변경은 수정하거나 정리하지 않고 그대로 보존한다.
- staged 상태는 사용자가 명시적으로 요청하지 않는 한 임의로 변경하지 않는다.


## Commit 기본 정책

- 디렉터리나 파일의 위치 또는 이름 변경이 있는 경우 변경 전 경로와 파일명을 참조하는 문서, 코드, 설정, 스크립트를 검색하고 변경된 경로와 이름에 맞게 함께 수정한다.


## 다음 항목은 일반적으로 소스 변경이나 Commit 대상으로 취급하지 않는다.

- build/, .gradle/
- IDE 메타데이터
- .codex/run/, .codex/config.toml
- _AI/_ai-generated/_cache/
- log/
- local-dev-support/h2DB/*.db
- log/와 log/logback/은 원인 분석을 위해 읽을 수 있지만 수정하거나 커밋 대상으로 취급하지 않는다.
- 단, {AI별 디렉토리의}/skills/의 repo-local Adapter는 팀 공통 AI 작업 규칙이므로 소스 및 커밋 대상으로 취급한다.


## Push 기본 정책

- Push 요청을 받으면 대상 변경사항에 Framework코드영역이 포함되어 있는지 확인한다. Framework의 사용 방법이나 주요 동작에 영향을 주는 변경으로 판단되면 Push 전에 `Framework-참고-기준-키워드.md`의 관련 내용을 수정하거나 새로운 기준을 추가한다.
- Push 요청이 있는 경우 `compileJava`, `test`, `build`까지 수행한다.
- 단 마지막 작업 기준 이미 `compileJava`, `test`, `build` 중 수행된 작업이 있는 경우 중복 수행하지 않는다.
