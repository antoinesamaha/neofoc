/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;

import com.foc.*;
import com.foc.gui.table.cellControler.editor.FDefaultComboBoxCellEditor;
import com.foc.property.*;

/**
 * @author Standard
 */
@SuppressWarnings("serial")
public abstract class FGAbstractComboBox extends JComboBox implements FocusListener, ActionListener, FPropertyListener {

  protected abstract String getPropertyStringValue();
  protected abstract void   setPropertyStringValue(String strValue);

  protected FProperty             property                = null;
  private   FGComboAutoCompletion autoCompletion          = null;
  public FGComboAutoCompletion getAutoCompletion() {
		return autoCompletion;
	}

	private   boolean               withAutoCompletion      = false;
  private   boolean               allowOutOfListSelection = false;
  private   FDefaultComboBoxCellEditor tableCellEditor = null;
  
	public FGAbstractComboBox(){
  	//setModel(new FGComboBoxModel());
  	makeWithAutoCompletion();
  	//ListCellRenderer renderer = getRenderer();
  }
  
  public void dispose(){
    if(property != null){
      property.removeListener(this);
      property = null;
    }
    if(autoCompletion != null){
    	autoCompletion.dispose();
    	autoCompletion = null;
    }
    tableCellEditor = null;
  }

  public FDefaultComboBoxCellEditor getTableCellEditor() {
		return tableCellEditor;
	}
  
	public void setTableCellEditor(FDefaultComboBoxCellEditor tableCellEditor) {
		this.tableCellEditor = tableCellEditor;
	}

	private void updatePropertyValue() {
		if(allowOutOfListSelection){
			String str = (String) getEditor().getItem();
			setPropertyStringValue(str);
		}else{
	    String str = (String) getItemAt(getSelectedIndex());
	    //String str = (String) getSelectedItem();
    	setPropertyStringValue(str);
		}
    //The combo box reactions in a table are not effective without this line
    //The combobox cell itself does not have problems, it is the other columns
    //That do not refresh otherwise.
    Globals.getDisplayManager().refresh();
  }
  
  public void makeWithAutoCompletion(){
  	if(!withAutoCompletion && Globals.getDisplayManager() != null && Globals.getDisplayManager().isComboBoxesWithAutoCompletion()){
	  	this.autoCompletion = FGComboAutoCompletion.makeWithAutoCompletion(this);
	  	this.withAutoCompletion = true;
  	}
  }

  public void setEnabled(boolean b) {
  	//super.setEnabled(b);
    super.setEditable(b);
    StaticComponent.setEnabled(this, b, getProperty() != null ? getProperty().isOutput() : false);
  }
  
  public void setEnabled_Super(boolean b) {  
    super.setEnabled(b);
    StaticComponent.setEnabled(this, b, getProperty() != null ? getProperty().isOutput() : false);
  }
  
  // FocusListener
  // -------------
  public void focusGained(FocusEvent e) {
  }

  public void focusLost(FocusEvent e) {
    updatePropertyValue();
  }

  // -------------

  // ActionListener
  // --------------
  public void actionPerformed(ActionEvent e) {
    //Globals.logString("event id ="+e.getID()+"command " + e.getActionCommand());
  	if(isEditable){
  		updatePropertyValue();
  	}else{
  		setSelectedItemWithColor();
  	}
  }

  // --------------

  /*
  public void setSelectedItem(Object obj){
    boolean backupEditable = isEditable();
    setEditable(true);
    super.setSelectedItem(getPropertyStringValue());
    setEditable(backupEditable);
  }
  */
  
  public void setSelectedItemWithColor(){
    setSelectedItem(getPropertyStringValue());
    if(getProperty() != null && getProperty().getBackground() != null){
    	setBackground(getProperty().getBackground());
    }else{
    	Color c = (Color)UIManager.get("ComboBox.background");
    	setBackground(c);      	
    }
  }
  
  @Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
	}
	// PropertyListener
  // ----------------
  public void propertyModified(FProperty property) {
    if (property != null) {
    	setSelectedItemWithColor();
    }
  }

  // ----------------

  /**
   * @return Returns the property.
   */
  public FProperty getProperty() {
    return property;
  }

  public void setProperty(FProperty prop) {
    if(property != prop){
      if(property != null){
        property.removeListener(this);
      }
      property = prop;
      //refreshList();
      if(!isAllowOutOfListSelection()){
      	setSelectedItemWithColor();
      }else{
      	setSelectedItemWithColor();
      }
      if(property != null){
        property.addListener(this);
      }
    }
  }
  
  public boolean isValueInItemList(Object value){
  	boolean found = false;
  	for(int i = 0; i<getItemCount() && !found; i++){
  		Object val = getItemAt(i);
  		found = val.toString().equals(value.toString());
  	}
  	return found;
  }
  

	public boolean isAllowOutOfListSelection() {
		return allowOutOfListSelection;
	}

	public void setAllowOutOfListSelection(boolean allowOutOfListSelection) {
		this.allowOutOfListSelection = allowOutOfListSelection;
		setEditable(allowOutOfListSelection);
	}
	
	/*
	public void filterChoicesThatContain(String partOfString){
		for(int i=getItemCount()-1; i>=0; i--){
			if(i<5){
				
			}else{
				removeItemAt(i);
			}
		}
	}
	*/
}
