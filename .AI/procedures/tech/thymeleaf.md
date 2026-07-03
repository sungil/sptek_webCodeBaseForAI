# Thymeleaf Procedure

- View Controller는 반환 view 이름, layout, fragment, 정적 리소스 연결을 함께 확인한다.
- `templates` 아래의 시스템/예제/업무 화면 경계를 유지한다.
- View 전역 예외 처리와 공통 모델 속성 주입 여부를 확인한다.
- 정적 리소스 변경 시 외부 라이브러리 minified 파일은 직접 수정하지 않는다.
