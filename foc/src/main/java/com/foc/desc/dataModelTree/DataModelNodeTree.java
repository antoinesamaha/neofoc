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
package com.foc.desc.dataModelTree;

import com.foc.desc.field.FField;
import com.foc.tree.objectTree.FObjectRootNode;
import com.foc.tree.objectTree.FObjectTree;

@SuppressWarnings("serial")
public class DataModelNodeTree extends FObjectTree {

  public DataModelNodeTree(DataModelNodeList list){
    setFatherNodeId(FField.FLD_FATHER_NODE_FIELD_ID);
    setDisplayFieldId(FField.FLD_NAME);
    growTreeFromFocList(list);
    sort();
    FObjectRootNode rootNode = (FObjectRootNode) getRoot();
    if(rootNode != null && list != null && list.getRootDesc() != null){
      rootNode.setTitle(list.getRootDesc().getStorageName());
    }
  }  
}
