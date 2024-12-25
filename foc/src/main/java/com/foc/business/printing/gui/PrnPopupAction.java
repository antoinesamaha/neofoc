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

import com.foc.Globals;
import com.foc.business.printing.IPrnReportCreator;
import com.foc.business.printing.PrnContext;
import com.foc.business.printing.PrnLayoutGuiBrowsePanel_Print;
import com.foc.business.printing.PrnReportLauncher;
import com.foc.business.printing.ReportFactory;

public abstract class PrnPopupAction implements IPrnReportCreator {

	private PrnReportLauncher launcher       = null;
	private String            prnContextName = null;

	public PrnPopupAction() {
    launcher = new PrnReportLauncher(this);
	}
	
	public void dispose(){
		if(launcher != null){
			disposeLauncherContent();
			launcher.dispose();
			launcher = null;
		}
	}
	
	public void actionPerformed() {
		PrnContext prnContext     = ReportFactory.getInstance().findContext(prnContextName);
		
		if(prnContext != null){
  		PrnLayoutGuiBrowsePanel_Print browse = new PrnLayoutGuiBrowsePanel_Print(prnContext, this);
  		Globals.getDisplayManager().popupDialog(browse, "Reports", true);
		}
	}

	public PrnReportLauncher getLauncher() {
		return launcher;
	}

	public String getPrnContextName() {
		return prnContextName;
	}

	public void setPrnContextName(String prnContextName) {
		this.prnContextName = prnContextName;
	}
}
