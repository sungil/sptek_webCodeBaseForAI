---
name: pre-commit-code-comments
description: 커밋 요청을 받았을 때 변경 대상 파일을 기준으로 코드 주석 보강 필요 여부를 점검하고, 필요한 경우 write-code-comments 절차를 적용한 뒤 커밋 범위와 검증 상태를 확인합니다. 사용자가 커밋을 요청하거나 커밋 직전 주석 점검을 요청할 때 사용합니다.
---

# Pre-Commit Code Comments

커밋 전에 변경 파일의 주석 보강 필요 여부를 점검할 때 이 스킬을 사용한다.

## 사용 절차

1. 루트 `AI.md`를 먼저 따른다.
2. 상세 절차는 `.AI/workflows/skills/pre-commit-code-comments.md`를 따른다.
3. 주석 작성이 필요하면 `write-code-comments` 스킬의 `.AI/workflows/skills/write-code-comments.md` 절차를 적용한다.
4. 실제 커밋을 만들 때는 주석 보강 후 변경 범위와 검증 결과를 확인한다.
