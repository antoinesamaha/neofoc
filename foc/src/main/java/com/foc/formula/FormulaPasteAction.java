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

import java.awt.event.ActionEvent;

import com.foc.gui.FAbstractListPanel;
import com.foc.gui.actions.CopyPasteContent;
import com.foc.gui.actions.PasteAction;
import com.foc.gui.table.FAbstractTableModel;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FTableView;
import com.foc.property.FProperty;

@SuppressWarnings("serial")
public class FormulaPasteAction extends PasteAction {

  private AbstractFormulaEnvironment formulaEnvironment = null;
  
  public FormulaPasteAction(FAbstractListPanel abstractListPanel, AbstractFormulaEnvironment formulaEnvironment) {
    super(abstractListPanel);
    this.formulaEnvironment = formulaEnvironment;
  }
  
  public void dispose(){
    super.dispose();
    formulaEnvironment = null;
  }
  
  public void actionPerformed(ActionEvent e) {
    
    FTable table = abstractListPanel.getTable();
    
    int row = table.getSelectedRow();
    int col = table.getSelectedColumn();
    FAbstractTableModel tableModel = table.getTableModel();
    FTableView tableView = tableModel.getTableView();
    col = tableView.getVisibleColumnIndex(col);
    
    FProperty property = tableModel.getFProperty(row, col);
    if(property != null && shouldPaste(property)){
      CopyPasteContent copyPasteContent = abstractListPanel.getCopyPasteContent();
      if(copyPasteContent.getFormulaExpression() != null){
        String expression = copyPasteContent.getFormulaExpression();
        formulaEnvironment.applyFormulaToProperty(property, expression);
      }else{
        //BAntoineS - To allow the past of a value upon a with formula cell
        if(property.isWithFormula()){
          PropertyFormulaContext context = property.getPropertyFormulaContext();
          PropertyFormula propFormula = context.getPropertyFormula();
          propFormula.setExpression("");
        }
        //EAntoineS
        super.actionPerformed(e);
      }
    }
  }
  
}
