package com.fmrt.blockchain.test;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.fmrt.blockchain.entity.Wallet;
import com.fmrt.blockchain.entity.transaction.Transaction;
import com.fmrt.blockchain.util.StringTool;

/**
 * 转账测试
 * 1、测试钱包（Wallet）
 * 2、验证签名（Signatures）
 * @author hzq
 * @date 2018/03/10
 */
public class MakeTransfer {

	public static Wallet walletA;

	public static Wallet walletB;

	/**
	 * 创建两个钱包
	 * 创建一笔交易
	 * 使用钱包A的私钥对这笔交易进行了签名
	 */
	public static void main(String[] args) {
		//使用bonceycastle来作为安全实现的提供者
		Security.addProvider(new BouncyCastleProvider());
		
		walletA = new Wallet();
		walletB = new Wallet();
		
		System.out.println("WalletA的公钥："+StringTool.getStringFromKey(walletA.publicKey));
		System.out.println("WalletA的私钥："+StringTool.getStringFromKey(walletA.privateKey));
		
		//生成交易数据
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		//用A的私钥对交易数据进行签名
		transaction.generateSignature(walletA.privateKey);
		//验证签名
		System.out.println("签名验证是否通过："+transaction.verifySignature());
	}
}
