package com.fmrt.blockchain.entity.transaction;

/**
 * ����������--����
 * һ�ʽ��׵ġ��������롱��ʵ��ָ����һ�ʽ��׵ġ���������� 
 * @author hzq
 * @date 2018/03/09
 */
public class TransactionInput {
	
	/**
	 * �����ڲ�����һ�ʽ��׵Ľ���������Id
	 */
	public String transactionOutputId; 
	
	/**
	 * �������UTXO��δʹ�õĽ��ף�
	 */
	public TransactionOutput UTXO; 

	/**
	 * ���췽��
	 */
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
