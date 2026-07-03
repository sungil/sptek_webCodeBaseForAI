# Spring MVC Procedure

- Controller의 `@RequestMapping`, HTTP method, consumes/produces를 먼저 확인한다.
- API Controller와 View Controller 역할이 섞이면 기존 예제 구조를 참고해 `controller/api`, `controller/view` 분리를 검토한다.
- DTO validation, binding, model attribute, session/request attribute 사용 여부를 확인한다.
- View 반환이면 Thymeleaf 템플릿과 정적 리소스 연결을 함께 확인한다.
- 공통 응답/예외 래핑은 `.AI/procedures/tech/api-response-exception.md`를 함께 본다.
