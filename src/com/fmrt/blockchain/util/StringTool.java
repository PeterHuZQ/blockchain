package com.fmrt.blockchain.util;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.fmrt.blockchain.entity.transaction.Transaction;
import com.google.gson.GsonBuilder;

public class StringTool {

	/**
	 * ͨ��GSON��ת����JSON
	 */
	public static String getJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}

	/**
	 * ����һ���� difficulty * "0" ��ɵ��ַ���
	 * @param difficulty �ڿ��Ӷ�
	 */
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}

	/**
	 * ����ECDSAǩ��
	 * ���շ��ͷ���˽Կ���ַ������룬�������ǩ���������ֽ�����
	 * @param privateKey
	 * @param input
	 */
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			// ����ǩ���㷨ECDSA
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}

	/**
	 * ��֤ǩ��
	 * ���ܹ�Կ���ַ������ݡ�ǩ�������ǩ������Ч�ģ��򷵻�true������false
	 * @param publicKey
	 * @param data
	 * @param signature
	 */
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ��Keyת��ΪString
	 * @param key
	 */
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	/**
	 * ���Ĭ�˶���(MerkleTree)
	 * �����а����������еĽ��״��������γ�MerkleTree
	 * @param transactions
	 */
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();
		List<String> previousTreeLayer = new ArrayList<String>();
	    for(Transaction transaction : transactions) {
	      previousTreeLayer.add(transaction.transactionId);
	    }
		List<String> treeLayer = previousTreeLayer;

		while(count > 1) {
		   treeLayer = new ArrayList<String>();
		   for(int i=1; i < previousTreeLayer.size(); i+=2) {
		      treeLayer.add(EncryptTool.applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
		   }
		   count = treeLayer.size();
		   previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}
}
