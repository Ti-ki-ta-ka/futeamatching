# ⚽️ futeamatching ⚽️
FuTeamMatching은 풋살 팀관리와 경기 일정 관리를 용이하게 하는 Kotlin기반의 종합 웹 플랫폼입니다.   
이 애플리케이션을 통해 사용자는 팀을 생성하고, 팀원을 관리하며, 경기를 일정에 맞춰 모집하고, 신청할 수 있습니다.
***

## 🚀 주요 기능

- 사용자 인증 및 권한 부여
- 팀 생성, 조회, 수정, 삭제
- 팀원 신청, 취소
- 매치 생성, 조회, 수정, 삭제
- 매치 신청, 취소
- 매치, 팀 검색
- 팀 랭킹
- JWT를 이용한 보안


## 🛠 사용 기술

| 태그           | 이름                        | 상세 설명                                  |
|----------------|---------------------------|-------------------------------------------|
| **Language**   | Kotlin                     | 사용 언어                                  |
| **Version Control** | Git, Github            | 버전 관리                                  |
| **Collaboration** | Slack, Zep, Notion, Figma | 협업 툴                                    |
| **DataBase**   | PostgreSQL                 | 주 DB                                      |
| **DataBase**   | Redis                      | 인증번호 저장, 로그아웃 시 토큰 저장, 매치, 팀 목록 |
| **Back-End**   | SpringBoot                 | 프레임워크                                 |
| **Back-End**   | Spring Batch               | 스프링 프레임워크 기반 배치 라이브러리        |
| **Back-End**   | Spring Validation          | 유효성 검증                                |
| **Back-End**   | Spring Security, JWT       | 인증, 인가                                  |
| **Back-End**   | QueryDSL                   | 쿼리 생성 프레임워크                         |
| **Back-End**   | Logback                    | 로깅 라이브러리                              |
| **Back-End**   | Kotest, Mockk              | 테스트 라이브러리                             |
| **Front-End**  | React, VITE                | 프론트엔드                                   |
| **Infrastructure** | Nginx                 | 웹서버                                       |
| **Infrastructure** | EC2, S3, CodeDeploy    | 배포 도구                                    |
| **Infrastructure** | Github Action          | CI/CD 도구                                   |
| **Infrastructure** | Grafana, Prometheus, Loki, Promtail | 모니터링 |


- **Java:** 17
- **Spring Boot:** 3.3.1
- **Kotlin:** 1.9.24
- **데이터베이스:** H2, PostgreSQL


## 📋 사전 요구 사항

- JDK 17 이상
- Gradle

## 📦 의존성

이 프로젝트는 다음과 같은 의존성을 사용합니다:

- `spring-boot-starter-web`: 웹 애플리케이션을 구축하기 위한 스타터.
- `jackson-module-kotlin`: Kotlin 지원을 위한 Jackson 모듈.
- `kotlin-reflect`: Kotlin 리플렉션 라이브러리.
- `spring-boot-starter-data-jpa`: Spring Data JPA를 위한 스타터.
- `postgresql`: PostgreSQL JDBC 드라이버.
- `spring-boot-starter-validation`: Hibernate Validator와 함께 Java Bean Validation을 사용하기 위한 스타터.
- `springdoc-openapi-starter-webmvc-ui`: Spring WebMVC와 Springdoc OpenAPI 통합을 위한 스타터.
- `spring-boot-starter-security`: Spring Security를 위한 스타터.
- `jjwt`: JWT를 사용하기 위한 라이브러리.



## 📂 프로젝트 구조

이 프로젝트는 표준 Spring Boot 구조를 따릅니다:

```
boardProject
├── gradle
├── src
│   ├── main
│   │   ├── kotlin
│   │   │   └── boardProject
│   │   │       ├── config
│   │   │       ├── domain
│   │   │       │   ├── auth
│   │   │       │   │   ├── controller
│   │   │       │   │   ├── dto
│   │   │       │   │   ├── model
│   │   │       │   │   ├── repository
│   │   │       │   │   └── service
│   │   │       │   ├── post
│   │   │       │   │   ├── controller
│   │   │       │   │   ├── dto
│   │   │       │   │   ├── model
│   │   │       │   │   ├── repository
│   │   │       │   │   └── service
│   │   │       │   ├── exception
│   │   │       │   │   ├── dto
│   │   │       │   └── infra
│   │   │       │       ├── security
│   │   │       │       │   ├── config
│   │   │       │       │   └── jwt
│   │   │       │       └── SwaggerConfig
│   │   │       └── BoardProjectApplication.kt
│   └── resources
└── build.gradle.kts

```


  

