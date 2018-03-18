package com.fmrt.blockchain.test;

import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.fmrt.blockchain.entity.Wallet;
import com.fmrt.blockchain.entity.block.TransactionBlock;
import com.fmrt.blockchain.entity.transaction.Transaction;
import com.fmrt.blockchain.entity.transaction.TransactionInput;
import com.fmrt.blockchain.entity.transaction.TransactionOutput;

/**
 * 1、矿工A挖到创世块（genesis block），并因此获得50个币的奖励
 * 2、一些测试信息，让我们看到内部运行的细节信息
 * 3、帐户交易中的“更新的链”的有效性检查
 * @author hzq
 * @date 2018/03/10
 */
public class TransactionBlockChain {
	
	public static ArrayList<TransactionBlock> blockchain = new ArrayList<TransactionBlock>();
	
	/**
	 * 未使用的交易输出集合（UTXOs）
	 */
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

	/**
	 * 挖矿复杂度
	 */
	public static int difficulty = 3;
	
	/**
	 * 最小交易资金金额
	 */
	public static float minimumTransaction = 0.1f;
	
	public static Wallet walletA;
	
	public static Wallet walletB;
	
	public static Transaction genesisTransaction;

	
	/**
	 * 创建CoinBase交易
	 * @param reciepient 收账的钱包地址
	 * @param value 转账的金额
	 */
	public static Transaction newCoinBaseTX(PublicKey reciepient,float value){
		Wallet coinbase = new Wallet();
		//生成创世交易数据
	    genesisTransaction = new Transaction(coinbase.publicKey, reciepient, value, null);
	    //对创世交易生成签名
	    genesisTransaction.generateSignature(coinbase.privateKey);  
	    //设置transactionId
	    genesisTransaction.transactionId = "0"; 
	    //创世交易凭空产生的交易输出outputs
	    genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); 
	    //把凭空创造的第一个交易输出，存储在未使用的交易输出结合UTXOs
	    UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); 
	    return genesisTransaction;
	}
	
	public static void main(String[] args) {
		//使用bonceycastle来作为安全实现的提供者
	    Security.addProvider(new BouncyCastleProvider()); 

	    walletA = new Wallet();
	    walletB = new Wallet();
	    
	    //测试挖掘“创世块”成功
	    TransactionBlock genesis = new TransactionBlock("0");
	    addBlock(genesis);
	    System.out.println("矿工A挖掘“创世块”成功，区块中添加CoinBase交易，矿工A获得50个币的奖励... ");
	    //创建CoinBase交易,矿工A获得50个币的奖励
	    Transaction genesisTransaction = newCoinBaseTX(walletA.publicKey,50f);
	    //往区块里添加一笔交易
	    genesis.addTransaction(genesisTransaction);
	    System.out.println("WalletA的钱包余额 : " + walletA.getBalance()+"\n");
	    
	    //测试WalletA正在尝试发送40个币给WalletB
	    System.out.println("WalletA正在尝试发送40个币给WalletB...");
	    TransactionBlock block1 = new TransactionBlock(genesis.hash);
	    addBlock(block1);
	    block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
	    System.out.println("WalletA的钱包余额: " + walletA.getBalance());
	    System.out.println("WalletB的钱包余额: " + walletB.getBalance()+"\n");

	    //测试WalletA尝试发送1000个币给WalletB
	    System.out.println("WalletA尝试发送1000个币给WalletB...");
	    TransactionBlock block2 = new TransactionBlock(block1.hash);
	    addBlock(block2);
	    block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
	    System.out.println("WalletA的钱包余额: " + walletA.getBalance());
	    System.out.println("WalletB的钱包余额: " + walletB.getBalance()+"\n");

	    //测试WalletB正在尝试发送20个币给WalletA
	    System.out.println("WalletB正在尝试发送20个币给WalletA...");
	    TransactionBlock block3 = new TransactionBlock(block2.hash);
	    addBlock(block3);
	    block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
	    System.out.println("WalletA的钱包余额: " + walletA.getBalance());
	    System.out.println("WalletB的钱包余额: " + walletB.getBalance()+"\n");

	    System.out.println("\n检查链的完整性: " + isChainValid());
	}
	
	/**
	 * 检查链的完整性
	 * 帐户交易中的“更新的链”的有效性检查
	 */
	public static Boolean isChainValid() {
		TransactionBlock currentBlock;
		TransactionBlock previousBlock;
	    String hashTarget = new String(new char[difficulty]).replace('\0', '0');
	    //a temporary working list of unspent transactions at a given block state.
	    HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); 
	    tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

	    //loop through blockchain to check hashes:
	    for(int i=1; i < blockchain.size(); i++) {
	    	currentBlock = blockchain.get(i);
	        previousBlock = blockchain.get(i-1);
	        //compare registered hash and calculated hash:
	        if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
	            System.out.println("#Current Hashes not equal");
	            return false;
	        }
	        //compare previous hash and registered previous hash
	        if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
	            System.out.println("#Previous Hashes not equal");
	            return false;
	        }
	        //check if hash is solved
	        if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
	            System.out.println("#This block hasn't been mined");
	            return false;
	        }

	        //loop thru blockchains transactions:
	        TransactionOutput tempOutput;
	        for(int t=0; t <currentBlock.transactions.size(); t++) {
	            Transaction currentTransaction = currentBlock.transactions.get(t);

	            if(!currentTransaction.verifySignature()) {
	               System.out.println("#Signature on Transaction(" + t + ") is Invalid");
	               return false;
	            }
	            
	            for(TransactionInput input: currentTransaction.inputs) {
	               tempOutput = tempUTXOs.get(input.transactionOutputId);

	               if(tempOutput == null) {
	                  System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
	                  return false;
	               }

	               if(input.UTXO.value != tempOutput.value) {
	                  System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
	                  return false;
	               }

	               tempUTXOs.remove(input.transactionOutputId);
	            }

	            for(TransactionOutput output: currentTransaction.outputs) {
	               tempUTXOs.put(output.id, output);
	            }

	            if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
	               System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
	               return false;
	            }
	            if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
	               System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
	               return false;
	            }
	        }
	      }
	      //System.out.println("Blockchain is valid");
	      return true;
	}
	
	/**
	 * 添加区块
	 */
	public static void addBlock(TransactionBlock newBlock) {
		  //通过矿工们来做工作量证明（POW）来挖掘并验证新的区块
		  newBlock.ProofOfWork(difficulty);
		  //添加区块
		  blockchain.add(newBlock);
	}
}
