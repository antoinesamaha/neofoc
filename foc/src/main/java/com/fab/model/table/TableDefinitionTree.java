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
package com.fab.model.table;

import com.foc.list.FocList;
import com.foc.tree.objectTree.FObjectTree;

public class TableDefinitionTree extends FObjectTree {
	
	public static final String ROOT_TITLE = "Object Structure";
	public static TableDefinitionTree tree = null;
	
	public TableDefinitionTree(FocList list){
    super();
    //FocList list = DepartmentList.getInstance();
  	//list.loadIfNotLoadedFromDB();
    setDisplayFieldId(TableDefinitionDesc.FLD_NAME);
    growTreeFromFocList(list);
    
    getRoot().setTitle(ROOT_TITLE);
  }
	
	public static TableDefinitionTree getInstance(){
		if(tree == null){
			tree = new TableDefinitionTree(TableDefinitionDesc.getList(FocList.LOAD_IF_NEEDED));
		}
		return tree;
	}
	
}
