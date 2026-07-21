# Tree Content Procedure

이 절차는 특정 디렉토리를 Markdown 파일 내용이 포함된 들여쓰기 트리 문서로 내보내거나, 그 문서를 다시 실제 디렉토리와 파일로 복원할 때 따른다.

## 내보내기

1. 루트 `AI.md`를 먼저 따른다.
2. 기본 대상은 `.AI`이며, 다른 디렉토리가 필요하면 명시적으로 지정한다.
3. `.AI/assets/scripts/tree-content/export-tree-content.py`를 사용한다.
4. 기본 출력 형식은 모든 디렉토리/파일명을 트리로 쓰고, `.md` 파일 내용만 파일명 바로 아래에 한 단계 더 들여써 붙인다.
5. 인자를 생략하면 현재 작업 폴더에서 `.AI`를 우선 찾고, 없으면 `.ai`를 대상으로 사용한다.
6. 출력 위치를 생략하면 `~/AI-tree-content-{yyyyMMdd-HHmmss}.md`에 저장한다.
7. `.md`가 아닌 파일은 확장자와 위치에 관계없이 파일명만 기록한다.

예:

```powershell
python .AI\assets\scripts\tree-content\export-tree-content.py
python .AI\assets\scripts\tree-content\export-tree-content.py .AI -o C:\Tmp\AI-tree-content.md
```

## 복원

1. 복원 전에 대상 경로와 덤프 파일을 확인한다.
2. `.AI/assets/scripts/tree-content/import-tree-content.py`를 사용한다.
3. 입력 파일을 생략하면 `~/AI-tree-content.md`를 읽는다.
4. 복원 위치를 생략하면 `~/tree-content-restore` 아래에 생성한다.
5. 먼저 `--dry-run`으로 파싱과 생성 대상 수를 확인한다.
6. 복원 시작 시 기존 대상 트리에는 있지만 새 트리에는 없는 파일 목록을 먼저 출력한다.
7. `.md` 파일은 새 트리 문서의 내용으로 생성하거나 갱신한다.
8. `.md`가 아닌 파일은 새 트리에서 위치가 달라졌고 대상 트리 안에 동일 파일명이 하나만 있으면 그 위치로 이동한다.
9. `.md`가 아닌 파일의 원본을 찾을 수 없거나 동일 파일명이 여러 개라면 생성하지 않고 unresolved로 보고한다.
10. 복원 스크립트는 덤프에 없는 기존 파일을 삭제하지 않는다.

예:

```powershell
python .AI\assets\scripts\tree-content\import-tree-content.py --dry-run
python .AI\assets\scripts\tree-content\import-tree-content.py
python .AI\assets\scripts\tree-content\import-tree-content.py C:\Tmp\AI-tree-content.md --target-parent C:\Tmp\restore --dry-run
python .AI\assets\scripts\tree-content\import-tree-content.py C:\Tmp\AI-tree-content.md --target-parent C:\Tmp\restore
```
