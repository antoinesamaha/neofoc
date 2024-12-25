/*
 * Created on 16-Jun-2005
 */
package com.foc.util;

import com.foc.Globals;

/**
 * @author 01Barmaja
 */
public class BCifer {
  
  private int middle = 127; 
  
  public BCifer(){
    
  }

  private char revertByte(char b1){
    char b2 = b1;
    if(b1 > middle){
      b2= (char)(middle - (char)(b1 - middle));
    }else if(b1 < middle){
      b2= (char)(middle + (char)(middle - b1));
    }
    return b2;
  }
  
  public String encode(String origin){
    char b[] = new char[origin.length()];
    origin.getChars(0, origin.length(), b, 0);
    for(int i=0; i<b.length; i++){
      b[i] = revertByte(b[i]);
    }
    
    char c[] = new char[2 * b.length];
    for(int i=0; i<b.length; i++){
      Int2ByteConverter int2Byte = new Int2ByteConverter(b[i]);
      c[2*i] = int2Byte.getHighByte();
      c[2*i+1] = int2Byte.getLowByte();    
    }
    
    return new String(c);
  }
  
  public String decode(String encripted){
    char c[] = new char[encripted.length()];
    encripted.getChars(0, encripted.length(), c, 0);
    
    char b[] = new char[c.length / 2];
    for(int i=0; i<b.length; i++){
      Byte2IntConverter byte2Int = new Byte2IntConverter(c[2*i], c[2*i+1]);
      b[i] = (char)byte2Int.getIntValue();
    }
    
    for(int i=0; i<b.length; i++){
      b[i] = revertByte(b[i]);
    }
    return new String(b);
  }
}
