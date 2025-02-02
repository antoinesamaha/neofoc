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
package com.foc.business.printing;

import java.io.InputStream;

import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class PrnLayoutDefinition extends FocObject {
	
	public PrnLayoutDefinition(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public String getContextDBName(){
		return getPropertyString(PrnLayoutDefinitionDesc.FLD_PRN_CONTEXT);
	}

	public void setContextDBName(String context){
		setPropertyString(PrnLayoutDefinitionDesc.FLD_PRN_CONTEXT, context);
	}
	
	public String getFileName(){
		return getPropertyString(PrnLayoutDefinitionDesc.FLD_PRN_FILE_NAME);
	}
	
	public void setFileName(String fileName){
		setPropertyString(PrnLayoutDefinitionDesc.FLD_PRN_FILE_NAME, fileName);
	}

  public void setJasperReport(InputStream inputStream){
  	Globals.getApp().getDataSource().focObject_addBlobFromInputStream(this, PrnLayoutDefinitionDesc.FLD_PRN_JASPER_FILE, inputStream);
  }

  public InputStream getJasperReport(){
  	return Globals.getApp().getDataSource().focObject_LoadInputStream(this, PrnLayoutDefinitionDesc.FLD_PRN_JASPER_FILE);
  }
}
