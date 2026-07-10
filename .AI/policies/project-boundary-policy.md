# Project Boundary Policy

이 문서는 Base 코드, 프로젝트 공통 코드, 업무 코드의 변경 경계를 정의한다.

## Base 코드 우선 원칙

이 저장소의 Base 코드는 개별 프로젝트에서 가장 짧은 구현보다 전사적인 코드 일관성, 학습 비용 절감, 유지보수성, 운영 안정성을 우선한다.

- 신규 기능이나 수정은 먼저 `__webFramework`, `_projectCommon`, `example`의 기존 구조와 사용 예제를 확인한다.
- 컨트롤러, 서비스, DTO, 예외 처리, 공통 응답, 보안, 필터, 인터셉터, 설정 방식은 Base 코드와 예제 형식을 우선 따른다.
- 더 간결하거나 일반적인 구현이 있더라도 프로젝트 전체의 일관성을 해치면 임의로 도입하지 않는다.
- 기존 Base 코드로 처리할 수 있으면 중복 구현체나 별도 공통 구조를 만들지 않는다.

## 변경 경계

- `__webFramework`: Base 프레임워크다. 명시적인 프레임워크 변경 요청이 없으면 실행 동작을 수정하지 않는다.
- `_projectCommon`: 여러 도메인에서 공유하는 프로젝트 공통 확장 지점이다. 도메인 전용 로직을 넣지 않는다.
- `projectName/domainName`: 업무 코드 작성 위치를 안내하는 placeholder다. 실제 기능은 `com._sptek.{project}.{domain}` 패키지로 만든다.
- `_example`: 사용법을 보여주는 기준 자료다. 신규 업무 코드를 추가하지 않는다.

## Base 코드 변경 판단

- 공통 API, 응답 형식, 보안 정책, 필터/인터셉터 순서, 데이터소스/profile 동작 변경은 여러 프로젝트에 영향을 주는 주요 변경으로 본다.
- 이런 변경은 바로 수정하지 말고 필요성, 영향 범위, 장단점을 설명하고 사용자 확인을 받는다.
- 단순 오타, 주석, 문서, 명백히 잘못된 예제 보정은 필요한 범위에서 바로 수정할 수 있다.

## 구현 관례

- 생성자 주입과 Lombok 관례(`@RequiredArgsConstructor`, `@Slf4j`)를 우선한다. 필드 주입을 새로 추가하지 않는다.
- 신규 Java 타입은 PascalCase, 메서드/필드는 camelCase를 사용한다.
- deprecated 또는 `DEPRECATED_`, `DPRECATED_`, `deplicated` 경로는 호환성 참고용이다. 신규 코드에서 의존하지 않는다.
- 기존 코드를 답변에 인용할 때는 필요한 부분만 제시하고 import와 무관한 주석은 생략한다.

## 계층별 기준

- API 컨트롤러는 공통 응답과 예외 정책이 필요한지 판단하고, 필요한 경우 `@Enable_ResponseOfApiCommonSuccess_At_RestController`, `@Enable_ResponseOfApiGlobalException_At_RestController` 관례를 따른다.
- View 컨트롤러는 Thymeleaf 템플릿, layout/fragment, view 전역 예외 처리 관례를 함께 확인한다.
- 비즈니스 규칙 실패는 기존 `ServiceException`과 프로젝트 오류 코드 체계를 우선 사용한다.
- 보안, XSS, CORS, 암호화, 비동기, 중복 요청 방지는 기존 필터/인터셉터/커스텀 애노테이션을 우선 재사용한다.
- JPA와 MyBatis를 임의로 혼합하지 않는다. 해당 도메인이나 예제가 따르는 방식을 우선하고, 트랜잭션 경계는 서비스 계층에 명시한다.
