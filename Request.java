import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Request {
/* 순서(IDX)를 바꿔가면서 테스트를 진행한다. 
 *      ㄴ 0 : 지점 등록/수정, 1 : 영업사원 등록/수정, 2 : 거래처 등록/수정/삭제, 3 : 거래내역조회, 4 : 정산내역조회, 5 : 결제취소, 6 : 결제취소(상점주문번호), 7 : 상품 등록/수정, 8 : 부분취소
 *      ㄴ 0, 1, 2, 7 인 경우 RESPONSE를 확인하여, result.status in (201, 202) 이면 데이터 정비 후 재요청 시도 한다.
 */
private final static int IDX = 3;
private final static List<String> SERVICECODE = Arrays.asList("branch", "member", "customer", "payments", "settlements", "cancel", "cancelShopOid", "goods", "partCancel");
private final static String PAYNOWBIZ_MERTID = "{mertid}";   //PaynowBiz에서 가입한 가맹점ID
//※중요 : {certkey, apikey}는 안전한 곳에 보관하시기 바랍니다.
private final static String PAYNOWBIZ_CERTKEY = "{certkey}"; //PaynowBiz에서 발급받은 인증키 ☎)1544-7772
private final static String PAYNOWBIZ_APIKEY = "{apikey}";   //PaynowBiz에서 발급받은 APIKEY ☎)1544-7772
private final static String PAYNOWBIZ_APIURL = "https://upaynowapi.tosspayments.com/2/v1/"; //호출URL
  
  public static void main(String[] args) {
    try{
        //지점 등록/수정
        String jsonBranch = "{" +
        "  \"certkey\":\""+PAYNOWBIZ_CERTKEY+"\"," + 
        "  \"reqid\":\""+getRequestApiTime()+"\"," +
        "  \"app2appyn\":\"N\"," + // Y = initialname_branchid로 등록됩니다.           
        "  \"list\": [" + 
        "    {\"branchid\":\"Gangnam\",\"branchnm\":\"강남점\",\"usernm\":\"강토스\",\"userphone\":\"01012341001\",\"validyn\":\"Y\",\"userpw\":\"change here\",\"branchaddress1\":\"\",\"branchzip\":\"\"},"+
        "    {\"branchid\":\"yeoksam\",\"branchnm\":\"역삼점\",\"usernm\":\"역토스\",\"userphone\":\"01012341002\",\"validyn\":\"Y\",\"userpw\":\"change here\",\"branchaddress1\":\"서울특별시 강남구 테헤란로 131\",\"branchaddress2\":\"한국지식재산센터(KIPS) 15층\",\"branchzip\":\"06133\",\"branchtel\":\"15447772\"}"+
        "  ]" + 
        "}";      
      
        //영업사원 등록/수정
        String jsonMember = "{" +
        "  \"certkey\":\""+PAYNOWBIZ_CERTKEY+"\"," + 
        "  \"reqid\":\""+getRequestApiTime()+"\"," +
        "  \"app2appyn\":\"N\"," + //가맹점APP(WEB) to PaynowBizAPP을 연동중 AUTO_REG = Y 로 넘겼을 경우 즉, initialname_userid로 등록되는 경우 Y로 넘겨야 합니다. 
        "  \"list\": [" + 
        "    {\"userid\":\"biz001\",\"usernm\":\"김비즈\",\"userphone\":\"01011110001\",\"validyn\":\"Y\",\"userpw\":\"change here\",\"branchid\":\"yeoksam\"},"+
        "    {\"userid\":\"biz002\",\"usernm\":\"이비즈\",\"userphone\":\"01011110002\",\"validyn\":\"Y\",\"userpw\":\"change here\"},"+ 
        "    {\"userid\":\"biz003\",\"usernm\":\"박비즈\",\"userphone\":\"01011110003\",\"validyn\":\"N\",\"userpw\":\"chage here\"}"+ 
        "  ]" + 
        "}";
      
        //거래처 등록/수정/삭제
        String jsonCustomer = "{" +
        "  \"certkey\":\""+PAYNOWBIZ_CERTKEY+"\"," + 
        "  \"reqid\":\""+getRequestApiTime()+"\"," +
        "  \"app2appyn\":\"N\"," + //APP(WEB) to PaynowBizAPP을 연동중 AUTO_REG = Y 로 넘겼을 경우 즉, initialname_userid로 등록되는 경우 Y로 넘겨야 합니다. 
        "  \"list\": [" + 
        "    {\"userid\":\"{mertid}\",\"custcode\":\"A001\",\"custphone\":\"01022220001\",\"custname\":\"역삼약국\",\"useyn\":\"Y\",\"custaddress1\":\"서울특별시 강남구 테헤란로 131\",\"custaddress2\":\"한국지식재산센터(KIPS) 15층\",\"custzip\":\"06133\",\"businessno\":\"4118601799\",\"custfax\":\"0222220001\",\"custemail\":\"yeoksam@medic.com\"},"+
        "    {\"userid\":\"biz001\",\"custcode\":\"A002\",\"custphone\":\"01022220002\",\"custname\":\"도래울약국\",\"useyn\":\"Y\",\"custaddress1\":\"경기도 고양시 덕양구 도래울로 131\",\"custaddress2\":\"도래울빌딩 1층\",\"custzip\":\"01899\",\"businessno\":\"4118601799\",\"custfax\":\"05022220002\",\"custemail\":\"doraeul@medic.com\"},"+ 
        "    {\"userid\":\"biz001\",\"custcode\":\"A002\",\"custphone\":\"01022220003\",\"custname\":\"도래울약국\",\"useyn\":\"N\",\"custaddress1\":\"경기도 고양시 덕양구 도래울로 131\",\"custaddress2\":\"도래울빌딩 1층\",\"custzip\":\"01899\",\"businessno\":\"4118601799\",\"custfax\":\"05022220002\",\"custemail\":\"doraeul@medic.com\"},"+ 
        "    {\"userid\":\"biz002\",\"custcode\":\"A002\",\"custphone\":\"01022220002\",\"custname\":\"도래울약국\",\"useyn\":\"Y\",\"custaddress1\":\"경기도 고양시 덕양구 도래울로 131\",\"custaddress2\":\"도래울빌딩 1층\",\"custzip\":\"01899\",\"businessno\":\"4118601799\",\"custfax\":\"05022220002\",\"custemail\":\"doraeul@medic.com\"},"+ 
        "    {\"userid\":\"biz003\",\"custcode\":\"A002\",\"custphone\":\"01022220003\",\"custname\":\"도래울약국\",\"useyn\":\"Y\",\"custaddress1\":\"경기도 고양시 덕양구 도래울로 131\",\"custaddress2\":\"도래울빌딩 1층\",\"custzip\":\"01899\",\"businessno\":\"4118601799\",\"custfax\":\"05022220002\",\"custemail\":\"doraeul@medic.com\"},"+
        "    {\"userid\":\"biz001\",\"custcode\":\"A004\",\"custphone\":\"01022220001\",\"useyn\":\"D\"}"+ //삭제일 경우만
        "  ]" + 
        "}";
      
        //거래^정산 내역조회
        String jsonRetrieve = "{" +
        "  \"certkey\":\""+PAYNOWBIZ_CERTKEY+"\"," + 
        "  \"reqid\":\""+getRequestApiTime()+"\"," +
        "  \"startdt\":\"20210315\"," + //조회 시작일
        "  \"enddt\":\"20210315\"," + //조회 종료일
        "  \"oid\":\"\"," + //PaynowBiz 주문번호
        "  \"tid\":\"\"}" + //TossPayments 거래번호
        "  ]" + 
        "}";
      
        //취소^취소(상점주문번호)
        String jsonCancel = "{" +
        "  \"certkey\":\""+PAYNOWBIZ_CERTKEY+"\"," + 
        "  \"reqid\":\""+getRequestApiTime()+"\"," +
        "  \"type\":\"card\"," +
        "  \"oid\":\"{PaynowBiz 주문번호}\"," +
        "  \"tid\":\"{TossPayments 거래번호}\"," +		    		
        "  \"shop_oid\":\"{간편결제로 상점에서 요청한 주문번호}\"" +		    		
        "}";
      
        //부분취소
        String jsonPartCancel = "{" +
        "  \"certkey\":\""+PAYNOWBIZ_CERTKEY+"\"," + 
        "  \"reqid\":\""+getRequestApiTime()+"\"," +
        "  \"type\":\"card\"," +
        "  \"cancelamount\":\"13000\"," +
        "  \"cancelreason\":\"치약*2ea 환불\"," +
        "  \"oid\":\"{PaynowBiz 주문번호}\"," +
        "  \"tid\":\"{TossPayments 거래번호}\"," +		    		
        "  \"shop_oid\":\"{간편결제로 상점에서 요청한 주문번호}\"" +		    		
        "}";
      
        //상품 등록/수정
        String jsonGoods = "{" +
        "  \"certkey\":\""+PAYNOWBIZ_CERTKEY+"\"," + 
        "  \"reqid\":\""+getRequestApiTime()+"\"," +
        "  \"list\": [" + 
        "    {\"no\":\"biz-001-0001\",\"name\":\"아메리카노(HOT)\",\"price\":\"5000\",\"visible\":\"1\",\"taxfree\":\"0\",\"group1\":\"음료\",\"group2\":\"커피\"},"+
        "    {\"no\":\"biz-001-0002\",\"name\":\"바닐라라떼(HOT)\",\"price\":\"6000\",\"visible\":\"1\",\"taxfree\":\"0\",\"group1\":\"\",\"group2\":\"커피\"},"+
        "    {\"no\":\"biz-002-0001\",\"name\":\"굿즈T-shirt\",\"price\":\"20000\",\"visible\":\"0\",\"taxfree\":\"1\"}"+
        "  ]" + 
        "}";      
        List<String> jsonData = Arrays.asList(jsonBranch, jsonMember, jsonCustomer, jsonRetrieve, jsonRetrieve, jsonCancel, jsonCancel, jsonGoods, jsonPartCancel);
    
        String encryptData = new AESUtil(PAYNOWBIZ_APIKEY).strEncode(jsonData.get(IDX));
        System.out.println("[REQUEST] data="+encryptData);
    
       //post request
       String postParams = "data="+URLEncoder.encode(encryptData, "UTF-8");
       RequestUrl(postParams);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
    * @category 실행
    * @param    String : data
    * @author   YooYoungJu
    */
  public static void RequestUrl(String params) {
    HttpsURLConnection conn = null;
        
    // Request
    try {
      String pathUrl = PAYNOWBIZ_MERTID + "/" + SERVICECODE.get(IDX);
      URL url = new URL(PAYNOWBIZ_APIURL + pathUrl);
            
      conn = (HttpsURLConnection) url.openConnection();
      conn.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
      conn.setRequestMethod("POST"); 
      conn.setDoInput(true); 
      conn.setDoOutput(true); 
      conn.setUseCaches(false); 
      conn.setConnectTimeout(30000);
      conn.setReadTimeout(30000);
      try(DataOutputStream dos = new DataOutputStream(conn.getOutputStream())){
        dos.writeBytes(params);
        dos.flush();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
        
    // Response
    try {
      StringBuilder sb = new StringBuilder();
      int responseCode = conn.getResponseCode();
      System.out.println("[RESPONSE] CODE = " + responseCode);
        
      if(responseCode == HttpsURLConnection.HTTP_OK) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(conn.getInputStream())))){
          String line;
          while ((line = reader.readLine()) != null) {
            sb.append(line);
          }
        }
      }
      System.out.println("[RESPONSE] sb : " + sb.toString());
        
      if(sb.length() > 0) {
        JSONObject json = (JSONObject) new JSONParser().parse(sb.toString());
        JSONObject data = (JSONObject) json.get("result");
        String status = String.valueOf(data.get("status"));
        System.out.println("[RESPONSE] result.status = " + status);
        
        //전체실패, 일부성공인 경우 오류내용 
        if("201".equals(status) || "202".equals(status)) {
          String result = String.valueOf(data.get("result"));
          //오류내용 복호화
          String decryptStr = new AESUtil(PAYNOWBIZ_APIKEY).strDecode(result);
          //System.out.println("[RESPONSE] result.result = " + decryptStr);
        
          JSONObject errJSON = (JSONObject) new JSONParser().parse(decryptStr);
          System.out.printf(String.format("▶▶ 성공카운트 : %s, 실패카운트 : %s\n", String.valueOf(errJSON.get("countOfSuccess")), String.valueOf(errJSON.get("countOfFailure"))));
        
          JSONArray arrJSON = (JSONArray)errJSON.get("list");

          //[오류확인] 지점 등록/수정
          if("branch".equals(SERVICECODE.get(IDX))){
            for (Object obj : arrJSON) {
              JSONObject jsonObj = (JSONObject)obj;
              System.out.printf(String.format("▶▶ err=%s, ", jsonObj.get("err")));
              System.out.printf(String.format("branchid[PK]=%s, ", jsonObj.get("branchid")));
              System.out.printf(String.format("branchnm=%s, ", jsonObj.get("branchnm")));
              System.out.printf(String.format("usernm=%s, ", jsonObj.get("usernm")));
              System.out.printf(String.format("userphone=%s, ", jsonObj.get("userphone")));
              System.out.printf(String.format("userpw=%s, ", jsonObj.get("userpw")));
              System.out.printf(String.format("validyn=%s, ", jsonObj.get("validyn")));
              System.out.printf(String.format("branchtel=%s, ", jsonObj.get("branchtel")));        					
              System.out.printf(String.format("branchzip=%s, ", jsonObj.get("branchzip")));        					
              System.out.printf(String.format("branchaddress1=%s, ", jsonObj.get("branchaddress1")));
              System.out.printf(String.format("branchaddress2=%s\n", jsonObj.get("branchaddress2")));
            }
          //[오류확인] 영업사원 등록/수정
          }else if("member".equals(SERVICECODE.get(IDX))){
            for (Object obj : arrJSON) {
              JSONObject jsonObj = (JSONObject)obj;
              System.out.printf(String.format("▶▶ err=%s, ", jsonObj.get("err")));
              System.out.printf(String.format("userid[PK]=%s, ", jsonObj.get("userid")));
              System.out.printf(String.format("usernm=%s, ", jsonObj.get("usernm")));
              System.out.printf(String.format("userphone=%s, ", jsonObj.get("userphone")));
              System.out.printf(String.format("userpw=%s, ", jsonObj.get("userpw")));
              System.out.printf(String.format("validyn=%s, ", jsonObj.get("validyn")));
              System.out.printf(String.format("branchid=%s\n", jsonObj.get("branchid")));              
            }
          //[오류확인] 영업사원별 거래처 등록/수정/삭제
          }else if("customer".equals(SERVICECODE.get(IDX))){
            for (Object obj : arrJSON) {
              JSONObject jsonObj = (JSONObject)obj;
              System.out.printf(String.format("▶▶ err=%s, ", jsonObj.get("err")));
              System.out.printf(String.format("userid[PK]=%s, ", jsonObj.get("userid")));
              System.out.printf(String.format("custcode[PK]=%s, ", jsonObj.get("custcode")));
              System.out.printf(String.format("custphone[PK]=%s, ", jsonObj.get("custphone")));
              System.out.printf(String.format("custname=%s, ", jsonObj.get("custname")));
              System.out.printf(String.format("useyn=%s, ", jsonObj.get("useyn")));
              System.out.printf(String.format("custaddress1=%s, ", jsonObj.get("custaddress1")));
              System.out.printf(String.format("custaddress2=%s, ", jsonObj.get("custaddress2")));
              System.out.printf(String.format("custzip=%s, ", jsonObj.get("custzip")));
              System.out.printf(String.format("businessno=%s, ", jsonObj.get("businessno")));
              System.out.printf(String.format("custfax=%s, ", jsonObj.get("custfax")));
              System.out.printf(String.format("custemail=%s\n", jsonObj.get("custemail")));
            }
          //[오류확인] 상품 등록/수정
          }else if("goods".equals(SERVICECODE.get(IDX))){
            for (Object obj : arrJSON) {
              JSONObject jsonObj = (JSONObject)obj;
              System.out.printf(String.format("▶▶ err=%s, ", jsonObj.get("err")));
              System.out.printf(String.format("no[PK]=%s, ", jsonObj.get("no")));
              System.out.printf(String.format("name=%s, ", jsonObj.get("name")));
              System.out.printf(String.format("price=%s, ", jsonObj.get("price")));
              System.out.printf(String.format("visible=%s, ", jsonObj.get("visible")));
              System.out.printf(String.format("taxfree=%s, ", jsonObj.get("taxfree")));
              System.out.printf(String.format("group1=%s, ", jsonObj.get("group1")));
              System.out.printf(String.format("group2=%s, ", jsonObj.get("group2")));
              System.out.printf(String.format("group3=%s, ", jsonObj.get("group3")));
              System.out.printf(String.format("medictype=%s\n, ", jsonObj.get("medictype")));
            }
          }
        }	
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      conn.disconnect();
    }
  }
  
  /**
    * @category API요청시간
    */
  public static String getRequestApiTime(){
    LocalDateTime date = LocalDateTime.now(ZoneId.of("GMT+09:00"));
    return date.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
  }
}
