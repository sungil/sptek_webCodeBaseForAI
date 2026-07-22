---
name: database-support
description: 코드 생성과 분석 중 필요한 DB 스키마, 테이블, 컬럼, 기준 데이터, 특정 값 확인을 읽기 전용으로 보조합니다. H2, MySQL, MariaDB 등 JDBC 기반 SELECT/SHOW/DESCRIBE 조회와 반복 SQL 조각 재사용이 필요할 때 사용합니다.
---

# DB 조회 보조

코드 생성, 분석, 사용자 질의 응답 중 DB 스키마나 값을 읽기 전용으로 확인해야 할 때 이 스킬을 사용한다. 이 파일은 Codex가 skill을 발견하고 진입하기 위한 얇은 adapter다.

## 사용 절차

- DB 작업 안전 기준은 `.AI/공통정책/DB작업-공통-정책.md`를 따른다.
- H2 File DB를 다룰 때는 `.AI/공통정책/H2-DB-작업-정책.md`를 함께 확인한다.
- 상세 조회 절차는 `.AI/작업-수행절차/읽기전용-DB조회-절차.md`를 따른다.
- JDBC 기반 조회는 `.AI/_ai-generated/scripts/db/query-jdbc.ps1`를 우선 사용한다.
- 반복 SQL은 `.AI/_ai-generated/snippets/sql` 아래에 저장하거나 재사용한다.
