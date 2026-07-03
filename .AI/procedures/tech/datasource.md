# Datasource Procedure

- datasource 전환은 `SptWfwApplication`의 `@Enable_DatasourceOf*`, Gradle DB 의존성, profile별 datasource 설정을 한 묶음으로 검토한다.
- 현재 메인 클래스 기준으로 H2 datasource와 JPA hybrid 구성이 활성화되어 있다.
- MySQL replication 및 JNDI replication datasource는 비활성 예시로 남아 있다.
- H2 file DB는 `infra/h2DB` 아래에 생성되며, 명시적 요청 없이 초기화/삭제/교체하지 않는다.
- DB 스키마나 값을 읽기 전용으로 확인할 때는 `.AI/procedures/tasks/readonly-database-query.md`를 따른다.
