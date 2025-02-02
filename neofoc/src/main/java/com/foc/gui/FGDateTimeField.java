/*
 * Created on 03 May. 2008
 */
package com.foc.gui;

import com.foc.property.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGDateTimeField extends FGDateField {
	
  public FGDateTimeField() {
    super(FDateTime.getDateTimeFormat());
    StaticComponent.setComponentToolTipText(this, "Format : dd/mm/yyyy HH:MM");
    setColumns(16);    
  }
  
}
