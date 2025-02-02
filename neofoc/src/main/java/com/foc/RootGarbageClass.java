/*
 * Created on Jan 23, 2006
 */
package com.foc;

/**
 * @author 01Barmaja
 */
public class RootGarbageClass {

  GarbageClass [] array = null;
  
  public RootGarbageClass(){
    
    array = new GarbageClass[10000];
    for(int i=0 ;i<10000; i++){
      array[i] = new GarbageClass(12);
      array[i].setToto(13);
    }
    
  }
  
  public void dispose(){
    if(array != null){
      for(int i=0 ;i<array.length; i++){        
        array[i].dispose();
      }
    }
  }
  
}
