# Filters And Interceptors Procedure

- 요청 흐름은 Servlet Filter, Spring Security filter chain, MVC Interceptor, AOP, ControllerAdvice, ArgumentResolver 순서로 나눠 확인한다.
- 커스텀 `@Enable_*` 애노테이션과 조건부 Bean 등록 여부를 함께 확인한다.
- 보안, XSS, CORS, 암호화, 비동기, 중복 요청 방지는 기존 필터/인터셉터/커스텀 애노테이션을 우선 재사용한다.
- 순서 변경은 실행 동작 변경으로 보고 영향 범위를 먼저 설명한다.
