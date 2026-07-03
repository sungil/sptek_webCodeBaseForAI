# AI Common Guide

`.AI`는 Codex, Claude, GitHub Copilot, 기타 agentic coding 도구가 공유하는 제품 중립 작업 기준을 보관한다. 도구별 폴더에는 자동 발견을 위한 얇은 adapter만 둔다.

## 읽는 순서

1. 루트 `AI.md`를 먼저 읽는다.
2. 작업 안전과 변경 경계가 필요하면 `policies`에서 관련 문서를 읽는다.
3. 저장소 구조나 런타임 맥락이 필요할 때만 `context` 문서를 읽는다.
4. 반복 작업 절차가 필요하면 `procedures`에서 작업 유형에 맞는 문서만 읽는다.
5. 스크립트, SQL, 템플릿 같은 재사용 파일은 `assets`에서 사용한다.

## 디렉터리

- `policies`: 항상 지켜야 하는 규칙과 변경 경계
- `context`: 저장소를 이해하기 위한 정적 정보
- `procedures`: 반복 작업 절차
- `assets`: 절차에서 재사용하는 스크립트, SQL, 템플릿
- `adapters`: Codex, Claude, Copilot 등 도구별 연결 방식 설명

## 작성 원칙

- 제품 전용 용어는 `.AI` 문서명에 쓰지 않는다. 예: `skill`, `command`, `rule` 대신 `procedure`, `adapter`, `policy`를 사용한다.
- 같은 내용을 여러 도구별 폴더에 복사하지 않는다.
- 루트 `AI.md`는 짧은 bootstrap으로 유지하고, 상세 맥락은 `.AI/context/**`에 둔다.
- 절차 문서는 실행 순서와 판단 기준을 담고, 긴 프로젝트 설명은 반복하지 않는다.
