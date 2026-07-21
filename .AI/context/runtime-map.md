

- API 컨트롤러는 공통 응답/예외 래핑 애노테이션과 SpringDoc 문서화 관례를 함께 확인한다.
- View 컨트롤러는 Thymeleaf 템플릿, layout/fragment, view 전역 예외 처리 관례를 함께 확인한다.

- 보안은 Spring Security, JWT, 세션/권한/역할 기반 구조, 프레임워크 기본 체인, 업무 프로젝트별 `SecurityFilterChainConfig`가 함께 구성한다.
- URL 접근 정책, Swagger/H2/static 예외, 로그인/에러 페이지 동작은 보안 matcher와 프레임워크 유틸의 공통 경로 정의를 함께 확인한다.
- 영속성은 JPA와 MyBatis가 공존한다. 도메인이나 예제가 따르는 방식을 우선하고 임의로 혼합하지 않는다.
