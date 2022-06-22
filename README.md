![그래픽 이미지3](https://user-images.githubusercontent.com/82200065/147528470-e637182b-6d71-4690-b12b-5a212263085b.png)
# 미세곰곰
미세곰곰은 미세먼지를 곰돌이라는 이미지로 시각화한 앱입니다.

## Installation
https://play.google.com/store/apps/details?id=com.sohee.finedust

## Features
- 메인 캐릭터가 곰돌이인 미세먼지 앱입니다.
- 미세먼지의 기준은 우리나라에서 정한 기준이 아닌 WHO의 기준을 적용하였습니다. (우리나라에서 적용한 기준은 WHO 기준 보다는 범위가 더 넓기때문입니다.)
- 첫 화면은 현위치에 관한 미세먼지 정보를 담고 있습니다.
- 위치 검색을 통해 원하는 장소를 검색할 수 있고, 또한 검색한 위치를 즐겨찾기로 저장할 수 있습니다.

## Screenshot
<img src="https://user-images.githubusercontent.com/82200065/147528813-fe6b33bc-0278-4279-8680-7fea1640ee98.jpg" width="20%" height="30%"> <img src="https://user-images.githubusercontent.com/82200065/147528819-db85b4c4-9856-4816-8848-054e23cfbcb1.jpg" width="20%" height="30%">
<img src="https://user-images.githubusercontent.com/82200065/147528832-42e17afa-402b-410c-bed5-f92c8e444815.jpg" width="20%" height="30%">
<img src="https://user-images.githubusercontent.com/82200065/147528836-1905764f-1cb0-4d13-9aed-2ddf85c1cb22.jpg" width="20%" height="30%">
<img src="https://user-images.githubusercontent.com/82200065/147529750-b02c6f52-3141-4a15-a577-d10d7ccfd89b.jpg" width="20%" height="30%">
<img src="https://user-images.githubusercontent.com/82200065/147529754-509c412c-89ba-4fcc-a834-815c8cec7257.jpg" width="20%" height="30%">
<img src="https://user-images.githubusercontent.com/82200065/147528860-20076fb7-687a-47c4-a9c3-d1dee4a85d72.jpg" width="20%" height="30%">

## Tech
- `MVVM`패턴을 이용하여 전체 로직을 구성하였습니다.
- 네트워크 통신은 `Retrofit`을 사용하였습니다.
- '공공데이터'에서 API를 이용하여 데이터를 받아와 적용합니다. 미세먼지에 대한 정보, 관측소에 대한 정보를 받아왔습니다.
- 장소를 검색하고, 좌표를 변환하는 방법으로는 kakaoAPI를 이용하였습니다.
- 위치는 GPS를 이용하는 코드를 작성하여 이용하였습니다.
- 즐겨찾기 장소를 저장하기 위해서 `Room`을 사용하였습니다.
- 리스트를 나열하는 경우에는 `RecyclerView`를 이용하였습니다.
- `DataBinding`과 `BindingAdapter` 를 이용하여 View에 데이터를 전달하였습니다.
- `NavigationView` ,`DrawerLayout` 을 이용하여 메뉴를 추가하였습니다.
- 'Coroutine&Flow' 비동기 프로그래밍을 사용하였습니다.

## process
앱을 제작하며 맞닥뜨린 문제들을 고민하고 해결했던 과정과 발전한 부분에 관한 설명입니다.
- Gps를 이용하여 위치를 가져올 때 사용자가 업데이트 버튼을 누를 경우 에러 발생

   → 실시간 업데이트가 아닌 사용자가 원할 경우에만 위치를 가져와야 하는 경우에는 getLastKnownLocation을 이용
- 가끔씩 앱을 열자마자 크래시 나는 케이스 확인

   → 디버깅 및 크래시리포트를 확인하여 API를 통해 받아오는 reponse에 사용하는 데이터가 null 로 내려오는것을 확인 및 null 체크 로직 추가 적용
- Gps 값을 불렀으나 미세먼지 관측소 에서는 변형된 값을 필요로 한 경우

   → Kakao API를 이용 Gps 값을 변형하여 적용
- GPS 허용함에도 불구하고 위치 검색이 안되는 경우

   → LocationListener 사용 시 불필요한 메소드이지만 구현해야만 위치 검색 가능 - 버전에 따른 LocationListener 처리를 다르게 해야한다는 것을 확인
- 즐겨찾기 기능을 추가 데이터를 저장할 DB를 사용해야함

   → 위도, 경도, 주소를 저장해야 하기 때문에 sharedPreferences를 사용할 수 없음. 저장, 삭제 등의 기능을 사용하기 위해 SQLite를 이용하는 것이 좋음. 하지만 SQLite는 수동적이여서 이를 자동으로 도와주는 라이브러리인 Room을 선택.
- Activity 파일에 방대한 코드

   → onClick과 BindingAdapter, DataBinding을 이용하여 정리
- 메뉴를 열고 백버튼을 누를 시 앱 종료

   → 메뉴를 눌렀을 경우 백버튼에 관한 코드 작성
- 위치 정확도 및 코드 개선

   → 위치 정보를 가져오는 방법 중 LocationListener를 사용하였는데, 정확도가 떨어지는 것 같아서 구글에서 지원하는 Fusedlocationproviderclient로 변경
- 비동기 프로그래밍 적용

   → 기존 코드에 적용되지 않던 비동기 프로그래밍인 Coroutine과 Flow를 적용
