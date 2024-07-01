# Lit-Map 프로젝트

## ⛓ 프로젝트 소개
책, 영화, 드라마, 웹툰과 같은 작품의 인물 관계도를 그려 해당 작품의 인물간의 이해도를 높일 수 있습니다

## 👩‍👧‍👧 BackEnd 팀원 소개
|윤태식|김숙현|최은지|
|:---:|:---:|:---:|
|[@Tedeeeee](https://github.com/Tedeeeee)|작성|작성|

#### 🛠 **기술 스택**
- Java 17

## 📜 프로젝트 설명 리스트
1. [주요 기능](#-주요-기능)   
2. [협업 규칙](#-협업-규칙) 
3. [ERD](#-ERD)
4. [API문서](#-API-문서) 
5. [서비스 구성 및 아키텍쳐](#-서비스-구성-및-아키텍처)   

------
## 🌟 주요 기능
### 회원 가입
> - 상세설명
<details><summary>기능 화면
</summary>

*Write here!*
</details>

### 로그인
> - 상세 설명
<details><summary>기능 화면
</summary>

*Write here!*
</details>

### 마이페이지
> - 상세설명
<details><summary>기능 화면
</summary>

*Write here!*
</details>

### 상세 검색 조건 
> - 상세 설명
<details><summary>기능 화면
</summary>

*Write here!*
</details>

### 작품 정렬
> - 상세 설명
<details><summary>기능 화면
</summary>

*Write here!*
</details>

### 카테고리 별 검색
> - 상세 설명
<details><summary>기능 화면
</summary>

*Write here!*
</details>

### 키워드 연관 검색
> - 상세 설명
<details><summary>기능 화면
</summary>

*Write here!*
</details>

### 유튜브 추천
> - 상세 설명
<details><summary>기능 화면
</summary>

*Write here!*
</details>

## 📈 협업 규칙
### *코드 컨벤션*
- **명명 규칙**
     - 변수, 클래스, 메서드는 camelCase 방식으로 작성하고 클래스는 파스칼을 사용한다
- **들여쓰기**
   -  else if과 같은 들여쓰기가 될 수 있는 내용은 자제한다
- **주석**
   - 다른 사람이 코드를 보면 바로 이해할 수 있도록 주석을 통해 상세히 설명
- **예외 처리**
   - 발생하는 예외를 advice를 통해 처리하고 서비스 단에 발생하는 예외도 enumType 방식으로 개발한다
- **TDD 방식**
   - 간단한 테스트를 통과시킨 후에 기능으로 도입한다.
- **DB 테이블, 필드 명**
   - 이름은 직관적으로 표기하고 snake표기법을 사용한다.

###  *커밋 전략*
각 커밋 별로 예시를 작성
- feat -> 새로운 기능 추가
   - feat : 사용자 등록 기능 추가
- fix -> 버그 수정
   - fix : 로그인 버그 수정
- refactor -> 코드 리팩터링 (기능 변경 없음)
   - refactor : 데이터베이스 연결 리팩터링
- docs -> 문서 변경
   - docs : 설명서 업데이트
- test -> 테스트 코드 추가 또는 수정
   - test : 회원가입 테스트 추가
- chore -> 위에 없는 작업을 한 경우 ( 디펜던시, 패키지, 빌드 등등 )
   - chore : validation 디펜던시 추가 

###  *작업의 흐름*
1. 생성한 브랜치에서 issue에 해당하는 작업을 진행한다
2. 생성한 브랜치에서 commit을 하고 push도 진행합니다.
3. 기능 완료가 되면 PR을 만들고 코드 리뷰를 진행합니다.
4. 완료되면 dev에 병합(merge)합니다.
   - merge 전 충돌이 나지 않도록 주의합니다.
5. 1~4번을 완료하고 dev를 테스트하여 완전하게 만들어진 기능은 main으로 PR합니다.


## 🗂 ERD

## 📚 API 문서
[<u>[Swagger API 문서](http://localhost:8080/swagger-ui/index.html)</u>] 

## 🚧 서비스 아키텍처
![아키텍쳐 사진](https://github.com/SWPY-12-Lit-map/lit_map-BackEnd/assets/118357403/42b192cf-9c2b-494a-b217-86c195b1a4ec)




