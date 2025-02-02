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
package com.foc.desc.dataModelTree;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.event.FValidationListener;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class DataModelEntryGuiDetailsPanel extends FPanel{

	DataModelEntry entry = null;
	
	public DataModelEntryGuiDetailsPanel(FocObject obj, int viewID){
		entry = (DataModelEntry) obj;
		if(entry != null){
			add(entry, DataModelEntryDesc.FLD_FOC_DESC , 0, 0);
			add(entry, DataModelEntryDesc.FLD_MAX_LEVEL, 0, 1);
		}
		FValidationPanel validPanel = showValidationPanel(true);
		validPanel.setValidationListener(new FValidationListener(){
			public void postCancelation(FValidationPanel panel) {
				
			}

			public void postValidation(FValidationPanel panel) {
				FocDesc rootDesc = entry.getPropertyDesc(DataModelEntryDesc.FLD_FOC_DESC);
				int     maxLevel = entry.getPropertyInteger(DataModelEntryDesc.FLD_MAX_LEVEL);
				DataModelNodeList list = new DataModelNodeList(rootDesc, maxLevel);
				DataModelNodeGuiTreePanel treePanel = new DataModelNodeGuiTreePanel(list, FocObject.DEFAULT_VIEW_ID);
				Globals.getDisplayManager().popupDialog(treePanel, "Data Dictionay Tree", true);
			}

			public boolean proceedCancelation(FValidationPanel panel) {
				return true;
			}

			public boolean proceedValidation(FValidationPanel panel) {
				return true;
			}
		});
	}
}
