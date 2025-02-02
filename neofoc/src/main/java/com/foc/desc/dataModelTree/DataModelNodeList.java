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

import java.util.Iterator;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FBlobField;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceItem;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FTypedObjectField;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.property.FObject;
import com.foc.property.FProperty;

public class DataModelNodeList extends FocList {
	private FocDesc   rootDesc   = null ;
	private FocObject rootObject = null ;
	private int       maxLevel   = 0    ;
	
  public DataModelNodeList(FocDesc rootDesc, int maxLevel){
  	this(rootDesc, maxLevel, null);
  }

  public DataModelNodeList(FocDesc rootDesc, int maxLevel, FocObject rootObject){
    super(new FocLinkSimple(DataModelNodeDesc.getInstance()));
    this.rootDesc   = rootDesc  ;
    this.maxLevel   = maxLevel  ;
    this.rootObject = rootObject;
    constructFocList();
  }

  public void dispose(){
  	rootDesc   = null;
  	rootObject = null;
  }
  
  public FocDesc getRootDesc(){
  	return rootDesc;
  }
  
  private boolean isDescInOneOfTheFathers(DataModelNode node, FocDesc focDesc){
  	boolean exist = false;
  	DataModelNode currNode = node;
  	while(currNode != null && !exist){
  		exist = currNode.getFocDesc() == focDesc;
  		currNode = currNode.getFather();
  	}
  	return exist;
  }

  private DataModelNode addDataModelNode_Atomic(DataModelNode fatherNode, FocDesc focDesc, FField field, String name, String pathSection, String description, String value){
  	DataModelNode modelNode = (DataModelNode) newEmptyItem();
  	String newName        = name.replace('.', '-');
  	String newPathSection = pathSection.replace('.', '-');
  	
		modelNode.setName(newName);
		modelNode.setPathSection(newPathSection);
		modelNode.setDescription(description);
		modelNode.setFather(fatherNode);
		modelNode.setFocDesc(focDesc);
		modelNode.setFField(field);
		modelNode.setValue(value);
		add(modelNode);
		return modelNode;
  }

  private DataModelNode addDataModelNode(DataModelNode fatherNode, FocDesc focDesc, FField fld, String name, String pathSection, String description, String dictionaryGroup, String value){
  	DataModelNode intermediateNode = fatherNode;
  	
  	if(dictionaryGroup != null && !dictionaryGroup.equals("")){
  		DataModelNode foundGroupNode = null;
      Iterator iter = newSubjectIterator();
      while(iter!=null && iter.hasNext()){
      	DataModelNode currNode = (DataModelNode)iter.next();
        if(currNode != null){
        	DataModelNode currFather = currNode.getFather();
        	if(currFather == fatherNode && currNode.getName().equals(dictionaryGroup)){
        		foundGroupNode = currNode;
        	}
        }
      }
      if(foundGroupNode == null){
      	intermediateNode = addDataModelNode_Atomic(intermediateNode, null, fld, dictionaryGroup, "", "", "");
      }else{
      	intermediateNode = foundGroupNode;
      }
  	}
  	
  	intermediateNode = addDataModelNode_Atomic(intermediateNode, focDesc, fld, name, pathSection, description, value);
  	
		return intermediateNode;
  }
  
  private void constructFocList(DataModelNode fatherNode, FocDesc focDesc, FocObject focObject){
  	if(focDesc != null && !isDescInOneOfTheFathers(fatherNode, focDesc)){
	    for (int i = 0; i < focDesc.getFieldsSize(); i++) {
	    	FField    field = focDesc.getFieldAt(i);	    	
	    	FProperty prop  = focObject != null ? focObject.getFocProperty(field.getID()) : null;
	  		if(field != null && field.getID() != FField.REF_FIELD_ID){
	  			boolean toBeInserted = !field.isObjectContainer() || field instanceof FTypedObjectField || field instanceof FObjectField || field instanceof FBlobField || field instanceof FBlobStringField; 

	  			if(toBeInserted){
	  				toBeInserted = fatherNode == null || fatherNode.getLevel() < maxLevel;
	  			}
	  			
	  			toBeInserted = toBeInserted && field.isShowInDictionary();
//	  			toBeInserted = toBeInserted && !(field instanceof FListField);
	  			
	  			if(toBeInserted){
	  				DataModelNode modelNode = addDataModelNode(fatherNode, focDesc, field, field.getName(), field.getName(), field.getTitle(), field.getDictionaryGroup(), prop != null ? prop.getString() : null); 
						
						//if(field.isObjectContainer()){
	  				if(field.getFocDesc() != null){
		  				FocObject nextFocObject = null;
		  				try{
		  					nextFocObject = (FocObject) ((FObject)prop).getObject_CreateIfNeeded();
		  				}catch(Exception e){
		  					//Excecption not caught on purpose.
		  				}
							
							if(field instanceof FTypedObjectField){
								FTypedObjectField typedField     = (FTypedObjectField) field;
								Iterator<FMultipleChoiceItem> iter = typedField.newIteratorOnPossibleDescs(focDesc);
								while(iter != null && iter.hasNext()){
									FMultipleChoiceItem multiItem = (FMultipleChoiceItem) iter.next();
									if(multiItem != null){
										String tableName    = multiItem.getTitle();
										FocDesc nextFocDesc = Globals.getApp().getFocDescByName(tableName);
					  				if(nextFocDesc != null && nextFocDesc != focDesc){
						  				DataModelNode typeModelNode = addDataModelNode(modelNode, null, typedField, nextFocDesc.getStorageName(), "", nextFocDesc.getStorageName(), "", ""); 
					  					constructFocList(typeModelNode, nextFocDesc, (nextFocObject != null && nextFocObject.getThisFocDesc() == nextFocDesc) ? nextFocObject : null);
					  				}
									}
								}
							}else{
			  				FocDesc nextFocDesc = field.getFocDesc();
			  				if(nextFocDesc != focDesc){
			  					constructFocList(modelNode, nextFocDesc, nextFocObject);
			  				}
							}
						}
	  			}
	  		}
	  	}
  	}
  }

  private void constructFocList(){
  	constructFocList(null, rootDesc, rootObject);
  }
}
