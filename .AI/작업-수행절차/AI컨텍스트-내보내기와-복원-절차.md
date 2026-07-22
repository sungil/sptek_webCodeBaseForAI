# AI 컨텍스트 내보내기와 복원 절차

이 절차는 `.AI/` 같은 지침 디렉토리를 AI가 한 번에 읽기 좋은 Markdown 컨텍스트 문서로 내보낼 때 사용한다.
복원 기능은 이 형식의 문서를 다시 디렉토리와 파일로 되돌려야 할 때만 사용한다.

## 내보내기

1. 기본 대상은 `.AI`이며, 다른 디렉토리가 필요하면 명시적으로 지정한다.
2. `.AI/_ai-generated/scripts/ai-context-export/export-ai-context.py`를 사용한다.
3. 모든 디렉토리/파일명을 트리로 쓰고, `.md` 파일 내용만 파일명 아래에 한 단계 더 들여써 붙인다.
4. 출력 위치를 생략하면 `~/AI-context-{yyyyMMdd-HHmmss}.md`에 저장한다.
5. `.md`가 아닌 파일은 확장자와 위치에 관계없이 파일명만 기록한다.

```powershell
python .AI\_ai-generated\scripts\ai-context-export\export-ai-context.py
python .AI\_ai-generated\scripts\ai-context-export\export-ai-context.py .AI -o C:\Tmp\AI-context.md
```

## 복원

1. 먼저 `--dry-run`으로 파싱과 생성 대상을 확인한다.
2. `.AI/_ai-generated/scripts/ai-context-export/import-ai-context.py`를 사용한다.
3. 입력 파일을 생략하면 `~/AI-context.md`를 읽는다.
4. 복원 위치를 생략하면 `~/ai-context-restore` 아래에 생성한다.
5. `.md` 파일은 문서 내용으로 생성하거나 갱신한다.
6. `.md`가 아닌 파일은 새 트리 위치와 동일 파일명을 가진 기존 파일이 하나뿐일 때만 이동한다.
7. 복원 스크립트는 덤프에 없는 기존 파일을 삭제하지 않는다.

```powershell
python .AI\_ai-generated\scripts\ai-context-export\import-ai-context.py --dry-run
python .AI\_ai-generated\scripts\ai-context-export\import-ai-context.py
python .AI\_ai-generated\scripts\ai-context-export\import-ai-context.py C:\Tmp\AI-context.md --target-parent C:\Tmp\restore --dry-run
python .AI\_ai-generated\scripts\ai-context-export\import-ai-context.py C:\Tmp\AI-context.md --target-parent C:\Tmp\restore
```
