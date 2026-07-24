---
name: ai-context-export
description: 디렉토리 구조와 Markdown 내용을 AI가 읽기 좋은 컨텍스트 문서로 내보낼 때 사용합니다. 필요하면 같은 형식의 문서를 다시 디렉토리와 파일로 복원하는 흐름도 지원합니다.
---

# AI 컨텍스트 내보내기

디렉토리 트리와 Markdown 파일 내용을 AI가 읽기 좋은 컨텍스트 문서로 내보낼 때 이 스킬을 사용한다.
복원 기능은 AI 컨텍스트 문서를 실제 디렉토리와 파일로 되돌려야 할 때만 사용한다.

## 사용 절차

- 상세 절차는 `_AI/_작업수행절차/AI컨텍스트-내보내기와-복원-절차.md`를 따른다.
- 내보내기는 `_AI/_ai-generated/_scripts/ai-context-export/export-ai-context.py`를 사용한다.
- 복원은 `_AI/_ai-generated/_scripts/ai-context-export/import-ai-context.py`를 사용한다.
- 사용자가 `_AI 트리 덤프`, `AI 컨텍스트 export`, `md 내용 포함 트리 출력`처럼 요청하면 기본 명령 `python _AI\_ai-generated\_scripts\ai-context-export\export-ai-context.py`를 실행한다.
- 사용자가 `AI 컨텍스트 복원`, `트리 문서 복원`처럼 요청하면 먼저 `python _AI\_ai-generated\_scripts\ai-context-export\import-ai-context.py --dry-run`으로 확인한 뒤 필요하면 실제 복원을 실행한다.
