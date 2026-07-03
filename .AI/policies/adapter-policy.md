# Adapter Policy

이 문서는 Codex, Claude, GitHub Copilot 등 도구별 파일을 관리하는 기준을 정의한다.

## 원칙

- `.AI`는 제품 중립 정책, 맥락, 절차, 자산을 보관한다.
- `.codex`, `.claude`, `.github` 같은 도구별 폴더는 자동 발견을 위한 metadata와 얇은 adapter만 둔다.
- 공통 절차를 도구별 폴더에 복사하지 않는다.
- 도구별 adapter와 `.AI` 문서가 충돌하면 `.AI`의 공통 원칙을 우선한다.

## Adapter에 둘 수 있는 것

- 도구가 요구하는 front matter, manifest, display name, description
- 언제 해당 adapter를 사용할지에 대한 짧은 설명
- 읽어야 할 `.AI/procedures/**` 또는 `.AI/policies/**` 경로
- 도구 특성상 필요한 얇은 실행 래퍼

## Adapter에 두지 않는 것

- 프로젝트 구조 설명의 전체 복사본
- Base 코드 변경 원칙의 중복 서술
- 검증/보고/커밋 메시지 절차의 중복 서술
- 도구별 구현과 무관한 긴 예제
