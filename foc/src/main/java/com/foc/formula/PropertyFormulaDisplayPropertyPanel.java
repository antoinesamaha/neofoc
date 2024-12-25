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
package com.foc.formula;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.dataModelTree.DataModelNodeGuiTreePanel;
import com.foc.desc.dataModelTree.DataModelNodeList;
import com.foc.desc.field.FField;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.FGButton;
import com.foc.gui.FGLabel;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.table.FAbstractTableModel;
import com.foc.gui.table.FTableView;
import com.foc.property.FProperty;
import com.foc.property.FString;

@SuppressWarnings("serial")
public class PropertyFormulaDisplayPropertyPanel extends FPanel {
  
  private FString            fString          = null;
  private FProperty          selectedProperty = null;
  private FAbstractListPanel selectionPanel   = null;
  private FGTextField        textField        = null;
  private FGLabel            expressionLabel  = null;
  private ICellTitleBuilder  cellTitleBuilder = null;
  private IOutputFieldFormulaGetter outputFieldFormulaGetter = null;
  private FGButton           equalsButton     = null;
  private FGButton           appendButton     = null;
  
  public PropertyFormulaDisplayPropertyPanel(FAbstractListPanel selectionPanel, final AbstractFormulaEnvironment formulaEnvironment){
  	this(selectionPanel, formulaEnvironment, null, null);
  }
  
  public PropertyFormulaDisplayPropertyPanel(FAbstractListPanel selectionPanel, final AbstractFormulaEnvironment formulaEnvironment, ICellTitleBuilder cellTitleBuilder, IOutputFieldFormulaGetter outputFieldFormulaGetter){
    super("Formula Expression", FPanel.FILL_HORIZONTAL);
    this.selectionPanel           = selectionPanel;
    this.cellTitleBuilder         = cellTitleBuilder;
    this.outputFieldFormulaGetter = outputFieldFormulaGetter;
    //setBackground(Globals.getDisplayManager().getBarmajaOrange());
    fString = new FString(null, FField.NO_FIELD_ID, "");
    textField = new FGTextField();
    textField.setName(selectionPanel.getName()+".FORMULA_EXPRESSION_FIELD");
    textField.addFocusListener(new FocusListener(){

      public void focusGained(FocusEvent e) {
      }

      public void focusLost(FocusEvent e) {
        if( selectedProperty != null ){
          
          if( !selectedProperty.isWithFormula() && !fString.getString().equals("")){
            formulaEnvironment.applyFormulaToProperty(selectedProperty, "");
          }
          
          PropertyFormulaContext propertyFormulaContext = selectedProperty.getPropertyFormulaContext();
          if (propertyFormulaContext != null) {
            PropertyFormula propertyFormula = propertyFormulaContext.getPropertyFormula();
            if (propertyFormula != null) {
              //A listener on the field expression will use the AbstractFormulaEnvironment to apply the formula
              propertyFormula.setExpression(fString.getString());
            }
          }
        }
      }
    });
    
    textField.setColumns(30);
    textField.setProperty(fString);
    
    FPanel expressionLabelPanel = new FPanel();
    expressionLabelPanel.setBackground(Globals.getDisplayManager().getBarmajaOrange());
    add(expressionLabelPanel, 0, 0, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
    expressionLabel = new FGLabel("Expression");
    expressionLabelPanel.add(expressionLabel, 0, 0);
  	addField(textField, 1, 0, GridBagConstraints.HORIZONTAL);
    //add("Expression", textField, 0, 0, GridBagConstraints.HORIZONTAL);
    
    equalsButton = new FGButton(Globals.getIcons().getEqualsIcon());
    appendButton = new FGButton(Globals.getIcons().getInsertIcon());
    add(equalsButton, 2, 0);
    add(appendButton, 3, 0);
    
    equalsButton.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				String fieldPath = popupFieldSelection();
				if(fieldPath != null) fString.setString(fieldPath);
				textField.requestFocusInWindow();
			}
    });

    appendButton.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				String fieldPath = popupFieldSelection();
				if(fieldPath != null) fString.setString(fString.getString()+fieldPath);
				textField.requestFocusInWindow();
			}
    });

    selectionPanel.getTable().getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Globals.getDisplayManager().popupMessage("Column");
        selectionChanged(e, getSelectionPanel());
      }
    });
    
    selectionPanel.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Globals.getDisplayManager().popupMessage("Row");
        selectionChanged(e, getSelectionPanel());
      }
    });
  }
  
  public void dispose(){
    super.dispose();
    if( fString != null ){
      fString.dispose();
      fString = null;
    }
    selectedProperty         = null;
    selectionPanel           = null;
    textField                = null;
    cellTitleBuilder         = null;
    outputFieldFormulaGetter = null;
    equalsButton             = null;
    appendButton             = null;
  }
  
  public FProperty getSelectedProperty(){
  	return selectedProperty;
  }

  public FAbstractListPanel getSelectionPanel(){
  	return selectionPanel;
  }

  public FocObject getSelectedFocObject(){
  	return getSelectionPanel() != null ? getSelectionPanel().getSelectedObject() : null;
  }
  
  public String popupFieldSelection(){
  	String str = null;
  	FocObject fatherObj = getSelectedFocObject();
		if(fatherObj != null){
			DataModelNodeList list = new DataModelNodeList(fatherObj.getThisFocDesc(), 5, fatherObj);
			DataModelNodeGuiTreePanel panel = new DataModelNodeGuiTreePanel(list, FocObject.DEFAULT_VIEW_ID);
			Globals.getDisplayManager().popupDialog(panel, "Select Field", true);
			str = panel.getSelectedFullPath();
		}
		return str;
  }
  
  private void selectionChanged(ListSelectionEvent e, FAbstractListPanel selectionPanel) {
    if (e != null && !e.getValueIsAdjusting() && selectionPanel != null) {
      //Globals.getDisplayManager().popupMessage("SELECTION");
      this.selectedProperty = null;
      
      int row = selectionPanel.getTable().getSelectedRow();
      int col = selectionPanel.getTable().getSelectedColumn();
      
      FAbstractTableModel tableModel = selectionPanel.getTable().getTableModel();
      FTableView tableView = tableModel.getTableView();
      col = tableView.getVisibleColumnIndex(col);
      
      boolean fStringEditable = true;
      
      FProperty selectedProperty = tableModel.getFProperty(row, col);
      if (selectedProperty != null) {
        this.selectedProperty = selectedProperty;
        
        PropertyFormulaContext propertyFormulaContext = selectedProperty.getPropertyFormulaContext();
        if(propertyFormulaContext != null){
          PropertyFormula propertyFormula = propertyFormulaContext.getPropertyFormula();
          if(propertyFormula != null){
            FProperty pfProperty = propertyFormula.getFocProperty(PropertyFormulaDesc.FLD_EXPRESSION);
            fString.setObject(pfProperty.getObject());
          }
        }else{
        	String expression = "";
        	if(selectedProperty.getFocField() != null){
        		if(selectedProperty.getFocField().getFormulaString() != null && !selectedProperty.getFocField().getFormulaString().isEmpty()){
        			expression = selectedProperty.getFocField().getFormulaString();
        			fStringEditable = false;
        		}else{
        			if(outputFieldFormulaGetter != null){
        				expression = outputFieldFormulaGetter.getOutputFieldFormulaExpression(selectionPanel, selectedProperty, row, col);
        				if(expression != null && !expression.isEmpty()){
        					fStringEditable = false;
        				}
        			}
        		}
        	}
          fString.setString(expression);
          //fString.setObject(String.valueOf(prop.getObject()));
        }
        
        textField.setEnabled(fStringEditable);
        appendButton.setEnabled(fStringEditable);
        equalsButton.setEnabled(fStringEditable);
      }
      
      if(cellTitleBuilder != null){
      	expressionLabel.setText(cellTitleBuilder.getCellTitle(selectionPanel, selectedProperty, row, col));
      }
      /*
       		String colTitle = "";
          {
    	      FTableColumn tableCol = tableView.getColumnAt(col);
    	      FocDesc      desc     = selectionPanel.getFocList().getFocDesc();
    	      if(tableCol != null && desc != null){
    	      	FFieldPath path = tableCol.getFieldPath();
    	      	if(path != null){
    	      		colTitle = path.getFieldCompleteName(desc);
    	      		expressionLabel.setText(colTitle);
    	      	}
    	      }
          }

       */
    }
  }
}
