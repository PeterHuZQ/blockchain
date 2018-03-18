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
 * Ǯ��
 * @author hzq
 * @date 2018/03/10
 */
public class Wallet {

	/**
	 * ˽Կ
	 */
	public PrivateKey privateKey;

	/**
	 * ��Կ��Ǯ����ַ��
	 */
	public PublicKey publicKey;

	/**
	 * �Լ�δʹ�õĽ���������ϣ�UTXOs��
	 */
	private HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	/**
	 * ���췽��
	 */
	public Wallet() {
		generateKeyPair();
	}

	/**
	 * ����˽Կ����Կ
	 */
	private void generateKeyPair() {
		try {
			//����ǩ���㷨ECDSA
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	        //�ַ��� "prime192v1" ������������
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
	        //��ʼ����Կ
			keyGen.initialize(ecSpec, random); 
	        //�����Բ������Կ��
			KeyPair keyPair = keyGen.generateKeyPair();
			//����˽Կ����Կ
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * �����ҵ�Ǯ�����
	 * ���͸��ҵ�δʹ�õĽ���������ܺ�
	 */
	public float getBalance() {
        //Ǯ�����
		float total = 0;   
      	//ѭ�����������������ϵ�δʹ�õĽ���������ϣ�UTXOs��
        for (Map.Entry<String, TransactionOutput> item: TransactionBlockChain.UTXOs.entrySet()){
           TransactionOutput UTXO = item.getValue();
            //��������������ʽ��Ǹ��ҵ�
            if(UTXO.isMine(publicKey)) { 
               //������ӵ��Լ���δʹ�õĽ��׼���
               UTXOs.put(UTXO.id,UTXO); 
               //Ǯ����������з��͸��ҵ�δʹ�õĽ���������ܺ�
               total += UTXO.value ; 
            }
        }  
        return total;
	}
	
	/**
	 * Ѱ���ܹ����ѵĽ��ף�
	 * Ǯ�����Լ�δʹ�õĽ���������ϣ�UTXOs����
     * ѡȡһ���������õĸ���ƴ�ճ�һ�����ڻ����һ�ʽ���������ʽ��� 
     * @param value ת�˽��
	 */
	public ArrayList<TransactionInput> getEnoughUXTOs(float value){
        //Ϊÿһ���ҵ���δʹ�õĽ������UXTO����������������
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
        
        float total = 0;
        //ѭ�������Լ���δʹ�õĽ���������ϣ�UTXOs��
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
           TransactionOutput UTXO = item.getValue();
           total += UTXO.value;
           //��ӵ���������inputs�У���Ϊת���½����е�����inputs��
           inputs.add(new TransactionInput(UTXO.id));
           //��������ܶ�ǡ�ô��ڻ��ߵ�����Ҫת�˵Ľ��ʱ��������ֹͣ����
           if(total >= value) {
        	   break;
           }   
        }
        return inputs;
	}
	
	/**
	 * ת��
	 * @param _recipient �ʽ���շ��Ĺ�Կ����ַ��
	 * @param value ת�˽��
	 */
	public Transaction sendFunds(PublicKey _recipient,float value) {
        //�����Ƿ����㹻�Ľ���ת��
		if(getBalance() < value) {
           System.out.println("#�ʽ��ͷ�û���㹻���ʽ������ͽ��ף�������ֹ...");
           return null;
        }
       
        //����ת�˽�Ѱ���ܹ����ѵĽ��� 
        ArrayList<TransactionInput> inputs = getEnoughUXTOs(value);
      
        //���ɽ�������
        Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
        
        //�÷��ͷ���˽Կ�Խ������ݽ���ǩ��
        newTransaction.generateSignature(privateKey);
        
        //���Լ�δʹ�õĽ���������ϣ�UTXOs����ɾ��������ʹ���˵������outputs��
        for(TransactionInput input: inputs){
          UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
	}
}
