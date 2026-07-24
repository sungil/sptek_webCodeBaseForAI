# 업무영역 Java 패키지 확장 정책

이 문서는 새 작업영역 추가 절차로 `src/main/java/com/{companyName}/_{domainName}` 또는 `src/main/java/com/{companyName}/__{companyName}Common` 최상위 패키지가 만들어진 뒤,
그 하위에 새 Java 패키지나 파일을 추가할 때의 배치 기준을 정의한다. 사용자가 별도 구조를 명시하지 않은 경우 AI Agent는 이 문서의 구조를 우선 따른다.
아래 트리는 프로젝트의 예상되는 최대 확장 구조를 보여준다. 새 작업에서는 필요한 패키지만 생성하고, 사용하지 않는 빈 구조를 미리 만들지 않는다.

## 기본 원칙

- 작업영역 추가 절차는 최상위 업무 영역 패키지와 그 `package-info.java`까지만 만든다는 전제로 둔다.
- 이후 기능 개발 중 새 패키지나 Java 파일이 필요해지면 이 문서의 트리에서 가장 가까운 위치를 선택한다.
- 패키지를 새로 만들 때는 해당 패키지의 책임을 설명하는 `package-info.java`를 함께 만든다.
- `api`, `view`, `batch`, `message`는 실행 채널이 다르므로 같은 하위 업무 기능명이어도 패키지를 분리한다.
- API 응답 DTO, View form/model DTO, MyBatis result DTO, JPA Entity, 외부 시스템 DTO는 서로 직접 대체해서 쓰지 않는다.
- 같은 aggregate/table을 JPA와 MyBatis가 동시에 수정하는 구조는 피한다.


## 확장 트리

```text
src/main/java/com/{companyName}/_{domainName}/
├─ package-info.java            # 업무 영역 최상위 설명. 새 하위 패키지는 이 문서의 확장 트리를 따른다고 명시한다.
├─ api/                         # REST API 진입 채널. @RestController, API 공통 응답, API 예외 처리 기준을 따르는 기능을 둔다.
│  └─ {bizDomain}/              # API 하위 업무 기능 단위. 예: customer, contract, order
│     ├─ controller/            # API endpoint만 둔다. View Controller나 batch 실행 코드를 섞지 않는다.
│     ├─ service/               # API 유스케이스를 조합한다. 트랜잭션 경계와 ServiceException 변환 위치다.
│     ├─ dto/                   # API 입출력 DTO 루트다. Entity, MyBatis row, 외부 시스템 DTO를 두지 않는다.
│     │  ├─ request/            # API 요청 DTO를 둔다. query, form, body 요청값과 validation annotation을 작성한다.
│     │  └─ response/           # API 응답 DTO를 둔다. 외부 노출 필드와 Swagger schema 설명을 작성한다.
│     ├─ repository/            # API 유스케이스 전용 데이터 접근 경계다. 여러 persistence 호출을 하나의 API 조회/저장 흐름으로 묶을 때 둔다.
│     └─ support/               # 이 API 기능 안에서만 쓰는 요청값 정리, 응답값 구성, 처리 조건 판단 코드를 둔다. 다른 기능에서 호출하면 루트 support로 이동한다.
├─ view/                        # 화면 진입 채널. @Controller, Thymeleaf, form binding, View 예외 처리 기준을 따르는 기능을 둔다.
│  └─ {bizDomain}/              # View 하위 업무 기능 단위. API와 같은 업무명이어도 패키지는 분리한다.
│     ├─ controller/            # 화면 route controller를 둔다. REST API endpoint를 섞지 않는다.
│     ├─ service/               # 화면 표시, form 처리, redirect 흐름에 필요한 유스케이스를 둔다.
│     ├─ dto/                   # View 전용 DTO 루트다. API request/response DTO와 섞지 않는다.
│     │  ├─ form/               # form binding DTO를 둔다. @Valid, BindingResult 대상이다.
│     │  └─ model/              # 화면 렌더링 model DTO를 둔다. Thymeleaf 표시값 중심이다.
│     ├─ repository/            # View 전용 조회 조건이나 화면 표시용 데이터 구성이 DB 접근 경계에 필요할 때 둔다.
│     └─ support/               # 이 View 기능 안에서만 쓰는 화면 표시값 정리, form 값 보정, model 구성 코드를 둔다.
├─ batch/                       # 배치, 스케줄, 비동기 실행 진입 채널. API/View 요청 흐름과 분리한다.
│  └─ {bizDomain}/              # Batch 하위 업무 기능 단위.
│     ├─ job/                   # batch job 구성 또는 실행 단위를 둔다.
│     ├─ step/                  # step 기반 배치라면 step 구성 코드를 둔다.
│     ├─ tasklet/               # tasklet 기반 처리 단위를 둔다.
│     ├─ scheduler/             # 업무 영역 전용 scheduler trigger를 둔다. 공용 executor 남용을 피한다.
│     ├─ service/               # batch 처리용 업무 service를 둔다. API/View service와 실행 조건이 다르면 분리한다.
│     ├─ repository/            # batch 전용 대량 조회, chunk 조회, 일괄 갱신 경계를 둔다.
│     ├─ dto/                   # batch 내부 DTO를 둔다. 외부 응답 DTO와 섞지 않는다.
│     └─ support/               # 이 batch 기능 안에서만 쓰는 재시도 기준, 실행 상태 값, 배치 파라미터 구성 코드를 둔다.
├─ message/                     # Kafka, RabbitMQ, JMS 같은 메시지 브로커를 통해 들어오거나 나가는 업무 처리를 둔다. HTTP 요청 흐름과 분리한다.
│  └─ {bizDomain}/              # Message 하위 업무 기능 단위.
│     ├─ listener/              # 브로커에서 전달된 메시지를 받는 listener를 둔다.
│     ├─ publisher/             # 브로커로 메시지를 보내는 publisher를 둔다.
│     ├─ service/               # listener나 publisher가 호출하는 메시지 처리 업무 흐름을 둔다.
│     ├─ dto/                   # 브로커 메시지 payload DTO를 둔다. API/View DTO와 섞지 않는다.
│     └─ support/               # 이 message 기능 안에서만 쓰는 payload 정리, header 구성, routing key 결정 코드를 둔다.
├─ persistence/                 # DB 접근 기술별 구현을 둔다. API/View/Batch/Message 채널과 분리한다.
│  ├─ jpa/                      # JPA 기반 영속성 구현을 둔다.
│  │  ├─ entity/                # JPA Entity를 둔다. API/View DTO로 직접 사용하지 않는다.
│  │  ├─ repository/            # Spring Data JPA Repository를 둔다.
│  │  └─ support/               # JPA Specification, Querydsl, custom repository 구현처럼 JPA repository를 보완하는 코드를 둔다.
│  └─ mybatis/                  # MyBatis 기반 영속성 구현을 둔다.
│     ├─ mapper/                # MyBatis mapper interface를 둔다. XML namespace와 일치시킨다.
│     ├─ dto/                   # MyBatis parameter/result DTO를 둔다. API 응답 DTO로 직접 쓰지 않는다.
│     └─ support/               # MyBatis 페이징, ResultHandler, 동적 SQL 파라미터 구성 코드를 둔다.
├─ model/                       # DB 기술이나 외부 노출 형식과 독립적인 업무 내부 모델을 둔다.
│  ├─ vo/                       # 불변 값 객체 또는 도메인 값 타입을 둔다.
│  └─ domain/                   # 여러 서비스 메서드가 공유하는 업무 상태나 업무 처리 결과를 표현하되 JPA Entity가 아닌 모델을 둔다.
├─ integration/                 # 외부 시스템 HTTP API, 외부 SDK, 외부 파일 연계처럼 애플리케이션 밖 시스템을 호출하는 코드를 둔다.
│  └─ {externalSystem}/         # 외부 시스템별 하위 패키지다. 예: erp, crm, payment
│     ├─ client/                # 외부 호출 client wrapper를 둔다.
│     ├─ dto/                   # 외부 요청/응답 DTO를 둔다. 내부 API DTO와 섞지 않는다.
│     ├─ mapper/                # 외부 DTO와 내부 모델 사이의 값 매핑 코드를 둔다.
│     └─ support/               # 해당 외부 시스템 연동 내부에서만 쓰는 인증 헤더 생성, 서명값 처리, 응답 코드 해석 코드를 둔다.
├─ file/                        # 파일 업로드, 다운로드, 저장 정책 관련 코드를 둔다.
│  └─ {bizDomain}/              # 파일 기능이 특정 하위 업무 기능에 묶이면 해당 이름으로 나눈다.
│     ├─ controller/            # 파일 endpoint가 별도 분리될 때만 둔다.
│     ├─ service/               # 파일 검증, 저장, 조회 처리를 둔다.
│     ├─ dto/                   # multipart/form/file response DTO를 둔다.
│     └─ support/               # 파일명 정규화, 저장 경로 결정, 확장자/크기 검증 코드를 둔다.
├─ config/                      # 이 업무 영역 전용 Spring 설정을 둔다. Framework 공통 동작 변경은 여기서 바로 하지 않는다.
├─ constant/                    # 이 업무 영역 안에서만 쓰는 상수와 enum을 둔다. 회사 공통 상수는 `__{companyName}Common` 작업 영역에서 다룬다.
├─ exception/                   # 업무 오류 코드와 예외 보조 코드를 둔다. ServiceException + BaseCode 관례를 우선한다.
├─ security/                    # 업무 권한, 인증 보조, 업무 SecurityFilterChain 관련 코드를 둔다.
├─ event/                       # Spring application event와 listener를 둔다. message queue 처리와 혼동하지 않는다.
├─ validation/                  # 둘 이상의 controller나 DTO가 함께 쓰는 custom validator를 둔다. 단일 DTO 전용이면 해당 DTO 패키지에 둔다.
└─ support/                     # 업무 영역 내부 여러 채널이나 기능이 함께 쓰는 업무 처리 보조 코드를 둔다. 책임 없는 util/common 이름으로 확장하지 않는다.
```


## 판단 기준

새 파일의 위치가 바로 결정되지 않으면 아래 순서로 판단한다.

- 먼저 실행 채널을 판단한다. HTTP JSON 요청을 처리하면 `api`, HTML 화면 요청을 처리하면 `view`, 사용자의 HTTP 요청 없이 정해진 시각이나 작업 실행 명령으로 동작하면 `batch`, Kafka/RabbitMQ/JMS 등 브로커 메시지를 입력 또는 출력으로 사용하면 `message`를 선택한다.
- 실행 채널에 속하지 않는 DB 기술 구현은 `persistence`를 선택한다.
- 외부 HTTP API, 외부 SDK, 외부 파일 연계처럼 애플리케이션 밖 시스템을 호출하면 `integration/{externalSystem}`을 선택한다.
- 파일 저장, 파일 응답, multipart 처리처럼 파일 정책이 핵심이면 `file/{bizDomain}`을 선택한다.
- Spring 설정, 상수, 오류 코드, 보안, Spring event, validation은 각각 `config`, `constant`, `exception`, `security`, `event`, `validation`을 선택한다.
- 특정 하위 업무 기능 안에서만 쓰는 업무 처리 보조 코드는 해당 `{bizDomain}/support`를 선택한다.
- 여러 채널이나 여러 하위 업무 기능이 공유하지만 현재 업무 영역 안에서만 쓰는 업무 처리 보조 코드는 루트 `support`를 선택한다.
- 입력된 작업 영역이 `com.{companyName}.__{companyName}Common`이면 회사 공통 영역으로 이해하며 기본적으로 동일한 확장 트리를 적용한다.
- 개발 코드를 작성할 때 Framework코드영역에 이미 있는 기능이면 새 구현보다 기존 기능을 우선 사용하고, 이 문서의 배치 기준에 맞는 패키지에 코드를 둔다.


## JPA와 MyBatis 판단 기준

- JPA Entity와 Spring Data Repository는 `persistence/jpa` 아래에 둔다.
- MyBatis mapper interface와 SQL parameter/result DTO는 `persistence/mybatis` 아래에 둔다.
- `api`, `view`, `batch`, `message` 하위의 `repository`는 여러 persistence 호출을 하나의 조회/저장 단위로 묶거나 채널 전용 조회 조건을 캡슐화할 때 둔다.
- 단일 JPA repository 또는 단일 MyBatis mapper 호출만 필요하면 service가 직접 `persistence`의 repository나 mapper를 사용할 수 있다.
- 하나의 aggregate/table을 수정하는 주된 기술은 JPA 또는 MyBatis 중 하나로 정한다.
- 조회 최적화 때문에 보조적으로 MyBatis를 쓰는 경우에도 JPA 영속성 컨텍스트와 직접 SQL 결과가 어긋나지 않도록 트랜잭션 경계를 먼저 확인한다.


## 이름 기준

- `{domainName}`은 업무 영역 이름이며 Java package에서는 `_{domainName}` 형식을 사용한다.
- `{bizDomain}`, `{externalSystem}`처럼 업무 의미를 담는 하위 패키지명이 필요하면 AI Agent가 요청 내용과 기존 코드 명칭을 기준으로 lower camelCase 패키지명을 먼저 정해 사용한다. 예: `customer`, `customerContract`, `paymentGateway`
- 코드 작성 후에는 새로 정한 업무 연관 패키지명을 사용자에게 보고하고, 명칭 변경이 필요한지 확인한다.
- 기능 확장 시 새로운 업무 연관 패키지를 추가하기보다 기존 패키지의 이름과 책임 범위를 확장하는 것이 더 적절하다고 판단되면, 변경 사유와 영향 범위를 설명하고 사용자의 의견을 확인한 후 처리한다.
- 클래스명은 하위 업무 기능명을 PascalCase로 바꾼 뒤 역할 suffix를 붙인다. 예: `{BizDomain}ApiController`, `{BizDomain}ApiService`.
- DTO는 용도에 맞춰 `RequestDto`, `ResponseDto`, `Form`, `ViewModel`처럼 suffix를 분리한다.
- 의미가 불분명한 `Common`, `Util`, `Helper`, `Manager` 이름은 피하고, 책임을 드러내는 이름을 우선한다.
- `support`에 파일을 둘 때도 클래스명만 보고 어떤 업무 값을 다루고 어떤 역할을 하는지 알 수 있어야 하며, `CommonSupport`, `BaseSupport`처럼 책임이 넓은 이름은 피한다.


## package-info.java 기준

작업영역 추가 절차가 생성하는 최상위 `package-info.java`에는 이 문서를 확인하라는 기준을 포함한다.

```java
/**
 * {domainName} 업무 영역의 최상위 패키지다.
 *
 * 이 업무 영역에 하위 패키지나 Java 파일을 추가할 때는
 * `_AI/_작업정책/업무영역-Java패키지-확장-정책.md`를 먼저 확인하고,
 * 사용자가 별도 구조를 명시하지 않은 경우 해당 문서의 트리 구조를 따른다.
 */
package com.{companyName}._{domainName};
```

새 하위 패키지를 생성할 때는 해당 패키지의 책임과 사용 범위를 설명하는 `package-info.java`를 함께 생성한다.
`package-info.java`에는 바로 아래 하위 패키지의 역할까지만 적고, 더 깊은 구조는 이 문서나 해당 하위 패키지의 `package-info.java`가 설명하게 한다.
