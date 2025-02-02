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
package com.foc.pivot;

import java.util.ArrayList;

import com.foc.tree.objectTree.FObjectNode;

public class FPivotRowNode extends FObjectNode<FPivotRowNode, FPivotRow> {

  public FPivotRowNode(String title, FPivotRowNode fatherNode) {
    super(title, fatherNode);
  }

  @Override
  public FPivotRowNode createChildNode(String childTitle){
    return new FPivotRowNode(childTitle, this);
  }

	public String getTitle(){
    return getObject() != null ? getObject().getTitle() : super.getTitle();
	}

  public ArrayList<FPivotRowNode> getChildrenList(){
    ArrayList<FPivotRowNode> result = new ArrayList<FPivotRowNode>();
    
    for(int i=0; i<getChildCount(); i++){
      result.add(getChildAt(i));
    }
    
    return result;
  }

  public boolean isBreakdown_Descendent(String groupBy){
  	boolean isDescendent = false;
		FPivotRow curr = getObject();
		if(curr != null){
			isDescendent = curr.isBreakdown_Descendent(groupBy);
		}
		return isDescendent;
  }
  
  public boolean isBreakdown(String groupBy){
  	boolean isDescendent = false;
		FPivotRow curr = getObject();
		if(curr != null){
			isDescendent = curr.isBreakdown(groupBy);
		}
		return isDescendent;
  }
}
