/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import com.foc.desc.*;
import com.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class FDescProperty extends FMultipleChoice implements IFDescProperty {

  public FDescProperty(FocObject focObj, int fieldID, int iVal) {
    super(focObj, fieldID, iVal);
  }

  public FocDesc getSelectedFocDesc(){
    FDescField descField = (FDescField) getFocField();
    return descField.getChoiceFocDesc(getInteger());
  }
}
