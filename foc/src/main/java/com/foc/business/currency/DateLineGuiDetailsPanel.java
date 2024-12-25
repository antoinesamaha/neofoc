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
package com.foc.business.currency;

import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.desc.FocObject;
import com.foc.event.FValidationListener;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class DateLineGuiDetailsPanel extends FPanel {
	
	private DateLine dateLine = null; 
	
	public DateLineGuiDetailsPanel(FocObject focObj, int viewID){
		dateLine = (DateLine) focObj; 
			
		add(focObj, DateLineDesc.FLD_DATE, 0, 0);

		FValidationPanel vPanel = showValidationPanel(true);
		vPanel.addSubject(focObj);
		vPanel.setValidationListener(new FValidationListener() {
			
			@Override
			public boolean proceedValidation(FValidationPanel panel) {
				DateLineList dateLineList = (DateLineList) getDateLine().getFatherSubject();
				boolean proceed = true;
				
				DateLine dateLine = (DateLine) dateLineList.findByDate_Exactly(getDateLine().getDate());
				if(dateLine != null){
					Globals.showNotification("Date exists", "", IFocEnvironment.TYPE_WARNING_MESSAGE);
					proceed = false;
				}
				
				return proceed;
			}
			
			@Override
			public boolean proceedCancelation(FValidationPanel panel) {
				return true;
			}
			
			@Override
			public void postValidation(FValidationPanel panel) {
				if(getDateLine() != null && getDateLine().getFatherSubject() != null){
					((FocList)(getDateLine().getFatherSubject())).sort();
				}
			}
			
			@Override
			public void postCancelation(FValidationPanel panel) {
			}
		});
	}
	
	public void dispose(){
		super.dispose();
	}

	public DateLine getDateLine() {
		return dateLine;
	}
	
	
}
