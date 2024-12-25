/*
 * Created on Oct 24, 2005
 */
package com.foc.file;

import java.util.*;


/**
 * @author 01Barmaja
 */
public class StringTranslator extends FocFileReader{
  private Hashtable table = null;

  private String key = null;
  private String value = null;
  
  public StringTranslator(){
    table = new Hashtable();
  }

  public void readToken(String token, int pos){
    switch(pos){
    case 0:
      key = token;
      break;
    case 1:
      value = token;
      break;
    }
  }
  
  public void readLine(StringBuffer buff){
    scanTokens(buff);

    table.put(key, value);
  }
  
  public String get(String key){
    String value = (String) table.get(key);
    if(value == null){
      value = key;
    }
    return value; 
  }
}
