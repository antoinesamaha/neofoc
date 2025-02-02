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

import com.foc.tree.objectTree.FObjectNode;

public class FocMenuItemNode extends FObjectNode<FocMenuItemNode, FocMenuItem>{

	public FocMenuItemNode(String title, FocMenuItemNode fatherNode) {
		super(title, fatherNode);
	}
	
	@Override
	public FocMenuItemNode createChildNode(String childTitle) {
		return new FocMenuItemNode(childTitle, this); 
	}
	
	@Override
	public boolean isVisible(){
		boolean vis = super.isVisible();
		return vis;
	}

}
