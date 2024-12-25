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
package com.foc.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings("serial")
public class FocCryptoUtils {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";
 
	public static void encrypt(String key, File inputFile, File outputFile) throws Exception {
		doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
	}
 
  public static void decrypt(String key, File inputFile, File outputFile) throws Exception {
  	doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
  }
 
  private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws Exception {
  	try {
  		Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(cipherMode, secretKey);
             
      FileInputStream inputStream = new FileInputStream(inputFile);
      byte[] inputBytes = new byte[(int) inputFile.length()];
      inputStream.read(inputBytes);
             
      byte[] outputBytes = cipher.doFinal(inputBytes);
             
      FileOutputStream outputStream = new FileOutputStream(outputFile);
      outputStream.write(outputBytes);
             
      inputStream.close();
      outputStream.close();
             
  	} catch (Exception ex) {
  		System.out.println(ex.getMessage());
  	}
  }
    
  public static void main(String[] args) {
    String key = "Mary has one cat";
    File inputFile = new File("c:/temp/SPIN-Selling.jpg");
    File encryptedFile = new File("c:/temp/SPIN-Selling_encrypted.jpg");
    File decryptedFile = new File("c:/temp/SPIN-Selling2.jpg");
     
    try {
      FocCryptoUtils.encrypt(key, inputFile, encryptedFile);
      FocCryptoUtils.decrypt(key, encryptedFile, decryptedFile);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      ex.printStackTrace();
    }
  }
}
