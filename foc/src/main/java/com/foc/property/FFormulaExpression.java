/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import com.foc.desc.*;

/**
 * @author Standard
 */
public class FFormulaExpression extends FString implements Cloneable{
  
  public FFormulaExpression(FocObject focObj, int fieldID, String str) {
    super(focObj, fieldID, str);
  }

  protected Object clone() throws CloneNotSupportedException {
    FFormulaExpression zClone = (FFormulaExpression)super.clone();
    zClone.setString(getString());
    return zClone;
  }
}
