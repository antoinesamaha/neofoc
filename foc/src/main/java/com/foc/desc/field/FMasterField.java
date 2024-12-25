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
package com.foc.desc.field;

import java.awt.Component;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.list.filter.FilterCondition;
import com.foc.property.FMaster;
import com.foc.property.FProperty;

public class FMasterField extends FField{
	FocDesc masterFocDesc = null;
	
	public FMasterField(FocDesc masterFocDesc){
		super("MASTER_MIRROR", "Master Mirror", MASTER_MIRROR_ID, false, 0, 0);
		setDBResident(false);
		this.masterFocDesc = masterFocDesc;
	}

	@Override
	public void addReferenceLocations(FocDesc pointerDesc) {
	}

	@Override
	public String getCreationString(String name) {
		return "";
	}

	@Override
	public FocDesc getFocDesc() {
		return masterFocDesc;
	}

	@Override
	public Component getGuiComponent(FProperty prop) {		
		return null;
	}

	@Override
	public int getSqlType() {
		return 0;
	}

	@Override
	public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop) {
		return null;
	}

	@Override
	public boolean isObjectContainer() {
		return true;
	}

	@Override
	public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue) {
		return new FMaster(masterObj);
	}

	@Override
	public FProperty newProperty_ToImplement(FocObject masterObj) {
		return new FMaster(masterObj);
	}
	
	protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		return null;
	}
}
