# 페이나우비즈 v1 API 연동 규격서

목차<br>
[1. 개요](#1-개요)<br>

 
## 1. 개요
 PaynowBiz를 이용하는 가맹점(상점)에서 영업사원과 거래처 정보를 PaynowBiz 서버로 연동하고, 거래내역 및 정산내역을 조회 할 수 있으며, 결제취소를 돕는 연동가이드 문서 입니다.

* [테스트 페이지](https://paynowbiz.tosspayments.com/sample/v1View.do) 에서 테스트를 해 볼수 있습니다.
* [Request.java](https://github.com/PaynowBiz/v1/blob/main/Request.java) 를 다운받아 코딩 하면 됩니다.
<br><br>
## 2. URL정보
https://upaynowapi.tosspayments.com/2/v1/{mertid}/{servicecode}&data=WLqCPfNlbzpJDJKy5WCX6rYg3==
>**`mertid`**[PaynowBiz 가맹점(상점)ID]  **`servicecode`**[서비스코드] **`data`**[요청할 정보를 암호화 한 값]
<br><br>
## 3. servicecode
 1) [memeber](#6-1.-영업사원-등록/수정(servicecode-=-member)) : 가맹점(상점) 영업사원 등록 / 수정
 2) [customer](#6-2.-고객(거래처)-등록/수정/삭제(servicecode-=-customer)) : 고객(거래처) 등록 /수정 / 삭제
 3) [payments](#6-3.-거래(servicecode-=-payments)-/-정산(servicecode-=-settlements)-내역-조회) : 거래내역 조회
 4) [settlements](#6-3.-거래(servicecode-=-payments)-/-정산(servicecode-=-settlements)-내역-조회) : 정산내역 조회
 5) [cancel](#6-4.-결제취소(servicecode-=-cancel)) : 결제취소
<br><br>
## 4. data
요청할 정보를 json으로 만든 후 AES암호화하여 POST방식으로 호출 합니다.
 * [AES-256](https://github.com/PaynowBiz/v1/blob/main/AES256Util.java) 암호화 
 * BASE64 인코딩
 * URL인코딩(UTF-8)
<br><br>
## 5. 주의사항
* `servicecode in (member, customer)` 인 경우만 해당하며, `app2appyn`를 Y로 할 경우 영업사원ID에 initialname 이 붙으므로, 가맹점에서 연동방식을 정확히 확인 후 넘겨야 합니다.
* `servicecode in (member)` 인 경우만 해당하며, `userpw` 최초 호출시만 저장됩니다. 비밀번호 수정을 하려고 재요청시 수정이 되지 않습니다. 이후 변경을 원하는 경우 [PaynowBiz상점관리자](https://paynowbiz.tosspayments.com/pnbmert/) 또는 PaynowBizAPP([안드로이드](https://play.google.com/store/apps/details?id=com.lguopg.paynowauth&hl=ko&gl=US)/[아이폰](https://apps.apple.com/kr/app/%ED%8E%98%EC%9D%B4%EB%82%98%EC%9A%B0-%EB%B9%84%EC%A6%88-%EC%9D%B8%EC%A6%9D%EC%9A%A9/id1261678163) )에서 변경 가능합니다.
* `custphone` 휴대폰번호가 없는 경우 010으로 시작하는 11자리 임의번호를 기재하시기 바랍니다.
<br><br>
----------------------------------------------------------------------------------------
## 6. REQUEST
## 6-1. 영업사원 등록/수정(servicecode = member)
Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,숫자|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`app2appyn`|필수|1|Y or N|**App(Web) to App 가맹점 유무**|
|`list`||||_아래 정보를 배열로 처리_|
|`userid` **[PK]**|필수|19|영문, 숫자|영업사원ID|
|`usernm`|필수|128||영업사원명|
|`userphone`|필수|11|숫자(-제외)|영업사원 휴대폰번호|
|`validyn`|필수|1|Y or N|활성화상태|
|`userpw`|필수|128|영문, 숫자, 특수문자 포함 8자 이상|패스워드|

```json
sample data json code
{
  "certkey":"{PanowBiz에서 발급받은 인증키}", "reqid":"{yyyyMMddHHmmssSSS}", "app2appyn":"N",
  "list": [
    {"userid": "biz001","usernm": "김토스","userpw": "change here","userhp": "01012340001","validyn": "Y"},
    {"userid": "biz002","usernm": "이토스","userpw": "change here","userhp": "01012340002","validyn": "Y"},
    {"userid": "biz003","usernm": "박토스","userpw": "change here","userhp": "01012340003","validyn": "Y"}    
  ]
}
```
<br><br>
## 6-2. 고객(거래처) 등록/수정/삭제(servicecode = customer)
|Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,숫자|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`app2appyn`|필수|1|Y or N|**App(Web) to App 가맹점 유무**|
|`list`|||| _아래 정보를 배열로 처리_|
|`userid` **[PK]**|필수|19|영문, 숫자	|영업사원ID<br/> 맵핑 정보 없으면 가맹점ID{mertid}|
|`custcode` **[PK]**|필수|100|영문, 숫자|고객(거래처)코드|
|`custphone` **[PK]**|필수|11|숫자(-제외)|고객(거래처)휴대폰번호|
|`custname`|필수|100||고객(거래처)명|
|`useyn`|필수|1|Y or N or D|Y:사용, N:사용안함, D:삭제|
|`custaddress1`|필수|128||고객(거래처)주소1|
|`custaddress2`|선택|128||고객(거래처)주소2|
|`custzip`|선택|5|숫자|고객(거래처)우편번호|
|`businessno`|선택|10|숫자(-제외)|고객(거래처)사업자번호|
|`custfax`|선택|20|숫자(-제외)|고객(거래처)팩스번호|
|`custemail`|선택|128|영어/숫자/특수문자|고객(거래처)이메일주소|

```json
sample data json code
{
  "certkey":"{PanowBiz에서 발급받은 인증키}", "reqid":"{yyyyMMddHHmmssSSS}", "app2appyn":"N",
  "list": [
    {"userid":"{mertid}","custcode":"A001","custname":"역삼약국","custaddress1":"서울시 강남구 역삼동 한국지식재산센터","custaddress2":"15층 역삼약국","custzip":"12345","custphone":"01012345678","custfax":"0212345678","custemail":"paynowbiz@tosspayments.com","useyn":"Y"},
    {"userid": "biz001","custcode":"A001","custname":"역삼약국","custaddress1":"서울시 강남구 역삼동 한국지식재산센터","custaddress2":"15층 역삼약국","custzip":"12345","custphone":"01077775678","custfax":"0212345678","custemail":"paynowbiz@tosspayments.com","useyn":"Y"}
  ]
}
```
<br><br>
## 6-3. 거래(servicecode = payments) / 정산(servicecode = settlements) 내역 조회
|Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`startdt`|필수|8|숫자|시작일[YYYYMMDD]|
|`enddt`|필수|8|숫자|종료일[YYYYMMDD]|
|`oid`|선택|18|영문,숫자|주문번호|
|`tid`|선택|24|영문,숫자|거래번호|

▶ _**`startdt`** ~ **`enddt`** 의 조회기준 은 아래와 같습니다._<br>
▶ 거래내역조회 = **`paydate`** OR **`canceldate`**<br>
▶ 정산내역조회 = **`adjustdate`**<br>
  ▶ ① 1회 조회시 180일 이상 조회 불가, ② 1회 조회시 1만건 이하만 가능<br>
```json
sample json code
{
  "certkey":"{PanowBiz에서 발급받은 인증키}", "reqid":"{yyyyMMddHHmmssSSS}", 
  "startdt":"20210101", "enddt":"20210131",
  "oid":"{PaynowBiz 주문번호}","tid":"{TossPayments 거래번호}"
}
```
<br><br>
## 6-4. 결제취소(servicecode = cancel)
Entity|Required|Length|Restriction|Description
|-----|:-----:|-----:|-----|-----|
|`certkey`|필수|16|영문,숫자|인증키|
|`reqid`|필수|17|숫자|yyyyMMddHHmmssSSS|
|`type`|필수|4|card|결제수단|
|`oid`|필수|18|영문,숫자|주문번호|
|`tid`|필수|24|영문,숫자|거래번호|

----------------------------------------------------------------------------------------

## 7. RESPONSE
## 7-1. RESPONSE status 설명
result.status|Description
:-----:|-----|
200|전체성공
201|전체실패
202|일부성공
101|정상적인 호출이 아닌 경우
104|servicecode, certkey가 맞지 않을 경우
405|mertid, certkey를 찾을수 없을 경우 또는 JSON 오류 
802|reqid, app2appyn이 없을 경우
999|시스템 오류가 있을 경우(고객센터 ☎1544-7772 문의)
<br><br>

## 7-2. RESPONSE SAMPLE (member, customer)
```json
sample result
{"result":{"status":"200","msg":"success","service":"paynowbiz","function":"/v1/{mertid}/{servicecode}","data":"","result":"/K+VQ9mi4fuWXGWLqCPfNlbztOpJDJKy5WCXeb+/vRej42gfpEfXLzQok+c6rYg3","success":true}}
```
_**`result.status in (201, 202)` 인 경우 `result.result` 를 복호화 하여 `result.result.list.err` 의 실패 원인을 확인**_
![image](https://user-images.githubusercontent.com/79068689/111751929-67293780-88d8-11eb-8c2f-bbdd76413379.png)
<br><br>
## 7-3. RESPONSE SAMPLE (payments, setllements)
```json
sample result
{"result":{"status":"200","msg":"success","service":"paynowbiz","function":"/v1/{mertid}/{servicecode}","data":"","result":"[{"usernm":"김*영","amount":"50000","authnum":"00000000","memo":"","oid":"biz210316143540327","userid":"bizbiz","paydate":"20210316143540","tid":"bizbi2021031614354150070","cashbill":"","canceldate":"","cardnum":"625******3043","financecode":"31","installment":"0","reserved3":"","reserved2":"","reserved1":"","servicename":"카드","custcode":"A002","productinfo":"","financename":"비씨","custname":"도래울약국","reserved5":"","status":"승인성공","reserved4":""},{"totalcnt":1}],"success":true}}"
```
## 7-4. RESPONSE SAMPLE (cancel)
```json
sample result
{"result":{"status":"200","msg":"success","service":"paynowbiz","function":"/v1/{mertid}/cancel","data":"","result":{"msg":"취소성공","code":"0000","oid":"biz210909153606587","tid":"bizte2021090915360751248"},"success":true}}
```
## 7-4-1. 결제 취소 코드 설명
|code|msg|
|-----|-----|
|0000|취소성공|
|0601|이미 취소된 거래입니다.|
<br><br>
## 7-5. 거래내역조회(servicecode = payments) RESPONSE 설명
|Entity|Description
|-----|-----|
|`userid`|영업사원ID|
|`usernm`|영업사원명|
|`amount`|결제금액|
|`servicename`|서비스명(카드,현금)|
|`status`|결제상태(승인성공,취소,취소예약중)|
|`oid`|주문번호|
|`tid`|거래번호|
|`cardnum`|카드번호|
|`authnum`|승인번호|
|**`paydate`**|**결제일(YYYYMMDDHH24MISS)**|
|**`canceldate`**|**취소일(YYYYMMDDHH24MISS)**|
|`financecode`|카드사코드(2자리)|
|`financename`|카드사명|
|`installment`|카드할부개월수|
|`productinfo`|상품명|
|`memo`|메모|
|`custcode`|고객(거래처)코드|
|`custname`|고객(거래처)명|
|`cashbill`|현금영수증발급여부(발급,발급취소,미발급)|
|`reserved1`|예약필드1|
|`reserved2`|예약필드2|
|`reserved3`|예약필드3|
|`reserved4`|예약필드4|
|`reserved5`|예약필드5|
|`medictype`|의약품구분(일반,전문)|
<br><br>
## 7-6. 정산내역조회(servicecode = settlements) RESPONSE 설명
>결제일 다음날 9시 이후부터 조회가 가능합니다.
>
|Entity|Description
|-----|-----|
|`amount`|매입금액|
|`vat`|부가세|
|`authnum`|승인번호|
|`servicename`|서비스명(카드,현금)|
|`purchasecode`|[매입상태코드](#6-1-%EC%A0%95%EC%82%B0-%EB%A7%A4%EC%9E%85%EC%83%81%ED%83%9C-%EC%84%A4%EB%AA%85)|
|`purchasename`|[매입상태명](#6-1-%EC%A0%95%EC%82%B0-%EB%A7%A4%EC%9E%85%EC%83%81%ED%83%9C-%EC%84%A4%EB%AA%85)|
|`oid`|주문번호|
|`tid`|거래번호|
|`regdate`|등록일(YYYY-MM-DD HH24:MI:SS)|
|`paydate`|결제일(YYYY-MM-DD)|
|**`adjustdate`**|**카드매출일(YYYY-MM-DD)**|
|`plandate`|카드지급일(YYYY-MM-DD)|
|`reqdate`|카드매입/취소 요청일(YYYY-MM-DD)|
|`settlementcode`|카드결제기관|
|`subservice`|카드할부(이자/무이자)|
|`installment`|카드할부개월수|
|`cardflag`|카드구분(신용,체크)|
|`productinfo`|상품명|
|`custcode`|고객(거래처)코드|
|`custname`|고객(거래처)명|
|`productinfo`|상품명|
|`mntype`|등급(일반,중소,영세)|
|`medictype`|의약품구분(일반,전문)|

## 7-6-1. 정산 매입상태 설명
|servicename|purchasecode|purchasename|
|-----|-----|-----|
|카드|CA01|매입|
|카드|CA02|매입취소|
|카드|CA03|매입반송|
|카드|CA04|매입보류|
|카드|CA06|매입취소반송|
|카드|CA11|부분취소|
|현금|200|결제|
|현금|300|취소|
