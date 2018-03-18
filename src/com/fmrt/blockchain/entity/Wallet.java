package com.fmrt.blockchain.entity;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fmrt.blockchain.entity.transaction.Transaction;
import com.fmrt.blockchain.entity.transaction.TransactionInput;
import com.fmrt.blockchain.entity.transaction.TransactionOutput;
import com.fmrt.blockchain.test.TransactionBlockChain;

/**
 * 钱包
 * @author hzq
 * @date 2018/03/10
 */
public class Wallet {

	/**
	 * 私钥
	 */
	public PrivateKey privateKey;

	/**
	 * 公钥（钱包地址）
	 */
	public PublicKey publicKey;

	/**
	 * 自己未使用的交易输出集合（UTXOs）
	 */
	private HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	/**
	 * 构造方法
	 */
	public Wallet() {
		generateKeyPair();
	}

	/**
	 * 生成私钥、公钥
	 */
	private void generateKeyPair() {
		try {
			//数字签名算法ECDSA
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	        //字符串 "prime192v1" ，是曲线名称
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
	        //初始化秘钥
			keyGen.initialize(ecSpec, random); 
	        //获得椭圆曲线密钥对
			KeyPair keyPair = keyGen.generateKeyPair();
			//设置私钥、公钥
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 汇总我的钱包余额
	 * 发送给我的未使用的交易输出的总和
	 */
	public float getBalance() {
        //钱包余额
		float total = 0;   
      	//循环遍历整个区块链上的未使用的交易输出集合（UTXOs）
        for (Map.Entry<String, TransactionOutput> item: TransactionBlockChain.UTXOs.entrySet()){
           TransactionOutput UTXO = item.getValue();
            //如果这个交易输出资金是给我的
            if(UTXO.isMine(publicKey)) { 
               //把它添加到自己的未使用的交易集合
               UTXOs.put(UTXO.id,UTXO); 
               //钱包余额是所有发送给我的未使用的交易输出的总和
               total += UTXO.value ; 
            }
        }  
        return total;
	}
	
	/**
	 * 寻找能够花费的交易，
	 * 钱包从自己未使用的交易输出集合（UTXOs）中
     * 选取一个或多个可用的个体拼凑出一个大于或等于一笔交易所需的资金金额 
     * @param value 转账金额
	 */
	public ArrayList<TransactionInput> getEnoughUXTOs(float value){
        //为每一个找到的未使用的交易输出UXTO创建引用它的输入
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
        
        float total = 0;
        //循环遍历自己的未使用的交易输出集合（UTXOs）
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
           TransactionOutput UTXO = item.getValue();
           total += UTXO.value;
           //添加到输入引用inputs中，作为转账新交易中的输入inputs，
           inputs.add(new TransactionInput(UTXO.id));
           //当计算的总额恰好大于或者等于需要转账的金额时，方法会停止遍历
           if(total >= value) {
        	   break;
           }   
        }
        return inputs;
	}
	
	/**
	 * 转账
	 * @param _recipient 资金接收方的公钥（地址）
	 * @param value 转账金额
	 */
	public Transaction sendFunds(PublicKey _recipient,float value) {
        //检验是否有足够的金额发起转账
		if(getBalance() < value) {
           System.out.println("#资金发送方没有足够的资金来发送交易，交易终止...");
           return null;
        }
       
        //根据转账金额，寻找能够花费的交易 
        ArrayList<TransactionInput> inputs = getEnoughUXTOs(value);
      
        //生成交易数据
        Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
        
        //用发送方的私钥对交易数据进行签名
        newTransaction.generateSignature(privateKey);
        
        //从自己未使用的交易输出集合（UTXOs）里删除交易中使用了的输出（outputs）
        for(TransactionInput input: inputs){
          UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
	}
}
