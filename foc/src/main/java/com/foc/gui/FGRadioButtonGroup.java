/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import java.awt.event.*;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.*;

import com.foc.*;
import com.foc.desc.field.FMultipleChoiceItem;
import com.foc.property.*;

/**
 * @author Standard
 */
@SuppressWarnings("serial")
public class FGRadioButtonGroup extends FPanel implements ActionListener, FPropertyListener {

  protected FProperty   property    = null;
  private   ButtonGroup buttonGroup = null;

  public static final int VERTICAL   = 0;
  public static final int HORIZONTAL = 1;
  
  private JToggleButton previouslyPressed   = null ;
  private JToggleButton invisibleButton     = null ;
  //private boolean       allowClearSelection = false;
  //private int           clearSelectionValue = 0    ;
  
  public FGRadioButtonGroup(Iterator iter, boolean checkBox, int allignement, int returnToLineIndex, boolean allowClearSelection, int clearSelectionValue){
  	setOpaque(false);
  	int x = 0;
  	int y = 0;
  	//this.allowClearSelection = allowClearSelection;
  	//this.clearSelectionValue = clearSelectionValue;
  	buttonGroup = new ButtonGroup();
  	while(iter != null && iter.hasNext()){
  		FMultipleChoiceItem choice = (FMultipleChoiceItem) iter.next();
  		JToggleButton b = null;
  		if(checkBox){
  			b = new FGCheckBox();
  		}else{
  			b = new FGRadioButton();
  		}
  		b.setText(choice.getTitle());
  		b.setOpaque(false);
  		b.addActionListener(this);
  		buttonGroup.add(b);
  		if(allowClearSelection && choice.getId() == clearSelectionValue){
  			invisibleButton = b;
  		}else{
	  		add(b, x, y);
	  		if(allignement == HORIZONTAL){
	  			x++;
	  			if(x == returnToLineIndex){
	  				x = 0;
	  				y++;
	  			}
	  		}else{
	  			y++;
	  			if(y == returnToLineIndex){
	  				y = 0;
	  				x++;
	  			}
	  		}
  		}
  	}
  }
  
  public void dispose(){
    if(property != null){
      property.removeListener(this);
      property = null;
    }
    
    if(buttonGroup != null){
    	Enumeration<AbstractButton> enumer = (Enumeration<AbstractButton>) buttonGroup.getElements();
    	while(enumer != null && enumer.hasMoreElements()){
    		JToggleButton radio = (JToggleButton) enumer.nextElement();
    		if(radio != null){
    			radio.removeActionListener(this);
    		}
    	}
    	buttonGroup = null;
    }
    
    if(previouslyPressed != null){
    	previouslyPressed = null;
    }
  }
  
  private void updatePropertyValue(){
  	JToggleButton button = getSelectedItem();
  	String str = button.getText();
    //String str = radioButton.getText();
    //String str = (String) getSelectedItem();
    if(property != null){
    	property.setString(str);
    }
    //The combo box reactions in a table are not effective without this line
    //The combobox cell itself does not have problems, it is the other columns
    //That do not refresh otherwise.
    Globals.getDisplayManager().refresh();
  }

  // --------------
  // ActionListener
  // --------------
  public void actionPerformed(ActionEvent e) {
 		updatePropertyValue();
 		if(previouslyPressed == e.getSource()){
 			//buttonGroup.clearSelection();
 			if(invisibleButton != null){
 				invisibleButton.setSelected(true);
 				updatePropertyValue();
 			}
 			previouslyPressed = null;
 		}else{
 			previouslyPressed = (JToggleButton) e.getSource();
 		}
  }
  // --------------
  
  public void setSelectedItem(){
  	String text = property.getString();
  	Enumeration<AbstractButton> enumer = buttonGroup.getElements();
  	while(enumer != null && enumer.hasMoreElements()){
  		JToggleButton radioButton = (JToggleButton) enumer.nextElement();
  		if(radioButton != null && text.equals(radioButton.getText())){ 
  			radioButton.setSelected(true);
  		}
  	}
  }

  public JToggleButton getSelectedItem(){
  	JToggleButton found = null;
  	Enumeration<AbstractButton> enumer = buttonGroup.getElements();
  	while(enumer != null && enumer.hasMoreElements() && found == null){
  		JToggleButton radioButton = (JToggleButton) enumer.nextElement();
  		if(radioButton != null && radioButton.isSelected()){
  			found = radioButton;
  		}
  	}
  	return found;
  }
  
  // PropertyListener
  // ----------------
  public void propertyModified(FProperty property) {
    if(property != null){
    	setSelectedItem();
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
    	setSelectedItem();
      if(property != null){
        property.addListener(this);
      }
    }
  }

	public Enumeration<AbstractButton> getElements() {
		return buttonGroup.getElements();
	}
}
