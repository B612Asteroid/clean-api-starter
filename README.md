# Clean API Starter Kit (Kotlin + Spring Boot)

이 레포지토리는 실무에서 자주 사용되는 공통 기능을 모듈화한 Kotlin 기반 Spring Boot Starter Kit입니다.  
파일 업로드, 사용자 인증, Enum 반환, 공통 예외 처리 등 자주 반복되는 기능을 구조화하여 제공합니다.

## 📦 폴더 구조

- `app/` - 도메인 기능 단위 (`file`, `user`, `product` 등)
    - `domain/` - JPA Entity, Repository
    - `infra/` - Aspect, Adapter, QueryRepository 등 인프라 구성요소
    - `service/` - UseCase 및 내부 서비스 로직
    - `ui/` - Controller 레이어

- `core/` - 전역 공통 모듈
    - `config/`, `log/`, `error/`, `response/`, `security/`, `util/`
    - `infra/` - jpa 설정, redis, s3, persistence 등 인프라 공통 구성

- `CleanApiStarterApplication.kt` - 메인 진입점

## ✨ 주요 기능

- [x] 파일 업로드 및 삭제 로직 분리 (`AttachmentService`, `AttachmentAspect`)
- [x] 사용자 인증 처리 (`UserDetailsAdapter`, `UserDetailsServiceImpl`)
- [x] Enum → 라벨 응답용 컨트롤러 (`EnumsController`)
- [x] 커스텀 예외 및 공통 응답 구조
- [x] QueryDSL / Redis / S3 연동

## 📌 구조 비교
- `user/`, `file/` : full-clean-architecture 적용(`domain`, `infra`, `service`, `ui` 폴더구조 전체를 적용한 완전한 구조)
- `product/`: 간단한 하나의 도메인 처리를 위한 경량형 clean-architecture 적용

## 🚀 사용 목적

- 팀 내 공통 모듈을 GitHub 기반으로 공유
- GitLab 내부 보일러플레이트를 외부 오픈소스로 재구성
- 실제 서비스에 사용되는 코드 중, 재사용 가능한 부분만 선별

## 🛠️ Getting Started

```bash
git clone https://github.com/your-org/clean-api-starter-kit.git
cd clean-api-starter-kit

# Redis 설치를 위해 다음을 실행
sh ./docker/run-redis-docker.sh

./gradlew bootRun
```

## 🤝 Contributing

이 프로젝트는 팀원 간의 공유를 목적으로 하지만, 외부 PR도 언제든 환영합니다.