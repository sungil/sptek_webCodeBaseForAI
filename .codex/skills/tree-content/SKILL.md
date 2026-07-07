---
name: tree-content
description: 디렉토리를 Markdown 파일 내용이 포함된 들여쓰기 트리 문서로 내보내거나, 그 문서를 다시 실제 디렉토리와 파일로 복원할 때 사용합니다. 사용자가 .AI 트리 덤프, md 내용 포함 트리 출력, 트리 문서 기반 복원을 요청하면 사용합니다.
---

# Tree Content

디렉토리 트리와 Markdown 파일 내용을 상호 변환할 때 이 스킬을 사용한다. 이 파일은 Codex가 skill을 발견하고 진입하기 위한 얇은 adapter다.

## 사용 절차

1. 루트 `AI.md`를 먼저 따른다.
2. 상세 절차는 `.AI/procedures/tasks/tree-content.md`를 따른다.
3. 내보내기는 `.AI/assets/scripts/tree-content/export-tree-content.py`를 사용한다.
4. 복원은 `.AI/assets/scripts/tree-content/import-tree-content.py`를 사용한다.
5. 사용자가 `@file:export-tree-content.py 처리`, `export-tree-content.py 실행`, `.AI 트리 덤프`처럼 요청하면 기본 명령 `python .AI\assets\scripts\tree-content\export-tree-content.py`를 실행한다.
6. 사용자가 `@file:import-tree-content.py 처리`, `import-tree-content.py 실행`, `트리 문서 복원`처럼 요청하면 먼저 `python .AI\assets\scripts\tree-content\import-tree-content.py --dry-run`으로 확인한 뒤 필요하면 실제 복원을 실행한다.
