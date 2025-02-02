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

import javax.swing.JLabel;

import com.foc.desc.field.FFieldPath;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.FPanel;
import com.foc.property.FProperty;

public abstract class AbstractFormulaEnvironment {
  public abstract void loadAllFormulas();
  public abstract void applyAllFormulas();
  public abstract void applyFormulaToProperty(FProperty property, String expression);
  
  //private Class propertyFormulaContextClass = null;
  
  public void dispose(){
    //propertyFormulaContextClass = null;
  }
  
  /*public PropertyFormulaContext newPropertyFormulaContext(FProperty property, PropertyFormula propertyFormula) {
    PropertyFormulaContext propertyFormulaContext = null;
    Class klass = getPropertyFormulaContextClass();
    if( klass != null ){
      try {
        Class[] argsDeclare = new Class[]{Formula.class, FFieldPath.class};
        Object[] args       = new Object[]{propertyFormula.getFormula(), FFieldPath.newFieldPath(property.getFocField().getID())};
        Constructor constr  = klass.getConstructor(argsDeclare);  
        propertyFormulaContext = (PropertyFormulaContext)constr.newInstance(args);
        propertyFormulaContext.setPropertyFormula(propertyFormula);
        propertyFormulaContext.setOriginProperty(property);
      }catch( Exception e){
        Globals.logException(e);
      }
    }
    return propertyFormulaContext;
  }
  
  public Class getPropertyFormulaContextClass(){
    return propertyFormulaContextClass != null ? propertyFormulaContextClass : PropertyFormulaContext.class;
  }

  public void setPropertyFormulaContextClass(Class propertyFormulaContextClass) {
    this.propertyFormulaContextClass = propertyFormulaContextClass;
  }*/
  
  public PropertyFormulaContext newPropertyFormulaContext(FProperty property, PropertyFormula propertyFormula) {
    PropertyFormulaContext context = new PropertyFormulaContext(propertyFormula.getFormula(), FFieldPath.newFieldPath(property.getFocField().getID()));
    context.setPropertyFormula(propertyFormula);
    context.setOriginProperty(property);
    return context;
  }
  
  private void setCopyPasteKeyStrokeActions(FAbstractListPanel selectionPanel){
    selectionPanel.setCopyKeyStrokeAction(new FormulaCopyAction(selectionPanel, this));
    selectionPanel.setPasteKeyStrokeAction(new FormulaPasteAction(selectionPanel, this));
  }
  
  public FPanel getPropertyFormulaDisplayPropertyPanel(FAbstractListPanel selectionPanel){
  	return getPropertyFormulaDisplayPropertyPanel(selectionPanel, null, null);
  }

  public FPanel getPropertyFormulaDisplayPropertyPanel(FAbstractListPanel selectionPanel, ICellTitleBuilder cellTitleBuilder, IOutputFieldFormulaGetter outputFieldFormulaGetter){
    setCopyPasteKeyStrokeActions(selectionPanel);
    return new PropertyFormulaDisplayPropertyPanel(selectionPanel, this, cellTitleBuilder, outputFieldFormulaGetter);
  }
  
  public FPanel addPropertyFormulaDisplayPropertyPanel(FAbstractListPanel selectionPanel){
  	return addPropertyFormulaDisplayPropertyPanel(selectionPanel, null, null);
  }
  
  public FPanel addPropertyFormulaDisplayPropertyPanel(FAbstractListPanel selectionPanel, ICellTitleBuilder cellTitleBuilder, IOutputFieldFormulaGetter outputFieldFormulaGetter){
  	//FPanel formulaHeaderPanel = new FPanel();
  	FPanel panel = getPropertyFormulaDisplayPropertyPanel(selectionPanel, cellTitleBuilder, outputFieldFormulaGetter);
  	//formulaHeaderPanel.add(new JLabel(""), 0, 0, 1, 1, 0.01, 0.01, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
  	//formulaHeaderPanel.add(panel, 1, 0, 1, 1, 0.99, 0.01, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
  	//panel.add(formulaHeaderPanel, 0, 0, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
    //return formulaHeaderPanel;
  	selectionPanel.add(new JLabel(""), 0, 0, 1, 1, 0.01, 0.01, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
  	selectionPanel.add(panel, 0, 0, 2, 1, 0.99, 0.01, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
  	return panel;
  }
}
