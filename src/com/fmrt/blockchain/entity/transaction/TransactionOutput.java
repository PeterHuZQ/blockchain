package com.fmrt.blockchain.entity.transaction;

import java.security.PublicKey;
import com.fmrt.blockchain.util.EncryptTool;
import com.fmrt.blockchain.util.StringTool;

/**
 * ���������--����
 * �ʽ��ͷ������ʽ�����ʽ���շ�
 * @author hzq
 * @date 2018/03/09
 */
public class TransactionOutput {
	
	public String id;
	
	/**
	 * �ʽ���շ��Ĺ�Կ��Ǯ����ַ��
	 */
	public PublicKey reciepient;
	
	/**
	 * �ʽ��ͷ�ӵ�е��ʽ���
	 */
	public float value; 
	
	/**
	 * ������Transaction��transactionId
	 */
	public String parentTransactionId; 

	/**
	 * ���췽��
	 */
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = EncryptTool.applySha256(StringTool.getStringFromKey(reciepient) + Float.toString(value) + parentTransactionId);
	}

	/**
	 * У�����Ǯ�Ƿ������ʽ���շ�
	 */
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
}
