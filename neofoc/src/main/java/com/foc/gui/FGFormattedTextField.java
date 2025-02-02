/*
 * Created on Aug 15, 2005
 */
package com.foc.gui;

import java.text.Format;

import javax.swing.*;

import com.foc.Globals;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public abstract class FGFormattedTextField extends JFormattedTextField{
	
  public abstract void dispose();
  public abstract boolean isOutput();
  
  /**
   * 
   */
  public FGFormattedTextField() {
    super();
    setFont(Globals.getDisplayManager().getDefaultFont());
    setDisabledTextColor(Globals.getDisplayManager().getDisabledTextColor());
  }   
  
  /**
   * @param format
   */
  public FGFormattedTextField(Format format) {
    super(format);
    if(Globals.getDisplayManager() != null){
	    setFont(Globals.getDisplayManager().getDefaultFont());
	    setDisabledTextColor(Globals.getDisplayManager().getDisabledTextColor());
    }
  }
  
  public void setEnabled(boolean b) {
    super.setEditable(b);
    StaticComponent.setEnabled(this, b, isOutput());
  }
  
  public void setEnabled_Super(boolean b) {
    super.setEnabled(b);
    //StaticComponent.setEnabled(this, b, isOutput());
  }
}
