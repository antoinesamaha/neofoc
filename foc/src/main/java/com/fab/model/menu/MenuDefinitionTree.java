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
package com.fab.model.menu;

import com.foc.event.FocEvent;
import com.foc.event.FocListener;
import com.foc.list.FocList;
import com.foc.property.FObject;
import com.foc.tree.TreeScanner;
import com.foc.tree.objectTree.FObjectNode;
import com.foc.tree.objectTree.FObjectTree;

public class MenuDefinitionTree extends FObjectTree {
	
	public MenuDefinitionTree(FocList list, int viewID){
    super();
    setDisplayFieldId(MenuDefinitionDesc.FLD_NAME);
    growTreeFromFocList(list);
    FocListener listListener = new FocListener(){

			public void dispose() {
			}

			public void focActionPerformed(FocEvent evt) {
				if(evt.getID() == FocEvent.ID_ITEM_ADD || evt.getID() == FocEvent.ID_ITEM_REMOVE){
					lockPropertiesForNoneLeafNodes();
				}
			}
    };
    list.addFocListener(listListener);
    listListener.focActionPerformed(new FocEvent(list, FocEvent.composeId(FocEvent.TYPE_LIST, FocEvent.ID_ITEM_ADD), ""));
	}
	
	private void lockPropertiesForNoneLeafNodes(){
  	scan(new TreeScanner<FObjectNode>(){

			public void afterChildren(FObjectNode node) {
				if(!node.isLeaf()){
					MenuDefinition menuDefinition = (MenuDefinition)node.getObject();
					if(menuDefinition != null){
						FObject prop = (FObject)menuDefinition.getFocProperty(MenuDefinitionDesc.FLD_USER_BROWSE_VIEW_DEFINITION);
						if(prop != null){
							prop.setValueLocked(true);
							prop.setObject(null);
						}
						
						prop = (FObject)menuDefinition.getFocProperty(MenuDefinitionDesc.FLD_TABLE_DEFINITION);
						if(prop != null){
							prop.setValueLocked(true);
							prop.setObject(null);
						}
					}
				}
			}

			public boolean beforChildren(FObjectNode node) {
				return true;
			}
    	
    });
  }
}
