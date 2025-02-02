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
package com.foc.desc.parsers.join;

import org.xml.sax.Attributes;

import com.foc.annotations.model.FocJoinField;
import com.foc.desc.parsers.xml.FXMLDesc;
import com.foc.desc.parsers.xml.XMLFocDescParser;

public class ParsedRequestField implements FXMLDesc {
	
	private String sourceFieldName = null;
	private String targetFieldName = null;
	private String groupByFormula  = null;
	private String groupByFormula_AdditionalFields  = null;
	
	public ParsedRequestField(FocJoinField joinFieldAnnotation){
  	sourceFieldName = joinFieldAnnotation.sourceFieldName();
  	targetFieldName = joinFieldAnnotation.targetFieldName();
  	groupByFormula  = joinFieldAnnotation.groupByFormula();
	}

	public ParsedRequestField(Attributes att){
  	sourceFieldName = XMLFocDescParser.getString(att, ATT_JOIN_FLD_SRC);
  	targetFieldName = XMLFocDescParser.getString(att, ATT_JOIN_FLD_TAR);
  	groupByFormula  = XMLFocDescParser.getString(att, ATT_GROUP_BY_FORMULA);
  	groupByFormula_AdditionalFields = XMLFocDescParser.getString(att, ATT_GROUP_BY_FORMULA_ADDITIONAL_FIELDS);
	}
	
	public String getSourceFieldName() {
		return sourceFieldName;
	}

	public String getTargetFieldName() {
		return targetFieldName;
	}

	public String getGroupByFormula() {
		return groupByFormula;
	}

	public String getGroupByFormula_AdditionalFields() {
		return groupByFormula_AdditionalFields;
	}
}
