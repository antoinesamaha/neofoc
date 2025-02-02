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
package com.foc.link;

import java.util.HashMap;

public class FocOperationFactory{
	
	private HashMap<String, IFocOperation> operationMap = null;

	private FocOperationFactory(){
		operationMap = new HashMap<String, IFocOperation>();
	}
	
  public void dispose() {
    instance     = null;
    operationMap = null;
  }

  public void putOperation(String name, IFocOperation operation) {
  	operationMap.put(name, operation);
  }

  public IFocOperation getOperationByName(String name) {
    return operationMap.get(name);
  }

  public int getOperationCount() {
    return operationMap.size();
  }

  // ----------------------------------------------------------
  // Instance
  // ----------------------------------------------------------
  private static FocOperationFactory instance = null;

  public static FocOperationFactory getInstance() {
    if (instance == null)
      instance = new FocOperationFactory();
    return instance;
  }


}
