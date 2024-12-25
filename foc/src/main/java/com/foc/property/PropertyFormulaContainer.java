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
package com.foc.property;

import com.foc.formula.Formula;

public class PropertyFormulaContainer {

  private Formula formula = null;
  private boolean owner = true;
  private boolean propertyLockBackUp = true;
  
  public void dispose(){
    if( isOwner() && formula != null ){
      formula.dispose();
    }
    formula = null;
  }
  
  public Formula getFormula() {
    return formula;
  }
  
  public void setFormula(Formula formula) {
    this.formula = formula;
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
  
}
