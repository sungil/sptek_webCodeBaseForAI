# Framework코드영역 코딩스타일 정책

이 문서는 `com._sptek.__webFramework` 하위의 Framework코드영역을 추가하거나 수정할 때의 코딩 스타일을 정의한다.
Framework코드영역은 여러 업무 프로젝트가 함께 사용할 기술 기반이므로, 짧은 구현보다 일관성, 예측 가능성, 설정 가능성을 우선한다.

새 Framework코드영역 코드는 특별한 이유가 없으면 현재 Framework코드영역의 패키지 구조, 클래스명, Bean 등록 방식, property 구조를 따른다.
더 나은 방식이 있더라도 기존 흐름과 다르면 영향 범위를 먼저 설명하고 변경한다.

클래스명은 역할이 드러나게 작성한다.
Filter, Interceptor, Aspect, Resolver, Converter, SecurityFilterChain 같은 Spring 확장 요소를 구현한 클래스는 가능하면 그 역할이 클래스명에 드러나야 한다.

변수명은 특별한 이유가 없으면 클래스명이나 타입명을 자연스럽게 낮춘 형태를 사용한다.

```java
private final JwtAuthenticationFilter jwtAuthenticationFilter;
private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;
```

Spring 확장 요소는 기존 Framework코드영역에서 사용하는 등록 방식을 우선 따른다.
`@Configuration` + `@Bean`, component scan, condition, custom annotation 중 무엇을 쓸지는 새 기능의 취향이 아니라 주변 코드의 기존 흐름을 기준으로 판단한다.

환경별로 달라질 수 있는 값은 property로 분리한다.
Spring Boot 표준 property가 있으면 우선 사용하고, 부족할 때만 `web-framework` 하위 custom property를 만든다.

주석은 코드만 보고 오해하기 쉬운 정책, Spring 동작 순서, 보안상 이유, profile별 차이를 설명할 때만 작성한다.

코딩 스타일을 맞추기 위한 전체 파일 포맷팅이나 광범위한 이름 변경은 하지 않는다.
요청한 작업과 직접 관련된 범위 안에서만 정리한다.
