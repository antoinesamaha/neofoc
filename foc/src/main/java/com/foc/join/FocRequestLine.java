/*
 * Created on Jan 9, 2006
 */
package com.foc.join;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.gui.FPanel;

/**
 * @author 01Barmaja
 */
public abstract class FocRequestLine extends FocObject{
  public abstract FPanel newDetailsPanel(int viewID);

  public FocRequestLine(FocConstructor constr, FocRequestDesc requestDesc){
    super(constr);
    
    FocDesc focDesc = requestDesc.getFocDesc();
    
    FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(enumer != null && enumer.hasNext()){
      FField field = (FField) enumer.next();
      field.newProperty(this, null);
    }
  }
}
