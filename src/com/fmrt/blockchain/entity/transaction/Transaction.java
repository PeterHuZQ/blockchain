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
 * ������Ϣ
 * @author hzq
 * @date 2018/03/09
 */
public class Transaction {

	/**
	 * ���׵�hash��Ψһ�ģ�
	 */
	public String transactionId;

	/**
	 * �������루inputs��
	 * ��������Ƕ���ǰ���׵����ã���Щ����֤���ʽ��ͷ�ӵ��Ҫ���͵��ʽ�
	 */
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	
	/**
	 * ���������outputs��
	 * ��ʾ�������յ�����ص�ַ��������Щ�����Ϊ�½����е��������ã�
	 */
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	/**
	 * δʹ�õĽ���������ϣ�UTXOs��
	 */
	private HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	/**
	 * �ʽ��ͷ��Ĺ�Կ��Ǯ����ַ��
	 */
	public PublicKey sender;

	/**
	 * �ʽ���շ��Ĺ�Կ��Ǯ����ַ��
	 */
	public PublicKey reciepient;

	/**
	 * Ҫת�Ƶ��ʽ���
	 */
	public float value;

	/**
	 * ����ǩ��
	 * ֤����ַ���������Ƿ���ý��׵��ˣ���������û�б����ġ�
	 */
	public byte[] signature;

	private static int sequence = 0;

	
	
	/**
	 * ���췽��
	 */
	public Transaction(PublicKey from, PublicKey to, float value,ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}

	/**
	 * ����Hashֵ
	 */
	private String calulateHash() {
		sequence++;
		//��������data
		String data = StringTool.getStringFromKey(sender) + StringTool.getStringFromKey(reciepient) + Float.toString(value)+ sequence;
		return EncryptTool.applySha256(data);
	}

	/**
	 * ����ǩ������
	 * ����ǩ���㷨ECDSA
	 */
	public void generateSignature(PrivateKey privateKey) {
		//��������data
		String data = StringTool.getStringFromKey(sender) + StringTool.getStringFromKey(reciepient) + Float.toString(value);
		signature = StringTool.applyECDSASig(privateKey, data);
	}

	/**
	 * ��֤ǩ������
	 */
	public boolean verifySignature() {
		//��������data
		String data = StringTool.getStringFromKey(sender) + StringTool.getStringFromKey(reciepient)+ Float.toString(value);
		return StringTool.verifyECDSASig(sender, data, signature);
	}
	
	/**
	 * ������
	 */
	public boolean processTransaction() {

		//����1��У��ǩ��
	    if(verifySignature() == false) {
	       System.out.println("У��ǩ��ʧ�ܣ�");
	       return false;
	    }

	    //����2�����Ҽ������
	    //�ռ��ʽ��ͷ�����ν���֮ǰ�����룬��ÿɱ���Ϊ���루inputs����δʹ�õĽ���
	    for(TransactionInput i : inputs) {
	       i.UTXO = TransactionBlockChain.UTXOs.get(i.transactionOutputId);
	    }

	    //����3����֤���ף�transaction���Ƿ���Ч
	    if(getInputsValue() < TransactionBlockChain.minimumTransaction) {
	       System.out.println("�����ʽ���̫С�ˣ���������������������ӳ� " + getInputsValue());
	       return false;
	    }

	    //����4���������
	    //����ʽ��ͷ������ν���֮��ʣ���������Ľ��
	    float leftOver = getInputsValue() - value; 
	    transactionId = calulateHash();
	    //�ʽ��ͷ������ʽ�����ʽ���շ�
	    outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); 
	    //�ʽ��ͷ��������ġ���ʣ����Ǯ���ظ��Լ�
	    outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); 

	    //����5����δʹ�õĽ���������ϣ�UTXOs����������outputs����Ľ���
	    for(TransactionOutput o : outputs) {
	      TransactionBlockChain.UTXOs.put(o.id , o);
	    }

	    //����6����δʹ�õĽ���������ϣ�UTXOs����ɾ��������ʹ���˵������outputs��
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
	 * ��ñ��ν���ǰ���ʽ��ͷ�����ת�Ƶ��ʽ���
	 * ͬWallet��getBalance()����
	 * @return
	 */
	public float getInputsValue() {
		//Ǯ�����
		float total = 0;   
		//ѭ�����������������ϵ�δʹ�õĽ���������ϣ�UTXOs��
		for (Map.Entry<String, TransactionOutput> item: TransactionBlockChain.UTXOs.entrySet()){
		    TransactionOutput UTXO = item.getValue();
		    //��������������ʽ��Ǹ��ҵ�
		    if(UTXO.isMine(sender)) { 
		       //������ӵ��Լ���δʹ�õĽ��׼���
		       UTXOs.put(UTXO.id,UTXO); 
		       //Ǯ����������з��͸��ҵ�δʹ�õĽ���������ܺ�
		       total += UTXO.value ; 
		    }
		 }  
		 return total;
	}

	
}
