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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.foc.business.notifier.FocNotificationEmail;
import com.foc.business.printing.gui.PrintingAction;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.util.Utils;

public class PrnLayout extends FocObject {

	private PrnLayoutDefinition layoutDefinition = null;
	
	public PrnLayout(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public String getFileName(){
		return getPropertyString(PrnLayoutDesc.FLD_FILE_NAME);
	}
	
	public String getFileNameWithoutExtensionIfExists(){
		String fileName = getPropertyString(PrnLayoutDesc.FLD_FILE_NAME);
		if(fileName.contains(".")){
			int index = fileName.lastIndexOf(".");
			fileName = fileName.substring(0, (index-1));
		}
		return fileName;
	}

	public void setFileName(String fileName){
		setPropertyString(PrnLayoutDesc.FLD_FILE_NAME, fileName);
	}

	public PrnContext getContext(){
		return (PrnContext) getPropertyObject(PrnLayoutDesc.FLD_CONTEXT);
	}
	
	public void setContext(PrnContext context){
		setPropertyObject(PrnLayoutDesc.FLD_CONTEXT, context);
	}

	public PrnLayoutDefinition getLayoutDefinition() {
		return layoutDefinition;
	}

	public void setLayoutDefinition(PrnLayoutDefinition layoutDefinition) {
		this.layoutDefinition = layoutDefinition;
	}

	public void attachToEmail(FocNotificationEmail email, PrintingAction printingAction, boolean withLogo){
		attachToEmail(email, printingAction, withLogo, null);
	}
	
	public void attachToEmail(FocNotificationEmail email, PrintingAction printingAction, boolean withLogo, String specificName){
		if(printingAction != null && printingAction.getLauncher() != null){
			printingAction.getLauncher().setWithLogo(withLogo);
	
			byte[] bytes = printingAction.getLauncher().web_FillReport(this, getFileName());
			if(bytes != null){
				InputStream stream = new ByteArrayInputStream(bytes);
				String name = specificName;
				if(Utils.isStringEmpty(name)) {
					name = getFileNameWithoutExtensionIfExists();
				}
				email.addAttachment(name+".pdf", stream, "application/pdf");
			}
		}
			
	//		if(template.getPrintFileName() != null && !template.getPrintFileName().isEmpty()){
	//			FocList focList = PrnLayoutDefinitionDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
	//			if(focList != null){
	//				PrnLayoutDefinition prnLayoutDefinition = (PrnLayoutDefinition) focList.searchByPropertyStringValue(PrnLayoutDefinitionDesc.FLD_PRN_FILE_NAME, template.getPrintFileName());
	//				if(prnLayoutDefinition != null){
	//					InputStream jasperFileStream = prnLayoutDefinition.getJasperReport();
	//					email.addAttachment(template.getPrintFileName()+".pdf", jasperFileStream, "application/pdf");
	//				}
	//			}
	//		}

	}
}
