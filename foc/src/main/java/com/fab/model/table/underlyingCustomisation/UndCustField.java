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
package com.fab.model.table.underlyingCustomisation;

import com.fab.model.table.FieldDefinition;
import com.foc.desc.FocConstructor;

@SuppressWarnings("serial")
public class UndCustField extends FieldDefinition {
	
	public UndCustField(FocConstructor constr){
		super(constr);
	}

	@Override 
	public int getID(){
		int id = super.getID();
		id += 1000;
		return id;
	}

	public boolean isNotPhysicalDifference(){
		return getPropertyBoolean(UndCustFieldDesc.FLD_NOT_PHYSICAL_DIFFERENCE);
	}

	public void setNotPhysicalDifference(boolean b){
		setPropertyBoolean(UndCustFieldDesc.FLD_NOT_PHYSICAL_DIFFERENCE, b);
	}
	
	public String getIdentificationPrefix(){
		return getPropertyString(UndCustFieldDesc.FLD_IDENTIFICATION_PREFIX);
	}
	
	public String getIdentificationSuffix(){
		return getPropertyString(UndCustFieldDesc.FLD_IDENTIFICATION_SUFFIX);
	}
}
