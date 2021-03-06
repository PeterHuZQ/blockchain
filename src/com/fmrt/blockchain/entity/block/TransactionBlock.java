package com.fmrt.blockchain.entity.block;

import java.util.ArrayList;
import java.util.Date;

import com.fmrt.blockchain.entity.transaction.Transaction;
import com.fmrt.blockchain.util.EncryptTool;
import com.fmrt.blockchain.util.StringTool;

/**
 * 区块（存储交易信息的区块）
 * @author hzq
 * @date 2018/03/09
 */
public class TransactionBlock {
	
	/**
	 * 区块哈希值
	 */
	public String hash;
	
	/**
	 * 前一个区块的哈希值
	 */
	public String previousHash;
	
	/**
	 * 区块上记录的数据(交易信息)
	 */
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	
	/**
	 * 时间戳：区块产生的近似时间（单位：秒）
	 */
	private long timeStamp; 

	/**
	 * 用于工作量证明算法的计数器
	 */
	private int nonce;

	/**
	 * Merkle树的根：该区块中所有交易形成的merkle树的树根
	 */
	public String merkleRoot;
	
	/**
	 * 构造方法
	 */
	public TransactionBlock(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}
	
	/**
	 * 计算区块hash
	 * 通过SHA256算法对区块头进行二次哈希计算而得到的数字签名
	 */
	public String calculateHash () {
		StringBuffer input = new StringBuffer().append(previousHash).append(merkleRoot).append(timeStamp).append(nonce);
	    return EncryptTool.applySha256(input.toString());
	}
	
	/**
	 * 工作量证明（Proof-of-Work）
	 * 验证hash，直到块的hash以difficulty个0开头
	 * @param difficulty 挖矿复杂度
	 */
	public void ProofOfWork (int difficulty) { 
		merkleRoot = StringTool.getMerkleRoot(transactions);
		String target = StringTool.getDificultyString(difficulty); 
		//截取hash前difficulty位，判断是否与target一致
		while(!hash.substring( 0, difficulty).equals(target)) {
			// 更新nonce可以不断通过calculaeHash方法生成所需的哈希值
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("矿工经过大量的计算，发现符合要求的新区块: \nhash:" + hash);
	} 
	
	/**
	 * 往区块里添加一笔交易
	 * @param transaction
	 */
	public boolean addTransaction(Transaction transaction) {
	      if(transaction == null) {
	    	  return false;
	      }
	      //非创世块
	      if((previousHash != "0")) {
	    	 //处理交易
	         if((transaction.processTransaction() != true)) {
	            System.out.println("交易处理失败");
	            return false;
	         }
	      }

	      transactions.add(transaction);
	      System.out.println("成功把交易记录到区块中");
	      return true;
	}
}
