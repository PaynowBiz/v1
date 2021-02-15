import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.CharEncoding;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Request {
/* 실행(IDX) 할 순서를 바꿔가면서 테스트를 진행한다. 
 *       ㄴ 0 : 영업사원 등록/수정, 1 : 고객(거래처) 등록/수정/삭제
 * RESPONSE를 확인하여, result.status in (201, 202) 이면 데이터 정비 후 재요청 시도 한다.
 */
private final static int IDX = 1;
private final static List<String> SERVICECODE = Arrays.asList("member", "customer");
private final static String PAYNOWBIZ_MERTID = "{mertid}";   //PaynowBiz에서 가입한 상점ID
private final static String PAYNOWBIZ_CERTKEY = "{certkey}"; //PaynowBiz에서 발급받은 인증키
private final static String PAYNOWBIZ_APIKEY = "{apikey}";   //PaynowBiz에서 발급받은 APIKEY
private final static String PAYNOWBIZ_APIURL = "https://upaynowapi.paynowbiz.tosspayments.com/2/v1/";
  public static void main(String[] args) {
    try{
        //영업사원 등록/수정
        String jsonMember = "{" +
        "  \"certkey\":\""+PAYNOWBIZ_CERTKEY+"\"," + 
        "  \"reqid\":\""+getRequestApiTime()+"\"," +
        "  \"app2appyn\":\"N\"," + //상점APP(WEB) to PaynowBizAPP을 연동중 임을 구분하기 위함. 
        "  \"list\": [" + 
        "    {\"userid\":\"bizbiz_001\",\"usernm\":\"김비즈\",\"userphone\":\"01011110001\",\"validyn\":\"Y\",\"userpw\":\"TossBiz+001!!\"},"+
        "    {\"userid\":\"bizbiz_002\",\"usernm\":\"이비즈\",\"userphone\":\"01011110002\",\"validyn\":\"Y\",\"userpw\":\"TossBiz+002!!\"},"+ 
        "    {\"userid\":\"bizbiz_003\",\"usernm\":\"박비즈\",\"userphone\":\"01011110003\",\"validyn\":\"N\",\"userpw\":\"TossBiz+003!!\"}"+ 
        "  ]" + 
        "}";
        //고객(거래처) 등록/수정/삭제
        String jsonCustomer = "{" +
        "  \"certkey\":\""+PAYNOWBIZ_CERTKEY+"\"," + 
        "  \"reqid\":\""+getRequestApiTime()+"\"," +
        "  \"list\": [" + 
        "    {\"userid\":\"bizbiz\",\"custcode\":\"A001\",\"custphone\":\"01022220001\",\"custname\":\"역삼약국\",\"useyn\":\"Y\",\"custaddress1\":\"서울특별시 강남구 테헤란로 131\",\"custaddress2\":\"한국지식재산센터(KIPS) 15층\",\"custzip\":\"06133\",\"businessno\":\"4118601799\",\"custfax\":\"0222220001\",\"custemail\":\"yeoksam@medic.com\"},"+
        "    {\"userid\":\"bizbiz_001\",\"custcode\":\"A002\",\"custphone\":\"01022220002\",\"custname\":\"도래울약국\",\"useyn\":\"Y\",\"custaddress1\":\"경기도 고양시 덕양구 도래울로 131\",\"custaddress2\":\"도래울빌딩 1층\",\"custzip\":\"01899\",\"businessno\":\"4118601799\",\"custfax\":\"05022220002\",\"custemail\":\"doraeul@medic.com\"},"+ 
        "    {\"userid\":\"bizbiz_001\",\"custcode\":\"A002\",\"custphone\":\"01022220003\",\"custname\":\"도래울약국\",\"useyn\":\"N\",\"custaddress1\":\"경기도 고양시 덕양구 도래울로 131\",\"custaddress2\":\"도래울빌딩 1층\",\"custzip\":\"01899\",\"businessno\":\"4118601799\",\"custfax\":\"05022220002\",\"custemail\":\"doraeul@medic.com\"},"+ 
        "    {\"userid\":\"bizbiz_002\",\"custcode\":\"A002\",\"custphone\":\"01022220002\",\"custname\":\"도래울약국\",\"useyn\":\"Y\",\"custaddress1\":\"경기도 고양시 덕양구 도래울로 131\",\"custaddress2\":\"도래울빌딩 1층\",\"custzip\":\"01899\",\"businessno\":\"4118601799\",\"custfax\":\"05022220002\",\"custemail\":\"doraeul@medic.com\"},"+ 
        "    {\"userid\":\"bizbiz_003\",\"custcode\":\"A002\",\"custphone\":\"01022220003\",\"custname\":\"도래울약국\",\"useyn\":\"Y\",\"custaddress1\":\"경기도 고양시 덕양구 도래울로 131\",\"custaddress2\":\"도래울빌딩 1층\",\"custzip\":\"01899\",\"businessno\":\"4118601799\",\"custfax\":\"05022220002\",\"custemail\":\"doraeul@medic.com\"},"+
        "    {\"userid\":\"bizbiz_001\",\"custcode\":\"A004\",\"custphone\":\"01022220001\",\"useyn\":\"D\"}"+ //삭제일 경우만
        "  ]" + 
        "}";
    
        List<String> jsonData = Arrays.asList(jsonMember, jsonCustomer);
    
        String encryptData = new AES256Util(PAYNOWBIZ_APIKEY).strEncode(jsonData.get(IDX));
        System.out.println("[REQUEST] data="+encryptData);
    
       //post request
       String postParams = "data="+URLEncoder.encode(encryptData, CharEncoding.UTF_8);
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
      conn.setReadTimeout(60000);
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
        
        if(!"200".equals(status)) {
          String result = String.valueOf(data.get("result"));
          //오류내용 복호화
          String decryptStr = new AES256Util(PAYNOWBIZ_APIKEY).strDecode(result);
          //System.out.println("[RESPONSE] result.result = " + decryptStr);
        
          JSONObject errJSON = (JSONObject) new JSONParser().parse(decryptStr);
          System.out.printf(String.format("▶▶ 성공카운트 : %s, 실패카운트 : %s\n", String.valueOf(errJSON.get("countOfSuccess")), String.valueOf(errJSON.get("countOfFailure"))));
        
          JSONArray arrJSON = (JSONArray)errJSON.get("list");
        
          //[오류확인] 영업사원 등록/수정
          if("member".equals(SERVICECODE.get(IDX))){
            for (Object obj : arrJSON) {
              JSONObject jsonObj = (JSONObject)obj;
              System.out.printf(String.format("▶▶ err=%s, ", jsonObj.get("err")));
              System.out.printf(String.format("userid[PK]=%s, ", jsonObj.get("userid")));
              System.out.printf(String.format("usernm=%s, ", jsonObj.get("usernm")));
              System.out.printf(String.format("userphone=%s", jsonObj.get("userphone")));
              System.out.printf(String.format("userpw=%s", jsonObj.get("userpw")));
              System.out.printf(String.format("validyn=%s", jsonObj.get("validyn")));
              System.out.printf(String.format("useyn=%s\n", jsonObj.get("useyn")));
            }
          //[오류확인] 영업사원별 고객(거래처) 등록/수정/삭제
          }else if("customer".equals(SERVICECODE.get(IDX))){
            for (Object obj : arrJSON) {
              JSONObject jsonObj = (JSONObject)obj;
              System.out.printf(String.format("▶▶ err=%s, ", jsonObj.get("err")));
              System.out.printf(String.format("userid[PK]=%s, ", jsonObj.get("userid")));
              System.out.printf(String.format("custcode[PK]=%s, ", jsonObj.get("custcode")));
              System.out.printf(String.format("custphone[PK]=%s,", jsonObj.get("custphone")));
              System.out.printf(String.format("custname=%s, ", jsonObj.get("custname")));
              System.out.printf(String.format("useyn=%s", jsonObj.get("useyn")));
              System.out.printf(String.format("custaddress1=%s", jsonObj.get("custaddress1")));
              System.out.printf(String.format("custaddress2=%s", jsonObj.get("custaddress2")));
              System.out.printf(String.format("custzip=%s", jsonObj.get("custzip")));
              System.out.printf(String.format("businessno=%s", jsonObj.get("businessno")));
              System.out.printf(String.format("custfax=%s", jsonObj.get("custfax")));
              System.out.printf(String.format("custemail=%s\n", jsonObj.get("custemail")));
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
