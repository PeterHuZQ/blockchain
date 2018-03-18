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
	 * 通过GSON库转换成JSON
	 */
	public static String getJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}

	/**
	 * 创建一个用 difficulty * "0" 组成的字符串
	 * @param difficulty 挖矿复杂度
	 */
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}

	/**
	 * 生成ECDSA签名
	 * 接收发送方的私钥和字符串输入，对其进行签名并返回字节数组
	 * @param privateKey
	 * @param input
	 */
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			// 数字签名算法ECDSA
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
	 * 验证签名
	 * 接受公钥、字符串数据、签名，如果签名是有效的，则返回true，否则false
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
	 * 把Key转换为String
	 * @param key
	 */
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	/**
	 * 获得默克尔树(MerkleTree)
	 * 将所有包含在区块中的交易串联起来形成MerkleTree
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
