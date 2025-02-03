# Spring Security + Thymeleaf + Oauth2-Client 예제

## 네이버, 카카오, 구글, 페이스북

### 카카오 로그인 실패
- 로그인 페이지 및 동의 화면은 뜨지만 로그인 이후, OAuth2UserService 메소드에 들어오지도 않음
- 추후 확인 예정

## Oauth2 장점
- SDK 불필요
- REST API 방식으로 해본적이 없으나 일관된 URL 형식
-	> 로그인 페이지 띄우기 URL
- 	> Redirect URL
- 사용자 정보 가져오기도 거의 유사한 형태

## Oauth2 단점
- JavaScript 방식과 다르므로 제외 설정 및 추가 설정 필요
- URI 등 정보를 알아야 함 (AI 도움 가능)

## JavaScript 소셜 로그인 참고 링크
[JavaScript 소셜 로그인](https://github.com/kdk1026/SocialLogin)
