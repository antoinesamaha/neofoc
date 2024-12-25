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
package com.foc.business.division;

import com.foc.list.FocList;
import com.foc.property.FProperty;
import com.foc.tree.TreeScanner;
import com.foc.tree.objectTree.FObjectNode;
import com.foc.tree.objectTree.FObjectTree;

@SuppressWarnings("serial")
public class DivisionTree extends FObjectTree {

	public static final String ROOT_TITLE = "Departments";
	public static DivisionTree departmentTree = null;
	
	public DivisionTree(){
    super();
  	FocList list = DivisionDesc.getInstance().getFocList();
  	list.loadIfNotLoadedFromDB();
    setDisplayFieldId(DivisionDesc.FLD_NAME);
    growTreeFromFocList(list);
    getRoot().setTitle(ROOT_TITLE);
  }
	
	public void compute(){
	  scan(new TreeScanner<FObjectNode>() {
      @Override
      public boolean beforChildren(FObjectNode node) {
      	Division department = (Division) node.getObject();
        if(department != null){
          FProperty property =  department.getFocProperty(DivisionDesc.FLD_END_DIVISION);
          property.setValueLocked(!node.isLeaf() || department.isEndDivision());
        }
        return true;
      }

      @Override
      public void afterChildren(FObjectNode node) {
      }
    });
	}
	
	public static DivisionTree getInstance(){
		if(departmentTree == null){
			departmentTree = new DivisionTree();
		}
		return departmentTree;
	}
}
