/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import java.awt.event.*;

import javax.swing.*;

import com.foc.Globals;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGCheckBox extends JCheckBox implements FPropertyListener, ItemListener{
	protected FBoolean booleanProperty = null;

	public FGCheckBox(Icon icon) {
		super(icon);
  	init();
	}

	public FGCheckBox(String text, Icon icon) {
		super(text, icon);
  	init();
	}

	public FGCheckBox(String text) {
		super(text);
  	init();
	}

  public FGCheckBox() {
  	init();
  }
  
  public void dispose(){
    removeItemListener(this);
    
    if(booleanProperty != null){
      booleanProperty.removeListener(this);
      booleanProperty = null;
    }
  }

  private void init(){
  	if(Globals.getDisplayManager() != null){
  		setFont(Globals.getDisplayManager().getDefaultFont());
  		addItemListener(this);
  		setBackground(Globals.getDisplayManager().getBackgroundColor());
  	}
  }
  
  public void setEnabled(boolean b) {
    super.setEnabled(b);
    StaticComponent.setEnabledNoBackground(this, b, booleanProperty != null ? booleanProperty.isOutput() : false);
  }
  
  public void setProperty(FProperty prop){
    if(prop != booleanProperty){
      if(booleanProperty != null){
        booleanProperty.removeListener(this);
      }
      booleanProperty = (FBoolean) prop;
      propertyModified(booleanProperty);
      if(booleanProperty != null){
        booleanProperty.addListener(this);
      }
    }
  }
    
  public void itemStateChanged(ItemEvent e) {
    if(booleanProperty != null){
      booleanProperty.setBoolean(isSelected());
    }
  }
  
  /* (non-Javadoc)
   * @see b01.foc.property.FPropertyListener#propertyModified(b01.foc.property.FProperty)
   */
  public void propertyModified(FProperty property) {
    if(booleanProperty != null){
      setSelected(booleanProperty.getBoolean());
    }
  }
}
