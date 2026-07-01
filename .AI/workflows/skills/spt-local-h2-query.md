# SPT Local H2 Query Workflow

이 문서는 SPT Framework Web Core 저장소의 `local` 프로파일 H2 file DB를 빠르게 조회할 때 따르는 도구 독립 절차다. Codex에서는 `.codex/skills/spt-local-h2-query/SKILL.md`가 이 문서를 참조한다.

## 핵심 규칙

1. 루트 `AI.md`를 먼저 따른다.
2. DB 파일을 삭제, 초기화, 재생성하지 않는다.
3. 조회 요청은 별도 연결 테스트를 먼저 하지 말고 바로 쿼리한다.
4. 기본 JDBC URL은 `src/main/resources/_frameworkWebCoreResources/_frameworkApplicationProperties/h2DB/h2DB-local.yml`의 값을 따른다.
5. 기본 DB 본체 파일은 `infra/h2DB/spt_web_fw.mv.db`다. 이 파일이 없으면 조회하지 말고 파일이 없다고 보고한다.
6. 기본은 읽기 쿼리만 실행한다. 데이터 변경 SQL이나 DDL은 사용자가 명시적으로 요청한 경우에만 수행한다.

## 빠른 실행

저장소 루트에서 공통 스크립트를 사용한다.

```powershell
.\.AI\workflows\scripts\query-local-h2.ps1 "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' ORDER BY TABLE_NAME"
```

스크립트는 Gradle 캐시의 H2 실행 JAR를 자동 탐색한다. H2 JAR가 없다는 오류가 나면 먼저 다음 명령으로 프로젝트 의존성을 받아온 뒤 다시 실행한다.

```powershell
.\gradlew.bat processResources
```

여러 줄 SQL은 파일로 저장한 뒤 실행한다.

```powershell
.\.AI\workflows\scripts\query-local-h2.ps1 -FilePath .\query.sql
```

사용자가 명시적으로 변경 SQL을 요청한 경우에만 `-AllowWrite`를 붙인다.

```powershell
.\.AI\workflows\scripts\query-local-h2.ps1 "UPDATE PUBLIC.TEST SET C1 = 'x'" -AllowWrite
```

## 자주 쓰는 쿼리

테이블 목록:

```sql
SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'PUBLIC'
ORDER BY TABLE_NAME;
```

컬럼 목록:

```sql
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'PUBLIC'
ORDER BY TABLE_NAME, ORDINAL_POSITION;
```

## 관련 파일

- H2 설정: `src/main/resources/_frameworkWebCoreResources/_frameworkApplicationProperties/h2DB/h2DB-local.yml`
- 스키마: `src/main/resources/_autoSqlInitialize/schema.sql`
- 초기 데이터: `src/main/resources/_autoSqlInitialize/data.sql`
- H2 파일 DB: `infra/h2DB/spt_web_fw.mv.db`

## 검증 참고

스킬 구조 검증이 필요하면 각 AI 도구의 검증 방식을 따른다. Codex `skill-creator`의 `quick_validate.py`를 사용할 때 Windows에서 한글이 포함된 `SKILL.md` 때문에 `UnicodeDecodeError`가 나면 `PYTHONUTF8=1`을 켜고 다시 실행한다.

```powershell
$env:PYTHONUTF8 = '1'
python C:\Users\이성일\.codex\skills\.system\skill-creator\scripts\quick_validate.py .codex\skills\spt-local-h2-query
```
