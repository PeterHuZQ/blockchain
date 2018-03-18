package com.fmrt.blockchain.entity.transaction;

/**
 * 交易输入类--进账
 * 一笔交易的“交易输入”其实是指向上一笔交易的“交易输出” 
 * @author hzq
 * @date 2018/03/09
 */
public class TransactionInput {
	
	/**
	 * 将用于查找上一笔交易的交易输出类的Id
	 */
	public String transactionOutputId; 
	
	/**
	 * 交易输出UTXO（未使用的交易）
	 */
	public TransactionOutput UTXO; 

	/**
	 * 构造方法
	 */
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
