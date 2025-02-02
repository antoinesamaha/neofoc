/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.util.Iterator;

import com.foc.desc.*;
import com.foc.property.FProperty;
import com.foc.property.FTypedObject;

/**
 * @author 01Barmaja
 */
public class FTypedObjectField extends FObjectField {
  private FFieldPath tableNameFieldPath = null;

  public FTypedObjectField(String name, String title, int id, boolean key, FocDesc focDesc, String keyPrefix, FFieldPath tableNameFieldPath) {
    super(name, title, id, key, focDesc, keyPrefix);
    this.tableNameFieldPath = tableNameFieldPath;
    setWithList(false);
  }
  
  public void dispose(){
    super.dispose();
    if(tableNameFieldPath != null){
    	tableNameFieldPath.dispose();
    	tableNameFieldPath = null;
    }
  }
  
  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FTypedObject(masterObj, getID(), (FocTypedObject) defaultValue);
  }
  
  public FProperty newProperty(FocObject masterObj){
    return newProperty(masterObj, null);
  }
  
	public FFieldPath getTableNameFieldPath() {
		return tableNameFieldPath;
	}
	
	public FDescFieldStringBased getStringBasedField(FocDesc currentFocDesc){
		FDescFieldStringBased stringBasedField = null;
		if(tableNameFieldPath != null){
//			stringBasedField = (FDescFieldStringBased) tableNameFieldPath.getFieldFromDesc(currentFocDesc);
		}
		return stringBasedField;
	}
	
	public Iterator<FMultipleChoiceItem> newIteratorOnPossibleDescs(FocDesc currentFocDesc){
		Iterator<FMultipleChoiceItem> multipleChoices = null;
		FDescFieldStringBased strBasedField = getStringBasedField(currentFocDesc);
		if(strBasedField != null){
			multipleChoices = strBasedField.getChoicesIterator();
		}
		return multipleChoices;
	}
	
  public void addReferenceLocations(FocDesc pointerDesc) {
		//	ATTENTION
		//  ATTENTION
		//  ATTENTION
		//  ATTENTION
  	
  	/*
    ObjectTypeMap map = getObjectTypeMap();
    Iterator iter = map.iterator();
    while(iter != null && iter.hasNext()){
      ObjectType type = (ObjectType)iter.next();
      FocDesc targetDesc = type.getFocDesc();//type.getSelectionList().getFocDesc();//type.getFocDesc();
      
      //Adding a field reference checker
      ReferenceCheckerAdapter refCheck = new ReferenceCheckerAdapter(pointerDesc, getID());
      if(targetDesc != null){
        targetDesc.addReferenceLocation(refCheck);
      }
    }
    */
  }
}
