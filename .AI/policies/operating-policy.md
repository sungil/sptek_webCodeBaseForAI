# Operating Policy

이 문서는 AI agent가 이 저장소에서 작업할 때의 기본 실행 규칙을 정의한다.

## 작업 시작

1. `git status --short`로 사용자 변경을 확인한다.
2. 요청 대상을 식별한다: 클래스, 메서드, URL, 애노테이션, 설정 키, 템플릿, SQL, 로그 라인, 테스트, HTTP 예제.
3. 저장소 사실관계는 기억에 의존하지 말고 현재 파일과 검색 결과로 확인한다.
4. 요구가 불명확해도 기존 구조에서 안전하게 결정할 수 있는 범위는 진행한다. 데이터 모델, 인증 정책, 외부 연동 방식처럼 결과가 크게 달라지는 선택만 사용자에게 확인한다.

## 검색과 파일 읽기

- 내용 검색은 `rg`를 우선 사용한다.
- 파일명 탐색은 `fd`가 있으면 `fd`, 없으면 `rg --files`를 사용한다.
- Windows에서는 현재 제공된 PowerShell 셸을 그대로 사용한다. 한글 문서나 주석을 읽을 때는 `Get-Content -Encoding UTF8`처럼 UTF-8을 명시한다.
- 새로 생성하거나 수정하는 텍스트 파일은 UTF-8로 저장한다. PowerShell에서 파일을 쓸 때는 가능한 경우 `-Encoding UTF8` 또는 UTF-8 no BOM API를 사용한다.
- 관련성이 높은 최소 파일만 열고, 큰 디렉터리 전체를 무작정 읽지 않는다.
- 프레임워크 기능과 연결되면 필요한 항목만 추가로 추적한다.
  - `SptWfwApplication`의 활성화 `@Enable_*` 애노테이션
  - 커스텀 `@Enable_*` 애노테이션과 조건/등록 코드
  - `_frameworkWebCoreResources/_frameworkApplicationProperties`
  - `_projectCommonResources/_projectApplicationProperties`
  - `_projectCommon` 확장 구현체와 `_example` 사용 예제

## 변경과 보고

- 변경 범위는 요청과 직접 관련된 파일로 제한한다.
- 공개 API 요청/응답 방식, 보안 matcher, 필터/인터셉터, 데이터소스, profile 동작처럼 실행 동작이 바뀌면 영향 범위를 먼저 설명하고 필요한 경우 확인을 받는다.
- 코드 변경 후에는 `.AI/procedures/common/change-verification.md`를 따른다.
- 완료 보고는 `.AI/procedures/common/completion-reporting.md`를 따른다.
