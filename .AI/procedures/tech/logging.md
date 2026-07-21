# Logging Procedure

- 로그 라인의 logger name, thread, MDC, timestamp를 먼저 해석한다.
- 로그만으로 결론 내리지 말고 해당 로그를 만드는 코드와 조건을 찾는다.
- Logback 설정은 현재 profile의 `spring.config.import`와 `_sptek/_webFrameworkExample/applicationProperties/logging` 설정을 함께 확인한다.
- `log`와 `log/logback`은 로컬 실행 산출물이며 커밋 대상으로 취급하지 않는다.
