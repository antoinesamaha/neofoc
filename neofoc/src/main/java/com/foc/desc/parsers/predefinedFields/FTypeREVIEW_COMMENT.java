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
package com.foc.desc.parsers.predefinedFields;

import com.foc.annotations.model.predefinedFields.FocREVIEW_COMMENT;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;

public class FTypeREVIEW_COMMENT extends FocPredefinedFieldTypeAbstract<FocREVIEW_COMMENT> {

	@Override
	public String getTypeName() {
		return TYPE_REVIEW_COMMENT;
	}

	@Override
	public FField newFField(FocDesc focDesc, FocREVIEW_COMMENT a) {
		FField fld = focDesc.addDescriptionField();
		if(a.size() > 0){
  		fld.setSize(a.size());
  	}
		return fld;
	}

}
