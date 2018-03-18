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
 * 1����A�ڵ������飨genesis block��������˻��50���ҵĽ���
 * 2��һЩ������Ϣ�������ǿ����ڲ����е�ϸ����Ϣ
 * 3���ʻ������еġ����µ���������Ч�Լ��
 * @author hzq
 * @date 2018/03/10
 */
public class TransactionBlockChain {
	
	public static ArrayList<TransactionBlock> blockchain = new ArrayList<TransactionBlock>();
	
	/**
	 * δʹ�õĽ���������ϣ�UTXOs��
	 */
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

	/**
	 * �ڿ��Ӷ�
	 */
	public static int difficulty = 3;
	
	/**
	 * ��С�����ʽ���
	 */
	public static float minimumTransaction = 0.1f;
	
	public static Wallet walletA;
	
	public static Wallet walletB;
	
	public static Transaction genesisTransaction;

	
	/**
	 * ����CoinBase����
	 * @param reciepient ���˵�Ǯ����ַ
	 * @param value ת�˵Ľ��
	 */
	public static Transaction newCoinBaseTX(PublicKey reciepient,float value){
		Wallet coinbase = new Wallet();
		//���ɴ�����������
	    genesisTransaction = new Transaction(coinbase.publicKey, reciepient, value, null);
	    //�Դ�����������ǩ��
	    genesisTransaction.generateSignature(coinbase.privateKey);  
	    //����transactionId
	    genesisTransaction.transactionId = "0"; 
	    //��������ƾ�ղ����Ľ������outputs
	    genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); 
	    //��ƾ�մ���ĵ�һ������������洢��δʹ�õĽ���������UTXOs
	    UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); 
	    return genesisTransaction;
	}
	
	public static void main(String[] args) {
		//ʹ��bonceycastle����Ϊ��ȫʵ�ֵ��ṩ��
	    Security.addProvider(new BouncyCastleProvider()); 

	    walletA = new Wallet();
	    walletB = new Wallet();
	    
	    //�����ھ򡰴����顱�ɹ�
	    TransactionBlock genesis = new TransactionBlock("0");
	    addBlock(genesis);
	    System.out.println("��A�ھ򡰴����顱�ɹ������������CoinBase���ף���A���50���ҵĽ���... ");
	    //����CoinBase����,��A���50���ҵĽ���
	    Transaction genesisTransaction = newCoinBaseTX(walletA.publicKey,50f);
	    //�����������һ�ʽ���
	    genesis.addTransaction(genesisTransaction);
	    System.out.println("WalletA��Ǯ����� : " + walletA.getBalance()+"\n");
	    
	    //����WalletA���ڳ��Է���40���Ҹ�WalletB
	    System.out.println("WalletA���ڳ��Է���40���Ҹ�WalletB...");
	    TransactionBlock block1 = new TransactionBlock(genesis.hash);
	    addBlock(block1);
	    block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
	    System.out.println("WalletA��Ǯ�����: " + walletA.getBalance());
	    System.out.println("WalletB��Ǯ�����: " + walletB.getBalance()+"\n");

	    //����WalletA���Է���1000���Ҹ�WalletB
	    System.out.println("WalletA���Է���1000���Ҹ�WalletB...");
	    TransactionBlock block2 = new TransactionBlock(block1.hash);
	    addBlock(block2);
	    block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
	    System.out.println("WalletA��Ǯ�����: " + walletA.getBalance());
	    System.out.println("WalletB��Ǯ�����: " + walletB.getBalance()+"\n");

	    //����WalletB���ڳ��Է���20���Ҹ�WalletA
	    System.out.println("WalletB���ڳ��Է���20���Ҹ�WalletA...");
	    TransactionBlock block3 = new TransactionBlock(block2.hash);
	    addBlock(block3);
	    block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
	    System.out.println("WalletA��Ǯ�����: " + walletA.getBalance());
	    System.out.println("WalletB��Ǯ�����: " + walletB.getBalance()+"\n");

	    System.out.println("\n�������������: " + isChainValid());
	}
	
	/**
	 * �������������
	 * �ʻ������еġ����µ���������Ч�Լ��
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
	 * �������
	 */
	public static void addBlock(TransactionBlock newBlock) {
		  //ͨ����������������֤����POW�����ھ���֤�µ�����
		  newBlock.ProofOfWork(difficulty);
		  //�������
		  blockchain.add(newBlock);
	}
}
