# Profiles Config Procedure

- 공통 기본값은 `application.yml`, 환경별 값과 import는 `application-{profile}.yml`에서 관리한다.
- profile은 `local`, `dev`, `stg`, `prd` 네 종류를 기준으로 한다.
- 설정 키를 추가하거나 구조를 바꿀 때 네 profile 파일을 모두 검토한다.
- `spring.config.import`는 현재의 `optional:classpath:` 패턴을 유지한다.
- profile별 설정 파일이 존재하는 기능은 `application-{profile}.yml`에서 `{기능}-{profile}.yml`을 import하는 패턴을 우선한다.
- 실제 운영값이나 비밀값을 추측해 채우지 않는다.
