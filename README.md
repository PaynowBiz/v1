# PaynowBiz API
[PaynowBiz API](https://github.com/PaynowBiz/v1/blob/main/Request.java) 테스트를 지원합니다.

https://upaynowapi.tosspayments.com/2/v1/{mertid}/{servicecode}
>mertid = PaynowBiz가입 상점(가맹점)ID, servicecode = 호출할 서비스코드

파라미터 data를 json으로 만든 후 AES암호화하여 POST방식으로 호출 합니다.
 * [AES-256](https://github.com/PaynowBiz/v1/blob/main/AES256Util.java) 암호화 
 * BASE64 인코딩
 * URL인코딩(UTF-8)

**[중요1] app2appyn 을 Y로 할 경우 영업사원ID에 이니셜ID 가 붙으므로, 상점에서 연동방식을 정확히 확인 후 넘겨야함.
**[중요2] userpw : 최초 호출시만 저장. 
이후 변경을 원하는 경우 [PaynowBiz상점관리자](https://paynowbiz.tosspayments.com/pnbmert/) 또는 PaynowBizAPP([안드로이드](https://play.google.com/store/apps/details?id=com.lguopg.paynowauth&hl=ko&gl=US)/[아이폰](https://apps.apple.com/kr/app/%ED%8E%98%EC%9D%B4%EB%82%98%EC%9A%B0-%EB%B9%84%EC%A6%88-%EC%9D%B8%EC%A6%9D%EC%9A%A9/id1261678163) )에서 변경 가능**
**_[중요3] custphone : 휴대폰번호가 없는 경우 010으로 시작하는 11자리 임의번호를 기재하시기 바랍니다._**

## 1. 영업사원 등록/수정(servicecode = member)
Entity|Required|Length|Restriction|Description
|-----|:-----:|-----|-----|-----|
|`certkey`|필수|16||인증키|
|`reqid`|필수|13|숫자|yyyyMMddHHmmssSSS|
|`app2appyn`|필수|1|Y or N|App(Web) to App 가맹점 유무|
|`list`||||_아래 정보를 배열로 처리_|
|`userid` **[PK]**|필수|19|영문, 숫자|영업사원ID|
|`usernm`|필수|128||영업사원명|
|`userphone`|필수|11|숫자(-제외)|영업사원 휴대폰번호|
|`validyn`|필수|1|Y or N|활성화상태|
|`userpw`|필수|128|영문, 숫자, 특수문자 포함 8자 이상|패스워드|

```json
sample json code
{
  "certkey":"{PanowBiz에서 발급받은 인증키}", "reqid":"{yyyyMMddHHmmssSSS}", "app2appyn":"N",
  "list": [
    {"userid": "biz001","usernm": "김토스","userpw": "change here","userhp": "01012340001","validyn": "Y"},
    {"userid": "biz002","usernm": "이토스","userpw": "change here","userhp": "01012340002","validyn": "Y"},
    {"userid": "biz003","usernm": "박토스","userpw": "change here","userhp": "01012340003","validyn": "Y"}    
  ]
}
```

## 2. 고객(거래처) 등록/수정/삭제(servicecode = customer)
|Entity|Required|Length|Restriction|Description
|-----|:-----:|-----|-----|-----|
|`certkey`|필수|16||인증키|
|`reqid`|필수|13|숫자|yyyyMMddHHmmssSSS|
|`app2appyn`|필수|1|Y or N|App(Web) to App 가맹점 유무|
|`list`|||| _아래 정보를 배열로 처리_|
|`userid` **[PK]**|필수|19|영문, 숫자	|영업사원ID<br/> 맵핑 정보 없으면 상점ID{mertid}|
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

## RESPONSE SAMPLE
```json
sample result json code
{"result":{"status":"200","msg":"success","service":"paynowbiz","function":"/v1/{mertid}/{servicecode}","data":"","result":"/K+VQ9mi4fuWXGWLqCPfNlbztOpJDJKy5WCXeb+/vRej42gfpEfXLzQok+c6rYg3","success":true}}
```

result.status|Description
-----|-----|
200|전체성공
201|전체실패
202|일부성공
101|정상적인 호출이 아닌 경우
104|servicecode, certkey가 맞지 않을 경우
405|mertid, certkey를 찾을수 없을 경우 또는 JSON 오류 
802|reqid, app2appyn이 없을 경우
999|시스템 오류가 있을 경우(고객센터 ☎1544-7772 문의)
