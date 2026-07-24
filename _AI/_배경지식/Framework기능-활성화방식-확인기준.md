# Framework기능 활성화방식 확인기준

이 문서는 `__webFramework`의 기능이 어떤 방식으로 켜지고 적용되는지 확인할 때 본다.
커스텀 어노테이션만 따로 보지 않고, 실제 처리 코드와 연결해서 판단한다.

## 어노테이션 기준

`Enable_*` 어노테이션은 프레임워크 기능을 명시적으로 켜거나, 특정 위치에 프레임워크 처리를 선택 적용하기 위한 표시다.
`At_Main`, `At_RestControllerMethod`, `At_ControllerMethod`, `At_Param`, `At_DtoString` 같은 suffix는 적용 위치를 드러낸다.

`At_Main`은 전역 기능 스위치로 보고, 업무 코드의 단순 feature flag처럼 남용하지 않는다.
Controller, 메서드, 파라미터, 필드에 붙는 어노테이션은 실제 처리 지점을 함께 확인한다.

## 함께 볼 연결 지점

- 어노테이션 JavaDoc과 `@Target`
- `@HasAnnotationOnMain_At_Bean`
- `MainClassAnnotationRegister`
- `@Configuration`, `@Bean`, condition 클래스
- Filter, Interceptor, Aspect, ControllerAdvice
- ArgumentResolver, MessageConverter, Jackson Module
- `_webFrameworkExample`의 사용 예시
- 관련 profile 설정과 테스트

어노테이션 파일만 보고 동작을 단정하지 않는다. 실제 동작은 그 어노테이션을 읽는 코드에서 결정된다.
