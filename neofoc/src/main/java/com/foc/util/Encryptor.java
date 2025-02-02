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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.foc.shared.Base64;

//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class Encryptor {
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
}
