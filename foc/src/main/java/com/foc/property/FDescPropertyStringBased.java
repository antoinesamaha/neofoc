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

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FDescFieldStringBased;

public class FDescPropertyStringBased extends FMultipleChoiceString implements IFDescProperty {

	private FocDesc lastFetchedFocDesc = null;  
	
	public FDescPropertyStringBased(FocObject focObj, int fieldID, String str) {
		super(focObj, fieldID, (String) (str == null ? "" : str));
	}

	public void dispose(){
		super.dispose();
	}

	public FocDesc getSelectedFocDesc(){
		String focDescName = getString();
		if(lastFetchedFocDesc == null || !lastFetchedFocDesc.getStorageName().equals(focDescName)){
			FDescFieldStringBased descField = (FDescFieldStringBased)getFocField();
			lastFetchedFocDesc = descField != null ? descField.getFocDesc(focDescName) : null; 
		}
		return lastFetchedFocDesc;
	}
	
	public void setString(String str){
		super.setString(str);
	}

}
