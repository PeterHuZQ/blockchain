package com.fmrt.blockchain.util;

import java.security.MessageDigest;

/**
 * ��SHA256����Hashֵ
 */
public class EncryptTool {

	/**
	 * ����Hashֵ
	 * ���������ת���ɹ�ϣֵ����
	 */
	public static String applySha256(String input){
		try {
			 //ͨ��MessageDigest��ʹ��SHA256�����㷨
	         MessageDigest digest = MessageDigest.getInstance("SHA-256");
	         //������inputʹ�� sha256 �㷨
	         byte[] hash = digest.digest(input.getBytes("UTF-8"));
	           
	         StringBuffer hexString = new StringBuffer(); 
	         
	         for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) {
	            	hexString.append('0');
	            }else{
	            	hexString.append(hex);
	            }
	         }
	         
	         return hexString.toString();
	      }
	      catch(Exception e) {
	         throw new RuntimeException(e);
	      }
	}
}
