## DontBeWeak-BE-Improved

팀 프로젝트 '**약해지지마**'의 백엔드 코드를 개인적으로 개선해 나갑니다.
<br>
<br>

### 기존 프로젝트 코드 보러가기
[**팀 리포지토리 링크**](https://github.com/finalproject-hanghae/DontBeWeak-BE.git)
<br>
<br>
### 개선 사항
* 모든 연관관계가 양방향 관계로 설정되어 있던 것을 User-Cat 양방향 OneToOne 관계만 남기고 모두 단방향으로 변경.<br>
이에 따라 각 서비스 레이어의 GET 로직도 변경하였습니다.<br><br>

* User-Cat 엔티티 OneToOne 양방향 관계 무한 참조 오류 발견 후 코드 개선.<br>
[**자세히**](https://mymydev.tistory.com/7)


[//]: # (<details>)

[//]: # (<summary>JPA 쿼리 개선으로 조회 속도 향상 및 N+1 문제 해결</summary>)

[//]: # ()
[//]: # ([**개선 방법 및 상세 내용 보러가기**]&#40;https://mymydev.tistory.com/7&#41;)

[//]: # (</details>)

[//]: # (<br>)

[//]: # (<details>)

[//]: # (<summary>JPA 쿼리 개선으로 조회 속도 향상 및 N+1 문제 해결</summary>)

[//]: # ()
[//]: # ([**개선 방법 및 상세 내용 보러가기**]&#40;https://mymydev.tistory.com/7&#41;)

[//]: # (</details>)
