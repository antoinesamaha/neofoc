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
package com.foc.business.printing.gui;

import java.sql.Types;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.access.FocDataMap;
import com.foc.business.printing.IPrnReportCreator;
import com.foc.business.printing.PrnContext;
import com.foc.business.printing.PrnLayoutGuiBrowsePanel_Print;
import com.foc.business.printing.PrnReportLauncher;
import com.foc.business.printing.ReportFactory;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.dataModelTree.DataModelNode;
import com.foc.desc.dataModelTree.DataModelNodeList;
import com.foc.desc.field.FField;
import com.foc.shared.dataStore.IFocData;

public abstract class PrintingAction implements IPrnReportCreator{

	private boolean           objectToPrint_Owner   = false;
	private IFocData          objectToPrint         = null;
	private String            printingContext       = null;
	private PrnReportLauncher launcher              = null;
	private boolean           launchedAutomatically = false;
	
	public PrintingAction() {
	}
	
	public void dispose() {
		disposeContent();
	}
	
	public void disposeContent() {
		if(objectToPrint != null) {
			if(objectToPrint_Owner) {
				objectToPrint.dispose();
			}
			objectToPrint       = null;
			objectToPrint_Owner = false;
		}
		
		if(launcher != null) {
			disposeLauncherContent();
			launcher.dispose();
			launcher = null;
		}
	}
	
	public void popupPrintingPanel() {
		if(objectToPrint != null) {
			String     prnContextName = printingContext;
			PrnContext prnContext     = ReportFactory.getInstance().findContext(prnContextName);
			
			if(prnContext != null){
	  		PrnLayoutGuiBrowsePanel_Print browse = new PrnLayoutGuiBrowsePanel_Print(prnContext, this);
	  		Globals.getDisplayManager().popupDialog(browse, "Reports", true);
			}
		}
	}

	public PrnReportLauncher getLauncher() {
		return launcher;
	}

	public boolean isObjectToPrint_Owner() {
		return objectToPrint_Owner;
	}

	public void setObjectToPrint_Owner(boolean objectToPrintOwner) {
		objectToPrint_Owner = objectToPrintOwner;
	}

	@Override
	public void initLauncher() {
		if(launcher == null){
			launcher = new PrnReportLauncher(this); 
		}
	}

	public FocObject getObjectToPrint() {
		FocObject focObject = null;
		if(objectToPrint != null && objectToPrint instanceof FocDataMap){
			FocDataMap dataMap = (FocDataMap) objectToPrint;
			focObject = (FocObject) dataMap.getMainFocData();
		}else{
			focObject = objectToPrint instanceof FocObject ? (FocObject) objectToPrint : null;
		}
		return focObject;
	}
	
	public IFocData getFocDataToPrint(){
		return objectToPrint;
	}

	public void setObjectToPrint(IFocData objectToPrint) {
		this.objectToPrint = objectToPrint;
	}
	
	public String getPrintingContext() {
		return printingContext;
	}

	public void setPrintingContext(String printingContext) {
		this.printingContext = printingContext;
	}

	public void outputFieldsAndParameters() {
		if(ConfigInfo.isDevMode()){
		  StringBuffer strBuffer = new StringBuffer();
		  
		  printForJasperDevelopers(strBuffer, getLauncher().getParameterDictionaryFocDesc(), false);
		  printForJasperDevelopers(strBuffer, getLauncher().getFieldDictionaryFocDesc(), false);
		  System.out.println(strBuffer.toString());
		}
	}

	private void printForJasperDevelopers(StringBuffer strBuffer, FocDesc focDesc, boolean isParameter) {
		if(focDesc != null) {
			DataModelNodeList fieldList = new DataModelNodeList(focDesc, 2);
			for(int i=0; fieldList != null && i<fieldList.size(); i++){
				DataModelNode dataNode = (DataModelNode) fieldList.getFocObject(i);
				if(true/*dataNode.isSelected()*/){
					String  name    = dataNode.getFullPath();
					String  clsname = "";
					//FocDesc focDesc = dataNode.getFather() != null ? dataNode.getFather().getFocDesc() : null;
					
					FField fld = dataNode.getFField();
					if(fld != null){
						switch(fld.getSqlType()){
						case Types.DATE:
							clsname = "java.lang.String";
							break;
						case Types.VARCHAR:
							clsname = "java.lang.String";
							break;
						case Types.DOUBLE:
							clsname = "java.lang.Number";
							break;
						case Types.INTEGER:
							clsname = "java.lang.Integer";
							break;
						}
					}
		
					if(isParameter){
						strBuffer.append("<parameter name=\""+name+"\" class=\""+clsname+"\" isForPrompting=\"false\"/>");
					}else{
						strBuffer.append("<field name=\""+name+"\" class=\""+clsname+"\"/>");
					}
					strBuffer.append("\n");
				}
				fieldList.dispose();
			}
		}
	}

	public boolean isLaunchedAutomatically() {
		return launchedAutomatically;
	}

	public void setLaunchedAutomatically(boolean launchedAutomatically) {
		this.launchedAutomatically = launchedAutomatically;
	}
}
