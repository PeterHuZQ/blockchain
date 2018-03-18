package com.fmrt.blockchain.test;

import java.util.ArrayList;

import com.fmrt.blockchain.entity.block.Block;
import com.fmrt.blockchain.util.StringTool;

/**
 * ����������������
 * @author hzq
 * @date 2018/02/03
 */
public class BlockChain {
	
	public static ArrayList<Block> blocklist = new ArrayList<Block>();
	
	/**
	 * �ڿ��Ӷ�
	 */
	public static int difficulty = 20;
	
	/**
	 * ���ɴ�����
	 */
	public static Block creatGenesisBlock(){
		return new Block(0,"����coinbase���ף����ɵ�һ���������", "0"); 
	}
	
	/**
	 * ����������
	 */
	public static Block newBlock(int index, String data, String previousHash){
	    return new Block(index,data,previousHash);
	}
	
	/**
	 * ��ȡ����������������
	 */
	public static Block getLatestBlock(){
		return blocklist.get(blocklist.size() - 1); 
	}
	
	/**
	 * �������
	 */
	public static void addBlock(Block block) {
	      //ͨ����������������֤����POW�����ھ���֤�µ�����
		  block.ProofOfWork(difficulty);
	      //�������
		  blocklist.add(block);
	}
	
	/**
	 * �������������
	 */
	public static Boolean isChainValid() {
	   Block currentBlock; 
	   Block previousBlock;
	   
	   //ѭ������ÿ������hash,��1��ʼ��0Ϊ�����飨���ݹ̶���
	   for(int i=1; i < blocklist.size(); i++) {
	      currentBlock = blocklist.get(i);
	      previousBlock = blocklist.get(i-1);
	      //�ж����е�ǰ���hash������ֵ����������hash�Ƿ����
	      if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
	         System.out.println("Current Hashes not equal");          
	         return false;
	      }
	      //�ж�previousHash������ֵ��ǰһ�����hash�Ƿ����
	      if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
	         System.out.println("Previous Hashes not equal");
	         return false;
	      }
	   }
	   return true;
	}
	
	public static void main(String[] args) {
      System.out.println("���ڳ����ھ�����... ");
      addBlock(creatGenesisBlock());

      System.out.println("���ڳ����ھ��2������... ");
      addBlock(newBlock(1,"���ǵڶ�������",getLatestBlock().hash));

      System.out.println("���ڳ����ھ��3������... ");
      addBlock(newBlock(2,"���ǵ���������",getLatestBlock().hash));

      System.out.println("\n�������������: " + isChainValid());
      
      System.out.println("\n����������: ");
      System.out.println(StringTool.getJson(blocklist));
	}
	
}
