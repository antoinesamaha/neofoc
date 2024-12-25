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
package com.fab.parameterSheet;

import java.util.ArrayList;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDescFieldStringBased;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class ParameterSheetSelectorDesc extends FocDesc {
	//public static final int FLD_PARAM_SET_ID = 1;
	public static final int FLD_PARAM_SET_NAME = 2;
	public static final int FLD_TABLE_NAME = 3;

	public static final String DB_TABLE_NAME = "PARAMETER_SET_SLECTOR";
	
	public ParameterSheetSelectorDesc(){
		super(ParameterSheetSelector.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		
		setGuiBrowsePanelClass(ParameterSheetSelectorGuiBrowsePanel.class);
		
		FField focFld = addReferenceField();
		
		//focFld = new FIntField("PARAM_SET_ID", "Param set ID", FLD_PARAM_SET_ID, false, 2);
		//addField(focFld);
		
		focFld = new FStringField("PARAM_SET_NAME", "Param set name", FLD_PARAM_SET_NAME, true, 50);
		addField(focFld);
		
		focFld = new FDescFieldStringBased("TABLE_NAME", "Table name", FLD_TABLE_NAME, false);
		addField(focFld);
	}
	
	protected void afterConstruction(){
		FDescFieldStringBased descFld = (FDescFieldStringBased)getFieldByID(ParameterSheetSelectorDesc.FLD_TABLE_NAME);
		if(descFld != null){
			descFld.fillWithAllDeclaredFocDesc();
		}
		//FilterDefinition.fillFDescFieldChoices(descFld);
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	public static FocList getList(int mode){
		return getInstance().getFocList(mode);
	}
	
	public FocList newFocList(){
		FocList list = super.newFocList();
		list.setDirectlyEditable(true);
		list.setDirectImpactOnDatabase(false);
		if(list.getListOrder() == null){
			FocListOrder order = new FocListOrder(FLD_PARAM_SET_NAME);
			list.setListOrder(order);
		}
		return list;		
	}
	
	public static boolean hasParamSheets(){
		FocList list = getList(FocList.LOAD_IF_NEEDED);
		return list != null && list.size() > 0; 
	}

	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // All DESC Fields for Param Set
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	private static ArrayList<FDescFieldStringBased> fieldArray = null;
	public static void addStringBasedField(FDescFieldStringBased fieldToAdd){
		if(fieldArray == null){
			fieldArray = new ArrayList<FDescFieldStringBased>();
		}
		fieldArray.add(fieldToAdd);
	}
	
	public static void refreshAllParamSetFieldChoices(){
		if(fieldArray != null){
			for(int i=0; i<fieldArray.size(); i++){
				FDescFieldStringBased field = fieldArray.get(i);
				if(field != null){
					field.re_fillWithParamSetFocDesc();
				}
			}
		}
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
  	return getInstance(DB_TABLE_NAME, ParameterSheetSelectorDesc.class);
  } 
}
