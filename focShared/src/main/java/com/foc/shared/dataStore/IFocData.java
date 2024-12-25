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
package com.foc.shared.dataStore;

public interface IFocData {
  
	public static final String DATA_PATH_SIGN = ">";
	
	public boolean   iFocData_isValid();
  public boolean   iFocData_validate();
  public void      iFocData_cancel();
  public IFocData  iFocData_getDataByPath(String path);
  public Object    iFocData_getValue();
  public void      dispose();
}
