/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import java.awt.Color;
import java.util.*;

import com.foc.Globals;
import com.foc.desc.field.*;
import com.foc.property.FProperty;
import com.foc.property.IFMultipleChoiceProperty;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGComboBox extends FGAbstractComboBox {
	private boolean sort = false;

  private void init(Iterator choices, boolean sort) {
  	this.sort = sort;
    fillChoices(choices, sort);
    addFocusListener(this);
    addActionListener(this);
    if(Globals.getDisplayManager() != null){
    	setFont(Globals.getDisplayManager().getDefaultFont());
    }
    setForeground(Color.BLACK);
  }
  
  /**
   * @param choices
   *        Choices iterator
   * @param sort
   *        sort choices in drop down
   */
  public FGComboBox(Iterator choices, boolean sort) {
    init(choices, sort);
  }
  
  public FGComboBox() {
  }

  public void dispose(){
    super.dispose();
    removeFocusListener(this);
    removeActionListener(this);
  }
  
  public void setProperty(FProperty prop) {
    refillChoices((IFMultipleChoiceProperty)prop);
    super.setProperty(prop);
    if(prop != null){
      setEnabled(!prop.isValueLocked());
    }
  }
  
  private boolean isSort(){
  	return this.sort;
  }

  protected void fillChoices(Iterator choices, boolean sort) {
    ArrayList intermediate = new ArrayList();
    
    while (choices != null && choices.hasNext()) {
      FMultipleChoiceItemInterface item = (FMultipleChoiceItemInterface) choices.next();
      if (item != null) {
        //addItem(item.getTitle());
        intermediate.add(item);
      }
    }
    
    Comparator comparator = null;
        
    if(sort){
      comparator = new Comparator(){
        public int compare(Object arg0, Object arg1) {
          FMultipleChoiceItemInterface item0 = (FMultipleChoiceItemInterface) arg0;    
          FMultipleChoiceItemInterface item1 = (FMultipleChoiceItemInterface) arg1;
          return item0.getTitle().compareTo(item1.getTitle()); 
        }
      };
    }else{
      comparator = new Comparator(){
        public int compare(Object arg0, Object arg1) {
          FMultipleChoiceItemInterface item0 = (FMultipleChoiceItemInterface) arg0;    
          FMultipleChoiceItemInterface item1 = (FMultipleChoiceItemInterface) arg1;
          return item0.getId() - item1.getId(); 
        }
      };
    }
    Collections.sort(intermediate, comparator);
    for(int i=0; i<intermediate.size(); i++){
      FMultipleChoiceItemInterface item = (FMultipleChoiceItemInterface) intermediate.get(i);
      addItem(item.getTitle());
    }
  }
  
  public void refillChoices(IFMultipleChoiceProperty fMultipleChoice){
  	if(fMultipleChoice != null){
    	removeAllItems();
  		fillChoices(fMultipleChoice.getChoiceIterator(), isSort());
  	}
  	//String selectedItem = fMultipleChoice.getString();
  	//setSelectedItem(selectedItem);
  }
  
  public void refillChoices(){
  	IFMultipleChoiceProperty fMultipleChoice = (IFMultipleChoiceProperty)getProperty();
  	if(fMultipleChoice != null){
  		refillChoices(fMultipleChoice);
  	}
  }

  protected void setPropertyStringValue(String strValue) {
    if (property != null) {
      property.setString(strValue);
    }
  }

  protected String getPropertyStringValue() {
  	FProperty prop = property != null ? property.getInhenritanceSourceProperty() : null;
    return prop != null ? prop.getString() : null;
  }
}
