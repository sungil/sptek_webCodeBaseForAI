# Project Overview

이 문서는 저장소의 전체 성격과 기술 기준을 요약한다.

## 프로젝트 성격

- 이 저장소는 SPT Framework Web Core 기반의 단일 모듈 Spring Boot 웹 애플리케이션이다.
- 단순 샘플 앱이 아니라 여러 업무 프로젝트가 공통으로 가져갈 Base 코드, 프로젝트 공통 확장 코드, 업무 코드 작성 예시를 함께 담는 기준 저장소다.
- 새 코드 작성 시 개별 기능의 단기 효율보다 Base 코드 관례와 전사 일관성을 우선한다.

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

## 코드 계층

- `__webFramework`: Base 프레임워크
- `_projectCommon`: 프로젝트 공통 확장 지점
- `projectName/domainName`: 업무 코드 작성 방식을 보여주는 placeholder

신규 업무 기능은 `__webFramework`나 `example`이 아니라 실제 프로젝트명과 도메인명을 반영한 `com._sptek.{project}.{domain}` 패키지 아래에 작성한다.
