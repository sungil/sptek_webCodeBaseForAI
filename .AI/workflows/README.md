# Workflows

이 디렉터리는 Codex, Claude 등 특정 도구에 종속되지 않는 공통 작업 절차를 보관한다. 작업 성격에 맞는 하위 문서를 필요한 만큼만 읽는다.

## 디렉터리 구분

- `common`: 검증, 보고, 커밋 메시지처럼 모든 작업에 공통으로 쓰는 절차를 둔다.
- `skills`: Codex skill, Claude command 등 도구별 진입점이 공유할 상세 절차를 둔다.
- `tech`: Spring Security, MyBatis, Thymeleaf, datasource처럼 기술 요소별로 재사용할 분석·구현 절차를 둔다.

같은 내용을 도구별 폴더에 복사하지 않는다. 도구별 파일에는 이 디렉터리의 관련 문서를 참조하는 최소 지침만 둔다.
