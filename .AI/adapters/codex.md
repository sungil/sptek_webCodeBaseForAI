# Codex Adapter

이 문서는 Codex 전용 adapter 작성 기준을 정의한다.

## 파일 위치

- Codex 호환 진입점은 루트 `AGENTS.md`다.
- Codex 전용 skill과 plugin 관련 파일은 `.codex/**` 아래에 둔다.

## 루트 진입점

- `AGENTS.md`는 루트 `AI.md`와 `.AI/README.md`를 읽도록 안내하는 얇은 진입점으로 유지한다.
- Codex 전용 상세 절차를 `AGENTS.md`에 복사하지 않는다.

## 확장 파일

- `.codex/skills/**/SKILL.md`에는 front matter, 짧은 사용 조건, 참조할 `.AI/procedures/**` 경로만 둔다.
- Codex plugin이나 추가 adapter를 만들 때도 공통 절차는 `.AI/procedures/**`에 둔다.

## 공통 문서

- 정책은 `.AI/policies/**`를 참조한다.
- 저장소 맥락은 필요한 경우에만 `.AI/context/**`를 참조한다.
- 반복 절차는 작업 성격에 맞는 `.AI/procedures/**`만 참조한다.

## 로컬 산출물

- repo-local Codex skill adapter는 팀 공통 AI 작업 규칙으로 취급하므로 커밋 대상이다.
- `.codex/run/`과 `.codex/config.toml`은 로컬 실행 산출물/개인 설정으로 취급한다.
