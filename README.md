# 📦 ReturnZone - 분실물 매칭 플랫폼

> **ReturnZone**은 현상금 기반 분실물 매칭 서비스입니다.  
> 잃어버린 사람과 발견자를 빠르게 연결하여  
> 단순 게시판을 넘어 실질적인 회수를 돕는 플랫폼입니다.

---

## 📌 프로젝트 소개

잃어버린 물건은 많지만,  
그 사이를 연결하는 체계적인 구조는 부족합니다.

ReturnZone은 분실물 등록, 현상금 설정,  
실시간 채팅 기능을 통해 분실자와 발견자를 직접 연결합니다.

단순한 분실물 게시판이 아닌,  
**"돌려주는 문화"를 만드는 연결 플랫폼**을 목표로 합니다.

---

## 📱 서비스 화면

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/574a68f4-9a59-4ddd-901d-d622cab702a1" width="320"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/0dcc2e12-6006-4094-a745-e851084329a7" width="320"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/d0d76b77-5a11-4331-8321-b759ee0a8020" width="320"/>
    </td>
  </tr>
</table>

---

## 🏗 시스템 아키텍처

<img width="2000" alt="architecture" src="https://github.com/user-attachments/assets/aa3ca769-4772-45e4-9617-26e447dc6ec8" />

---

## ⚙️ 주요 기능

| 기능 | 설명 |
|------|------|
| 🔐 로그인 | 카카오 OAuth 기반 인증 |
| 📦 분실물 등록 | 분실물 등록 및 발견물 제보 |
| 💬 실시간 채팅 | WebSocket 기반 1:1 채팅 |
| 🗺️ 지도 | 카카오 지도 기반 위치 표시 |

---

## 🔎 핵심 기술 설계 및 문제 해결

### 1️⃣ 문자열 유사도 기반 분실물 자동 추천

단순 LIKE 검색은 오탈자나 표현 차이에 취약하여  
정확도가 낮은 문제가 있었습니다.

#### 해결

- **Jaro-Winkler 문자열 유사도 알고리즘 적용**
- 분실물 itemName 간 유사도 점수 계산
- 유사도 기준 상위 N개 자동 추천

#### 결과

✔ 오탈자 및 표현 차이 보완  
✔ 검색 정확도 향상  
✔ 사용자 탐색 시간 단축  

---

### 2️⃣ WebSocket 기반 1:1 실시간 채팅 시스템 구축

댓글 기반 구조는 요청-응답 모델로 인해  
실시간성이 부족했습니다.

#### 해결

- WebSocket 기반 양방향 통신 설계
- 사용자 간 1:1 채팅 세션 관리
- 서버 Push 기반 메시지 즉시 전달
- 연결 종료 및 예외 상황 처리 로직 구현

#### 결과

✔ HTTP Polling 대비 지연 최소화  
✔ 실시간 의사소통 환경 구축  
✔ 분실물 매칭 속도 개선  

---

## 🛠 기술 스택

| 영역 | 기술 |
|------|------|
| Backend | Spring Boot |
| Database | MySQL |
| Realtime | WebSocket |
| Infra | Docker, AWS EC2, Amazon S3, Caddy |
| Auth | Kakao OAuth |

---

## 📈 프로젝트 성과

- Jaro-Winkler 기반 유사 추천 기능 구현
- WebSocket 기반 실시간 채팅 시스템 구축
- 분실자–발견자 간 직접 연결 구조 완성
- 사용자 경험과 시스템 구조를 동시에 고려한 설계
