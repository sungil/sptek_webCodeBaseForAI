# Tech Procedures

이 디렉터리는 특정 기술 요소별 분석·구현 절차를 보관한다.

현재 분류는 다음과 같다.

- `spring-mvc.md`: Controller, request mapping, validation, View/API 흐름 분석
- `spring-security.md`: SecurityFilterChain, matcher, JWT, 로그인/권한 정책 분석
- `api-response-exception.md`: 공통 응답, 예외 래핑, validation, SpringDoc 분석
- `filters-interceptors.md`: Filter, Interceptor, AOP, ArgumentResolver 적용 순서 분석
- `persistence-jpa-mybatis.md`: JPA/MyBatis, mapper, transaction 기준
- `datasource.md`: H2, MySQL replication, JNDI, JPA/MyBatis 연결 분석
- `thymeleaf.md`: layout, fragment, view controller, 정적 리소스 분석
- `profiles-config.md`: profile별 설정과 `spring.config.import` 분석
- `logging.md`: Logback 설정과 로그 출력 분석

기술별 문서는 공통 정책을 반복하지 않고, 해당 기술을 분석할 때 먼저 볼 연결 지점만 짧게 정리한다.
