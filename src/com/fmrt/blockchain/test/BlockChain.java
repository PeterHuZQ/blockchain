package com.fmrt.blockchain.test;

import java.util.ArrayList;

import com.fmrt.blockchain.entity.block.Block;
import com.fmrt.blockchain.util.StringTool;

/**
 * 创建基本的区块链
 * @author hzq
 * @date 2018/02/03
 */
public class BlockChain {
	
	public static ArrayList<Block> blocklist = new ArrayList<Block>();
	
	/**
	 * 挖矿复杂度
	 */
	public static int difficulty = 20;
	
	/**
	 * 生成创世块
	 */
	public static Block creatGenesisBlock(){
		return new Block(0,"进行coinbase交易，生成第一个交易输出", "0"); 
	}
	
	/**
	 * 创建新区快
	 */
	public static Block newBlock(int index, String data, String previousHash){
	    return new Block(index,data,previousHash);
	}
	
	/**
	 * 获取区块链中最新区块
	 */
	public static Block getLatestBlock(){
		return blocklist.get(blocklist.size() - 1); 
	}
	
	/**
	 * 添加区块
	 */
	public static void addBlock(Block block) {
	      //通过矿工们来做工作量证明（POW）来挖掘并验证新的区块
		  block.ProofOfWork(difficulty);
	      //添加区块
		  blocklist.add(block);
	}
	
	/**
	 * 检查链的完整性
	 */
	public static Boolean isChainValid() {
	   Block currentBlock; 
	   Block previousBlock;
	   
	   //循环遍历每个块检查hash,从1开始，0为创世块（数据固定）
	   for(int i=1; i < blocklist.size(); i++) {
	      currentBlock = blocklist.get(i);
	      previousBlock = blocklist.get(i-1);
	      //判断链中当前块的hash变量的值与计算出来的hash是否相等
	      if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
	         System.out.println("Current Hashes not equal");          
	         return false;
	      }
	      //判断previousHash变量的值与前一个块的hash是否相等
	      if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
	         System.out.println("Previous Hashes not equal");
	         return false;
	      }
	   }
	   return true;
	}
	
	public static void main(String[] args) {
      System.out.println("正在尝试挖掘创世块... ");
      addBlock(creatGenesisBlock());

      System.out.println("正在尝试挖掘第2个区块... ");
      addBlock(newBlock(1,"这是第二个区块",getLatestBlock().hash));

      System.out.println("正在尝试挖掘第3个区块... ");
      addBlock(newBlock(2,"这是第三个区块",getLatestBlock().hash));

      System.out.println("\n检查链的完整性: " + isChainValid());
      
      System.out.println("\n区块链如下: ");
      System.out.println(StringTool.getJson(blocklist));
	}
	
}
