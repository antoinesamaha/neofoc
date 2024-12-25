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

import com.foc.desc.FocConstructor;
import com.foc.property.FProperty;

public class CompositeKeyPropertyFormula extends PropertyFormula {

	private FProperty property = null;
	
  public CompositeKeyPropertyFormula(FocConstructor constr) {
    super(constr);
  }
  
  public void dispose(){
  	super.dispose();
  	property = null;
  }
  
  public void setProperty(FProperty property){
  	this.property = property;
  }

  public FProperty getProperty(){
  	return this.property;
  }

  public void setFilterCriteria(String filterCriteria){
    setPropertyString(CompositeKeyPropertyFormulaDesc.FLD_FILTER_CRITERIA, filterCriteria);
  }
  
  public void setViewName( String viewName ){
    setPropertyString(CompositeKeyPropertyFormulaDesc.FLD_VIEW_NAME, viewName);
  }
  
  public void setObjectRefs( String objectRefs ){
    setPropertyString(CompositeKeyPropertyFormulaDesc.FLD_OBJECT_REFS, objectRefs);
  }
  
  public String getObjectRefs(){
    return getPropertyString(CompositeKeyPropertyFormulaDesc.FLD_OBJECT_REFS);
  }

  public void updateFatherFormulaProperties() {
    CompositeKeyPropertyFormulaEnvironment compositeKeyPropertyFormulaEnvironment  = (CompositeKeyPropertyFormulaEnvironment)formulaEnvironment;
    if(compositeKeyPropertyFormulaEnvironment != null){
      IFormulaModel formulaModel = compositeKeyPropertyFormulaEnvironment.getFormulaModel();
      FProperty installedFormulaProperty = formulaModel.getFormulaProperty(getObjectRefs());
      
      if(installedFormulaProperty != null){
        if(installedFormulaProperty.isWithFormula()){
          String expression = getExpression(); 
          if(expression.equals("")){
            installedFormulaProperty.removeFormula();
            setDeleted(true);
          }else{
            formulaEnvironment.applyFormulaToProperty(installedFormulaProperty, expression);
          }
        }
      }
    }
  }
  
  public void updateUsingProperty(){
    CompositeKeyPropertyFormulaEnvironment compositeKeyPropertyFormulaEnvironment  = (CompositeKeyPropertyFormulaEnvironment)formulaEnvironment;
    if(compositeKeyPropertyFormulaEnvironment != null){
      IFormulaModel formulaModel = compositeKeyPropertyFormulaEnvironment.getFormulaModel();

      if(property != null){
      	setFilterCriteria(formulaModel.getFilterCriteria());
      	setObjectRefs(formulaModel.getObjectRefs(property));
      }
    }
  }
}
