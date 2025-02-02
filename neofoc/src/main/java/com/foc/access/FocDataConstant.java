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
package com.foc.access;

import com.foc.shared.dataStore.IFocData;

public class FocDataConstant implements IFocData {
  private String value = "";

  public FocDataConstant(String value){
    this.value = value;
  }
  
  @Override
  public boolean iFocData_isValid() {
    return true;
  }

  @Override
  public boolean iFocData_validate() {
    return false;
  }

  @Override
  public void iFocData_cancel() {
  }

  @Override
  public IFocData iFocData_getDataByPath(String path) {
    return null;
  }

  @Override
  public Object iFocData_getValue() {
    return value;
  }

  @Override
  public void dispose() {
  }
  
}
