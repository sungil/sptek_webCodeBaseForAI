---
name: database-support
description: 코드 생성과 분석 중 필요한 DB 스키마, 테이블, 컬럼, 기준 데이터, 특정 값 확인을 읽기 전용으로 보조합니다. H2, MySQL, MariaDB 등 JDBC 기반 SELECT/SHOW/DESCRIBE 조회와 반복 SQL 조각 재사용이 필요할 때 사용합니다.
---

# Database Support

코드 생성, 분석, 사용자 질의 응답 중 DB 스키마나 값을 읽기 전용으로 확인해야 할 때 이 스킬을 사용한다. 이 파일은 Codex가 skill을 발견하고 진입하기 위한 얇은 adapter다.

## 사용 절차

1. 루트 `AI.md`를 먼저 따른다.
2. 상세 조회 절차는 `.AI/procedures/tasks/readonly-database-query.md`를 따른다.
3. JDBC 기반 조회는 `.AI/assets/scripts/db/query-jdbc.ps1`를 우선 사용한다.
4. 반복 SQL은 `.AI/assets/snippets/sql` 아래에 저장하거나 재사용한다.
5. 검증이나 완료 보고가 필요한 경우 `.AI/procedures/common/change-verification.md`, `.AI/procedures/common/completion-reporting.md`를 함께 따른다.
