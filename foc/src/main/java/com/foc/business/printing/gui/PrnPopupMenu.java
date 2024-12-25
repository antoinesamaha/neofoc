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

import java.awt.event.ActionEvent;

import com.foc.Globals;
import com.foc.business.printing.PrnReportLauncher;
import com.foc.desc.FocObject;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.table.FGPopupMenuItem;

@SuppressWarnings("serial")
public class PrnPopupMenu extends FGPopupMenuItem {

	private boolean        printingAction_Owner   = false;
	private PrintingAction printingAction         = null;
	private boolean        duplicateObjectToPrint = true;

	public PrnPopupMenu(FAbstractListPanel listPanel, PrintingAction printingAction) {
		this("Print Line...", listPanel);
		this.printingAction = printingAction;
		printingAction_Owner = true;
	}
	
	public PrnPopupMenu(FAbstractListPanel listPanel) {
		this("Print Line...", listPanel);
	}
	
	public PrnPopupMenu(String title, FAbstractListPanel listPanel) {
		super(title, Globals.getIcons().getPrintIcon(), listPanel);
		if(getPrintingAction() != null) getPrintingAction().initLauncher();
	}
	
	public void dispose(){
		super.dispose();
		if(printingAction != null){
			printingAction.disposeContent();//Because this is a static object we are not the owners
			if(printingAction_Owner){
				printingAction.dispose();
			}
			printingAction = null;
		}
	}
	
	public PrintingAction getPrintingAction(){
		if(			printingAction == null 
				&& 	getListPanel() != null 
				&& 	getListPanel().getFocList() != null 
				&& 	getListPanel().getFocList().getFocDesc() != null){
			printingAction = getListPanel().getFocList().getFocDesc().newPrintingAction();
			printingAction_Owner = true;
		}
		return printingAction;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		FocObject objectToPrint = getListPanel().getSelectedObject();
		
		if(getPrintingAction() != null){
			if(duplicateObjectToPrint){
				getPrintingAction().setObjectToPrint_Owner(true);
				getPrintingAction().setObjectToPrint(objectToPrint.newObjectReloaded());
			}else{
				getPrintingAction().setObjectToPrint_Owner(false);
				getPrintingAction().setObjectToPrint(objectToPrint);
			}
			getPrintingAction().popupPrintingPanel();
		}
	}

	public PrnReportLauncher getLauncher() {
		return printingAction != null ? printingAction.getLauncher() : null;
	}
}
