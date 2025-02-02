/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import com.foc.desc.*;

/**
 * @author Standard
 */
public class FEMailProperty extends FString implements Cloneable{
  
  public FEMailProperty(FocObject focObj, int fieldID, String email) {
    super(focObj, fieldID, email);
  }

  protected Object clone() throws CloneNotSupportedException {
    FEMailProperty zClone = (FEMailProperty)super.clone();
    //zClone.setColor(getColor());
    return zClone;
  }
    
  /*
  public Object getTableDisplayObject(Format format) {
  	Color clr = getColor();
  	if(clr == null){
  		FColorField clrFld = (FColorField) getFocField();
  		clr = clrFld.getNullColorDisplay();
  	}
    return clr;
  }

  public void setTableDisplayObject(Object obj, Format format) {
    setColor((Color)obj);
  }
  */
}
