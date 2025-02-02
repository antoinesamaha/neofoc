/*
 * Created on 16-Jun-2005
 */
package com.foc.util;

/**
 * @author 01Barmaja
 */
public class Byte2IntConverter {
  private char hb = 0;
  private char lb = 0;
  private int v = 0;
  private boolean hBits[] = null;
  private boolean lBits[] = null;    
  
  public Byte2IntConverter(char hb, char lb){
    this.hb = hb;
    this.lb = lb;
    v = 0;
    accumulateOneByte(hb, true);
    accumulateOneByte(lb, false);    
  }
  
  public int getIntValue(){
    return v;
  }
  
  private void accumulateOneByte(char b, boolean isHigh){
    switch (b){
    case '1':
      v += isHigh ? 16 : 1 ; 
      break;
    case '2':        
      v += isHigh ? 32 : 2 ;
      break;
    case '3':        
      v += isHigh ? 48 : 3 ;
      break;
    case '4':        
      v += isHigh ? 64 : 4 ; 
      break;
    case '5':        
      v += isHigh ? 80 : 5 ;
      break;
    case '6':        
      v += isHigh ? 96 : 6 ; 
      break;
    case '7':        
      v += isHigh ? 112 : 7 ;
      break;
    case '8':        
      v += isHigh ? 128 : 8 ; 
      break;
    case '9':        
      v += isHigh ? 144 : 9 ;
      break;
    case 'A':        
      v += isHigh ? 160 : 10 ; 
      break;
    case 'B':        
      v += isHigh ? 176 : 11 ;
      break;
    case 'C':        
      v += isHigh ? 192 : 12 ; 
      break;
    case 'D':        
      v += isHigh ? 208 : 13 ;
      break;
    case 'E':        
      v += isHigh ? 224 : 14 ; 
      break;
    case 'F':        
      v += isHigh ? 240 : 15 ; 
      break;
    }
  }
}
