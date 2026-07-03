# Persistence JPA MyBatis Procedure

- 이 저장소는 JPA와 MyBatis가 공존한다.
- 신규/수정 작업에서는 해당 도메인이나 예제가 따르는 영속화 방식을 우선하고 임의로 혼합하지 않는다.
- JPA 작업은 entity/repository와 transaction 경계를 확인한다.
- MyBatis 작업은 mapper XML, 공통 DAO, result handler, pagination 사용 여부를 확인한다.
- 신규 업무 mapper는 실제 도메인명 기준 하위 경로에 둔다.
- 트랜잭션 경계는 서비스 계층에 명시한다.
