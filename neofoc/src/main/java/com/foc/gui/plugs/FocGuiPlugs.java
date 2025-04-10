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
package com.foc.gui.plugs;

import java.util.HashMap;

import com.foc.gui.FAbstractListPanel;

public class FocGuiPlugs {
	
	private HashMap<String, ITableAfterConstruction> tableAfterConstructionArray = null;
	
	public FocGuiPlugs(){
		tableAfterConstructionArray = new HashMap<String, ITableAfterConstruction>(); 
	}
	
	public void dispose(){
		if(tableAfterConstructionArray != null){
			tableAfterConstructionArray.clear();
			tableAfterConstructionArray = null;
		}
	}
	
	public void putTableAfterConstruction(String listPanelClassName, ITableAfterConstruction tableAfterConstruction){
		tableAfterConstructionArray.put(listPanelClassName, tableAfterConstruction);
	}
	
	public void callTableAfterConstruct(FAbstractListPanel listPanel){
		ITableAfterConstruction afterConstructionInterface = tableAfterConstructionArray.get(listPanel.getClass().getName());
		if(afterConstructionInterface != null){
			afterConstructionInterface.afterConstruction(listPanel);
		}
	}
	
	private static FocGuiPlugs focGuiPlugs = null;
	public static FocGuiPlugs getInstance(){
		if(focGuiPlugs == null){
			focGuiPlugs = new FocGuiPlugs(); 
		}
		return focGuiPlugs;
	}
}
