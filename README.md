# p-14141-1

이 리포지토리는 Next.js 기반 클라이언트 프론트엔드와 Kotlin + Spring Boot API 백엔드, 그리고 이를 호스팅하기 위한 Terraform (AWS) 인프라로 구성된 풀스택 서비스 프로젝트입니다.
본 루트 문서는 하위 도메인별 문서들의 빠른 접근을 돕는 통합 허브(Hub)입니다.

---

## 🏗️ 전체 아키텍처 개요

```mermaid
flowchart TD
    Client[Browser / Next.js Client] -->|HTTP / WebSocket| NPMplus[Nginx Proxy Manager]
    
    subgraph AWS EC2 (t3.micro)
        NPMplus -->|API Requests| SpringBoot[Spring Boot Backend]
        NPMplus -->|SSE / WebSockets| SpringBoot
        SpringBoot --> Redis[(Redis)]
        SpringBoot --> PostgreSQL[(PostgreSQL)]
    end

    subgraph GitHub Actions
        Push[Push to main] --> Build[Build & Push to GHCR]
        Build --> Deploy[SSM Blue/Green Deploy]
        Deploy --> NPMplus
    end
```

---

## 📚 프로젝트 구성 및 메뉴얼 허브

이 프로젝트는 역할을 명확히 분리하기 위해 4개의 핵심 디렉토리로 구성되어 있습니다. 각 디렉토리에는 심층적인 기술 및 설계 방법이 담긴 개별 문서가 존재합니다.

| 서비스 영역 | 경로 | 역할 및 기술 스택 | 문서 링크 |
|------------|----------|-------------------|-----------|
| **Backend** | `back/` | Kotlin, Spring Boot, JPA, QueryDSL, OAuth2 | [📖 백엔드 63강 설계 리뷰 보기](back/readme.md) |
| **Frontend** | `front/` | Next.js (App Router), TypeScript, Tailwind | [📖 프론트엔드 연동 가이드 보기](front/README.md) |
| **Infra** | `infra/` | Terraform, AWS EC2, VPC, Security Group | [📖 인프라 프로비저닝 가이드 보기](infra/README.md) |
| **CI/CD** | `.github/workflows/` | GitHub Actions, GHCR, Blue/Green Deploy | [📖 자동화 배포 파이프라인 가이드 보기](.github/workflows/README.md) |

---

## 🚀 로컬 환경 빠른 시작

각 파트별 상세 설정은 상단의 문서 링크들을 참고하시고, 로컬에서 가장 빠르게 띄워보는 방법은 아래와 같습니다.

### 1) 백엔드 기동
```bash
cd back
# Docker 환경이 준비되어 있다면 (DB 및 Redis 필수)
./gradlew bootRun
```

### 2) 프론트엔드 기동
```bash
cd front
pnpm install
pnpm dev
# http://localhost:3000 접속
```

### 3) 인프라 프로비저닝 (AWS 계정 필요)
```bash
cd infra
cp secrets.tf.default secrets.tf # 민감 정보 입력
terraform init
terraform apply
```

---

## 📌 확인된 구조적 개선 과제 (Anomaly Report)

현재 아키텍처 상 추가적인 보완이 논의된 주요 항목들입니다.
1. **프론트엔드 배포 자동화 부재**: 현재 `.github/workflows/` 배포는 백엔드만 담당합니다. 프론트엔드(Vercel 등) CD 연결이 누락되어 있습니다.
2. **보안 그룹 개방 및 평문 키**: `infra`의 AWS Security Group이 전체 개방 상태이며, `user_data` 셸에 DB 비밀번호가 평문으로 들어갑니다. (AWS SSM Parameter Store 활용 필요)
3. **Redis 휘발성 정책 제한**: Docker로 구동되는 Redis가 50MB 용량 및 `allkeys-lru` 정책으로 구동되어, Oauth2 세션이나 분산 락(ShedLock) 키 유실의 원인이 될 수 있습니다. 

상세 분석 내용은 별도로 작성된 **[구조적 이상점 리포트](anomaly_report.md)**를 참조하세요.
