/*
 * Created on 16-Jun-2005
 */
package com.foc.util;

import java.io.*;

/**
 * @author 01Barmaja
 */
public class PCID {
  
  public PCID(){
    
  }
  
  public static String getUniqueID(){
    String retValue = "";
    try{
      char c[] = {105,112,99,111,110,102,105,103,32,47,97,108,108};
      Process process = Runtime.getRuntime().exec(new String(c));
      Thread.sleep(1000);
      InputStream stream = process.getInputStream();
      BufferedReader bufferReader = new BufferedReader(new InputStreamReader(stream));
                   
      char c2[] = {80,104,121,115,105,99,97,108,32,65,100,100,114,101,115,115};
      String searchLine = new String(c2);
      String lastLineThatMatch = null; 
      String line = null;
      do{
        line = bufferReader.readLine();
        if(line != null){
          int index = line.indexOf(searchLine);
          if(index >= 0){
            lastLineThatMatch = line;
          }
        }
      }while(line != null);
      
      if(lastLineThatMatch != null){
        int colCharIndex = lastLineThatMatch.indexOf(58);
        retValue = lastLineThatMatch.substring(colCharIndex+1, colCharIndex+1+18);
      }
    }catch(Exception e){
      e.printStackTrace();
    }
    //Globals.logString("MMMM|"+retValue+"|");
    //retValue = " 00-50-FC-87-3B-FC"; //Ghorra Cobas Integra PC
    return retValue;
  }
  
  public static String getUniqueIDWithoutLeadingSpace(){
    String macAdd = null;
    macAdd = PCID.getUniqueID();
    if( macAdd != null ){
      macAdd = macAdd.substring(1, macAdd.length());  
    }
    return macAdd;
  }
  
  public static String getDigestedUniqueID(){
    String digest = Encryptor.encrypt_MD5(getUniqueIDWithoutLeadingSpace());
    digest = digest.replaceAll("\n", "");
    return digest;
  }
}
