# H2 DB 작업 정책

이 문서는 H2 File DB를 조회하거나 다룰 때 지켜야 하는 기준을 정의한다.

- 실행 애플리케이션의 메인 클래스에 `@Enable_DatasourceOfH2_At_Main`이 활성화되어 있으면 Framework코드영역은 H2 datasource 설정 Bean을 사용한다.
- H2 사용 여부를 판단할 때는 메인 클래스의 `@Enable_DatasourceOfH2_At_Main`, Gradle H2 의존성, profile별 datasource 설정을 함께 확인한다.
- H2 File DB는 새 DB 파일이 생성되지 않도록 기존 본체 파일의 존재 여부를 먼저 확인한다.
- `jdbc:h2:file:` URL을 사용할 때는 대상 경로의 `*.mv.db` 파일이 실제로 존재하는지 확인한다.
- local H2 조회는 가능하면 `.AI/_ai-generated/scripts/db/query-jdbc.ps1` 또는 호환 스크립트 `.AI/_ai-generated/scripts/db/query-local-h2.ps1`를 사용한다.
- `local-dev-support/h2DB` 아래의 DB 파일은 테스트나 원인 분석을 위해 읽을 수 있지만, 명시적 요청 없이 삭제, 초기화, 재생성하지 않는다.
