package com.fmrt.blockchain.entity.block;

import java.util.Date;

import com.fmrt.blockchain.util.EncryptTool;
import com.fmrt.blockchain.util.StringTool;

/**
 * 区块
 * @author hzq
 * @date 2018/02/03
 */
public class Block {
	
	/**
	 * 索引：标识区块编号
	 */
	public int index;
	
	/**
	 * 区块哈希值（数字签名）
	 */
	public String hash;
	
	/**
	 * 前一个区块的哈希值
	 */
	public String previousHash;
	
	/**
	 * 时间戳:区块产生的近似时间（单位：秒）
	 */
	private long timeStamp; 
	
	/**
	 * 用于工作量证明算法的计数器
	 */
	private int nonce;
	
	/**
	 * 区块上记录的数据（自定义）
	 */
	private String data; 
	
	/**
	 * 构造方法
	 */
	public Block(int index, String data, String previousHash) {
		this.index = index;
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}
	
	/**
	 * 计算区块hash
	 * 通过SHA256算法对区块头进行二次哈希计算而得到的数字签名
	 */
	public String calculateHash () {
		StringBuffer input = new StringBuffer().append(previousHash).append(data).append(timeStamp).append(nonce);
	    return EncryptTool.applySha256(input.toString());
	}
	
	/**
	 * 工作量证明（Proof-of-Work）
	 * 验证hash，直到块的hash以difficulty个0开头
	 * @param difficulty 挖矿复杂度
	 */
	public void ProofOfWork (int difficulty) { 
		//目标难度
		String target = StringTool.getDificultyString(difficulty); 
		//截取hash前difficulty位，判断是否与target一致
		while(!hash.substring( 0, difficulty).equals(target)) {
			// 更新nonce可以不断通过calculaeHash方法生成所需的哈希值
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("矿工经过大量的计算，发现符合要求的新区块: \nhash:" + hash +"\n次数:"+ nonce);
	} 
}
