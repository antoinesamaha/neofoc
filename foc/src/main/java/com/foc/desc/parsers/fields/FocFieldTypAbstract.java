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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.foc.Globals;

public abstract class FocFieldTypAbstract<A extends Annotation> implements IFocFieldType<A> {

	protected String getDBFieldName(Field field){
		String strValue = null;
		try{
			strValue = (String) field.get (null);
		}catch (Exception e){
			Globals.logException(e);
		}
		return strValue;
	}
	
	protected String getFieldTitle(Field field){
		return getDBFieldName(field);
	}
	
}
