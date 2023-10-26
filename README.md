# 광명찾자 API

### 실행순서
1. 아파트 거래(매매,전월세) 데이터 정리
2. 아파트 목록_거래정보
3. 아파트 목록_시군구 조회
4. 아파트 목록_비교
5. 아파트 리스트 다운로드
6. apart.json 파일 업로드
----

#### 📌 공공데이터 OpenAPI를 이용한 광명시 아파트 거래 내역 정보를 제공합니다.
* [국토교통부_아파트매매 실거래자료](https://www.data.go.kr/data/15058747/openapi.do)
* [국토교통부_아파트 전월세 자료](https://www.data.go.kr/iim/api/selectAPIAcountView.do)
* [국토교통부_시군구 아파트 목록](https://www.data.go.kr/iim/api/selectAPIAcountView.do)

### API 종류
* 아파트 거래(매매,전월세) 데이터 정리
* 아파트 목록_거래정보
* 아파트 목록_시군구 조회
* 아파트 목록_비교
* 아파트 리스트 다운로드

----

### API 정보
> ### ✔ 아파트 거래(매매,전월세) 데이터 정리
> : 시작~종료년도만큼 데이터를 최신화합니다.

요청 주소
|메소드|URL|
|-----|---|
|POST|http://localhost:21010/trade/data?lawdCd=41210&fromYear=2010&toYear=2023|

요청 파라미터
|이름|설명|
|-----|---|
|lawdCd|지역코드|
|fromYear|시작연도|
|toYear|종료연도|

<br>

> ### ✔ 아파트 목록_거래정보
> : 거래정보를 그룹화하여 아파트를 리스트업합니다.

요청 주소
|메소드|URL|
|-----|---|
|POST|http://localhost:21010/trade/apart?lawdCd=41210|

요청 파라미터
|이름|설명|
|-----|---|
|lawdCd|지역코드|

<br>

> ### ✔ 아파트 목록_시군구 조회
> : 국토교통부_시군구 아파트 목록을 호출해 아파트 코드를 포함하는 아파트 리스트를 등록한다.
> <u>아파트 코드는 아파트 기본/상세 정보를 조회할 때 사용된다.</u>

요청 주소
|메소드|URL|
|-----|---|
|POST|http://localhost:21010/trade/apart/code?lawdCd=41210|

요청 파라미터
|이름|설명|
|-----|---|
|lawdCd|지역코드|

<br>

> ### ✔ 아파트 목록_비교
> : 거래정보/시군구 조회로 생성된 아파트 리스트를 비교해 아파트 코드를 등록한다.

요청 주소
|메소드|URL|
|-----|---|
|POST|http://localhost:21010/trade/apart/compare?lawdCd=41210|

요청 파라미터
|이름|설명|
|-----|---|
|lawdCd|지역코드|

<br>

> ### ✔ 아파트 리스트 다운로드
> : 생성된 아파트 리스트를 json파일로 다운로드 한다.

요청 주소
|메소드|URL|
|-----|---|
|GET|http://localhost:21010/trade/apart/fgm/file?lawdCd=41210|

요청 파라미터
|이름|설명|
|-----|---|
|lawdCd|지역코드|



> ### ✔ 중복 데이터 체크(경도,위도)
```
SELECT A.*
  FROM APART_LIST A
       INNER JOIN 
       (
		SELECT APART_DONG
			 , ADDRESS
			 , CONV_X
			 , COUNT(APART_NAME)
			 , MAX(APART_NAME)
		  FROM APART_LIST
		 WHERE LAWD_CD = '41210'
		 GROUP BY APART_DONG, ADDRESS, CONV_X
		 HAVING COUNT(APART_NAME) > 1
		) B
        ON A.APART_DONG = B.APART_DONG
       AND A.ADDRESS    = B.ADDRESS
```
