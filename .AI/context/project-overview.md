# Project Overview

이 문서는 저장소의 전체 성격과 기술 기준을 요약한다.


## 빌드와 런타임 기준

- Gradle 루트 프로젝트명: `spt-webfw1`
- group: `com._sptek`
- Java 기준: 17
- Spring Boot 기준: 3.2.5
- Gradle Wrapper 기준: 7.6.1
- 로컬 JDK는 17 이상을 사용할 수 있지만 생성 코드는 Java 17과 호환되어야 한다.

## 주요 기술

- Spring MVC
- Thymeleaf
- Spring Security
- JPA
- MyBatis
- H2, MySQL, MariaDB
- Redis
- SpringDoc
- Lombok

의존성 판단은 Spring Boot 3.2.5 BOM과 현재 `build.gradle`의 버전 조합을 기준으로 한다.
