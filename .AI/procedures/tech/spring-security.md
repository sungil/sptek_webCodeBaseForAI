# Spring Security Procedure

- URL 접근 정책은 프로젝트 공통 `SecurityFilterChainConfig`, matcher, 프레임워크 유틸의 공통 경로 정의를 함께 확인한다.
- Swagger, H2, static resource, 로그인, 에러 페이지 예외 경로를 분리해서 확인한다.
- JWT, 세션, 권한, 역할, 약관, 사용자 상세정보 흐름을 필요한 만큼 추적한다.
- 테스트 편의용 CSRF/CORS 완화 TODO를 운영 보안 정책으로 일반화하지 않는다.
- 보안 matcher나 filter chain 순서 변경은 실행 동작 변경이므로 영향 범위를 먼저 설명한다.
