/*
 * Created on Aug 23, 2005
 */
package com.foc.desc;

import com.foc.*;

/**
 * @author 01Barmaja
 */
public class FocRef implements Cloneable, Comparable{
  long reference = 0;
  
  public FocRef(){
    setLong(0);
  }

  public FocRef(long ref){
  	setLong(ref);
  }
  
  public void dispose(){
  }
  
  public long getLong() {
    return reference;
  }

  public void setLong(long ref) {
    this.reference = ref;
  }
  
  public void copy(FocRef focRef){
  	setLong(focRef.reference);
  }
  
  public Object clone(){
    FocRef cRef = null;
    try{
      cRef = (FocRef) super.clone();
    }catch (Exception e){
      Globals.logException(e);
    }
    return cRef;
  }

  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object arg0) {
  	int comp = 0;
  	long diff = reference - ((arg0 != null) ? ((FocRef)arg0).reference : 0);
  	if(diff > 0) comp = 1;
  	else if(diff < 0) comp = -1;
    return comp;
  }
}
