package com.tacacs.TacacsPlusServer.utils.security.venusEncrypt;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AesTool {

	public static final String KEY_ALGORITHM = "AES";
	public static final String CIPHER_ALGORITHM = "AES/CTR/PKCS5Padding";
	public static final String ivParameter = "1234567890abcdef";

	public static String Encrypt(String key, String text) {
		try {
			byte[] keyBytes = key.getBytes();
			IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
			SecretKeySpec sKeySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);
			byte[] encrypted = cipher.doFinal(text.getBytes("utf-8"));
			return new BASE64Encoder().encode(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	public static String Decrypt(String Key, String text) {
		try {
			byte[] raw = Key.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_ALGORITHM);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());			
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = new BASE64Decoder().decodeBuffer(text);			
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "utf-8");
			return originalString;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * 用于生成加密的key
	 * @return
	 */
	public static String generateKey(){
		String storeStr="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789";
		int len=16;
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < len; i++) {
			double index=Math.floor(Math.random()*storeStr.length());
			sb.append(storeStr.charAt((int)index));
		}
		return sb.toString();
	}
	 
	 public static void main(String[] args) throws Exception {
		 String str=AesTool.generateKey();
		 System.out.println(str);
		 String value = AesTool.Encrypt("8401673977560707", "{\"id\":\"1\",\"name\":\"123\"}");
		 System.out.println(value);
	}
}
