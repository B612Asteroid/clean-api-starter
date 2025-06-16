# 🛠️ Clean DDD Example - 실전 설계 기반 프로젝트

## 📦 구조 요약

- `domain/` : 핵심 비즈니스 로직 (Entity, VO, 도메인 서비스)
- `app/` : UseCase, 서비스 계층
- `infra/` : Redis, DB, 외부 연동
- `ui/` : Controller 및 외부 인터페이스

## 🧪 테스트 전략

- 단위 테스트: UseCase 중심 테스트, Mock 기반
- 통합 테스트: Redis Stream은 Testcontainer 사용
- @Profile("test") 환경 분리

## ⚙️ 문제 해결 기록

| 구간 | 문제 | 대응 |
|------|------|------|
| Redis 통합 테스트 | Mocking 한계 | 실제 Redis Docker + Config override |
| CI/CD | gradlew 권한 문제 | `chmod +x gradlew` 추가 |
| Profile 누락 | 테스트 실패 | `.gitlab-ci.yml`에 `-Dspring.profiles.active=test` 추가 |

## 🔄 구조적 리버트

초기에는 DTO → Service → Command → Domain 구조 적용 시도했으나  
실제 팀원 이해도 및 규모 문제로 리버트.  
아카이빙 및 향후 확장용 구조로 보존.

## ✨ 마무리

이 프로젝트는 실전 환경에서의 구조 고민과 트레이드오프를 중심으로 설계됨.  
단순한 폴더 구성이 아니라, 실제 적용 시 “왜 이렇게 했는가”를 설명하는 문서가 포함됨.
