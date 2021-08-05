package com.tacacs.TacacsPlusServer.utils.security.enterprise;


public class CryptUtil {

	/**
	 * 解密函数
	 * @param encryptString String 已经加密的字符串
	 * @return String 解密后的字符串
	 */
	public static String decrypt(String encryptString){
		if(AES.getInstance() != null){
			try{
				return AES.getInstance().decrypt(encryptString);
			}
			catch(Exception e){
				e.printStackTrace();
				return encryptString;
			}
		}
		else{
			System.out.println("AES.getInstance() error");
			return encryptString;
		}
	}
	
	
	/**
	 * 加密函数
	 * @param originString String 原始字符串，即需要加密的字符串
	 * @return String 加密后的字符串
	 */
	public static String encrypt(String originString){
		if(AES.getInstance() != null){
			try{
				return AES.getInstance().encrypt(originString);
			}
			catch(Exception e){
				return originString;
			}
		}
		else{
			return originString;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(CryptUtil.decrypt("b95554f429edb37cad26f866ae5ae7f0"));
	}
}
