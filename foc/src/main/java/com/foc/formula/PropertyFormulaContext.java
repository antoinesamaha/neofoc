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

import java.util.HashMap;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.property.PropertyFocObjectLocator;

public class PropertyFormulaContext extends FocAbstractFormulaContext implements FPropertyListener  {
  
  private   boolean         owner 							= true;
  private   boolean         propertyLockBackUp 	= true;
  private   PropertyFormula propertyFormula 		= null;
  protected FProperty 			originProperty 			= null;
  
  public PropertyFormulaContext(Formula formula, FFieldPath fieldPath) {
    super(formula, fieldPath);
  }
 
  public void dispose(){
    if( isOwner() && getFormula() != null ){
      getFormula().dispose();
    }
    super.dispose();
    
    propertyFormula = null;
    originProperty = null;
  }

  public FocObject getOriginFocObject() {
    return originProperty.getFocObject();
  }
  
  public void plugListeners(String expression) {
    plugUnplugListeners(expression, true);
  }

  public void unplugListeners(String expression) {
    plugUnplugListeners(expression, false);
  }
  
  public void plugUnplugListeners(String expression, boolean plug){
    FProperty property = getSourceProperty(expression);
    if( property != null ){
      if(plug){
        compute();
        property.addListener(this);  
      }else{
        property.removeListener(this);
      }
    }
  }
  
  public FProperty getSourceProperty(String expression){
    FProperty property = null;
    if( expression != null){
      //property = FAttributeLocationProperty.newFieldPathReturnProperty(false, expression, getOriginFocObject().getThisFocDesc(), getOriginFocObject());
    
      PropertyFocObjectLocator propertyFocObjectLocator = new PropertyFocObjectLocator();
      propertyFocObjectLocator.parsePath(expression, getOriginFocObject().getThisFocDesc(), getOriginFocObject(), originProperty);
      property = propertyFocObjectLocator.getLocatedProperty();
    
    }
    return property;
  }
  
  public void propertyModified(FProperty property) {
    compute();
  }
  
  public boolean isOwner() {
    return owner;
  }
  
  public void setOwner(boolean owner) {
    this.owner = owner;
  }
  
  public boolean getPropertyLockBackUp() {
    return propertyLockBackUp;
  }
  
  public void setPropertyLockBackUp(boolean propertyLockBackUp) {
    this.propertyLockBackUp = propertyLockBackUp;
  }

  public void popPropertyFormulaGuiDetailPanel(){
    if( propertyFormula != null ){
      Globals.getDisplayManager().newInternalFrame(new PropertyFormulaGuiDetailsPanel(getPropertyFormula(), 0));  
    }
  }
  
  public PropertyFormula getPropertyFormula() {
    return propertyFormula;
  }

  public void setPropertyFormula(PropertyFormula propertyFormula) {
    this.propertyFormula = propertyFormula;
  }

  public void setOriginProperty(FProperty originProperty) {
    this.originProperty = originProperty;
  }

	public String getNewExpression(String oldExpression, HashMap<String, String> oldValuesNewValuedMap) {
		return oldExpression;
	}
}
