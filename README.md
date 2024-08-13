# 🚀 E-Commere 프로젝트 소개
- 프로젝트의 주요 내용은 대용량 트래픽 처리, 동시성 제어 입니다.
- 사용자에게 최적화된 쇼핑 경험을 제공하는 이커머스 플랫폼입니다.
<br/>

## 📅 프로젝트 진행 기간
2024.06 ~ 2024.07 (4주)
<br/><br/>

## 📚 기술스택
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-007ACC?style=for-the-badge&logo=hibernate&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![nGrinder](https://img.shields.io/badge/nGrinder-000000?style=for-the-badge&logo=ngrinder&logoColor=white)
<br/><br/>

## 🛠️ 구현기능
1. 사용자 관리
    - 유저 관리
       - 회원가입
       - 로그인
       - 로그아웃
     - 위시리스트 관리
       - 위시리스트 CRUD
2. 상품 관리
    - 상품 CRUD
    - 재고 조회
3. 주문 관리
    - 주문 처리
      - 주문하기
    - 장바구니 관리
      - 장바구니 CRUD
4. 주문 내역
    - 주문 정보 조회
    - 주문 취소 및 반품

<br/>

## 🗂️ API 명세

<br/>

## 🧑‍💻 ERD
<img width="1108" alt="스크린샷 2024-07-20 오전 3 50 34" src="https://github.com/user-attachments/assets/98201886-2080-4602-aa3a-c13b969095ef">
<br/>
<br/>


## 🔍 아키텍처
## v1: Redis를 사용한 이메일 인증

<img width="1086" alt="스크린샷 2024-08-09 오전 9 06 44" src="https://github.com/user-attachments/assets/2c062b67-2ec2-4b69-8743-8337df4a470e">

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

<img width="1080" alt="스크린샷 2024-08-09 오전 9 13 01" src="https://github.com/user-attachments/assets/0cc5eb57-19ce-47eb-bd93-0aadcbdd7e03">

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

<img width="1085" alt="스크린샷 2024-08-09 오전 9 14 53" src="https://github.com/user-attachments/assets/35c8d117-9bb1-4439-9927-6aeb8e285a50">


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
