# Code Analysis Procedure

이 문서는 Base 코드와 Base 코드 관례를 바탕으로 작성되는 실제 업무 코드를 분석할 때 따르는 실행 절차다. 저장소 공통 원칙, 보안/민감 파일 규칙, profile 정책, Base 코드 우선 원칙은 루트 `AI.md`와 `.AI/policies/**`를 기준으로 하고 여기서는 반복하지 않는다.

## 역할

- `AI.md`: 최상위 bootstrap과 필수 원칙을 정의한다.
- 이 문서: 코드 분석 요청을 받았을 때 어떤 순서로 근거를 수집하고 결론을 낼지 정의한다.
- `.codex/skills/code-analysis/SKILL.md`: Codex가 이 procedure를 찾기 위한 얇은 adapter다.

## 기본 흐름

1. 사용자가 언급한 대상을 먼저 식별한다: 클래스, 메서드, URL, 애노테이션, 프로퍼티, 템플릿, SQL, 로그 라인.
2. `git status --short`로 현재 작업트리 기준을 확인한다. 원본, staged diff, working tree 중 어떤 기준으로 답하는지 필요한 경우 명시한다.
3. `.AI/procedures/common/search-and-navigation.md` 기준으로 정의, 호출자, 설정 키, 테스트, 예제를 찾는다.
4. 관련성이 높은 최소 파일을 UTF-8로 열고, 문자열 검색만으로 부족하면 컴파일, 테스트, 로그, Spring Bean 등록 흐름까지 확인한다.
5. 결론은 코드로 확인한 사실, 코드 흐름에서 나온 추론, 일반 권고를 구분한다.

## 공통 추적 지점

분석 대상이 프레임워크 기능과 연결되면 다음 중 필요한 항목만 추적한다.

- `SptWfwApplication`의 활성화 `@Enable_*` 애노테이션
- 커스텀 `@Enable_*` 애노테이션 정의와 조건 클래스
- Bean 등록 방식: `@Component`, `@Configuration`, `@Bean`, `@Conditional`
- 필터, 인터셉터, AOP, ArgumentResolver, ControllerAdvice 적용 순서
- `__webFramework`, `_projectCommon`, `example`의 사용 예제와 확장 지점
- 관련 테스트와 `http-client` 공개 예제

## 요청 유형별 체크리스트

### 컨트롤러와 요청 흐름

- request mapping, HTTP method, consumes/produces를 확인한다.
- 클래스/메서드에 붙은 커스텀 애노테이션과 공통 응답/예외 래핑 여부를 확인한다.
- 보안 matcher, 필터, 인터셉터, argument resolver가 요청 전후에 어떤 순서로 개입하는지 확인한다.
- DTO validation, binding, model attribute, session/request attribute 사용 여부를 확인한다.
- View 반환이면 Thymeleaf 템플릿과 정적 리소스 연결을 확인한다.

### 설정과 서버 시작

- 관련 `application-{profile}.yml`과 `spring.config.import`로 로딩되는 파일을 따라간다.
- `@Conditional`, 프로파일, 메인 클래스 `@Enable_*` 조합으로 실제 Bean 등록 여부를 확인한다.
- 서버 시작 오류는 에러 메시지의 bean dependency chain, condition report, 관련 자동 설정 순서부터 확인한다.
- 데이터소스나 외부 인프라가 엮이면 Gradle 의존성, 프로파일 설정, 활성화 애노테이션을 한 묶음으로 본다.

### 로그와 런타임 동작

- 로그 라인의 logger name, thread, MDC, timestamp를 먼저 해석한다.
- 같은 요청 흐름에서 filter, interceptor, aspect, exception handler 순서를 맞춰본다.
- 로그만으로 결론 내리지 말고 해당 로그를 만드는 코드와 조건을 찾아 확인한다.

### 공통 유틸과 Base 코드

- 호출 범위가 넓은 API는 기존 호출자와 예제 사용 방식을 먼저 확인한다.
- static 상태, request thread 의존성, ApplicationContext 접근, 캐시 초기화 시점을 별도로 점검한다.
- Base 코드 변경 제안은 실행 동작 변경인지, 문서/주석/단순 보정인지 구분한다.

## 답변 기준

- 결론 앞에 확인한 기준을 짧게 밝힌다: 현재 working tree, 특정 커밋, 로그, 테스트 결과 등.
- 파일 근거가 필요한 설명은 경로와 라인을 제시한다.
- 근거가 부족한 내용은 “추론” 또는 “추가 확인 필요”로 분리한다.
- 변경 제안은 즉시 필요한 것, 나중에 해도 되는 것, 운영 리스크가 큰 것으로 나눠 우선순위를 둔다.
