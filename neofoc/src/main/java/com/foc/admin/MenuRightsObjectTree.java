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
package com.foc.admin;

import java.util.Comparator;

import com.foc.list.FocList;
import com.foc.tree.objectTree.FObjectNode;
import com.foc.tree.objectTree.FObjectTree;

public class MenuRightsObjectTree extends FObjectTree{

  @SuppressWarnings("unchecked")
	public MenuRightsObjectTree(FocList list, int viewID){
    super();
    setFatherNodeId(MenuRightsDesc.FLD_FATHER_MENU_RIGHT);
    setDisplayFieldId(MenuRightsDesc.FLD_MENU_TITLE);
    
    growTreeFromFocList(list);
    setSortable(new Comparator<FObjectNode>(){
			public int compare(FObjectNode o1, FObjectNode o2) {
				int compare = 0;
				MenuRights r1 = (MenuRights) o1.getObject();
				MenuRights r2 = (MenuRights) o2.getObject();
				compare = r1.getTitle().compareTo(r2.getTitle());
				return compare;
			}
    });
    sort();
  }
}
