# Resource Map

이 문서는 `src/main/resources` 하위 리소스 구조와 profile 설정 기준을 요약한다.



프로파일은 `local`, `dev`, `stg`, `prd` 네 종류를 기준으로 한다. `spring.config.import`는 현재의 `optional:classpath:` 패턴을 유지한다.

## 리소스 경계


- `_autoSqlInitialize`: 로컬 H2 등 초기 DB 스키마와 seed SQL

## 설정 변경 기준

- profile별 설정 키를 추가하거나 구조를 바꿀 때 `local/dev/stg/prd` 파일을 모두 검토한다.
- profile별 설정 파일이 이미 분리된 기능은 `application-{profile}.yml`에서 해당 `{기능}-{profile}.yml`을 import하는지 함께 확인한다.
- 값이 환경마다 달라야 하면 구조만 동기화하고 실제 운영 값을 추측하지 않는다.
- datasource 전환은 `SptWfwApplication`의 `@Enable_DatasourceOf*`, Gradle DB 의존성, 해당 profile 설정을 한 묶음으로 본다.
- 신규 업무 MyBatis mapper는 실제 도메인명 기준 하위 경로에 둔다.
