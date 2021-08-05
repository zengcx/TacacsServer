package com.tacacs.TacacsPlusServer.utils.security.venusEncrypt;

/**
 * 
 * @since :		JDK 1.7
 */
public class VenusEncrypt {
	
	private static String SECRETKEY = "venus_4a@1234567";


	/**
	 * 获取4a加密密匙
	 * 
	 * @param keyid
	 *            调用者keyid
	 * @param secrytKey
	 *            密匙
	 * @param data
	 *            加密数据
	 */
	public String encryptData(String secrytKey, String data) {
		String encrypt = "";
		if(secrytKey == null  && "".equals(secrytKey.trim())){
			return convertUTF8("密匙为空!");
		}
		if(data == null  && "".equals(data.trim())){
			return convertUTF8("加密数据不能为空!");
		}
		try {
//			String temp = new AESEncrypt(SECRETKEY).decrypt(secrytKey); // 解密后密匙
//			encrypt = new AESEncrypt(temp).encrypt(data);
			encrypt = new AESEncrypt(secrytKey).encrypt(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return encrypt;
	}
	
	/**
	 * 获取4a加密密匙
	 * 
	 * @param keyid
	 *            调用者keyid
	 * @param secrytKey
	 *            密匙
	 * @param data
	 *            加密数据
	 */
	public String decryptData(String secrytKey, String data) {
		String encrypt = "";
		if(secrytKey == null  && "".equals(secrytKey.trim())){
			return convertUTF8("密匙为空!");
		}
		if(data == null  && "".equals(data.trim())){
			return convertUTF8("解密数据不能为空!");
		}
		try {
//			String temp = new AESEncrypt(SECRETKEY).decrypt(secrytKey); // 解密后密匙
//			encrypt = new AESEncrypt(temp).decrypt(data);
			encrypt = new AESEncrypt(secrytKey).decrypt(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return encrypt;
	}
	
	private String convertUTF8(String info){
		String result = info;
		try{
			result = new String(info.getBytes(),"UTF-8");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		
		String encryptData = new VenusEncrypt().encryptData("7727656213482279", "111");
		System.out.println(encryptData);
	}
}
