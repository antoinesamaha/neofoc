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
import com.foc.business.printing.IPrnReportCreator;
import com.foc.business.printing.PrnContext;
import com.foc.business.printing.PrnLayoutGuiBrowsePanel_Print;
import com.foc.business.printing.PrnReportLauncher;
import com.foc.business.printing.ReportFactory;
import com.foc.desc.FocObject;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.table.FGPopupMenuItem;

@SuppressWarnings("serial")
public abstract class CopyOfPrnPopupMenu extends FGPopupMenuItem implements IPrnReportCreator{

	private boolean           objectToPrint_Owner = false;
	private FocObject         objectToPrint       = null;
	private PrnReportLauncher launcher            = null;
	
	public CopyOfPrnPopupMenu(FAbstractListPanel listPanel) {
		this("Print...", listPanel);
	}
	
	public CopyOfPrnPopupMenu(String title, FAbstractListPanel listPanel) {
		super(title, Globals.getIcons().getPrintIcon(), listPanel);
    launcher = new PrnReportLauncher(this);
    initLauncher();
	}
	
	public void dispose(){
		super.dispose();
		objectToPrint = null;
		if(launcher != null){
			disposeLauncherContent();
			launcher.dispose();
			launcher = null;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		objectToPrint = getListPanel().getSelectedObject();
		
		String     prnContextName = getListPanel().getPrnContextName();
		PrnContext prnContext     = ReportFactory.getInstance().findContext(prnContextName);
		
		if(prnContext != null && objectToPrint != null){
  		PrnLayoutGuiBrowsePanel_Print browse = new PrnLayoutGuiBrowsePanel_Print(prnContext, this);
  		Globals.getDisplayManager().popupDialog(browse, "Reports", true);
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
}
