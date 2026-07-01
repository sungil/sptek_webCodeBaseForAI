---
name: pre-commit-code-comments
description: 커밋 전 변경 대상 파일과 직접 연관된 파일을 검토해 클래스 상단 주석과 주요 메서드 주석을 보강합니다. 사용자가 커밋을 요청하거나, 커밋 전 주석 정리/코드 이해 보조 주석 추가/기존 주석 갱신을 요청할 때 사용합니다.
---

# Pre-Commit Code Comments

커밋 전에 변경 파일과 관련 파일의 주석을 Base 코드 관례에 맞게 점검할 때 이 스킬을 사용한다.

## 사용 절차

1. 루트 `AGENT.md`를 먼저 따른다.
2. 상세 절차는 `.agents/workflows/skills/pre-commit-code-comments.md`를 따른다.
3. 실제 커밋을 만들 때는 주석 보강 후 변경 범위와 검증 결과를 확인한다.
