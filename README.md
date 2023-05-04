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
* Member 단건 및 전체 조회 시 양방향 연관관계 엔티티 조회가 추가적으로 쿼리가 나가던 것을 쿼리 한 번으로 개선 (N+1 문제 개선)
* MemberService-CatService, CatService-ItemHistoryService에서 Service 레이어 사이에 의존 관계를 형성하던 것들을 분리.
* 회원 탈퇴 기능 추가. 회원의 모든 기록 삭제.
* Naver, Kakao 소셜로그인 서비스 레이어에서 'OAuthService' 공통 인터페이스로 분리 및 한 메소드에서 처리되던 긴 소셜로그인 처리 과정을 관심사별로 캡슐화하여 메소드 분리.