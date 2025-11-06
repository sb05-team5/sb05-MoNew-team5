# 🧭 MoNew — 뉴스 자동 수집·게시물·댓글·알림 백엔드

## 📘 프로젝트 개요
**MoNew**는 외부 뉴스 데이터를 자동으로 수집해 게시물로 가공하고, 사용자 댓글과 알림(Notification) 기능을 제공하는 **Spring Boot 기반 백엔드 시스템**입니다.  
Spring Batch, Docker, Actuator를 통해 자동화된 뉴스 파이프라인과 안정적인 운영 환경을 구축했습니다.

---

## 🚀 주요 기능
### 📰 뉴스 자동 수집
- `@Scheduled` 기반 주기적 뉴스 수집 (`BackupScheduler`)
- RSS 또는 외부 API로부터 뉴스 데이터 수집 (`ArticleReader`)
- `ArticleProcessor`에서 정규화 및 중복 제거 처리
- 중복 기준: `sourceUrl + title` 해시 (유사도 검사 추가 가능)
- 관심사(Interest) 기준 뉴스 분류

### 💬 댓글(Comment)
- 게시물별 사용자 댓글 작성 및 관리 (`CommentController`)
- 댓글 작성 시 게시물 작성자에게 자동 알림 생성

### 🔔 알림(Notification)
- 사용자별 알림(`NotificationEntity`) 관리
- 읽음 상태(`confirmed`) 추적 가능
- `PATCH /api/notifications/{notificationId}` 로 상태 변경
- 리소스 타입(`POST`, `COMMENT`)별 연결 지원

### 🔄 Batch & Scheduler
- `backupBatch/job/ArticleBackupJobConfig`에서 뉴스 수집 작업 정의
- `BackupScheduler`로 5분 단위 실행 (`@Scheduled(cron = "0 0/5 * * * ?")`)
- 오래된 게시물 및 알림 정리 Job 수행
- `JobParameters`(timestamp) 기반 중복 실행 방지
- 예외 발생 시 `skipLimit`과 `faultTolerant`로 안정성 확보

### 🧩 Docker & 배포
- `Dockerfile`, `docker-compose.yml` 제공  
- PostgreSQL·MongoDB·Spring Boot 컨테이너 구성
- `depends_on`, `healthcheck` 설정으로 DB 준비 후 앱 실행
- JVM 메모리 최적화 (`-Xms256m -Xmx512m`)
- `.env` 파일 기반 환경 변수 관리

### 📊 모니터링 (Actuator)
- `/actuator/health`, `/actuator/metrics` 활성화
- 주요 모니터링 지표:
  - 수집 성공률, 재시도 횟수
  - 중복 차단율, 게시물 생성 속도
  - 알림 읽음률, DB 커넥션 풀 상태
- Prometheus / Grafana 연동 가능

---

## 🧱 시스템 구조

```
[News Source]
   │
   ▼
[Reader] → [Processor] → [Writer]
   │
   ▼
[Post Service] → [Comment Service] → [Notification Service]
   │
   ▼
[User API]
```

---

## 📂 패키지 구조
```
src/main/java/com/sprint/monew/
 ┣ article/           # 게시물(뉴스) 관리
 ┣ articleBackup/     # 뉴스 수집 배치 Job
 ┣ comment/           # 댓글 기능
 ┣ notification/      # 알림 기능
 ┣ interest/          # 관심사/구독 관리
 ┣ common/            # 공통 유틸/설정
 ┣ global/exception/  # 예외 처리
 ┣ config/            # Batch, JPA, Docker, Actuator 설정
```

---

## ⚙️ 기술 스택
| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.x |
| ORM | Spring Data JPA |
| Database | PostgreSQL |
| Batch/Scheduler | Spring Batch / `@Scheduled` |
| Monitoring | Spring Actuator |
| Deploy | Docker / Docker Compose |
| Build Tool | Gradle |
| Mapper | MapStruct |
| Extra | Cursor Pagination, Auditing, ExceptionHandler |

---

## ▶️ 실행 방법
### Docker Compose
```bash
docker compose up -d --build
```
> 접속: [http://localhost:8081](http://localhost:8081)  
> 주요 엔드포인트: `/actuator/health`, `/api/articles`, `/api/notifications`

### 로컬 실행
```bash
./gradlew clean bootRun
```
> `application.yml`의 DB 설정을 환경에 맞게 수정하세요.  
> 기본값: `jdbc:postgresql://localhost:5432/monew`

---

## 🌐 주요 API
| 기능 | Method | Endpoint | 설명 |
|------|---------|-----------|------|
| 게시물 조회 | GET | `/api/articles?cursor=...&size=...` | 커서 기반 페이지네이션 |
| 댓글 등록 | POST | `/api/comments` | 댓글 작성 |
| 알림 목록 | GET | `/api/notifications` | 사용자 알림 조회 |
| 알림 읽음 처리 | PATCH | `/api/notifications/{id}` | 알림 상태 변경 |
| 수집 트리거 | POST | `/admin/ingest` | 뉴스 수집 수동 실행 (선택) |

---

## 🧾 환경 변수 (.env)
```
SPRING_PROFILES_ACTIVE=prod
DB_HOST=postgres
DB_PORT=5432
DB_USER=appuser
DB_PASSWORD=apppass
AWS_ACCESS_KEY=******
AWS_SECRET_KEY=******
AWS_REGION=ap-northeast-2
```

---

## 🧪 테스트
- 단위 테스트: JUnit5 / MockMvc
- 배치 통합 테스트: `JobLauncherTestUtils`
- 로컬 테스트: `docker compose up` 후 Postman으로 API 검증

---

## 📈 개선 로드맵
- NLP 기반 뉴스 분류/요약
- WebSocket 실시간 알림
- Prometheus + Grafana 모니터링 대시보드
- GitHub Actions CI/CD 구축
- Redis 큐 기반 비동기 알림 처리

---

## 👥 팀 구성
| 이름   | 역할        | 주요 담당 |
|------|-----------|-----------|
| 정서연  | 관심사 관리    |
| 류승민 | 뉴스 기사 관리  |
| 김준희 | 사용자 관리    |
| 김재민 | 댓글 관리     |
| 임승택 | 알림 , 예외처리 |

---

## 📚 License
본 프로젝트는 **학습 및 데모 목적**으로 제작되었습니다.  
외부 뉴스 API 사용 시 관련 서비스의 **이용 약관 및 저작권 정책을 준수**해야 합니다.
