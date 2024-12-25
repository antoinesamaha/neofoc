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
package com.foc.menuStructure;

import java.util.Comparator;

import com.foc.tree.objectTree.FObjectTree;

@SuppressWarnings("serial")
public class FocMenuItemTree extends FObjectTree<FocMenuItemNode, FocMenuItem> {
	
	public static final String ROOT_TITLE = "Main Menu";
	public static FocMenuItemTree departmentTree = null;
	
	public FocMenuItemTree(FocMenuItemList focMenuItemList){
    //super(ROOT_MODE_NO_OBJECT, false);
		//super(false);
  	//list.loadIfNotLoadedFromDB();
    setDisplayFieldId(FocMenuItemDesc.FLD_TITLE);
    growTreeFromFocList(focMenuItemList);
    /*
    setSortable(new Comparator<FocMenuItemNode>() {
			@Override
			public int compare(FocMenuItemNode arg0, FocMenuItemNode arg1) {
				int compare = 1;
				FocMenuItem item1 = arg0.getObject();
				FocMenuItem item2 = arg1.getObject();
				if(item1 != null && item2 != null){
					compare = item1.getTitle().compareTo(item2.getTitle());
				}
				return compare;
			}
		});
    sort();
    */
    getRoot().setTitle(ROOT_TITLE);
  }
	
  @Override
  protected FocMenuItemNode newRootNode(){
  	return new FocMenuItemRootNode("Everpro", this);
  }
	
}
