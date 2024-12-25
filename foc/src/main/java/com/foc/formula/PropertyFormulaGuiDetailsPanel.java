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
import javax.swing.JTextField;

import com.foc.desc.FocObject;
import com.foc.event.FValidationListener;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class PropertyFormulaGuiDetailsPanel extends FPanel {

	private PropertyFormula propertyFormula = null;

  public PropertyFormulaGuiDetailsPanel(FocObject pf, int view){
  	super("Property Formula", FPanel.FILL_NONE);  	
  	this.propertyFormula = (PropertyFormula) pf;
  
    JTextField comp = (JTextField)add(propertyFormula, PropertyFormulaDesc.FLD_EXPRESSION, 0, 0);
    comp.setColumns(30);
    
    FValidationPanel savePanel = showValidationPanel(true);
    if (savePanel != null) {
      savePanel.addSubject(propertyFormula);
      savePanel.setValidationListener(new FValidationListener(){

        public void postCancelation(FValidationPanel panel) {
        }

        public void postValidation(FValidationPanel panel) {
        }

        public boolean proceedCancelation(FValidationPanel panel) {
          propertyFormula.updateFatherFormulaProperties();
          return true;
        }

        public boolean proceedValidation(FValidationPanel panel) {
          return true;
        }
        
      });
    }
    
  }
  
  public void dispose(){
  	super.dispose();
    propertyFormula = null;
  }
}
