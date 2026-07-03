# Write Code Comments Procedure

이 절차는 사용자가 주석 작성을 요청하거나 다른 절차가 코드 주석 보강 기준을 재사용할 때 따른다.

## 목표

- 다음 개발자가 파일을 열었을 때 클래스의 책임 경계와 주요 메서드의 사용 조건을 빠르게 이해하게 한다.
- 구현을 줄마다 설명하지 않고, 코드만으로 드러나기 어려운 의도와 제약을 짧게 남긴다.
- 오래됐거나 동작과 맞지 않는 기존 주석은 현재 코드 기준으로 수정한다.

## 사전 확인

1. 주석을 달 대상 파일, 클래스, 메서드, 애노테이션을 식별한다.
2. 대상 코드만으로 의미가 부족하면 직접 연관된 파일을 필요한 만큼 확인한다.
   - 호출자와 피호출자
   - 상속/구현 관계
   - 같은 기능의 설정, 애노테이션, 테스트
   - 기존 사용 예제
3. 주석으로 설명할 내용이 코드 사실인지, 코드 흐름에서 나온 추론인지 구분한다.
4. 동작 변경이 필요한 문제를 주석만으로 덮지 않는다. 코드 수정이 필요하면 사용자 요청 범위와 영향도를 먼저 판단한다.

## 클래스 상단 주석 원칙

클래스 상단 주석은 현재 메서드 목록을 설명하지 않는다. 대신 이 클래스에 어떤 특성의 코드가 모이는지, 어디까지가 이 클래스의 책임인지 설명한다.

권장 형식:

```java
/**
 * Spring 내부 컨텍스트나 Spring MVC 요청 컨텍스트에 의존하는 유틸리티를 모아두는 클래스.
 *
 * <p>순수 Java 유틸리티가 아니라 ApplicationContext, RequestContextHolder, Environment 처럼
 * Spring 런타임 상태가 필요한 기능을 이곳에 둔다. request thread 가 아닐 수 있는 흐름에서는
 * 강한 조회 메서드보다 *OrNull 계열 메서드를 우선 검토한다.</p>
 */
public class SpringUtil {
}
```

다른 예:

```java
/**
 * HTTP 요청/응답 처리 과정에서 반복되는 request 관련 보조 기능을 모아두는 유틸리티.
 *
 * <p>Servlet API 또는 현재 요청의 attribute/header/body/cache 상태를 해석하는 코드처럼
 * 컨트롤러, 필터, 인터셉터에서 공통으로 필요한 request 중심 로직을 이곳에 둔다.</p>
 */
public class RequestUtil {
}
```

작성 기준:

- “무엇을 담는 클래스인가”와 “무엇을 담지 않는가”가 드러나게 쓴다.
- Base 코드에서는 전사 공통 사용자가 이해할 수 있게 책임 경계를 우선 적는다.
- 현재 구현 세부사항은 꼭 필요한 경우에만 두 번째 문단에 짧게 적는다.
- 메서드 이름을 나열하거나 파일 변경 이력을 적지 않는다.
- 한국어 주석을 기본으로 하되, 코드 식별자와 프레임워크 용어는 원문을 유지한다.

## 메서드 주석 원칙

public/protected 메서드와 외부 사용 가능성이 높은 package-private 메서드를 우선 검토한다. private 메서드는 정책, 분기 이유, 복잡한 helper 역할이 있을 때만 주석을 단다.

권장 형식:

```java
/**
 * 현재 thread 에 바인딩된 HttpServletRequest 를 반환한다.
 *
 * <p>요청 컨텍스트가 없는 thread 에서는 예외를 던진다.
 * scheduler/async worker 처럼 request 가 없을 수 있는 흐름에서는 {@code getRequestOrNull()}을 사용한다.</p>
 */
public static HttpServletRequest getRequest() {
    return null;
}
```

```java
/**
 * 현재 thread 에 요청 컨텍스트가 있으면 HttpServletRequest 를 반환하고, 없으면 null 을 반환한다.
 *
 * <p>request 존재 여부가 선택적인 공통 유틸, scheduler, async worker 에서 사용한다.</p>
 */
public static @Nullable HttpServletRequest getRequestOrNull() {
    return null;
}
```

작성 기준:

- 첫 문장은 메서드가 제공하는 기능을 짧게 쓴다.
- 두 번째 문단은 예외 조건, 대체 API, 트랜잭션/스레드/요청 컨텍스트 같은 사용 조건이 있을 때만 쓴다.
- `@param`, `@return`, `@throws`는 값의 의미가 코드만으로 부족할 때만 사용한다.
- getter/setter, 단순 위임, 이름만으로 명확한 private helper에는 주석을 달지 않는다.
- “값을 변수에 넣는다” 같은 구현 설명은 쓰지 않는다.

## 애노테이션 주석 원칙

- 애노테이션 이름에 `At_*`가 포함되면 어느 위치에 적용하는 애노테이션인지 먼저 설명한다.
- `@Target`과 이름의 적용 위치가 일치하는지 확인한다.
- 첫 문장은 “어디에 붙이는 애노테이션인지”와 “무엇을 표시하거나 활성화하는지”를 함께 쓴다.
- 두 번째 문단은 해당 애노테이션을 실제로 해석하는 코드나 프레임워크 확장 지점을 설명한다.
- 활성화 조건, 적용 범위, 연결되는 프레임워크 확장 지점이 있으면 짧게 남긴다.
- 예: `At_Param`은 파라미터 위치, `At_RestControllerMethod`는 RestController 메서드 위치에 붙이는 애노테이션임을 안내한다.

권장 형식:

```java
/**
 * Controller 메서드의 파라미터에 붙여 특정 HandlerMethodArgumentResolver 적용 여부를 표시하는 마커 애노테이션.
 *
 * <p>이름의 At_Param은 이 애노테이션을 메서드나 클래스가 아니라 파라미터 위치에 적용한다는 뜻이다.
 * ArgumentResolver 구현체의 supportsParameter에서 파라미터 타입 조건과 함께 이 애노테이션을 확인하면,
 * 같은 DTO 타입이라도 명시적으로 표시한 파라미터에만 커스텀 바인딩을 적용할 수 있다.</p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Enable_ArgumentResolver_At_Param {
}
```

## 기존 주석 처리

- 현재 동작과 맞는 기존 주석은 유지한다.
- `//`로 시작하는 기존 라인 주석은 사람이 남긴 의도 설명이나 작업 흔적일 수 있으므로, 새 JavaDoc 작성과 별개로 기본 보존한다.
- `//` 라인 주석의 내용이 현재 코드와 맞지 않거나 오해를 만들면 삭제하지 말고 문맥에 맞게 최소한으로 수정해 남긴다. 단, 명백히 무의미한 임시 주석이나 코드와 충돌하는 잘못된 설명은 현재 동작 기준으로 정리한다.
- `todo`가 실제 TODO가 아니라 사용 주의나 설계 설명이면 `NOTE` 또는 JavaDoc 문장으로 정리한다.
- 오래된 구현 방식, 삭제된 의존성, 변경된 우선순위를 설명하는 주석은 현재 코드 기준으로 수정한다.
- 주석만으로 코드 문제를 숨기지 않는다. 주석이 길어져야 이해되는 코드라면 작은 리팩터링이 더 적절한지 판단한다.

## 완료 전 확인

1. 주석 변경이 코드 동작을 바꾸지 않았는지 diff를 확인한다.
2. Java 코드 변경이 함께 있으면 최소 `compileJava`, 로직 변경이면 관련 테스트 또는 `test`를 실행한다.
3. 사용자가 커밋을 요청한 것이 아니라면 staged 상태를 임의로 변경하지 않는다.
