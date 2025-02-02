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
package com.foc.gridView;

import com.foc.desc.field.FField;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FColumnGroup;
import com.foc.gui.table.FColumnGroupHeaderConstructor;
import com.foc.gui.table.FTableView;

@SuppressWarnings("serial")
public class GridLineGuiPanel extends FTreeTablePanel{

	private GridDefinition definition = null;
	
	public GridLineGuiPanel(GridDefinition definition){
		super(definition.getGridLineTree());
		setMainPanelSising(FILL_BOTH);
		this.definition = definition;

		addColumns();

		construct();
		getTableView().setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
		
		FValidationPanel panel = showValidationPanel(true);
		panel.setValidationType(FValidationPanel.VALIDATION_OK);
		
		expandAll();
		
		showModificationButtons(false);
	}
	
	public void dispose(){
		super.dispose();
		definition = null;
	}
	
	public void addColumns(){
		FTableView tableView = getTableView();

		FColumnGroupHeaderConstructor columnHeader = tableView.getColumnGroupHeaderConstructor();
		
		GridLineDesc desc           = definition.getGridLineDesc();
		String       prevGroupTitle = null;
		FColumnGroup colGroup       = null; 
		
		for(int i=desc.getFirstFieldID(); i<=desc.getLastFieldID(); i++){
			FField field = desc.getFieldByID(i);
			
			String fieldName  = field.getName();
			String groupTitle = desc.extractNodeName(fieldName);
			if(colGroup == null || !groupTitle.equals(prevGroupTitle)){
				colGroup = new FColumnGroup(groupTitle);
				columnHeader.addChildGroup(colGroup);
			}

			colGroup.addFTableColumn(tableView.addColumn(desc, field.getID(), true));
			
			prevGroupTitle = groupTitle; 
		}			
	}
}
