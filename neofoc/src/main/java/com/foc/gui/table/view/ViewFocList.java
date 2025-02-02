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
package com.foc.gui.table.view;

import com.foc.db.SQLFilter;
import com.foc.desc.FocConstructor;
import com.foc.gui.table.FTableView;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class ViewFocList extends FocList{

	private FTableView tableView = null;

	public ViewFocList(String key) {
		this(key, true);
	}
	
	public ViewFocList(String key, boolean load) {
		super(new FocLinkSimple(ViewConfigDesc.getInstance()));
		initFilter(key);
		if(load){
			loadIfNotLoadedFromDB();
		}
	}
		
	private void initFilter(String key){
		ViewConfig config = new ViewConfig(new FocConstructor(ViewConfigDesc.getInstance(), null));
		config.setViewKey(key);
		SQLFilter filter = getFilter();
		filter.setFilterFields(SQLFilter.FILTER_ON_SELECTED);
		filter.addSelectedField(ViewConfigDesc.FLD_VIEW_KEY);
		filter.setObjectTemplate(config);
		setDirectlyEditable(false);
		setDirectImpactOnDatabase(true);
		setListOrder(new FocListOrder(ViewConfigDesc.FLD_CODE));
	}

	@Override
	public void dispose(){
		super.dispose();
		tableView = null;
	}

	public FTableView getTableView() {
		return tableView;
	}

	public void setTableView(FTableView tableView) {
		this.tableView = tableView;
	}
}
