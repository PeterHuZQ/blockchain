package com.fmrt.blockchain.test;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.fmrt.blockchain.entity.Wallet;
import com.fmrt.blockchain.entity.transaction.Transaction;
import com.fmrt.blockchain.util.StringTool;

/**
 * ת�˲���
 * 1������Ǯ����Wallet��
 * 2����֤ǩ����Signatures��
 * @author hzq
 * @date 2018/03/10
 */
public class MakeTransfer {

	public static Wallet walletA;

	public static Wallet walletB;

	/**
	 * ��������Ǯ��
	 * ����һ�ʽ���
	 * ʹ��Ǯ��A��˽Կ����ʽ��׽�����ǩ��
	 */
	public static void main(String[] args) {
		//ʹ��bonceycastle����Ϊ��ȫʵ�ֵ��ṩ��
		Security.addProvider(new BouncyCastleProvider());
		
		walletA = new Wallet();
		walletB = new Wallet();
		
		System.out.println("WalletA�Ĺ�Կ��"+StringTool.getStringFromKey(walletA.publicKey));
		System.out.println("WalletA��˽Կ��"+StringTool.getStringFromKey(walletA.privateKey));
		
		//���ɽ�������
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		//��A��˽Կ�Խ������ݽ���ǩ��
		transaction.generateSignature(walletA.privateKey);
		//��֤ǩ��
		System.out.println("ǩ����֤�Ƿ�ͨ����"+transaction.verifySignature());
	}
}
