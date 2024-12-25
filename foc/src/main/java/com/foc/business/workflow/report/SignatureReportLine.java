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
package com.foc.business.workflow.report;

import java.awt.image.BufferedImage;
import java.sql.Date;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.property.FImageProperty;

public class SignatureReportLine extends FocObject {
	
	public SignatureReportLine(FocConstructor constr){
		super(constr);
		newFocProperties();
	}

	public void setName(int index, String name){
		setPropertyString(SignatureReportLineDesc.FLD_NAME_FIRST + index, name);
	}

	public void setDate(int index, Date date){
		setPropertyDate(SignatureReportLineDesc.FLD_DATE_FIRST + index, date);
	}

	public void setTitle(int index, String title){
		setPropertyString(SignatureReportLineDesc.FLD_TITLE_FIRST + index, title);
	}
	
	public void setSignature(int index, BufferedImage imageBuffer){
		FImageProperty iProp = (FImageProperty) getFocProperty(SignatureReportLineDesc.FLD_SIGNATURE_FIRST + index);
		iProp.setImageValue(imageBuffer);
	}
}
