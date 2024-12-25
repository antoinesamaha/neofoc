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
package com.foc.wrapper;

import java.util.ArrayList;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDescFieldStringBased;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FTypedObjectField;

public abstract class WrapperDesc extends FocDesc {
	
  private WrapperLevel rootLevel = null;
	private int nextWrappedObjectFieldId = -1;

  public static final int FLD_WRAPPED_TYPES = 3000;
  public static final int FLD_WRAPPED       = 3001;
  
	public WrapperDesc(){
		this("WRAPER");
	}

	public WrapperDesc(String storageName){
		super(Wrapper.class, FocDesc.NOT_DB_RESIDENT, storageName, false);
	}

	public void dispose(){
		super.dispose();
		rootLevel = null;
	}
	
	protected void setRootWrapperLevel(WrapperLevel rootWrapperLevel){
		this.rootLevel = rootWrapperLevel;
	}
	
	public WrapperLevel getRootWrapperLevel(){
		return this.rootLevel;
	}
	
	protected void fillFocDesc(){
		fillFocDescWithGeneralFields();
		fillFocDescWithWrappedObjects();
	}
	
	private void fillFocDescWithGeneralFields(){
		FField focFld = new FStringField ("NODE_NAME", "Node Name", FField.FLD_NAME, false, 20);
    addField(focFld);
    
    FDescFieldStringBased descStringBasedField = new FDescFieldStringBased("WRAPPED_TYPES", "Wrapped types", FLD_WRAPPED_TYPES, false);
    descStringBasedField.setDBResident(false);
    descStringBasedField.setShowInDictionary(false);
    addField(descStringBasedField);
    
    FTypedObjectField typedObjField = new FTypedObjectField("WRAPPED", "WRAPPED", FLD_WRAPPED, false, /*TypedParameterSheet.getFocDesc()*/rootLevel.getFocDesc(), "WRAPPED_", FFieldPath.newFieldPath(FLD_WRAPPED_TYPES));
    typedObjField.setReflectingField(true);
    typedObjField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(typedObjField);
    
    setWithObjectTree();
	}
	
	private void fillFocDescWithWrappedObjects(){
		WrapperLevel rootWrapperLevel = getRootWrapperLevel();
		if(rootWrapperLevel != null){
			addLevelToDesc(rootWrapperLevel);
		}
	}
	
	private void addLevelToDesc(WrapperLevel wrapperLevel){
		if(wrapperLevel != null){
			FField field = newObjectField(wrapperLevel.getFocDesc());;
			wrapperLevel.setObjectFieldID(field.getID());
			ArrayList<WrapperLevel> childrenLevelArray = wrapperLevel.getChildrenLevelList();
			if(childrenLevelArray != null){
				for(int i = 0; i < childrenLevelArray.size(); i++){
					WrapperLevel childLevel = childrenLevelArray.get(i);
					if(childLevel != null){
						addLevelToDesc(childLevel);
					}
				}
			}
		}
	}
	
  public FObjectField newObjectField(FocDesc desc){
    int currentFieldID = getNextFieldId();
    FObjectField objField = new FObjectField(desc.getStorageName(), desc.getStorageName(), currentFieldID , false, desc, desc.getStorageName()+"_");
    objField.setWithList(false);
    addField(objField);
    return objField; 
  }
  
  private void initializeNextWrappedObjectFieldId(){
  	int maxFiedlId = 0;
  	int fieldsSize = getFieldsSize();
  	for(int i = 0; i < fieldsSize; i++){
  		FField field = getFieldAt(i);
  		if(field != null){
  			int fieldID = field.getID();
  			if(fieldID > maxFiedlId){
  				maxFiedlId = fieldID;
  			}
  		}
  	}
  	this.nextWrappedObjectFieldId = maxFiedlId + 1;
  }
  
  private int getNextFieldId(){
  	if(nextWrappedObjectFieldId < 0){
  		initializeNextWrappedObjectFieldId();
  	}
  	return nextWrappedObjectFieldId++;
  }

}
