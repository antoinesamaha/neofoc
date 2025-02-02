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
package com.foc.list.filter;

import com.fab.model.table.FieldDefinition;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;

public class FilterConditionFactory {
	
	public static FilterCondition newConditionForField(FField field, FFieldPath dateFieldPath, String fieldPrefix){
		FilterCondition condition = null;
		
		switch(field.getFabType()){
		case FieldDefinition.SQL_TYPE_ID_CHAR_FIELD:
		case FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE_FOC_DESC:
		case FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE_STRING_BASED:
			condition = new StringCondition(dateFieldPath, fieldPrefix); 
			break;			
		case FieldDefinition.SQL_TYPE_ID_BOOLEAN:
			condition = new BooleanCondition(dateFieldPath, fieldPrefix);
			break;
		case FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE:
			condition = new MultipleChoiceCondition(dateFieldPath, fieldPrefix);
			break;		
		case FieldDefinition.SQL_TYPE_ID_DATE:
			condition = new DateCondition(dateFieldPath, fieldPrefix);
			break;
		case FieldDefinition.SQL_TYPE_ID_TIME:
			condition = new TimeCondition(dateFieldPath, fieldPrefix);
			break;
		case FieldDefinition.SQL_TYPE_ID_DATE_TIME:
			condition = new DateTimeCondition(dateFieldPath, fieldPrefix);
			break;			
		case FieldDefinition.SQL_TYPE_ID_DOUBLE:
			condition = new NumCondition(dateFieldPath, fieldPrefix);
			break;
		case FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD:
			condition = new ObjectCondition(dateFieldPath, fieldPrefix);
			break;			
		case FieldDefinition.SQL_TYPE_ID_INT:
			condition = new IntegerCondition(dateFieldPath, fieldPrefix);
			break;
		case FieldDefinition.SQL_TYPE_ID_LONG:
			condition = new LongCondition(dateFieldPath, fieldPrefix);
			break;			
		}
		
		return condition;
	}
	
}

