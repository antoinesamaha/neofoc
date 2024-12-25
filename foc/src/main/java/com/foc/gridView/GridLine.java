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
package com.foc.gridView;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.property.FProperty;
import com.foc.shared.dataStore.IFocData;
import com.foc.tree.FNode;
import com.foc.tree.FTree;

public class GridLine extends FocObject{

	private FNode node = null;
	
  public GridLine(FocConstructor constr){
    super(constr);
    newFocProperties();
  }
  
  public void dispose(){
  	super.dispose();
  	node = null;
  }

	@Override
	public FProperty getFocProperty(int fieldID) {
		FProperty prop = super.getFocProperty(fieldID);

//		if(prop == null){
//			Globals.showNotification("Property NULL ? in Grid", "", IFocEnvironment.TYPE_WARNING_MESSAGE);
//		}else{
			if(fieldID >= ((GridLineDesc)getThisFocDesc()).getFirstFieldID()){
				GridLineDesc desc = (GridLineDesc) getThisFocDesc();
				
				FField field = desc.getFieldByID(fieldID);
				String fieldName = field.getName();
				fieldName = fieldName.replaceAll("#", ".");
				String nodeName     = desc.extractNodeName(fieldName);
				String subFieldName = desc.extractOriginalFieldName(fieldName);
				
				if(node != null){
					FNode childNode = node.findChild(nodeName);
					if(childNode != null){
						FocObject objectInNode = (FocObject) childNode.getObject();
	//					prop = objectInNode.getFocPropertyByName(subFieldName);
						IFocData iFocData = objectInNode.iFocData_getDataByPath(subFieldName);
						if(iFocData instanceof FProperty){
							prop = (FProperty) iFocData;
						}
					}
				}
			}
//		}
		
		return prop;
	}
	
	public void setCode(String code){
		setPropertyString(GridLineDesc.FLD_CODE, code);
	}

	public String getCode(){
		return getPropertyString(GridLineDesc.FLD_CODE);
	}

	public void setTitle(String title){
		setPropertyString(GridLineDesc.FLD_TITLE, title);
	}

	public String getTitle(){
		return getPropertyString(GridLineDesc.FLD_TITLE);
	}
	
  public void setNode(FNode node){
  	this.node = node;
  }

  public FNode getNode(){
  	return node;
  }

  public GridLineList getList(){
  	return (GridLineList) getFatherSubject();
  }
  
  public FTree getTree(){
  	GridLineList list = getList();
  	GridDefinition definition = list != null ? list.getGridDefinition() : null;
  	return definition != null ? definition.getTree() : null;
  }
}
