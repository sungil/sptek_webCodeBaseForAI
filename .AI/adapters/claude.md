# Claude Adapter

이 문서는 Claude 전용 adapter 작성 기준을 정의한다.

## 파일 위치

- Claude 호환 진입점은 루트 `CLAUDE.md`다.
- Claude 전용 command와 rule 관련 파일은 `.claude/**` 아래에 둔다.

## 루트 진입점

- `CLAUDE.md`는 루트 `AI.md`와 `.AI/README.md`를 읽도록 안내하는 얇은 진입점으로 유지한다.
- Claude 전용 상세 절차를 `CLAUDE.md`에 복사하지 않는다.

## 확장 파일

- `.claude/commands/**`에는 command 설명, 짧은 사용 조건, 참조할 `.AI/procedures/**` 경로만 둔다.
- Claude rule이나 추가 adapter를 만들 때도 공통 절차는 `.AI/procedures/**`에 둔다.

## 공통 문서

- 정책은 `.AI/policies/**`를 참조한다.
- 저장소 맥락은 필요한 경우에만 `.AI/context/**`를 참조한다.
- 반복 절차는 작업 성격에 맞는 `.AI/procedures/**`만 참조한다.

## 로컬 산출물

- `.claude/**` 안의 개인 설정이나 실행 산출물은 커밋 전에 별도 ignore 여부를 검토한다.
- 팀 공통 command/rule adapter는 커밋 대상이다.
