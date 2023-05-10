# DontBeWeak-BE-Improved

팀 프로젝트 '**약해지지마**'의 백엔드 코드를 개인적으로 개선해 나갑니다.
<br>
<br>

## 기존 프로젝트 코드 보러가기
[**팀 리포지토리 링크**](https://github.com/finalproject-hanghae/DontBeWeak-BE.git)
<br>
<br>
## 개선 사항
* 모든 연관관계가 양방향 관계로 설정되어 있던 것을 Member-Cat 양방향 OneToOne 관계만 남기고 모두 단방향으로 변경.<br>이에 따라 각 서비스 레이어의 조회 로직도 함께 변경.
* Member-Cat 엔티티 OneToOne 양방향 관계 무한 참조 오류 발견 후 코드 개선.
  [**자세히**](https://mymydev.tistory.com/7)
* Member 단건 및 전체 조회 시 양방향 연관관계 엔티티 조회가 추가적으로 쿼리가 나가던 것을 쿼리 발생 1회로 개선 (N+1 문제 개선)
* MemberService-CatService, CatService-ItemHistoryService에서 Service 레이어 사이에 의존 관계를 형성하던 것들을 분리.
* 회원 탈퇴 기능 추가. 회원의 모든 기록 삭제.
* Naver, Kakao 소셜로그인 서비스 레이어에서 'OAuthService' 공통 인터페이스로 분리 및 한 메소드에서 처리되던 긴 소셜로그인 처리 과정을 관심사별로 캡슐화하여 메소드 분리.
* Controller에서 RequestDto 객체를 그대로 Service 파라미터로 넘기던 것을 Dto 내의 각각의 값을 따로 Service 파라미터로 넘기도록 변경.
* 고양이 이미지 URL을 DB에 따로 테이블을 만들어 관리하던 것을 Enum 클래스로 관리하도록 변경.<br>고양이 진화 시 저장된 URL을 가져오기 위한 DB로의 조회없이 레벨에 맞는 이미지 변경이 가능하게 함.
* 내 영양제 삭제 기능 추가.<br>
영양제 기록 엔티티에 영양제의 FK가 걸려있어 기록만 남기고 영양제 삭제 불가능한 문제 발생. (참조 무결성) itemHistory 관련 로직이 영양제 FK 없이 기능하도록 코드 리팩토링.
