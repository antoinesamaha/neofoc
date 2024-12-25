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
package com.fab.model.table.underlyingCustomisation;

import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.menu.FAbstractMenuAction;
import com.foc.menu.FMenuItem;
import com.foc.menu.FMenuList;

public class UndCustMenu {
	@SuppressWarnings("serial")
	public static void addMenu(FMenuList list){
		list.addMenu(new FMenuItem("Underlying Custom Data", 'U', new FAbstractMenuAction(null, true){
			@Override
			public FPanel generatePanel() {
				UndCustTable                undCustTable = UndCustTable.getInstance();
				UndCustTableGuiDetailsPanel detailsPanel = new UndCustTableGuiDetailsPanel(undCustTable, FocObject.DEFAULT_VIEW_ID);
				return detailsPanel;
			}
		}));
	}
}
