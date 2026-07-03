# GitHub Copilot Adapter

이 문서는 GitHub Copilot 전용 adapter 작성 기준을 정의한다.

## 파일 위치

- GitHub Copilot 호환 진입점은 `.github/copilot-instructions.md`다.
- Copilot 전용 instruction 관련 파일은 `.github/**` 아래에 둔다.

## 루트 진입점

- `.github/copilot-instructions.md`는 루트 `AI.md`와 `.AI/README.md`를 읽도록 안내하는 얇은 진입점으로 유지한다.
- Copilot 전용 상세 절차를 `.github/copilot-instructions.md`에 복사하지 않는다.

## 확장 파일

- 추가 Copilot instruction 파일을 만들 경우 짧은 사용 조건과 참조할 `.AI/procedures/**` 경로만 둔다.
- Copilot이 긴 문서를 매번 읽지 않도록 작업 성격에 맞는 `.AI/procedures/**` 문서를 선택하도록 안내한다.

## 공통 문서

- 정책은 `.AI/policies/**`를 참조한다.
- 저장소 맥락은 필요한 경우에만 `.AI/context/**`를 참조한다.
- 반복 절차는 작업 성격에 맞는 `.AI/procedures/**`만 참조한다.

## 로컬 산출물

- `.github/**` 아래에는 팀 공통 instruction만 둔다.
- 개인 설정이나 실험용 instruction은 커밋 전에 별도 ignore 또는 로컬 보관을 검토한다.
