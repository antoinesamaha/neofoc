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
package com.foc.tree.objectTree;

import com.foc.desc.FocObject;
import com.foc.tree.FNode;

public class FObjectNode<N extends FObjectNode, O extends FocObject> extends FNode<N, O>{

	public FObjectNode(String title, N fatherNode){
		super(title, fatherNode);
	}

	public void dispose(){
    super.dispose();
	}
  
	@SuppressWarnings("unchecked")
	@Override
  public N createChildNode(String childTitle) {
    return (N) new FObjectNode(childTitle, this);
  }

  @Override
  public int getLevelDepth() {
    return 0;
  }
  
  @Override
  public boolean isSortable(){
  	FObjectTree tree = (FObjectTree) getTree();
  	return (tree != null) ? tree.isSortable() : false;
  }
}
