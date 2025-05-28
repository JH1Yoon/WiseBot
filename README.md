# 🤖 WiseBot

- WiseBot의 AI 기반 질문/답변 챗봇 서비스입니다.

---

## 📌 프로젝트 목적

- #### AI 챗봇의 기본 동작 원리 구현
    - OpenAI API를 연동하여 질문을 입력하면 답변이 생성되는 프로세스를 설계합니다.

- #### 회원/비회원 관리 기능 제공
    - 로그인 기반 기능과 비회원 제한 기능(일일 질문 횟수 제한)을 통해 사용자 유형별 접근을 분리합니다.

- #### 실제 서비스 수준의 백엔드 아키텍처 구성
    - RESTful API, 예외 처리, Swagger 문서화, 테스트 코드를 포함하여 실무에 가까운 구조를 구현합니다.

- #### 사용자 친화적인 서비스 제공
    - 채팅 이력 저장, 키워드 검색, 통계 조회, 가이드 안내 등 사용자의 편의성과 UX를 고려한 기능을 제공합니다.

---

## 🌐 배포 주소

### GitHub Repository 링크

- **GitHub Repository**: [https://github.com/JH1Yoon/WiseBot](https://github.com/JH1Yoon/WiseBot)

### Swagger UI

- **Swagger UI URL**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## API 명세서

---

### 목차

- **예시 엔드포인트**:
    - 유저
        - 사용자/관리자 회원가입: `POST /v1/users/signup`
        - 로그인: `POST /v1/users/login`
        - 가입된 모든 유저 정보 확인(관리자만): `GET /v1/users/search`
        - 유저 정보 변경: `PATCH /v1/users/update`
        - 회원 탈퇴: `DELETE /v1/users/delete`
        - 관리자 통계 항목 조회: `GET /v1/users/admin/statistics`
    - 채팅
        - 채팅 저장: `POST /v1/chats`
        - 사용자의 채팅 단일 조회: `GET /v1/chats/{chatId}`
        - 회원의 채팅 이력 모두 조회: `GET /v1/chats?keyword=부트`
        - 채팅 삭제: `DELETE /v1/chats/{chatId}`
    - 가이드
        - 처음 접속한 사용자에게 사용법 안내: `GET /v1/guides`

---

## User

<details>
<summary>1. 회원가입</summary>

### 1. 회원가입

- **POST** `/v1/users/signup`

#### 요청 예시:

##### RequestBody

```json
{
  "username": "JIN HO",
  "password": "12341234",
  "email": "jh1234@naver.com"
}
```

#### 응답 예시:

``` json
{
    "username": "JIN HO",
    "email": "jh1234@naver.com",
    "roles": [
        {
            "role": "USER"
        }
    ]
}
```

#### 상태 코드:

- 201 CREATED - 성공
- 409 CONFLICT - 이미 존재하는 유저

---
</details>

<details>
<summary>2. 로그인</summary>

### 2. 로그인 (Login)

- **POST** `/v1/users/login`

#### 요청 예시:

##### RequestBody

```json
{
  "email": "jh1234@naver.com",
  "password": "12341234"
}
```

#### 응답 예시:

``` json
{
  "token": "eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL"
}
```

#### 상태 코드:

- 200 OK - 성공
- 401 CONFLICT - 로그인 실패

---
</details>

<details>
<summary>3. 가입된 모든 유저 정보 확인(관리자만)</summary>

### 3. 가입된 모든 유저 정보 확인(관리자만)

- **POST** `/v1/users/search`

#### 요청 예시:

##### Authorization

```
 token: eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL
```

#### 응답 예시:

``` json
[
    {
        "username": "JIN HO",
        "email": "jh1234@naver.com",
        "role": "USER"
    },
    {
        "username": "admin",
        "email": "admin@naver.com",
        "role": "ADMIN"
    }
]
```

#### 상태 코드:

- 200 OK - 성공
- 403 FORBIDDEN - 권한 부족 (접근 제한)

---
</details>

<details>
<summary>4. 유저 정보 변경</summary>

### 4. 유저 정보 변경

- **PATCH** `/v1/users/update`

#### 요청 예시:

##### Authorization

```
 token: eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL
```

##### RequestBody

```json
{
  "username": "JIN",
  "password": "1234"
}
```

#### 응답 예시:

``` json
{
    "username": "JIN",
    "email": "jh1234@naver.com",
    "roles": [
        {
            "role": "USER"
        }
    ]
}
```

#### 상태 코드:

- 200 OK - 성공
- 404 NOT FOUND - 발견하지 못함

---
</details>

<details>
<summary>5. 회원 탈퇴</summary>

### 5. 회원 탈퇴

- **DELETE** `/v1/users/delete`

#### 요청 예시:

##### Authorization

```
 token: eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL
```

##### RequestBody

```json
{
  "password": "1234"
}
```

#### 응답 예시:

``` json
{
    "code": 200,
    "message": "JIN을(를) 삭제했습니다."
}
```

#### 상태 코드:

- 200 OK - 성공
- 401 UNAUTHORIZED - 비밀번호가 맞지 않음

---
</details>

<details>
<summary>6. 관리자 통계 항목 조회:</summary>

### 6. 관리자 통계 항목 조회

- **GET** `/v1/users/admin/statistics`

#### 요청 예시:

##### Authorization

```
 token: eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL
```

#### 응답 예시:

``` json
{
    "totalChats": 2,
    "totalUsers": 2,
    "todayChats": 1,
    "todayUsers": 0,
    "recentChats": [
        {
            "question": "Spring Boot에서 의존성 주입이란 무엇인가요?",
            "answer": "Spring Boot에서 의존성 주입은 객체 간의 의존 관계를 외부에서 주입하는 것을 말합니다. 이는 객체 간의 결합을 느슨하게 하여 유지보수성을 높이고 코드의 재사용성을 높이는데 도움을 줍니다.\n\n의존성 주입은 Spring 프레임워크의 핵심 기능 중 하나이며, 주로 Java 클래스의 생성자, 필드 또는 메서드를 통해 의존하는 객체를 주입받는 방식으로 사용됩니다. Spring Boot는 자동으로 의존성을 관리해주는데, @Autowired 어노테이션을 사용하여 의존성 주입을 수행할 수 있습니다.\n\n의존성 주입은 객체 간의 결합을 낮추어 유연하고 확장 가능한 애플리케이션을 만들 수 있게 해주며, 테스트하기 쉽고 읽기 쉬운 코드를 작성할 수 있도록 도와줍니다.",
            "createdAt": "2025-05-23T14:41:54.013214"
        },
        {
            "question": "JPA에서 N+1 문제가 무엇인가요?",
            "answer": "N+1 문제는 JPA(Java Persistence API)를 사용하여 데이터베이스에서 데이터를 가져올 때 발생하는 성능 이슈를 의미합니다. 이 문제는 일반적으로 관계형 데이터베이스에서 일대다 또는 다대다 관계를 가지는 엔티티를 조회할 때 발생합니다.\n\nN+1 문제는 다음과 같은 상황에서 발생합니다.\n\n1. 처음에 엔티티를 조회할 때는 N개의 엔티티를 가져오는 쿼리를 실행합니다. (예: 모든 부서를 조회)\n2. 그 다음에 각 엔티티에 대해 추가적인 쿼리를 실행하여 연관된 엔티티를 가져옵니다. (예: 각 부서에 속한 직원을 조회)\n\n이렇게 되면 총 N+1개의 쿼리가 실행되어 성능에 부담을 주게 됩니다. 이는 데이터베이스와의 네트워크 트래픽이 늘어나고, 데이터베이스에 부하를 주어 시스템 전체적인 성능을 저하시키는 결과를 가져올 수 있습니다.\n\nN+1 문제를 해결하기 위해서는 JPA에서 제공하는 FetchType 옵션을 적절히 설정하거나, JPQL(Java Persistence Query Language)을 사용하여 조인을 통해 한 번에 필요한 데이터를 한 번에 조회하는 방법을 사용할 수 있습니다. FetchType.LAZY 옵션을 사용하여 지연로딩을 하거나, JPQL을 사용하여 조인을 통해 한 번에 필요한 데이터를 조회하는 방법을 사용하여 N+1 문제를 방지할 수 있습니다.",
            "createdAt": "2025-05-22T14:15:02.971342"
        }
    ]
}
```

#### 상태 코드:

- 201 CREATED - 성공
- 403 FORBIDDEN - 권한 부족 (접근 제한)

---
</details>

## Chat

<details>
<summary>1. 채팅 저장</summary>

### 1. 채팅 저장

- **POST** `/v1/chats`

#### 요청 예시:

##### Authorization

```
 token: eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL
```

##### RequestBody

```json
{
  "question" : "스프링 부트란 무엇인가요?"
}
```

#### 응답 예시:

``` json
{
    "question": "스프링 부트란 무엇인가요?",
    "answer": "스프링 부트(Spring Boot)는 자바 개발자들이 더 쉽게 스프링 프레임워크로 웹 애플리케이션을 개발할 수 있도록 도와주는 프레임워크입니다. 스프링 부트는 기본 설정을 자동화해주고, 내장된 톰캣 서버를 제공하여 별도의 서버 설정이 필요하지 않습니다. 또한 내장된 라이브러리들을 제공하여 개발자들이 빠르게 애플리케이션을 개발할 수 있도록 도와줍니다. 스프링 부트는 높은 생산성과 개발 환경의 편의성을 제공하여 많은 개발자들에게 인기가 있는 프레임워크입니다.",
    "createdAt": "2025-05-21T16:28:44.6486266"
}
```

#### 상태 코드:

- 200 OK - 성공
- 429 TOO MANY REQUESTS - 비회원 질문 수 제한(최대 3개)

---
</details>


<details>
<summary>2. 사용자의 채팅 단일 조회</summary>

### 2. 사용자의 채팅 단일 조회

- **GET** `/v1/chats/{chatId}`

#### 요청 예시:
##### PathVariable
```

 userID : 1
```
##### Authorization
```
 token: eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL
```

#### 응답 예시:
``` json
{
    "question": "스프링 부트란 무엇인가요?",
    "answer": "스프링 부트(Spring Boot)는 자바 웹 애플리케이션을 빠르고 쉽게 개발할 수 있도록 도와주는 프레임워크입니다. 스프링 부트는 스프링 프레임워크를 기반으로 한 마이크로서비스 및 웹 애플리케이션을 쉽게 구축할 수 있도록 많은 기능을 제공합니다.\n\n스프링 부트는 자동 구성(auto-configuration) 기능을 통해 개발자가 별도의 설정을 하지 않아도 기본적인 설정을 자동으로 처리해줍니다. 또한 내장형 서버(embedded server)를 제공하여 애플리케이션을 쉽게 실행하고 배포할 수 있도록 도와줍니다.\n\n스프링 부트는 스프링 프레임워크와 다양한 외부 라이브러리와의 통합을 쉽게 할 수 있도록 지원하며, 테스트, 보안, 모니터링 등 다양한 기능을 제공합니다. 이러한 기능들을 통해 개발자들은 빠르고 효율적으로 안정적인 웹 애플리케이션을 개발할 수 있습니다.",
    "createdAt": "2025-05-22T13:35:09.577233"
}
```

#### 상태 코드:
- 200 OK - 성공
- 401 UNAUTHORIZED - 접근 권한 제한
- 404 NOT FOUND - 발견하지 못함
</details>

<details>
<summary>3. 회원의 채팅 이력 모두 조회</summary>

### 3. 회원의 채팅 이력 모두 조회

- **GET** `/v1/chats?keyword=부트

#### 요청 예시:
##### RequestParam
```

 keyword : 부트
```
##### Authorization

```
 token: eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL
```

#### 응답 예시:

``` json
{
    "content": 
        [
            {
                "question": "자바와 자바스크립트의 차이점은 무엇인가요?",
                "answer": "자바와 자바스크립트는 모두 프로그래밍 언어지만, 몇 가지 중요한 차이점이 있습니다.\n\n1. 자바는 서버 측 언어이며, 자바 가상 머신(JVM)에서 실행됩니다. 반면 자바스크립트는 클라이언트 측 언어이며, 웹 브라우저에서 실행됩니다.\n\n2. 자바는 정적 타입 언어이며, 컴파일 시간에 변수의 데이터 타입을 결정합니다. 자바스크립트는 동적 타입 언어이며, 실행 시간에 변수의 데이터 타입이 결정됩니다.\n\n3. 자바는 객체 지향 프로그래밍 언어이며, 클래스와 인터페이스를 지원합니다. 자바스크립트는 객체 기반 프로그래밍 언어이며, 프로토타입을 사용하여 상속을 구현합니다.\n\n4. 자바는 복잡한 애플리케이션을 개발하는 데 많이 사용되며, 대규모 시스템을 구축하는 데 적합합니다. 자바스크립트는 주로 웹 개발에 사용되며, 사용자와 상호작용하는 동적인 웹 페이지를 만드는 데 주로 사용됩니다.",
                "createdAt": "2025-05-22T13:35:23.056684"
            },
            {
                "question": "스프링 부트란 무엇인가요?",
                "answer": "스프링 부트(Spring Boot)는 자바 웹 애플리케이션을 빠르고 쉽게 개발할 수 있도록 도와주는 프레임워크입니다. 스프링 부트는 스프링 프레임워크를 기반으로 한 마이크로서비스 및 웹 애플리케이션을 쉽게 구축할 수 있도록 많은 기능을 제공합니다.\n\n스프링 부트는 자동 구성(auto-configuration) 기능을 통해 개발자가 별도의 설정을 하지 않아도 기본적인 설정을 자동으로 처리해줍니다. 또한 내장형 서버(embedded server)를 제공하여 애플리케이션을 쉽게 실행하고 배포할 수 있도록 도와줍니다.\n\n스프링 부트는 스프링 프레임워크와 다양한 외부 라이브러리와의 통합을 쉽게 할 수 있도록 지원하며, 테스트, 보안, 모니터링 등 다양한 기능을 제공합니다. 이러한 기능들을 통해 개발자들은 빠르고 효율적으로 안정적인 웹 애플리케이션을 개발할 수 있습니다.",
                "createdAt": "2025-05-22T13:35:09.577233"
            }
        ],
        "page": 0,
        "size": 10,
        "totalElements": 2,
        "totalPages": 1,
        "last": true
}
```

#### 상태 코드:

- 200 OK - 성공
- 403 UNAUTHORIZED - 로그인 필요

---
</details>

<details>
<summary>4. 채팅 삭제</summary>

### 4. 채팅 삭제

- **DELETE** `/v1/chats/{chatId}`

#### 요청 예시:
##### PathVariable
```

 chatId : 1
```
##### Authorization

```
 token: eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL
```

#### 응답 예시:

``` json
{
    "code": 200,
    "message": "jh1234@naver.com의 1 채팅을 삭제했습니다."
}
```

#### 상태 코드:

- 200 OK - 성공
- 401 UNAUTHORIZED - 접근 권한 제한
</details>

## GUIDE
<details>
<summary>1. 처음 접속한 사용자에게 사용법 안내</summary>

### 4. 채팅 삭제

- **GET** `/v1/guides`

#### 응답 예시:

``` json
{
    "guide": "🤖 WiseBot에 오신 것을 환영합니다!\n\n- 질문을 입력하면 인공지능이 답변해 드립니다.\n- 비회원은 하루 3회 질문이 가능합니다.\n- 회원가입 시 이전 기록 조회, 무제한 질문 등의 기능이 제공됩니다.\n\n지금 바로 질문을 시작해보세요!\n"
}
```

#### 상태 코드:

- 200 OK - 성공
</details>