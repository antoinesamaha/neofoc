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
import com.foc.desc.FocObject;

public class PropertyFormula extends FocObject {

  protected AbstractFormulaEnvironment formulaEnvironment = null;
  
  public PropertyFormula(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public void dispose(){
    super.dispose();
    formulaEnvironment = null;
  }
  
  public String getFieldName(){
    return getPropertyString(PropertyFormulaDesc.FLD_FIELD_NAME);
  }
  
  public void setFieldName( String fieldName ){
    setPropertyString(PropertyFormulaDesc.FLD_FIELD_NAME, fieldName);
  }
  
  public String getExpression(){
    return getPropertyString(PropertyFormulaDesc.FLD_EXPRESSION);
  }
  
  public void setExpression( String expression ){
    setPropertyString(PropertyFormulaDesc.FLD_EXPRESSION, expression);
  }
  
  public Formula getFormula(){
    return new Formula(getExpression());
  }
  
  public void updateFatherFormulaProperties(){
    
  }
  
  public void setFormulaEnvironment(AbstractFormulaEnvironment formulaEnvironment) {
    this.formulaEnvironment = formulaEnvironment;
  }
}
