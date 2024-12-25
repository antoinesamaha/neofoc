/*
 * Created on 16-Jun-2005
 */
package com.foc.util;

/**
 * @author 01Barmaja
 */
public class Int2ByteConverter {
  private char hb = 0;
  private char lb = 0;
  private int v = 0;
  private boolean hBits[] = null;
  private boolean lBits[] = null;    
  
  public Int2ByteConverter(int v){
    this.v = v;
    hb = 0;
    lb = 0;
    hBits = new boolean[4];
    lBits = new boolean[4];
    convert();
  }
  
  public char getHighByte(){
    return hb;
  }

  public char getLowByte(){
    return lb;
  }
  
  private void computeBit(boolean bits[], int bitIndex, int bitValue){
    bits[bitIndex] = v >= bitValue;
    if(bits[bitIndex]) v -= bitValue;
  }
  
  private char computeByteFromBits(boolean bits[]){
    char b = 0;
    int power = 1;
    int byteValue = 0;
    for(int i=0; i<4; i++){
      if(bits[i]){
        byteValue = byteValue + power;
      }
      power = power * 2;
    }
    
    switch (byteValue){
    case 0:
      b = '0'; 
      break;
    case 1:        
      b = '1'; 
      break;
    case 2:        
      b = '2'; 
      break;
    case 3:        
      b = '3'; 
      break;
    case 4:        
      b = '4'; 
      break;
    case 5:        
      b = '5'; 
      break;
    case 6:        
      b = '6'; 
      break;
    case 7:        
      b = '7'; 
      break;
    case 8:        
      b = '8'; 
      break;
    case 9:        
      b = '9'; 
      break;
    case 10:        
      b = 'A'; 
      break;
    case 11:        
      b = 'B'; 
      break;
    case 12:        
      b = 'C'; 
      break;
    case 13:        
      b = 'D'; 
      break;
    case 14:        
      b = 'E'; 
      break;
    case 15:        
      b = 'F'; 
      break;
    }
    
    return b;
  }
  
  private void convert(){
    computeBit(hBits, 3, 128);
    computeBit(hBits, 2, 64);
    computeBit(hBits, 1, 32);
    computeBit(hBits, 0, 16); 
    
    computeBit(lBits, 3, 8);
    computeBit(lBits, 2, 4);
    computeBit(lBits, 1, 2);
    computeBit(lBits, 0, 1);
    
    hb = computeByteFromBits(hBits);
    lb = computeByteFromBits(lBits);      
  }
}
