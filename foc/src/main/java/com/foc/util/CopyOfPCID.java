/*
 * Created on 16-Jun-2005
 */
package com.foc.util;

import java.io.*;

import com.foc.Globals;

/**
 * @author 01Barmaja
 */
public class CopyOfPCID {
  
  public CopyOfPCID(){
    
  }
  
  public static String getUniqueID(){
    String retValue = "";
    try{
      char c[] = {105,112,99,111,110,102,105,103,32,47,97,108,108};
      Process process = Runtime.getRuntime().exec(new String(c));
      Thread.sleep(1000);
      InputStream stream = process.getInputStream();
      BufferedReader bufferReader = new BufferedReader(new InputStreamReader(stream));
                   
      char c2[] = {80,104,121,115,105,99,97,108,32,65,100,100,114,101,115,115,46,32,46,32,46,32,46,32,46,32,46,32,46,32,46,32,46,32,58};
      String searchLine = new String(c2);
      
      String line = null;
      do{
        line = bufferReader.readLine();
        if(line != null){
          int index = line.indexOf(searchLine);
          if(index >= 0){
            
            retValue = line.substring(index+searchLine.length()+1, index+searchLine.length()+1+17);
          }
        }
      }while(line != null);
    }catch(Exception e){
      e.printStackTrace();
      Globals.logString("Error number 1");
    }
    return retValue;
  }
}
