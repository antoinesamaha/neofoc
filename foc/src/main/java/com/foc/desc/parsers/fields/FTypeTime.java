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

import com.foc.annotations.model.fields.FocTime;
import com.foc.desc.field.FField;
import com.foc.desc.field.FTimeField;

public class FTypeTime extends FocFieldTypAbstract<FocTime> {

	@Override
	public String getTypeName() {
		return TYPE_TIME_FIELD;
	}

	@Override
	public FField newFField(Class focObjClass, Field f, FocTime a) {
		FField focField = null;
		focField = new FTimeField(getDBFieldName(f), getFieldTitle(f), FField.NO_FIELD_ID, false);
		focField.setMandatory(a.mandatory());
		focField.setDBResident(a.dbResident());
		focField.setLockValueAfterCreation(a.lockAfterCreation());
		return focField;
	}
}