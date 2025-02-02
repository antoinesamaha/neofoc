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
package com.foc.desc.parsers.fields;

import java.lang.reflect.Field;

import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.annotations.model.fields.FocForeignEntity;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.util.Utils;

public class FTypeObject extends FocFieldTypAbstract<FocForeignEntity> {

	@Override
	public String getTypeName() {
		return TYPE_FOREIGN_ENTITY;
	}

	@Override
	public FField newFField(Class focObjClass, Field f, FocForeignEntity a) {
  	boolean cascade          = a.cascade();
  	boolean detach           = a.detach();
  	boolean directlyEditable = !a.saveOnebyOne();
  	FObjectField focField = new FObjectField(getDBFieldName(f), getFieldTitle(f), FField.NO_FIELD_ID, null);
  	focField.setFocDescStorageName(a.table(), cascade, directlyEditable);
  	focField.setWithList(a.cachedList());
  	focField.setDBResident(a.dbResident());
  	focField.setMandatory(a.mandatory());
  	if(detach && !cascade){
  		focField.setReferenceChecker_PutToZeroWhenReferenceDeleted(true);
  	}else if(detach && cascade){
  		Globals.showNotification("Incompatible attributes", "Table: "+"???"+" Field: "+getDBFieldName(f)+" cannot have both CASCADE and DETACH", IFocEnvironment.TYPE_WARNING_MESSAGE);
  	}
  	
  	String forcedDBName = a.dbName();
  	if(!Utils.isStringEmpty(forcedDBName)) focField.setForcedDBName(forcedDBName);
  	
  	boolean nullAllowed = a.allowNull();
  	if(!nullAllowed){
  		focField.setNullValueMode(FObjectField.NULL_VALUE_NOT_ALLOWED);
  	}

  	String filterProperty = a.listFilterProperty();
  	
  	if(!Utils.isStringEmpty(filterProperty)){
  		focField.setSelectionFilter_PropertyDataPath(filterProperty);
  	}

  	String filterValue = a.listFilterValue();
  	if(!Utils.isStringEmpty(filterValue)){
  		focField.setSelectionFilter_Propertyvalue(filterValue);
  	}

  	String filterExpresion = a.listFilterExpression();
  	if(!Utils.isStringEmpty(filterExpresion)){
  		focField.setSelectionFilterExpression(filterExpresion);
  	}

		return focField;
	}

}
