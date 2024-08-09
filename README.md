# E-Commerce

프로젝트 소개
이커머스 서비스 플랫폼입니다.
<br/>

프로젝트 진행기간
2024.06 ~ 2024.07 (4주)

## API 명세
https://documenter.getpostman.com/view/35026905/2sA3kUGMeX

<br/>

## 🛠 기술스택


<img src="https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=Spring&logoColor=white">

<br/>

## ERD
<img width="1108" alt="스크린샷 2024-07-20 오전 3 50 34" src="https://github.com/user-attachments/assets/98201886-2080-4602-aa3a-c13b969095ef">


## 🔍 아키텍처
## v1: Redis를 사용한 이메일 인증

<img width="1108" alt="스크린샷 2024-07-20 오전 3 50 34" src="https://github.com/user-attachments/assets/2f254bd0-ac4d-4b2c-a1f4-18f09c5b1b39">

**문제점**
- 초기 이메일 인증 구현에서는 이메일 인증 코드를 저장하고 확인할 메커니즘이 필요했음.

**해결 방법**
- Redis는 이메일 인증 코드와 같은 단기적이고 중요하지 않은 데이터를 저장하기에 적합하여 선택.
- Redis는 TTL을 설정하여 설정된 시간이 지나면 자동으로 인증 코드를 만료시켜 이 사용 사례에 이상적.

**주요 사항**
- 이메일 인증 코드를 저장하고 관리하기 위해 Redis를 사용.
- 코드가 자동으로 만료되어 시스템에 불필요한 데이터가 남아있지 않으며 사용자에게 서비스 제공 가능

**아키텍처:**
- Redis가 인증 코드의 저장을 처리.
- 사용자가 제출한 인증 코드는 Redis와 대조하여 확인.

<br/>

## v2: Spring Cloud를 사용한 마이크로서비스 아키텍처

<img width="1107" alt="스크린샷 2024-07-20 오전 3 51 01" src="https://github.com/user-attachments/assets/9e7d32e2-722d-41fc-9759-3cd4c0bbecec">

**문제점:**
- 서비스가 커졌을 때를 대비해 모놀리식 아키텍쳐에서 마이크로서비스 아키텍쳐로의 변환이 필요.

**해결 방법:**
- 마이크로서비스 아키텍처를 구현하기 위해 Spring Cloud의 Eureka를 사용.

**주요 변경 사항:**
- Eureka를 서비스 디스커버리 서버로 설정하여 각 마이크로 서비스를 Eureka 서버에 등록.
- API Gateway를 사용하여 클라이언트 트래픽을 관리하고, 각 서비스로 라우팅.
- 서비스 간 통신은 Spring Cloud의 Feign Client를 사용하여 서비스 위치를 쉽게 찾고 호출.
- Spring Cloud LoadBalancer를 사용하여 서비스 간 통신 시 로드 밸런싱을 자동으로 처리.

**아키텍처:**
- Eureka 서버를 통해 마이크로서비스를 등록하고, API Gateway와 Feign Client를 통해 서비스 간 통신을 관리.

<br/>

## v3: RabbitMQ를 사용한 동시성 문제 해결

<img width="1105" alt="스크린샷 2024-07-20 오전 3 51 52" src="https://github.com/user-attachments/assets/a4c55704-1299-4b48-94e0-1812633ca2ea">


**문제점**
- 부하 테스트를 진행하는 도중 동시성 문제로 인해 서비스 간의 요청이 동시에 들어올 때 DB 정합성이 맞지 않는 현상 발생.

**해결책**
- RabbitMQ를 도입하여 메시지 큐를 사용한 비동기 통신을 구현.
- 이벤트 기반 아키텍처(EDA)를 적용하여 서비스 간의 의존성을 줄이고 실시간으로 이벤트를 처리.

**주요 변경 사항**
- RabbitMQ는 메시지의 영구 저장과 재전송을 지원하여 메시지 손실을 방지.
- 메시지 큐를 통해 서비스 간의 동시성 문제를 해결하고, 이벤트 기반 아키텍처를 구현.

**아키텍처:**
- RabbitMQ를 통해 비동기 통신을 구현하고, EDA를 적용하여 이벤트를 실시간으로 처리.

<br/>

## v4: 쿠버네티스를 통한 트래픽 라우팅 및 모니터링

<img width="1109" alt="스크린샷 2024-07-20 오전 3 52 23" src="https://github.com/user-attachments/assets/275de05d-5ede-43b8-a9ad-1dfa0db6e836">

**문제점:**
- 다양한 서비스에 대한 트래픽을 효과적으로 라우팅 필요

**해결책:**
- 인그레스를 추가하여 쿠버네티스 클러스터 내에서 외부 트래픽을 내부 서비스로 라우팅.

**주요 변경 사항:**
- 인그레스를 통해 도메인 기반 라우팅을 구현하여 트래픽을 적절한 서비스로 분배.
- 
**아키텍처:**
- 인그레스를 통해 외부 트래픽을 내부 서비스로 라우팅.
