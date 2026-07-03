# Adapter Guide

이 디렉터리는 Codex, Claude, GitHub Copilot 같은 도구별 adapter 작성 방식을 설명한다. 실제 도구가 읽는 파일은 `.codex`, `.claude`, `.github` 같은 각 도구 폴더에 둔다.

## 원칙

- 각 도구별 adapter 문서는 같은 구조로 작성한다.
- adapter 파일에는 자동 발견에 필요한 metadata, 짧은 사용 조건, 참조할 `.AI/**` 경로만 둔다.
- 공통 정책, 저장소 맥락, 반복 절차는 `.AI` 아래 문서를 참조하고 도구별 폴더에 복사하지 않는다.
- adapter를 추가하거나 수정할 때는 `.AI/policies/adapter-policy.md`를 함께 확인한다.

## 도구별 문서

- `codex.md`: Codex skill/plugin 연결 방식
- `claude.md`: Claude command/rule 연결 방식
- `copilot.md`: GitHub Copilot instructions 연결 방식

## 권장 형식

도구별 adapter 설명 문서는 다음 구성을 따른다.

1. 도구 전용 파일 위치
2. 루트 진입점
3. 확장 파일 작성 기준
4. 공통 문서 참조 기준
5. 로컬 산출물 또는 개인 설정 처리 기준
