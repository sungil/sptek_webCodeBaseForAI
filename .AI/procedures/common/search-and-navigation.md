# Search And Navigation Procedure

이 문서는 저장소 분석 시 검색과 파일 읽기를 빠르게 수행하기 위한 기준을 정의한다.

## 기본 순서

1. 요청 대상을 식별한다: 클래스, 메서드, URL, 애노테이션, 설정 키, 템플릿, SQL, 로그 라인.
2. 파일명 탐색은 `fd`가 있으면 `fd`, 없으면 `rg --files`를 사용한다.
3. 내용 검색은 `rg`를 사용한다.
4. 검색 결과에서 관련성이 높은 최소 파일만 연다.
5. 클래스명이나 기능명은 기억에 의존하지 말고 현재 파일을 다시 읽는다.

## 추천 명령

```powershell
rg --files
rg -n "SearchText" src/main/java src/main/resources
rg -n "@Enable_|SecurityFilterChain|RequestMapping" src/main/java
```

`fd`가 설치되어 있으면 파일명 탐색에 사용할 수 있다.

```powershell
fd "Controller" src/main/java
fd "application-.*\\.yml" src/main/resources
```

## 읽기 범위 조절

- 먼저 직접 정의 파일, 호출자, 설정, 테스트, 예제를 찾는다.
- 프레임워크 기능이면 `SptWfwApplication`, 관련 `@Enable_*`, 조건 클래스, profile 설정을 필요한 만큼만 따라간다.
- 로그만으로 결론 내리지 말고 해당 로그를 생성하는 코드와 조건을 찾는다.
- 대량 파일 출력이나 전체 디렉터리 재귀 읽기는 피한다.
