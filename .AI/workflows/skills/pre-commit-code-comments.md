# Pre-Commit Code Comments Workflow

이 절차는 커밋 전에 변경 대상 파일의 주석 보강 필요 여부를 점검할 때 따른다. 실제 주석 작성 기준은 `.AI/workflows/skills/write-code-comments.md`를 재사용한다.

## 목표

- 커밋 대상 변경이 코드 이해에 필요한 주석을 빠뜨리지 않았는지 확인한다.
- 필요한 경우 `write-code-comments` 절차를 적용해 클래스 상단 주석, 주요 메서드 주석, 오래된 주석을 보강한다.
- 주석 보강 후 커밋 범위와 검증 결과가 요청과 맞는지 확인한다.

## 사전 확인

1. `git status --short`로 커밋 대상 후보를 확인한다.
2. `git diff --cached --name-only`와 `git diff --name-only`를 모두 확인해 staged/unstaged 변경을 구분한다.
3. 커밋 대상 파일마다 주석 보강 필요 여부를 판단한다.
   - 새 public/protected 클래스, 메서드, 애노테이션이 추가되었는가?
   - 기존 책임 경계나 사용 조건이 달라졌는가?
   - 기존 주석이 현재 동작과 어긋나게 되었는가?
   - 변경된 코드가 Base 코드 관례, 스레드/요청 컨텍스트, 트랜잭션, 보안 정책 같은 사용 조건을 갖는가?
4. 주석 보강이 필요하면 `.AI/workflows/skills/write-code-comments.md`를 적용한다.
5. 주석 변경 후 실제 커밋 전에는 변경 범위가 요청과 맞는지 다시 확인한다.

## 완료 전 확인

1. 주석 변경이 코드 동작을 바꾸지 않았는지 diff를 확인한다.
2. Java 코드 변경이 함께 있으면 최소 `compileJava`, 로직 변경이면 관련 테스트 또는 `test`를 실행한다.
3. 커밋 전 `git diff --cached --stat`로 의도한 파일만 staged 되었는지 확인한다.
