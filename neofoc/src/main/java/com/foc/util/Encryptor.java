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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.foc.shared.Base64;

//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class Encryptor {
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  public static String encrypt_MD5(String str){
    String encrypted = null;
    byte [] data = str.getBytes();
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(data);
      byte[] digest = md.digest();
      
      encrypted =  Base64.encode(digest).toString();
      encrypted = encrypted.substring(0, encrypted.indexOf("=="));
    }catch (NoSuchAlgorithmException e){ 
      e.printStackTrace();
    }
    return encrypted;
  }

  public static String encrypt_PBKDF2(String password, String salt, int numberOfIterations, int keyLength) {
    String hashed = null;
    try {
      SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), numberOfIterations, keyLength*4);
      SecretKey key = skf.generateSecret(spec);
      byte[] res = key.getEncoded();
      hashed = BytesToHex(res);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
    }
    return hashed;
  }

  public static String createSecureRandomString() {
    SecureRandom random = new SecureRandom();
    return new BigInteger(130, random).toString(32);
  }

  private static String BytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j=0; j<bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j*2] = HEX_ARRAY[v>>>4];
      hexChars[j*2+1] = HEX_ARRAY[v&0x0F];
    }
    return new String(hexChars);
  }
}
