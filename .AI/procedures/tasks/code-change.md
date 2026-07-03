# Code Change Procedure

이 문서는 기능 추가, 버그 수정, 리팩터링 요청을 처리할 때 따르는 기본 절차다.

## 기본 흐름

1. `git status --short`로 사용자 변경을 확인한다.
2. 요청 대상의 정의, 호출자, 설정, 테스트, 예제를 `rg`로 찾는다.
3. Base 코드나 프로젝트 공통 확장 지점으로 처리 가능한지 먼저 판단한다.
4. 변경 범위를 요청과 직접 관련된 파일로 제한한다.
5. 실행 동작이 바뀌는 Base 코드 변경이면 영향 범위와 장단점을 먼저 설명하고 확인을 받는다.
6. 변경 후 `.AI/procedures/common/change-verification.md` 기준으로 검증한다.
7. 완료 보고는 `.AI/procedures/common/completion-reporting.md`를 따른다.

## 구현 기준

- 주변 코드의 패키지, 계층, Lombok, 예외 처리, 응답 포맷 관례를 따른다.
- 새 공통 구조는 실제 중복이나 복잡도를 줄일 때만 만든다.
- 공개 API 변경은 컨트롤러, DTO validation, 공통 응답 래핑, 보안 matcher, HTTP 예제를 함께 검토한다.
- 설정/profile 변경은 `local/dev/stg/prd` 구조와 `spring.config.import`를 함께 검토한다.
