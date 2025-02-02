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
// PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// DESCRIPTION
// LIST

/*
 * Created on 17-Apr-2005
 */
package com.foc.desc;

/**
 * @author 01Barmaja
 */
public class FocTypedObject extends FocObject {
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public FocTypedObject(FocConstructor constr) {
    super(constr);
  }

  /*
  protected void initialize(ObjectTypeMap objectTypeMap, int displayField){
    this.objectTypeMap = objectTypeMap;
    int defaultTypeInt = objectTypeMap.getDefaultType();
    type = new FMultipleChoice(this, FLD_TYPE, defaultTypeInt);    
    
    ObjectType objDefaultType = objectTypeMap.get(defaultTypeInt);
    
    FocList typeList = objDefaultType.getSelectionList();
    if(typeList != null){
      object = new FObject(this, FLD_OBJECT, typeList.newEmptyDisconnectedItem(), displayField, typeList);
    }else{
      FocConstructor focConstr = new FocConstructor(objDefaultType.getFocDesc(), null, this);
      FocObject focObj = focConstr.newItem();
      focObj.setDbResident(false);
      object = new FObject(this, FLD_OBJECT, focObj, displayField);      
    }
    
    //Listen to the type to modify the selection list of the object and the object
    FPropertyListener typeListener = new FPropertyListener() {
      public void propertyModified(FProperty prop) {
        FList list = null;
        FocObject objToReplace = (FocObject) object.getObject();
        
        ObjectType objType = getLocalObjectTypeMap().get(type.getInteger());
        
        if(objType != null){
          FocList selectionList = objType.getSelectionList();
          if(selectionList != null){
            object.setLocalSourceList(selectionList);
            if(objToReplace.getThisFocDesc().getFocObjectClass() != selectionList.getFocDesc().getFocObjectClass()){        
              if(prop.isLastModifiedBySetSQLString()){
              }else{
                object.setObject(selectionList.newEmptyDisconnectedItem());
              }
            }
          }else{
            object.setFocDesc(objType.getFocDesc());
            if(!type.isLastModifiedBySetSQLString()){
              object.setObject(null);
            }else{
              object.setObjectToNullWithoutLocalReferenceModification();
            }
          }
        }
      }

      public void dispose() {
      }
    };
    type.addListener(typeListener);
  }
  
  public ObjectTypeMap getLocalObjectTypeMap(){
    return objectTypeMap;
  }
    
  public FPanel newDetailsPanel(int viewID) {
    return null;
  }
  
  public int getType(){
    return type.getInteger();
  }

  public void setType(int typeValue){
    type.setInteger(typeValue);
  }
  
  public FocObject getFocObject(){
    return object != null ? (FocObject) object.getObject() : null;
  }

  public FocObject getFocObject_CreateIfNeeded(){
    return (FocObject) object.getObject_CreateIfNeeded();
  }
  
  public void setFocObject(FocObject focObj){
    object.setObject(focObj);
  }
  
  public void setModified(boolean modified) {
    super.setModified(modified);
    if(modified && getMasterObject() != null){
      getMasterObject().setModified(true);
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  final public static int FLD_TYPE = 1;
  final public static int FLD_OBJECT = 2;
  
  public static void fillFocDesc(FocDesc focDesc, ObjectTypeMap objTypeMap, String keyPrefix){
    FMultipleChoiceField typeFld = new FMultipleChoiceField("TYPE", "Type", FLD_TYPE, true, 3);
    Iterator iter = objTypeMap.iterator(); 
    while(iter != null && iter.hasNext()){
      ObjectType objType = (ObjectType)iter.next();
      if(objType != null){
        typeFld.addChoice(objType.getType(), objType.getTitle());
      }
    }
    focDesc.addField(typeFld);
    
    ObjectType defaultType = objTypeMap.get(objTypeMap.getDefaultType());
    FObjectField focObjFld = new FObjectField("OBJECT", "Object", FLD_OBJECT, true, defaultType.getFocDesc(), keyPrefix);
    focObjFld.setWithList(false);
    focDesc.addField(focObjFld);
    
    focDesc.setPropertyArrayLength(focDesc.getPropertyArrayLength() + 1);
  }
  
  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(FocTypedObject.class, FocDesc.NOT_DB_RESIDENT, "TYPED_OBJ", false);

      //fillFocDesc(focDesc, null, "");
    }
    return focDesc;
  }

	public FObject getObjectProperty() {
		return object;
	}
	*/
}
