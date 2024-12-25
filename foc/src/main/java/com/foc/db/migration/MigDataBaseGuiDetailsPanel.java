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
package com.foc.db.migration;

import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class MigDataBaseGuiDetailsPanel extends FPanel{

	public final static int VIEW_SELECTION = 2; 
	
	public MigDataBaseGuiDetailsPanel(FocObject obj, int viewID){
		if(viewID == VIEW_SELECTION){
			setInsets(0, 0, 0, 0);
			FGTextField comp = (FGTextField) obj.getGuiComponent(FField.FLD_NAME);
			comp.setEditable(false);
			add(comp, 0, 0);
			comp = (FGTextField) obj.getGuiComponent(MigDataBaseDesc.FLD_DESCRIPTION);
			comp.setEditable(false);
			add(comp, 1, 0);
		}else{
			add(obj, FField.FLD_NAME, 0, 0);
			add(obj, MigDataBaseDesc.FLD_DESCRIPTION, 0, 1);
			add(obj, MigDataBaseDesc.FLD_URL, 0, 2);
			add(obj, MigDataBaseDesc.FLD_JDBC_DRIVER, 0, 3);
			add(obj, MigDataBaseDesc.FLD_USER_NAME, 0, 4);			
			add(obj, MigDataBaseDesc.FLD_PASSWORD, 0, 5);
	    
			FValidationPanel vPanel = showValidationPanel(true);
			vPanel.addSubject(obj);
		}
	}
}

