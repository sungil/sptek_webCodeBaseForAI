# API Response And Exception Procedure

- API 컨트롤러는 공통 응답과 예외 정책이 필요한지 먼저 판단한다.
- 필요한 경우 `@Enable_ResponseOfApiCommonSuccess_At_RestController`, `@Enable_ResponseOfApiGlobalException_At_RestController` 관례를 따른다.
- 비즈니스 규칙 실패는 기존 `ServiceException`과 프로젝트 오류 코드 체계를 우선 사용한다.
- 임의의 응답 포맷이나 중복 전역 예외 처리기를 만들지 않는다.
- 공개 API 요청/응답 방식이 바뀌면 DTO validation, SpringDoc, HTTP 예제, 보안 matcher를 함께 검토한다.
