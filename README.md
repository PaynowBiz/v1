# 토스페이먼츠 페이나우비즈 v1 API 연동 규격서

[1. 개요](#1-개요) <br>
[2. URL 정보](#2-url정보) <br>
[3. servicecode](#3-servicecode) <br>
[4. data](#4-data) <br>
[5. 주의사항](#5-주의사항) <br>
[6. 요청 정보](#6-요청정보) <br>
　[6-1. 지점관리](#6-1-지점관리-servicecode--branch) <br>
　[6-2. 영업사원관리](#6-2-영업사원관리-servicecode--member) <br>
　[6-3. 거래처관리](#6-3-거래처관리-servicecode--customer) <br>
　[6-4. 거래/정산 내역 조회](#6-4-거래정산-내역-조회-servicecode-inpayments-settlements) <br>
　[6-5. 결제취소](#6-5-결제취소-servicecode--cancel) <br>
　[6-6. 결제취소(상점 주문번호)](#6-6-결제취소상점-주문번호-servicecode--cancelshopoid) <br>
　[6-7. 상품관리](#6-7-상품관리-servicecode--goods) <br> 
　[6-8. 부분취소](#6-8-부분취소-servicecode--partcancel) <br>
[7. 응답 정보](#7-응답정보) <br>
　[7-1. status](#7-1-응답-status) <br>
　[7-2. 지점, 영업사원, 거래처, 상품](#7-2-지점-영업사원-거래처-상품-servicecode-inbranch-member-customer-goods) <br>
　[7-3. 거래/정산 내역 조회](#7-3-거래정산-내역-조회-servicecode-inpayments-setllements) <br>
　[7-4. 결제취소](#7-4-결제취소-servicecode--cancel) <br>
　[7-5. 결제취소(상점 주문번호)](#7-5-결제취소상점-주문번호-servicecode--cancelshopoid) <br>
　[7-6. 부분취소](#7-6-부분취소-servicecode--partcancel) <br>
　[7-7. 거래내역조회 응답값 설명](#7-7-거래내역조회-응답값-설명-servicecode--payments) <br>
　[7-8. 정산내역조회 응답값 설명](#7-8-정산내역조회-응답값-설명-servicecode--settlements)<br>
　[7-8-1. 정산 매입상태 설명](#7-8-1-정산-매입상태-설명) <br>
　[7-9. 결제취소 응답값 설명](#7-9-결제취소-응답값-설명-servicecode-incancel-cancelshopoid-partcancel) <br>
[8. 문의하기](#8-문의하기) <br>
 
## 1. 개요
 PaynowBiz를 이용하는 가맹점(상점) 이하, 상점에서 영업사원과 고객(거래처) 이하, 거래처 정보를 PaynowBiz 서버로 연동하여 동기화 하고, 거래내역 및 정산내역을 조회 할 수 있으며, 결제 취소를 돕는 연동가이드 문서 입니다.

* [테스트 페이지](https://paynowbiz.tosspayments.com/sample/v1View.do) 에서 테스트로 발급된 상점ID(bizbiz)로 테스트해 볼수 있습니다.
* [Request.java](https://github.com/PaynowBiz/v1/blob/main/Request.java) 를 다운받아 코딩 하면 됩니다.
<br>

## 2. URL정보
`https://upaynowapi.tosspayments.com/2/v1/{mertid}/{servicecode}&data=WLqCPfNlbzpJDJKy5WCX6rYg3==`
>**`mertid`**[PaynowBiz 상점ID]  **`servicecode`**[서비스코드] **`data`**[요청할 정보를 암호화 한 값]
<br>

## 3. servicecode
 0) [branch](#6-1-지점관리-servicecode--branch) : 지점 등록/수정
 1) [member](#6-2-영업사원관리-servicecode--member) : 상점 영업사원 등록/수정
 2) [customer](#6-3-거래처관리-servicecode--customer) : 거래처 등록/수정/삭제
 3) [payments](#6-4-거래정산-내역-조회-servicecode-inpayments-settlements) : 거래내역 조회
 4) [settlements](#6-4-거래정산-내역-조회-servicecode-inpayments-settlements) : 정산내역 조회
 5) [cancel](#6-5-결제취소-servicecode--cancel) : 결제취소
 6) [cancelShopOid](#6-6-결제취소상점-주문번호-servicecode--cancelshopoid) : 결제취소(상점 주문번호)
 7) [goods](#6-7-상품관리-servicecode--goods) : 상품 등록/수정
 8) [partCancel](#6-8-부분취소-servicecode--partcancel) : 부분취소
<br>

## 4. data
요청할 정보를 json으로 만든 후 아래 단계로 감싼 후 POST방식으로 호출 합니다.
 * [AES-128](https://github.com/PaynowBiz/v1/blob/main/AESUtil.java) 암호화 
 * [BASE64](https://docs.oracle.com/javase/8/docs/api/java/util/Base64.Encoder.html) 인코더
 * [URLEncoder(UTF-8)](https://docs.oracle.com/javase/8/docs/api/java/net/URLEncoder.html)
<br>

## 5. 주의사항
* `servicecode in (member, customer)` 
  >`app2appyn`= Y 는 영업사원ID에 `initialname`(PaynowBiz제공) 이 붙으므로, 상점 에서 연동방식을 정확히 확인 후 넘겨야 합니다.<br>
  >`userphone` `custphone` 휴대폰번호가 없는 경우 010으로 시작하는 11자리 임의번호를 기재하시기 바랍니다.
* `servicecode in (member, branch)`
  >`userpw` 최초 호출시만 저장됩니다. 비밀번호 변경  재요청시 수정이 되지 않습니다.<br> 이후 변경을 원하는 경우 [PaynowBiz상점관리자](https://paynowbiz.tosspayments.com/pnbmert/) 또는 PaynowBiz앱([안드로이드](https://play.google.com/store/apps/details?id=com.lguopg.paynowauth&hl=ko&gl=US)/[iOS](https://apps.apple.com/kr/app/%ED%8E%98%EC%9D%B4%EB%82%98%EC%9A%B0-%EB%B9%84%EC%A6%88-%EC%9D%B8%EC%A6%9D%EC%9A%A9/id1261678163) )에서 변경 가능합니다.
* 페이나우비즈 API와 통신하는 서버는 HTTP 및 HTTPS 가 지원 되어야 하며, TLS 1.2를 지원하는 https 환경이어야 합니다. <br>
  > 구) 암호화 프로토콜인 SSL2.0 / SSL3.0 , TLS1.0, TLS1.1 은 차단되며, TLS1.2 프로토콜만 허용합니다.
* 언어별 TLS1.2 지원환경
  >`JAVA` JDK8 (1.8) 이상
  >`PHP` 5.6 이상 + openssl 1.0.1 이상
  >`.net` .NET Framework 4.7 이상  
<br>

## 6. 요청정보
<br>

## 6-1. 지점관리 `servicecode = branch`
Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,숫자|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`app2appyn`|선택|1|Y(기본값) or N|`Y` = initialname_`branchid`<br>`N` = `branchid`|
|`list`||||<span style="background-color:beige;">_아래 정보를 배열로 처리_</span>|
|`branchid` **[PK]**|필수|19|영문, 숫자|지점ID|
|`branchnm`|필수|65||지점명|
|`userphone`|필수|11|숫자(-제외)|지점 관리자 휴대폰번호|
|`usernm`|필수|128||지점 관리자 명|
|`validyn`|필수|1|Y or N|활성화상태|
|`userpw`|필수|128|영문, 숫자, 특수문자 포함 8자 이상|지점 관리자 패스워드|
|`branchaddress1`| 선택|128||지점 주소1|
|`branchaddress2`|선택|128||지점 주소2|
|`branchzip`|선택|5||지점 우편번호|
|`branchtel`|선택|20|숫자|지점 전화번호|

**_Data Type of json is STRING_**
```json
{
  "certkey": "{PanowBiz에서 발급받은 인증키}",
  "reqid": "{yyyyMMddHHmmssSSS}",
  "app2appyn": "N",
  "list": [
    {
      "branchid": "Gangnam",
      "branchnm": "강남점",
      "userphone": "01012341001",
      "usernm": "강토스",
      "validyn": "Y",
      "userpw": "change here"
    },
    {
      "branchid": "Yeoksam1",
      "branchnm": "역삼1호점",
      "userphone": "01012341002",
      "usernm": "역토스",
      "validyn": "Y",
      "userpw": "change here",
      "branchaddress1": "서울특별시 강남구 테헤란로 131 (역삼동, 한국지식재산센터)",
      "branchaddress2": "15층 토스페이먼츠",
      "branchzip": "06133",
      "branchtel": "15447772"
    }
  ]
}
```
<br>

## 6-2. 영업사원관리 `servicecode = member`
Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,숫자|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`app2appyn`|필수|1|Y or N|**App(Web) to App 상점 유무**|
|`list`||||_아래 정보를 배열로 처리_|
|`userid` **[PK]**|필수|19|영문, 숫자|영업사원ID|
|`usernm`|필수|128||영업사원명|
|`userphone`|필수|11|숫자|영업사원 휴대폰번호|
|`validyn`|필수|1|Y or N|활성화상태|
|`userpw`|필수|128|영문, 숫자, 특수문자 포함 8자 이상|영업사원 패스워드|
|`branchid`|선택|19|영문, 숫자|지점ID|

**_Data Type of json is STRING_**
```json
{
  "certkey": "{PanowBiz에서 발급받은 인증키}",
  "reqid": "{yyyyMMddHHmmssSSS}",
  "app2appyn": "Y",
  "list": [
    {
      "userid": "biz001",
      "usernm": "김토스",
      "userpw": "change here",
      "userhp": "01012340001",
      "validyn": "Y",
      "branchid": "Gangnam"
    },
    {
      "userid": "biz002",
      "usernm": "이토스",
      "userpw": "change here",
      "userhp": "01012340002",
      "validyn": "Y",
      "branchid": "Yeoksam1"
    },
    {
      "userid": "biz003",
      "usernm": "박토스",
      "userpw": "change here",
      "userhp": "01012340003",
      "validyn": "Y"
    }
  ]
}
```
<br>

## 6-3. 거래처관리 `servicecode = customer`
|Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,숫자|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`app2appyn`|필수|1|Y or N|**App(Web) to App 상점 유무**|
|`list`|||| _아래 정보를 배열로 처리_|
|`userid` **[PK]**|필수|19|영문, 숫자	|영업사원ID|
|`custcode` **[PK]**|필수|100|영문, 숫자|거래처 코드|
|`custphone` **[PK]**|필수|11|숫자|거래처 연락처처|
|`custname`|필수|100||거래처명|
|`useyn`|필수|1|Y or N or D or U|Y:사용, N:사용안함, D:삭제, U:수정(chgcustphone변경시)|
|`custaddress1`|필수|128||거래처 주소1|
|`custaddress2`|선택|128||거래처 주소2|
|`custzip`|선택|5|숫자|거래처 우편번호|
|`businessno`|선택|10|숫자|거래처 사업자번호|
|`custfax`|선택|20|숫자|거래처 팩스번호|
|`custemail`|선택|128|영어/숫자/특수문자|거래처 이메일주소|
|`chgcustphone`|선택|11|숫자|변경 거래처 휴대폰번호(useyn:U)|
|`bigo`|선택|128||비고|

**_Data Type of json is STRING_**
```json
{
  "certkey": "{PanowBiz에서 발급받은 인증키}",
  "reqid": "{yyyyMMddHHmmssSSS}",
  "app2appyn": "Y",
  "list": [
    {
      "userid": "{mertid}",
      "custcode": "A001",
      "custname": "역삼약국",
      "custaddress1": "서울시 강남구 역삼동 한국지식재산센터",
      "custaddress2": "15층 역삼약국",
      "custzip": "12345",
      "custphone": "027778888",
      "custfax": "0212345678",
      "custemail": "paynowbiz@tosspayments.com",
      "useyn": "Y"
    },
    {
      "userid": "biz001",
      "custcode": "A001",
      "custname": "역삼약국",
      "custaddress1": "서울시 강남구 역삼동 한국지식재산센터",
      "custaddress2": "15층 역삼약국",
      "custzip": "12345",
      "custphone": "01077775678",
      "chgcustphone": "01099995678",
      "custfax": "0212345678",
      "custemail": "paynowbiz@tosspayments.com",
      "useyn": "U",
      "bigo": "VIP고객"
    }
  ]
}
```
<br>

## 6-4. 거래/정산 내역 조회 `servicecode in(payments, settlements)`
|Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,숫자|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`startdt`|필수|8|숫자|시작일[YYYYMMDD]|
|`enddt`|필수|8|숫자|종료일[YYYYMMDD]|
|`oid`|선택|18|영문,숫자|PaynowBiz 주문번호|
|`tid`|선택|24|영문,숫자|TossPayments 거래번호|
|`userid`|선택|19|영문,숫자|직원ID| 

▶ _**`startdt`** ~ **`enddt`** 의 조회기준 은 아래와 같습니다._<br>
▶ 거래내역조회 = **`paydate`** OR **`canceldate`**<br>
▶ 정산내역조회 = **`adjustdate`**<br>
▶ ① 1회 조회시 180일 이상 조회 불가, ② 1회 조회시 1만건 이하만 가능<br>

**_Data Type of json is STRING_**
```json
{
  "certkey":"{PanowBiz에서 발급받은 인증키}", 
  "reqid":"{yyyyMMddHHmmssSSS}", 
  "startdt":"20210101", 
  "enddt":"20210131",
  "oid":"{PaynowBiz 주문번호}",
  "tid":"{TossPayments 거래번호}",
  "userid":"직원ID" 
}
```
<br>

## 6-5. 결제취소 `servicecode = cancel`
Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,숫자|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`type`|필수|10|영문|카드:card,현금:cash,이체:transfer)|
|`oid`|필수|18|영문,숫자|PaynowBiz 주문번호|
|`tid`|선택|24|영문,숫자|TossPayments 거래번호|

**_Data Type of json is STRING_**
```json
{
  "certkey":"{PanowBiz에서 발급받은 인증키}", 
  "reqid":"{yyyyMMddHHmmssSSS}", 
  "type":"card", 
  "oid":"{PaynowBiz 주문번호}",
  "tid":"{TossPayments 거래번호}"
}
```
<br>

## 6-6. 결제취소(상점 주문번호) `servicecode = cancelShopOid`
Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,숫자|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`type`|필수|10|영문|카드:card,현금:cash,이체:transfer|
|`shop_oid`|필수|64|영문,숫자|상점 주문번호|
|`oid`|선택|18|영문,숫자|PaynowBiz 주문번호|
|`tid`|선택|24|영문,숫자|TossPayments 거래번호|

**_Data Type of json is STRING_**
```json
{
  "certkey": "{PanowBiz에서 발급받은 인증키}",
  "reqid": "{yyyyMMddHHmmssSSS}",
  "type": "transfer",
  "shop_oid": "{상점에서 간편결제 요청한 주문번호}",
  "oid": "{PaynowBiz 주문번호}",
  "tid": "{TossPayments 거래번호}"
}
```
<br>

## 6-7. 상품관리 `servicecode = goods`
Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,숫자|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`list`||||_아래 정보를 배열로 처리_|
|`no` **[PK]**|필수|20||상점에서 관리하는 상품번호|
|`name` |필수|25||상품명|
|`price`|필수|9|숫자|판매가격|
|`visible`| 필수|1|0:비노출,1:노출|상품노출여부|
|`taxfree`|필수|1|0:과세,1:면세|부가세설정(스마트팜상점은 무조건 과세)|
|`group1`|선택|128||대분류|
|`group2`|선택|128||중분류|
|`group3`|선택|128||소분류|
|`medictype`|선택|1|0:일반,1:전문|의약품설정(스마트팜상점 전용)|

**_Data Type of json is STRING_**
```json
{
  "certkey": "{PanowBiz에서 발급받은 인증키}",
  "reqid": "{yyyyMMddHHmmssSSS}",
  "list": [
    {
      "no": "biz-001-0001",
      "name": "아메리카노(HOT)",
      "price": "1500",
      "visible": "1",
      "taxfree": "0",
      "group1": "음료",
      "group2": "커피",
      "group3": "",
      "mdictype": ""
    },
    {
      "no": "biz-001-0002",
      "name": "아메리카노(ICE)",
      "price": "2000",
      "visible": "0",
      "taxfree": "0",
      "group1": "",
      "group2": "커피",
      "group3": "",
      "medictype": ""
    }
  ]
}
```
<br>

## 6-8. 부분취소 `servicecode = partCancel`
Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,숫자|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`type`|필수|10|영문|카드:card,이체:transfer|
|`cancelamount`|필수|10|숫자|부분취소금액|
|`cancelreason`|필수|100||취소사유|
|`oid`|부분필수|18|영문,숫자|PaynowBiz 주문번호, **shop_oid 넘기면 oid 안 넘겨도됨**|
|`shop_oid`|부분필수|64|영문,숫자|App2App 간편결제시 넘긴 상점 주문번호|
|`tid`|선택|24|영문,숫자|TossPayments 거래번호|

**_Data Type of json is STRING_**
```json
{
  "certkey":"{PanowBiz에서 발급받은 인증키}", 
  "reqid":"{yyyyMMddHHmmssSSS}", 
  "type":"card", 
  "cancelamount":"11000", 
  "cancelreason":"치약*2ea 환불", 
  "oid":"{PaynowBiz 주문번호}",
  "tid":"{TossPayments 거래번호}"
}
```
<br>

## 7. 응답정보
<br>

## 7-1. 응답 status
result.status|Description
:-----:|-----|
200|전체성공
201|전체실패
202|일부성공
101|정상적인 호출이 아닌 경우
104|servicecode, certkey가 맞지 않을 경우
405|mertid, certkey를 찾을수 없을 경우 또는 JSON 오류 
802|reqid, app2appyn이 없을 경우
999|시스템 오류가 있을 경우
<br>

## 7-2. 지점, 영업사원, 거래처, 상품 `servicecode in(branch, member, customer, goods)`
**_Data Type of json is STRING_**
```json
{
  "result": {
    "status": "200",
    "msg": "success",
    "service": "paynowbiz",
    "function": "/v1/{mertid}/{servicecode}",
    "data": "",
    "result": "/K+VQ9mi4fuWXGWLqCPfNlbztOpJDJKy5WCXeb+/vRej42gfpEfXLzQok+c6rYg3",
    "success": true
  }
}
```
_**`result.status in (201, 202)` 인 경우 `result.result` 를 복호화 하여 `result.result.list.err` 의 실패 원인을 확인**_
![image](https://user-images.githubusercontent.com/79068689/111751929-67293780-88d8-11eb-8c2f-bbdd76413379.png)
<br>

## 7-3. 거래/정산 내역 조회 `servicecode in(payments, setllements)`
**_Data Type of json is STRING_**
```json
{
  "result": {
    "status": "200",
    "msg": "success",
    "service": "paynowbiz",
    "function": "/v1/{mertid}/{servicecode}",
    "data": "",
    "result": [
      {
        "userid": "bizbiz",
        "usernm": "유*주",
        "amount": "50000",
        "servicename": "카드",
        "status": "승인성공",        
        "oid": "{PaynowBiz 주문번호}",
        "tid": "{TossPayments 거래번호}",
        "cardnum": "49063212******43",
        "authnum": "12345678",
        "paydate": "20210316143540",
        "canceldate": "",
        "financecode": "31",
        "financename": "비씨",
        "installment": "0",
        "cardflag": "신용",
        "productinfo": "",
        "memo": "",
        "cashbill": "",
        "reserved1": "",
        "reserved2": "",
        "reserved3": "",
        "reserved4": "",
        "reserved5": "",
        "mgrcode": "bizbiz",
        "custcode": "A002",
        "custname": "도래울약국",
        "medictype":"일반"
      },
      {
        "totalcnt": 1
      }
    ],
    "success": true
  }
}
```
<br>

## 7-4. 결제취소 `servicecode = cancel`
**_Data Type of json is STRING_**
```json
{
  "result": {
    "status": "200",
    "msg": "success",
    "service": "paynowbiz",
    "function": "/v1/{mertid}/cancel",
    "data": "",
    "result": {
      "msg": "취소성공",
      "code": "0000",
      "oid": "{PaynowBiz 주문번호}",
      "tid": "{TossPayments 거래번호}"
    },
    "success": true
  }
}
```
<br>

## 7-5. 결제취소(상점 주문번호) `servicecode = cancelShopOid`
**_Data Type of json is STRING_**
```json
{
  "result": {
    "status": "200",
    "msg": "success",
    "service": "paynowbiz",
    "function": "/v1/{mertid}/cancelShopOid",
    "data": "",
    "result": {
      "msg": "취소성공",
      "code": "0000",
      "shop_oid": "{상점에서 결제 요청한 주문번호}",
      "oid": "{PaynowBiz 주문번호}",
      "tid": "{TossPayments 거래번호}"
    },
    "success": true
  }
}
```
<br>

## 7-6. 부분취소 `servicecode = partCancel`
**_Data Type of json is STRING_**
```json
{
  "result": {
    "status": "200",
    "msg": "success",
    "service": "paynowbiz",
    "function": "/v1/{mertid}/partCancel",
    "data": "",
    "result": {
      "msg": "부분취소 성공",
      "code": "0000",
      "oid": "{PaynowBiz 주문번호}",
      "tid": "{TossPayments 거래번호}",
      "paymentamount" : "{원거래 금액}",
      "cancelamount" : "{부분취소 금액}",
      "balanceamount" : "{남은 금액}"
    },
    "success": true
  }
}
```
<br>

## 7-7. 거래내역조회 응답값 설명 `servicecode = payments`
|Entity|Required|Description
|-----|-----|-----|
|`userid`|필수|영업사원ID|
|`usernm`|필수|영업사원명|
|`amount`|필수|결제금액|
|`servicename`|필수|서비스명(카드,현금)|
|`status`|필수|결제상태(카드[승인성공,취소,취소예약중], 현금^이체[결제,취소])|
|`oid`|필수|PaynowBiz 주문번호|
|`tid`|카드필수|TossPayments 거래번호|
|`cardnum`|카드필수|카드번호|
|`authnum`|카드필수,현금부분필수|승인번호|
|**`paydate`**|카드필수,현금부분필수|**결제일(YYYYMMDDHH24MISS)**|
|**`canceldate`**|취소필수|**취소일(YYYYMMDDHH24MISS)**|
|`financecode`|카드필수|발급사 카드사코드(2자리)|
|`financename`|카드필수|발급사 카드사명|
|`installment`|카드필수|카드할부개월수|
|`cardflag`|카드필수|카드구분(신용,체크,기프트)|
|`productinfo`|선택|상품명|
|`memo`|선택|메모|
|`cashbill`|부분필수|현금(발급,발급취소,미발급),이체(소득공제,지출증빙)|
|`reserved1`|선택|예약필드1|
|`reserved2`|선택|예약필드2|
|`reserved3`|선택|예약필드3|
|`reserved4`|선택|예약필드4|
|`reserved5`|선택|예약필드5|
|`mgrcode`|부분필수|담당자코드|
|`custcode`|부분필수|거래처코드|
|`custname`|부분필수|거래처명|
|`medictype`|부분필수|의약품구분(일반,전문)|
|||_부분필수 : 거래처 등록 상점 일 경우 필수_|
<br>

## 7-8. 정산내역조회 응답값 설명 `servicecode = settlements`
>결제일`paydate` 다음날 9시 이후부터 조회가 가능합니다.
>
|Entity|Required|Description
|-----|-----|-----|
|`userid`|필수|영업사원ID|
|`usernm`|필수|영업사원명|
|`amount`|필수|매입금액|
|`vat`|필수|수수료[부가세포함]|
|`vatFee`|필수|수수료의부가세|
|`authnum`|필수|승인번호|
|`servicename`|필수|서비스명(카드,현금,계좌좌이체)|
|`cardnum`|카드필수|카드번호|
|`purchasecode`|카드필수|[매입상태코드](#7-7-1-%EC%A0%95%EC%82%B0-%EB%A7%A4%EC%9E%85%EC%83%81%ED%83%9C-%EC%84%A4%EB%AA%85)|
|`purchasename`|카드필수|[매입상태명](#7-7-1-%EC%A0%95%EC%82%B0-%EB%A7%A4%EC%9E%85%EC%83%81%ED%83%9C-%EC%84%A4%EB%AA%85)|
|`oid`|필수|PaynowBiz 주문번호|
|`tid`|필수|TossPayments 거래번호|
|`regdate`|필수|등록일(YYYY-MM-DD HH24:MI:SS)|
|`paydate`|필수|결제일(YYYY-MM-DD)|
|`paytime`|필수|결제일시(YYYY-MM-DD HH24:MI:SS)|
|`canceltime`|부분필수|취소일시(YYYY-MM-DD HH24:MI:SS)취소일때만|
|**`adjustdate`**|카드필수|**카드매출일(YYYY-MM-DD)**|
|`plandate`|카드필수|카드지급일(YYYY-MM-DD)|
|`reqdate`|카드필수|카드매입/취소 요청일(YYYY-MM-DD)|
|`settlementcode`|카드필수|카드결제기관|
|`subservice`|카드필수|카드할부(이자/무이자)|
|`installment`|카드필수|카드할부개월수|
|`cardflag`|카드필수|카드구분(신용,체크,기프트)|
|`productinfo`|선택|상품명|
|`mntype`|카드필수|등급(일반,중소,영세)|
|`mgrcode`|부분선택|담당자코드|
|`custcode`|부분필수|거래처코드|
|`custname`|부분필수|거래처명|
|`medictype`|부분필수|의약품구분(일반,전문)|
|||_부분필수 : 거래처 등록 상점 일 경우 필수_|

## 7-8-1. 정산 매입상태 설명
|servicename|purchasecode|purchasename|Description
|-----|-----|-----|-----|
|카드|CA01|매입|승인 및 취소된 거래를 카드사로 청구(요청)하는 행위입니다.<br>(매입이 되어야 승인 및 취소가 확정됩니다.)
|카드|CA02|매입취소|승인 후 취소 발생 시 정산에서 차감되는 기준 거래입니다.
|카드|CA03|매입반송|매입 후 특정사유에 의해 카드사가 거절한 거래로 결제대금 미입금에 따라 정산에서 차감될 수 있습니다.
|카드|CA04|매입보류|특정 사유로 상점의 결제대금이 지급 보류된 상태입니다.<br>(서류미비, 한도초과, 보증만료, 리스크 보류 등)
|카드|CA06|매입취소반송|매입 후 특정사유에 의해 카드사가 거절한 거래로 결제대금 미입금에 따라 정산에서 차감될 수 있습니다.
|카드|CA11|부분취소|
|현금|200|결제|
|현금|300|취소|
|이체|AC99|계좌이체|
|이체|AC01|이체성공|
|이체|AC02|이체환불|
|이체|AC03|이체미통지환불|
|이체|AC04|이체미확인환불|
|이체|AC05|이체실패환불|
|이체|AC07|이체부분환불|
|이체|AC09|이체부분환불(기타)|
|이체|AC11|이체당일전체환불|
|이체|AC12|이체당일부분환불|
<br>

## 7-9. 결제취소 응답값 설명 `servicecode in(cancel, cancelShopOid, partCancel)`
_**`result.status = 200` 이며 `result.result.code` = 0000 인 경우만 `취소성공` `취소예약성공` `부분취소성공`입니다. <br>
`취소예약성공`은 당일취소가 아니며,다음날 새벽에 취소처리를 위해 예약이 된 상태를 말합니다.<br>
그 외의 코드는 msg를 확인 하시기 바랍니다.**_
|code|msg|
:-----:|-----|
0000|취소성공, 취소예약성공, 부분취소 성공
0201|취소 오류가 발생 하였습니다. 다시 시도 해주시기 바랍니다
0601|이미 취소된 거래입니다
0602|승인 실패건은 취소할 수 없습니다
0603|취소예약된 거래입니다
0604|부분취소 권한이 없습니다
0605|직가맹 카드 결제는 부분취소 할 수 없습니다
0606|분할결제 된 거래는 부분취소 할 수 없습니다
0607|부분취소 금액이 결제액과 같으므로 부분취소 할 수 없습니다
0608|부분취소 금액이 결제액 보다 크므로 부분취소 할 수 없습니다
<br>

## 8. 문의하기
 >이메일 : techsupport@tosspayments.com<br>
 >디스코드 : https://discord.gg/b9GFMxqJVN<br>
 >☎ 1544-7772
