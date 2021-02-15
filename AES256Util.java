import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.CharEncoding;

public class AES256Util {
  private final Key keySpec;
  private final byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

  /**
    * @category 생성자
    * @param    암호화키
    * @exception 
    */
  public AES256Util(String key) throws Exception {
    byte[] keyBytes = key.getBytes(CharEncoding.UTF_8);
    SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

    this.keySpec = keySpec;
  }
	
  /**
    * @category 암호화
    * @param    암호화 할 문자
    * @return   암호화 된 문자
    * @exception 
    */
  public String strEncode(String str) throws Exception {
    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
    c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));

    byte[] encrypted = c.doFinal(str.getBytes(CharEncoding.UTF_8));
    String enStr = new String(Base64.encodeBase64(encrypted));

    return enStr;
  }

  /**
    * @category 복호화
    * @param    복호화 할 문자 
    * @return   복호화 된 문자 
    * @exception
    */
  public String strDecode(String str) throws Exception {
    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
    c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));

    byte[] byteStr = Base64.decodeBase64(str.getBytes());

    return new String(c.doFinal(byteStr), CharEncoding.UTF_8);	
  }
}
