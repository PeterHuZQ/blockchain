package com.fmrt.blockchain.entity.block;

import java.util.ArrayList;
import java.util.Date;

import com.fmrt.blockchain.entity.transaction.Transaction;
import com.fmrt.blockchain.util.EncryptTool;
import com.fmrt.blockchain.util.StringTool;

/**
 * ���飨�洢������Ϣ�����飩
 * @author hzq
 * @date 2018/03/09
 */
public class TransactionBlock {
	
	/**
	 * �����ϣֵ
	 */
	public String hash;
	
	/**
	 * ǰһ������Ĺ�ϣֵ
	 */
	public String previousHash;
	
	/**
	 * �����ϼ�¼������(������Ϣ)
	 */
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	
	/**
	 * ʱ�������������Ľ���ʱ�䣨��λ���룩
	 */
	private long timeStamp; 

	/**
	 * ���ڹ�����֤���㷨�ļ�����
	 */
	private int nonce;

	/**
	 * Merkle���ĸ��������������н����γɵ�merkle��������
	 */
	public String merkleRoot;
	
	/**
	 * ���췽��
	 */
	public TransactionBlock(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}
	
	/**
	 * ��������hash
	 * ͨ��SHA256�㷨������ͷ���ж��ι�ϣ������õ�������ǩ��
	 */
	public String calculateHash () {
		StringBuffer input = new StringBuffer().append(previousHash).append(merkleRoot).append(timeStamp).append(nonce);
	    return EncryptTool.applySha256(input.toString());
	}
	
	/**
	 * ������֤����Proof-of-Work��
	 * ��֤hash��ֱ�����hash��difficulty��0��ͷ
	 * @param difficulty �ڿ��Ӷ�
	 */
	public void ProofOfWork (int difficulty) { 
		merkleRoot = StringTool.getMerkleRoot(transactions);
		String target = StringTool.getDificultyString(difficulty); 
		//��ȡhashǰdifficultyλ���ж��Ƿ���targetһ��
		while(!hash.substring( 0, difficulty).equals(target)) {
			// ����nonce���Բ���ͨ��calculaeHash������������Ĺ�ϣֵ
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("�󹤾��������ļ��㣬���ַ���Ҫ���������: \nhash:" + hash);
	} 
	
	/**
	 * �����������һ�ʽ���
	 * @param transaction
	 */
	public boolean addTransaction(Transaction transaction) {
	      if(transaction == null) {
	    	  return false;
	      }
	      //�Ǵ�����
	      if((previousHash != "0")) {
	    	 //������
	         if((transaction.processTransaction() != true)) {
	            System.out.println("���״���ʧ��");
	            return false;
	         }
	      }

	      transactions.add(transaction);
	      System.out.println("�ɹ��ѽ��׼�¼��������");
	      return true;
	}
}
