# 읽기전용 DB조회 절차

이 문서는 AI가 코드 생성, 분석, 사용자 질의 응답 과정에서 필요한 DB 스키마, 테이블, 컬럼, 기준 데이터, 특정 값을 읽기 전용으로 확인할 때 따르는 실행 절차다. 특정 DB 제품이나 `local` H2에 한정하지 않고, 현재 애플리케이션 설정이 바라보는 DB 또는 사용자가 명시한 DB에서 필요한 사실만 빠르게 확인하는 것을 목표로 한다.

스키마 생성, 데이터 입력, 마이그레이션, 초기화는 이 절차의 책임이 아니다.

## DB 선택 기준

1. 사용자가 DB를 명시하면 해당 DB를 우선한다.
   - 비밀번호나 민감 접속 정보가 필요하면 사용자에게 제공 방식만 요청하고, 값을 문서나 커밋 대상에 남기지 않는다.
2. 사용자가 DB를 명시하지 않았고 AI가 구현/분석을 위해 데이터 확인이 필요하면 현재 실행 기준을 확인한다.
   - `application.yml`의 기본 active profile
   - `application-{profile}.yml`의 `spring.config.import`
   - 활성화된 datasource 관련 `@Enable_*` 애노테이션
   - profile별 datasource 설정 파일
3. 현재 설정값이 암호화되어 있거나 외부 비밀 설정에 의존하면 복호화를 시도하지 않는다.
   - 설정에서 확인 가능한 DB 종류와 후보 경로까지만 보고한다.
   - 접속이 필요하면 사용자에게 접속 가능한 JDBC URL, 계정, 비밀번호 또는 실행 환경 제공을 요청한다.
4. H2 file DB는 새 DB 파일이 생기지 않도록 본체 파일(`*.mv.db`) 존재 여부를 먼저 확인한다.
   - 본체 파일이 없으면 접속하지 않고 중단한다.
   - DB 생성이나 `_h2SqlInitialization` 적용이 필요하면 이 절차로 처리하지 말고 애플리케이션 기동, Spring SQL init, Flyway/Liquibase 같은 별도 초기화·마이그레이션 절차로 다룬다.

## 조회 원칙

- 읽기 쿼리만 실행한다.
- `INSERT`, `UPDATE`, `DELETE`, `MERGE`, `CREATE`, `ALTER`, `DROP`, `TRUNCATE`, `GRANT`, `REVOKE`, `RUNSCRIPT`, `SCRIPT`, `BACKUP`, `SHUTDOWN` 같은 변경 SQL이나 DB 제어 SQL은 수행하지 않는다.
- 운영 또는 운영 유사 DB로 보이는 경우에는 필요한 최소 컬럼과 최소 건수만 조회한다.
- 개인정보, 토큰, 비밀번호, 암호화 키, 세션 값이 포함될 수 있는 컬럼은 원문을 그대로 출력하지 않는다. 필요한 경우 마스킹하거나 건수/존재 여부만 보고한다.
- 대량 조회를 피하고 `LIMIT`, `FETCH FIRST`, 조건절, 집계 쿼리를 우선 사용한다.
- 조회 결과는 코드 구현에 필요한 사실 중심으로 요약하고, 실행한 DB, 기준 설정, SQL 의도, 행 수를 함께 보고한다.

## 실행 도구

저장소 루트에서 범용 JDBC 조회 스크립트를 우선 사용한다.

```powershell
.\.AI\_ai-generated\scripts\db\query-jdbc.ps1 `
  -DbType h2 `
  -JdbcUrl "jdbc:h2:file:./local-dev-support/h2DB/webFrameworkExample;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=9092" `
  -User "sa" `
  -Password "" `
  -Sql "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' ORDER BY TABLE_NAME"
```

MySQL 예시:

```powershell
.\.AI\_ai-generated\scripts\db\query-jdbc.ps1 `
  -DbType mysql `
  -JdbcUrl "jdbc:mysql://localhost:3306/spt_web_fw?serverTimezone=UTC" `
  -User "user" `
  -Password "password" `
  -FilePath .\.AI\_ai-generated\snippets\sql\common\table-list.mysql.sql
```

여러 줄 SQL이나 반복 조회 SQL은 파일로 저장한 뒤 실행한다.

```powershell
.\.AI\_ai-generated\scripts\db\query-jdbc.ps1 `
  -DbType h2 `
  -JdbcUrl "jdbc:h2:file:./local-dev-support/h2DB/webFrameworkExample;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=9092" `
  -User "sa" `
  -Password "" `
  -FilePath .\.AI\_ai-generated\snippets\sql\common\table-list.h2.sql
```

기존 local H2 전용 조회가 필요하면 호환 스크립트를 사용할 수 있다.

```powershell
.\.AI\_ai-generated\scripts\db\query-local-h2.ps1 "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' ORDER BY TABLE_NAME"
```

`query-jdbc.ps1`는 마지막 성공 접속 정보, JDBC driver jar 경로, 컴파일된 Java runner를 `.AI/_ai-generated/.cache/query-jdbc` 아래에 저장한다.
같은 DB에 반복 조회할 때는 `-JdbcUrl`을 생략하면 마지막 성공 접속 정보를 우선 사용하고, 실패했을 때만 다시 명시 URL과 driver jar 탐색 경로를 점검한다.

스키마 확인은 먼저 `.AI/_ai-generated/.cache/query-jdbc/schema-cache.json`을 확인한다.
캐시가 없거나, 사용자가 묻는 테이블/컬럼이 캐시에 없거나, 실제 조회에서 컬럼/테이블 오류가 발생했을 때만 다음 명령으로 갱신한다.

```powershell
.\.AI\_ai-generated\scripts\db\query-jdbc.ps1 -RefreshSchemaCache
```

스키마 캐시는 테이블/컬럼 구조를 빠르게 파악하기 위한 보조 자료다. 정확성이 중요하거나 최근 마이그레이션, 앱 재기동, SQL 초기화 이후라면 갱신 후 사용한다.

## 반복 쿼리 보관

반복적으로 사용하는 SQL은 `.AI/_ai-generated/snippets/sql` 아래에 저장한다.

- `common`: DB 구조 확인, 테이블/컬럼 목록, 건수 확인처럼 범용적인 SQL
- `{domain}`: 특정 업무 도메인 분석에 반복적으로 쓰는 SQL
- `{feature}`: 특정 화면, API, 배치, 이벤트 흐름 분석에 쓰는 SQL

SQL 파일 작성 기준:

- 파일명은 조회 목적과 DB 종류가 드러나게 작성한다. 예: `{조회목적}.{DB종류}.sql`
- 부분 쿼리로 재사용할 SQL은 파일 상단 주석에 사용 위치와 필요한 alias를 적는다.
- 실제 비밀번호, 토큰, 개인 식별값, 운영 데이터 샘플은 저장하지 않는다.
- 특정 사용자의 일회성 조건값은 placeholder 주석으로 남기고 값은 실행 시 직접 넣는다.

결과가 많으면 전체 테이블을 붙이지 말고 필요한 컬럼만 요약한다. 사용자가 원문 결과를 요청한 경우에도 민감 컬럼은 마스킹한다.
