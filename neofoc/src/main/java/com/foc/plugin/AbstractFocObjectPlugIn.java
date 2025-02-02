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
package com.foc.plugin;

import com.foc.desc.FocObject;
import com.foc.desc.field.FField;

public abstract class AbstractFocObjectPlugIn implements IFocObjectPlugIn {

	private FocObject focObject = null;
	
	public void setFocObject(FocObject focObject) {
		this.focObject = focObject;
	}

	public FocObject getFocObject() {
		return focObject;
	}

	@Override
	public void dispose() {
		focObject = null;
	}
	
	@Override
	public String getTransactionCodePrefix(String areaPrefix, String transactionPrefix){
		return "";
	}
	
	@Override
	public int getTransactionCodeNumberOfDigits(){
		return -1;//<=0 if you want the standard Everpro behaviour coming from the Transaction Configuration 
	}
	
	@Override
  public int getMandatoryFieldCount(int count_FromFocObject){
		return count_FromFocObject;
	}
	
	@Override
  public FField getMandatoryFieldAt(int i, FField field_FromFocObject){
		return field_FromFocObject;
	}
}
