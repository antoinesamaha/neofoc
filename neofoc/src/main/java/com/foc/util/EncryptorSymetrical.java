/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.foc.Globals;

public class EncryptorSymetrical {
	private static Cipher cipher = null;

	public static void main(String[] args) throws Exception {
		String str = "heelo tab\u0009\u0009\u0009\u0009after tab \u000C AfterEnter \\u000D After";
		System.out.println(str);
		str = str.replace("\u0009","|");
		str = str.replace("\u000C","-");
		str = str.replace("\\u000D","-");
		System.out.println(str);		
	}
	
	public static void main2(String[] args) throws Exception {
		//sample2();
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		long time = System.currentTimeMillis();
		System.out.println(TimeZone.getDefault()+"Time:"+time+" "+sdf.format(time));
		
		TimeZone zone = TimeZone.getTimeZone("Asia/Beirut");
//		TimeZone tz = TimeZone.getTimeZone("Asia/Beirut");
//		tz.conv
		
		
		
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Beirut"));
		sdf = new SimpleDateFormat("HH:mm");
//		time = System.currentTimeMillis();
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Beirut"));
		System.out.println(TimeZone.getDefault()+"Time:"+time+" "+sdf.format(time));
		
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
		sdf = new SimpleDateFormat("HH:mm");		
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		time = System.currentTimeMillis();
		System.out.println(TimeZone.getDefault()+"Time:"+time+" "+sdf.format(time));
	}
	
	private static void sample2() throws Exception {

		// uncomment the following line to add the Provider of choice
		//Security.addProvider(new com.sun.crypto.provider.SunJCE());
		
		String keyStr = "egksi354WEsi9QWe";
		SecretKey secretKey = new SecretKeySpec(keyStr.getBytes(), "AES");
		
//		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//		// keysize must be equal to 112 or 168 for this provider
//		keyGenerator.init(128);
//		SecretKey secretKey = keyGenerator.generateKey();
		
		byte[] encoded = secretKey.getEncoded();
		System.out.println("Encoded Key: " + encoded);
		cipher = Cipher.getInstance("AES");

		String plainText = "Java Cryptography Extension";
		System.out.println("Plain Text Before Encryption: " + plainText);

		byte[] plainTextByte = plainText.getBytes("UTF8");
		byte[] encryptedBytes = encrypt(plainTextByte, secretKey);

		String encryptedText = new String(encryptedBytes, "UTF8");
		System.out.println("Encrypted Text After Encryption: " + encryptedText);

		byte[] decryptedBytes = decrypt(encryptedBytes, secretKey);
		String decryptedText = new String(decryptedBytes, "UTF8");
		System.out.println("Decrypted Text After Decryption: " + decryptedText);
	}

	
	private static void sample() throws Exception {

		// uncomment the following line to add the Provider of choice
		//Security.addProvider(new com.sun.crypto.provider.SunJCE());

		
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
		// keysize must be equal to 112 or 168 for this provider
		keyGenerator.init(168);
		SecretKey secretKey = keyGenerator.generateKey();
		
		byte[] encoded = secretKey.getEncoded();
		System.out.println("Encoded Key: " + encoded);
		cipher = Cipher.getInstance("DESede");

		String plainText = "Java Cryptography Extension";
		System.out.println("Plain Text Before Encryption: " + plainText);

		byte[] plainTextByte = plainText.getBytes("UTF8");
		byte[] encryptedBytes = encrypt(plainTextByte, secretKey);

		String encryptedText = new String(encryptedBytes, "UTF8");
		System.out.println("Encrypted Text After Encryption: " + encryptedText);

		byte[] decryptedBytes = decrypt(encryptedBytes, secretKey);
		String decryptedText = new String(decryptedBytes, "UTF8");
		System.out.println("Decrypted Text After Decryption: " + decryptedText);
	}

	private static Cipher getCipher() {
		if(cipher == null) {
			try{
				cipher = Cipher.getInstance("DESede");
			}catch (NoSuchAlgorithmException e){
				Globals.logException(e);
			}catch (NoSuchPaddingException e){
				Globals.logException(e);
			}
		}
		return cipher;
	}
	
	public static byte[] encrypt(byte[] plainTextByte, SecretKey secretKey) throws Exception {
		byte[] encryptedBytes = null;
		if(getCipher() != null) {
			getCipher().init(Cipher.ENCRYPT_MODE, secretKey);
			encryptedBytes = getCipher().doFinal(plainTextByte);
		}
		return encryptedBytes;
	}

	public static byte[] decrypt(byte[] encryptedBytes, SecretKey secretKey) throws Exception {
		byte[] decryptedBytes = null;
		if(getCipher() != null) {
			getCipher().init(Cipher.DECRYPT_MODE, secretKey);
			decryptedBytes = getCipher().doFinal(encryptedBytes);
		}
		return decryptedBytes;
	}
}
