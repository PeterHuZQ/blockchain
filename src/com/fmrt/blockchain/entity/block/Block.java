package com.fmrt.blockchain.entity.block;

import java.util.Date;

import com.fmrt.blockchain.util.EncryptTool;
import com.fmrt.blockchain.util.StringTool;

/**
 * ����
 * @author hzq
 * @date 2018/02/03
 */
public class Block {
	
	/**
	 * ��������ʶ������
	 */
	public int index;
	
	/**
	 * �����ϣֵ������ǩ����
	 */
	public String hash;
	
	/**
	 * ǰһ������Ĺ�ϣֵ
	 */
	public String previousHash;
	
	/**
	 * ʱ���:��������Ľ���ʱ�䣨��λ���룩
	 */
	private long timeStamp; 
	
	/**
	 * ���ڹ�����֤���㷨�ļ�����
	 */
	private int nonce;
	
	/**
	 * �����ϼ�¼�����ݣ��Զ��壩
	 */
	private String data; 
	
	/**
	 * ���췽��
	 */
	public Block(int index, String data, String previousHash) {
		this.index = index;
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}
	
	/**
	 * ��������hash
	 * ͨ��SHA256�㷨������ͷ���ж��ι�ϣ������õ�������ǩ��
	 */
	public String calculateHash () {
		StringBuffer input = new StringBuffer().append(previousHash).append(data).append(timeStamp).append(nonce);
	    return EncryptTool.applySha256(input.toString());
	}
	
	/**
	 * ������֤����Proof-of-Work��
	 * ��֤hash��ֱ�����hash��difficulty��0��ͷ
	 * @param difficulty �ڿ��Ӷ�
	 */
	public void ProofOfWork (int difficulty) { 
		//Ŀ���Ѷ�
		String target = StringTool.getDificultyString(difficulty); 
		//��ȡhashǰdifficultyλ���ж��Ƿ���targetһ��
		while(!hash.substring( 0, difficulty).equals(target)) {
			// ����nonce���Բ���ͨ��calculaeHash������������Ĺ�ϣֵ
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("�󹤾��������ļ��㣬���ַ���Ҫ���������: \nhash:" + hash +"\n����:"+ nonce);
	} 
}
