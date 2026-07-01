# Verification Workflow

이 문서는 이 저장소의 검증 절차를 정의한다.

## 기본 원칙

- 현재 `src/test` 테스트 소스가 없다. 신규 동작이나 버그 수정에는 가능하면 `src/test/java`에 회귀 테스트를 추가한다.
- 단위 테스트는 외부 MySQL, Redis, 네트워크에 의존하지 않도록 한다. 통합 테스트가 필요하면 H2, mock 또는 명시적인 테스트 프로파일을 사용한다.
- 검증 실패가 기존 환경, 네트워크, 비밀 설정 부재 때문이면 숨기지 말고 실행한 명령, 핵심 오류, 미검증 범위를 보고한다.
- 장시간 실행 프로세스는 시작 성공을 확인한 뒤 종료한다.

## Gradle 명령

Gradle wrapper에 실행 비트가 없으므로 Unix 계열에서는 `bash ./gradlew`를 사용한다.

```bash
# Windows PowerShell 기준
.\gradlew.bat compileJava
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat bootRun --args=--spring.profiles.active=local

# Unix 계열 기준
bash ./gradlew compileJava
bash ./gradlew clean test
bash ./gradlew build
bash ./gradlew bootRun --args='--spring.profiles.active=local'
```

## 변경 유형별 기준

- 문서만 변경한 경우: 빌드나 테스트는 생략할 수 있다. 대신 변경한 문서 경로와 미실행 사유를 보고한다.
- Java 코드 변경: 최소 `compileJava`를 실행한다.
- 로직 변경 또는 버그 수정: 관련 테스트를 추가하거나 보강하고 `test`를 실행한다. 테스트가 없고 추가가 어렵다면 이유를 보고한다.
- 의존성, 설정, 패키징, 리소스 import 변경: `build`까지 수행한다.
- 보안 필터, 데이터소스, 프로파일 import, 서버 시작 과정 변경: 가능하면 `local` 프로파일 `bootRun`으로 기동을 확인한다.
- 공개 API 계약 변경: 컨트롤러, DTO validation, 공통 응답 래핑, 보안 matcher, HTTP 예제, 관련 문서를 함께 검토한다.
