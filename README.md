# board-msa-practice

인프런 **대용량 트래픽 & 데이터 처리 - 게시판** 강의를 따라가며 구현하는 실습 프로젝트입니다.
게시판 도메인을 여러 서비스로 나눈 MSA 구조를, **하나의 Gradle 멀티모듈(모노레포)** 로 관리합니다.

## 구조

```
board/
├── settings.gradle        # 모든 모듈을 include
├── common/                # 서비스 공용 모듈
│   ├── snowflake          # 분산 ID 생성
│   ├── event              # 이벤트 정의
│   ├── outbox-message-relay
│   └── data-serializer
└── service/               # 게시판 서비스들
    ├── article            # 게시글
    ├── comment            # 댓글
    ├── like               # 좋아요
    ├── view               # 조회수
    ├── hot-article        # 인기글
    └── article-read       # 게시글 조회(CQRS)
```

> 강의 진도에 따라 모듈을 하나씩 추가해 나갑니다.

## 기술 스택

- Java 21, Spring Boot 4.x, Gradle (Kotlin/Groovy DSL)
- JPA, MySQL, Redis, Kafka

## 실행

```bash
./gradlew :service:article:bootRun
```

---

⚠️ 강의 PPT/PDF 등 저작권이 있는 강의자료는 이 저장소에 포함하지 않습니다.
