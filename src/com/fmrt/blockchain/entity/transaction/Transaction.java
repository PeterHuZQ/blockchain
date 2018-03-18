package com.fmrt.blockchain.entity.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fmrt.blockchain.test.TransactionBlockChain;
import com.fmrt.blockchain.util.EncryptTool;
import com.fmrt.blockchain.util.StringTool;

/**
 * 交易信息
 * @author hzq
 * @date 2018/03/09
 */
public class Transaction {

	/**
	 * 交易的hash（唯一的）
	 */
	public String transactionId;

	/**
	 * 交易输入（inputs）
	 * 这个输入是对以前交易的引用，这些交易证明资金发送方拥有要发送的资金。
	 */
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	
	/**
	 * 交易输出（outputs）
	 * 显示交易中收到的相关地址量。（这些输出作为新交易中的输入引用）
	 */
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	/**
	 * 未使用的交易输出集合（UTXOs）
	 */
	private HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	/**
	 * 资金发送方的公钥（钱包地址）
	 */
	public PublicKey sender;

	/**
	 * 资金接收方的公钥（钱包地址）
	 */
	public PublicKey reciepient;

	/**
	 * 要转移的资金金额
	 */
	public float value;

	/**
	 * 加密签名
	 * 证明地址的所有者是发起该交易的人，并且数据没有被更改。
	 */
	public byte[] signature;

	private static int sequence = 0;

	
	
	/**
	 * 构造方法
	 */
	public Transaction(PublicKey from, PublicKey to, float value,ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}

	/**
	 * 计算Hash值
	 */
	private String calulateHash() {
		sequence++;
		//交易数据data
		String data = StringTool.getStringFromKey(sender) + StringTool.getStringFromKey(reciepient) + Float.toString(value)+ sequence;
		return EncryptTool.applySha256(data);
	}

	/**
	 * 生成签名方法
	 * 数字签名算法ECDSA
	 */
	public void generateSignature(PrivateKey privateKey) {
		//交易数据data
		String data = StringTool.getStringFromKey(sender) + StringTool.getStringFromKey(reciepient) + Float.toString(value);
		signature = StringTool.applyECDSASig(privateKey, data);
	}

	/**
	 * 验证签名方法
	 */
	public boolean verifySignature() {
		//交易数据data
		String data = StringTool.getStringFromKey(sender) + StringTool.getStringFromKey(reciepient)+ Float.toString(value);
		return StringTool.verifyECDSASig(sender, data, signature);
	}
	
	/**
	 * 处理交易
	 */
	public boolean processTransaction() {

		//步骤1、校验签名
	    if(verifySignature() == false) {
	       System.out.println("校验签名失败！");
	       return false;
	    }

	    //步骤2、查找检查输入
	    //收集资金发送方在这次交易之前的输入，获得可被作为输入（inputs）的未使用的交易
	    for(TransactionInput i : inputs) {
	       i.UTXO = TransactionBlockChain.UTXOs.get(i.transactionOutputId);
	    }

	    //步骤3、验证交易（transaction）是否有效
	    if(getInputsValue() < TransactionBlockChain.minimumTransaction) {
	       System.out.println("交易资金金额太小了，对其进入区块链有明显延迟 " + getInputsValue());
	       return false;
	    }

	    //步骤4、生成输出
	    //获得资金发送方，本次交易之后，剩余可以输出的金额
	    float leftOver = getInputsValue() - value; 
	    transactionId = calulateHash();
	    //资金发送方发送资金金额给资金接收方
	    outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); 
	    //资金发送方将“更改”还剩多少钱返回给自己
	    outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); 

	    //步骤5、在未使用的交易输出集合（UTXOs）标记输出（outputs）里的交易
	    for(TransactionOutput o : outputs) {
	      TransactionBlockChain.UTXOs.put(o.id , o);
	    }

	    //步骤6、从未使用的交易输出集合（UTXOs）里删除交易中使用了的输出（outputs）
	    for(TransactionInput i : inputs) {
	      if(i.UTXO == null) {
	    	  continue; 
	      }else{
	    	  TransactionBlockChain.UTXOs.remove(i.UTXO.id);
	      }
	    }
	    return true;
	}

	/**
	 * 获得本次交易前，资金发送方可以转移的资金金额
	 * 同Wallet的getBalance()方法
	 * @return
	 */
	public float getInputsValue() {
		//钱包余额
		float total = 0;   
		//循环遍历整个区块链上的未使用的交易输出集合（UTXOs）
		for (Map.Entry<String, TransactionOutput> item: TransactionBlockChain.UTXOs.entrySet()){
		    TransactionOutput UTXO = item.getValue();
		    //如果这个交易输出资金是给我的
		    if(UTXO.isMine(sender)) { 
		       //把它添加到自己的未使用的交易集合
		       UTXOs.put(UTXO.id,UTXO); 
		       //钱包余额是所有发送给我的未使用的交易输出的总和
		       total += UTXO.value ; 
		    }
		 }  
		 return total;
	}

	
}
