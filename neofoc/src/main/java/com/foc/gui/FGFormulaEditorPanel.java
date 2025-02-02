/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.gui;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.dataModelTree.DataModelNodeGuiTreePanel;
import com.foc.desc.dataModelTree.DataModelNodeList;
import com.foc.gui.FGButton;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.property.FFormulaExpression;
import com.foc.property.FString;

@SuppressWarnings("serial")
public class FGFormulaEditorPanel extends FPanel {
  
  private FFormulaExpression formulaProperty = null;
  private FGTextField        textField       = null;
  private FocDesc            originDesc      = null;
  private FocObject          originObject    = null;
  private FGButton           equalsButton    = null;
  private FGButton           appendButton    = null;
  
  public FGFormulaEditorPanel(FocDesc originDesc, FocObject originObject, FFormulaExpression formulaProperty){
    super("Formula Expression", FPanel.FILL_HORIZONTAL);
    this.formulaProperty = formulaProperty;
    setOriginDesc  (originDesc  );
    setOriginObject(originObject);
    
    textField = new FGTextField();
    textField.setColumns(30);
    textField.setProperty(formulaProperty);

    //FField fld = formulaProperty.getFocField();
    //add(fld.getTitle(), textField, 0, 0, GridBagConstraints.HORIZONTAL);
    add(textField, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    equalsButton = new FGButton(Globals.getIcons().getEqualsIcon());
    appendButton = new FGButton(Globals.getIcons().getInsertIcon());
    add(equalsButton, 1, 0);
    add(appendButton, 2, 0);
    
    equalsButton.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				String fieldPath = popupFieldSelection();
				if(fieldPath != null) getFormulaProperty().setString(fieldPath);
				textField.requestFocusInWindow();
			}
    });

    appendButton.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				String fieldPath = popupFieldSelection();
				if(fieldPath != null) getFormulaProperty().setString(getFormulaProperty().getString()+fieldPath);
				textField.requestFocusInWindow();
			}
    });
  }
  
  public void dispose(){
    super.dispose();
    formulaProperty = null;
    textField       = null;
    originDesc      = null;
    originObject    = null;
    equalsButton    = null;
    appendButton    = null;
  }
  
  @Override
  public void setEnabled(boolean enabled){
  	super.setEnabled(enabled);
  	
  	if(textField != null){
  		textField.setEnabled(enabled);
  	}
  	if(equalsButton != null){
  		equalsButton.setEnabled(enabled);
  	}
  	if(appendButton != null){
  		appendButton.setEnabled(enabled);
  	}
  }
  
  protected FString getFormulaProperty(){
  	return formulaProperty;
  }

  public String popupFieldSelection(){
  	String str = null;
		DataModelNodeList list = new DataModelNodeList(originDesc, 5, originObject);
		DataModelNodeGuiTreePanel panel = new DataModelNodeGuiTreePanel(list, FocObject.DEFAULT_VIEW_ID);
		Globals.getDisplayManager().popupDialog(panel, "Select Field", true);
		str = panel.getSelectedFullPath();
		return str;
  }

	public FocDesc getOriginDesc() {
		return originDesc;
	}

	public void setOriginDesc(FocDesc originDesc) {
		this.originDesc = originDesc;
	}

	public FocObject getOriginObject() {
		return originObject;
	}

	public void setOriginObject(FocObject originObject) {
		this.originObject = originObject;
	}
}
