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
package com.foc.gui.actions;

public class CopyPasteContent {
  private Object tableDisplayObject = null;
  private String formulaExpression = null;
  
  public void dispose(){
    tableDisplayObject = null;
    formulaExpression = null;
  }
  
  public Object getTableDisplayObject() {
    return tableDisplayObject;
  }

  public void setTableDisplayObject(Object tableDisplayObject) {
    this.tableDisplayObject = tableDisplayObject;
  }

  public String getFormulaExpression() {
    return formulaExpression;
  }

  public void setFormulaExpression(String formulaExpression) {
    this.formulaExpression = formulaExpression;
  }
  
}
