# AI.md

이 파일은 Codex, Claude, GitHub Copilot 등 AI 코딩 도구가 공통으로 읽는 최상위 안내 문서다. 상세한 규칙과 저장소 맥락은 특정 도구에 종속되지 않는 `.AI` 문서에 둔다.

## 문서 우선순위

1. 현재 작업 범위에 더 가까운 하위 `AI.md`, `AGENTS.md`, `CLAUDE.md`가 있으면 그 문서를 먼저 따른다.
2. 하위 문서가 없거나 공통 원칙이 필요하면 이 파일과 `.AI/README.md`를 따른다.
3. 문서 간 내용이 충돌하면 더 구체적인 하위 문서가 우선하되, 루트의 안전 원칙과 Base 코드 경계는 우회하지 않는다.

## 필수 원칙

- 작업을 시작할 때는 `git status --short`로 기존 변경을 확인하고, 요청과 무관한 변경은 그대로 보존한다.
- 내용 검색은 `rg`, 파일명 탐색은 `fd`가 있으면 `fd`, 없으면 `rg --files`를 우선 사용한다.
- 새로 생성하거나 수정하는 텍스트 파일은 UTF-8로 저장한다.
- 필요한 문서와 파일만 읽는다. 저장소 구조 설명이 필요할 때만 `.AI/context/**`를 연다.
- 작업을 위해 md 파일을 읽게 되는 경우는 반드시 '지식 확정을 위해 {파일명}.md 을 먼저 읽겠습니다.' 라는 안내를 먼저 한다.
- 이 저장소는 여러 프로젝트가 공통으로 사용하는 SPT Framework Web Core(Base 프레임워크)를 활용하여 특정 영역의 비즈니스를 개발하기 위한 프로젝트이다. 새 코드는 단순한 구현보다 Base 프레임워크 코드와의 일관성을 우선한다.
- 프레임워크 성격이 강한 공통 기술 요소는 Base 프레임워크 영역인 `_frameworkWebCore`에 두고, 비즈니스 전반에서 공유하는 확장 요소는 `_projectCommon`에, 구체적인 비즈니스 업무 코드는 `com.sptek.{project}.{domain}` 패키지 아래에 작성한다.
- Base 프레임워크 코드의 실행 동작을 바꾸는 변경은 영향 범위와 장단점을 먼저 설명하고 사용자 확인을 받는다. 단순 오타, 주석, 문서, 명백히 잘못된 예제 보정은 필요한 범위에서 바로 수정할 수 있다.
- 민감값, 비공개 환경 파일, keystore, 운영 설정값, 로컬 DB 파일은 명시적 요청 없이 출력하거나 수정하지 않는다.

## 필요한 문서

- `.AI/policies/operating-policy.md`: 작업 시작, 검색, 파일 읽기, 수정, 보고 기본 규칙
- `.AI/policies/safety-policy.md`: 사용자 변경 보존, 금지 작업, 민감값, DB/로그 안전 기준
- `.AI/policies/project-boundary-policy.md`: Base 코드, 프로젝트 공통, 업무 코드의 변경 경계
- `.AI/context/project-overview.md`: 프로젝트 성격, 기술 스택, 빌드 기준
- `.AI/context/repository-map.md`: 루트 디렉터리와 주요 경로 역할
- `.AI/context/framework-map.md`: `_frameworkWebCore`, `_projectCommon`, `_example` 구조
- `.AI/context/resource-map.md`: resources, profile, template, static 구조
- `.AI/context/runtime-map.md`: `SptWfwApplication`, `@Enable_*`, profile, datasource 기준
- `.AI/procedures/common/search-and-navigation.md`: 빠른 검색과 최소 파일 읽기 기준
- `.AI/procedures/common/change-verification.md`: 변경 유형별 검증 기준
- `.AI/procedures/common/completion-reporting.md`: 완료 보고 기준
- `.AI/procedures/common/commit-message.md`: 커밋 메시지 작성 기준

## 도구별 어댑터

`.codex`, `.claude`, `.github` 같은 도구별 폴더에는 각 도구가 인식하는 데 필요한 최소 설정과 `.AI` 문서로 연결되는 안내만 둔다. 공통 지침을 도구별 폴더에 복사하지 말고 `.AI` 문서를 참조한다. 자세한 기준은 `.AI/policies/adapter-policy.md`와 `.AI/adapters/README.md`를 따른다.
