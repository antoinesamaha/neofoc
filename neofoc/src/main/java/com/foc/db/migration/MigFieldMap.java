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
package com.foc.db.migration;

import java.util.ArrayList;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;

@SuppressWarnings("serial")
public class MigFieldMap extends FocObject {

  public MigFieldMap(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  public int getDBFldType(){
    return getPropertyInteger(MigFieldMapDesc.FLD_DB_FIELD_TYPE);
  }

  public void setDBFldType(int id){
    setPropertyInteger(MigFieldMapDesc.FLD_DB_FIELD_TYPE, id);
  }

  public int getDBFldID(){
    return getPropertyInteger(MigFieldMapDesc.FLD_DB_FIELD_ID);
  }

  public void setDBFldID(int id){
    setPropertyInteger(MigFieldMapDesc.FLD_DB_FIELD_ID, id);
  }

  public String getDBFldName(){
    return getPropertyString(MigFieldMapDesc.FLD_DB_FIELD_NAME);
  }

  public void setDBFldName(String name){
    setPropertyString(MigFieldMapDesc.FLD_DB_FIELD_NAME, name);
  }

  public String getColumnTitle(){
    return getPropertyString(MigFieldMapDesc.FLD_COLUMN_TITLE);
  }

  public String getDBFldTitle(){
    return getPropertyString(MigFieldMapDesc.FLD_DB_FIELD_TITLE);
  }

  public void setDBFldTitle(String name){
    setPropertyString(MigFieldMapDesc.FLD_DB_FIELD_TITLE, name);
  }

  public String getDBFieldExplanation(){
    return getPropertyString(MigFieldMapDesc.FLD_DB_FIELD_EXPLANATION);
  }

  public void setDBFldExplanation(String name){
    setPropertyString(MigFieldMapDesc.FLD_DB_FIELD_EXPLANATION, name);
  }
  
  public boolean isMandatory(){
  	return getPropertyBoolean(MigFieldMapDesc.FLD_MANDATORY);
  }

  public boolean isKeyField(){
  	return getPropertyBoolean(MigFieldMapDesc.FLD_KEY_FIELD);
  }
  
  public String getDBForeignFieldName(){
  	return getPropertyString(MigFieldMapDesc.FLD_DB_FOREIGN_FIELD);
  }
  
  public MigrationSource getMigrationSource(){
  	return (MigrationSource) getPropertyObject(MigFieldMapDesc.FLD_MIG_SOURCE);
  }
  
  public FField getFocField(){
		MigrationSource migSource = getMigrationSource();
		FocDesc destinationFocDesc = migSource != null ? migSource.getDestinationFocDesc() : null;
		//MIG_121
//		FField fld = destinationFocDesc != null ? destinationFocDesc.getFieldByID(getDBFldID()) : null;
		FField fld = (FField) (destinationFocDesc != null ? destinationFocDesc.iFocData_getDataByPath(getDBFldName()) : null);
		
		return fld;
  }
  
  public void adjustEditableFields(){
  	FField fld = getFocField();
  	if(fld == null){
  		//Debug only
  		fld = getFocField();
  	}
  	boolean locked = fld.getFocDesc() == null;
 		getFocProperty(MigFieldMapDesc.FLD_DB_FOREIGN_FIELD).setValueLocked(locked);
  }
  
	@Override
	public ArrayList<String> getMultipleChoiceStringBased_ArrayOfValues(int fieldID) {
		ArrayList<String> arrayList = null;
		if(fieldID == MigFieldMapDesc.FLD_DB_FOREIGN_FIELD){
			FField fld = getFocField();
			FocDesc focDesc = fld != null ? fld.getFocDesc() : null;
			if(focDesc != null){
				FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
				while(enumer != null && enumer.hasNext()){
					FField fldToAdd = enumer.nextField();
					String fieldName = fldToAdd.getName();
					if(arrayList == null){
						arrayList = new ArrayList<String>();
						arrayList.add("");
					}
					arrayList.add(fieldName);
				}
				enumer.dispose();
				enumer = null;
			}
		}else if(fieldID == MigFieldMapDesc.FLD_COLUMN_TITLE){
			arrayList = getMigrationSource() != null ? getMigrationSource().getColumnTitleArray() : null;
		}
		return arrayList;
	}
}
