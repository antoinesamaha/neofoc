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

import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;

public class DataModelNode extends FocObject {
	
	private FocDesc focDesc = null;
	private FField  fField  = null; 
	
  public DataModelNode(FocConstructor constr){
    super(constr);
    newFocProperties();
  }
  
  public String getPathSection(){
    return getPropertyString(DataModelNodeDesc.FLD_PATH_SECTION);
  }
  
  public void setPathSection(String code){
    setPropertyString(DataModelNodeDesc.FLD_PATH_SECTION, code);
  }
  
  public String getName(){
    return getPropertyString(FField.FLD_NAME);
  }
  
  public void setName(String name){
    setPropertyString(FField.FLD_NAME, name);
  }
  
  public String getDescription(){
    return getPropertyString(DataModelNodeDesc.FLD_DESCRIPTION);
  }
  
  public void setDescription(String descrip){
    setPropertyString(DataModelNodeDesc.FLD_DESCRIPTION, descrip);
  }

  public boolean isSelected(){
  	return getPropertyBoolean(DataModelNodeDesc.FLD_SELECT);
  }

  public void setSelected(boolean b){
  	setPropertyBoolean(DataModelNodeDesc.FLD_SELECT, b);
  }

  public void setFather(DataModelNode father){
  	setPropertyObject(FField.FLD_FATHER_NODE_FIELD_ID, father);
  }

  public DataModelNode getFather(){
  	return (DataModelNode) getPropertyObject(FField.FLD_FATHER_NODE_FIELD_ID);
  }

	public FocDesc getFocDesc() {
		return focDesc;
	}

	public void setFocDesc(FocDesc focDesc) {
		this.focDesc = focDesc;
	}

	public FField getFField() {
		return fField;
	}

	public void setFField(FField focFld) {
		this.fField = focFld;
	}

	public String getValue() {
		return getPropertyString(DataModelNodeDesc.FLD_VALUE);
	}

	public void setValue(String value) {
		setPropertyString(DataModelNodeDesc.FLD_VALUE, value);
	}

	public String getFullPath(){
		StringBuffer buffer = new StringBuffer();
		
		DataModelNode node = this;
		while(node != null){
			String pathSection = node.getPathSection();
			if(buffer.length() > 0 && pathSection.length() > 0){
				buffer.insert(0, ".");
			}
			if(pathSection.length() > 0){
				buffer.insert(0, pathSection);
			}
			node = node.getFather();
		}
		
		return buffer.toString();
	}
	
	public int getLevel(){
		int level = 0;
		DataModelNode node = this;
		while(node != null){
			level++;
			node = node.getFather();
		}
		return level;
	}
}
