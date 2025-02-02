/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.foc.*;
import com.foc.desc.*;
import com.foc.property.*;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class FFieldPath implements Cloneable {
  private int[] fieldPath = null;

  private void init(int nbrOfLevels){
    if (nbrOfLevels > 0) {
      this.fieldPath = new int[nbrOfLevels];
    }
  }
  
  public FFieldPath(int nbrOfLevels) {
    init(nbrOfLevels);
  }

  public void dispose(){
    fieldPath = null;
  }
  
  public Object clone(){
    FFieldPath newFieldPath = null;
    try{
      newFieldPath = (FFieldPath) super.clone();
      newFieldPath.init(size());
      for(int i=0; i<size(); i++){
        newFieldPath.set(i, get(i));  
      }
    }catch(Exception e){
      Globals.logException(e);
    }
    return (Object) newFieldPath ;
  }
  
  public void add(int fieldId){
    if(fieldPath == null){
      fieldPath = new int[1];
    }else{
      int[] oldFieldPath = fieldPath;
      fieldPath = new int[oldFieldPath.length + 1];
      for(int i=0; i<oldFieldPath.length; i++){
        fieldPath[i] = oldFieldPath[i];
      }
    }
    fieldPath[fieldPath.length - 1] = fieldId;
  }

  public void removeLast(){
    if(fieldPath != null && fieldPath.length > 0){
      if(fieldPath.length - 1 == 0){
        fieldPath = null;
      }else{
        int[] oldFieldPath = fieldPath;
        fieldPath = new int[oldFieldPath.length - 1];
        for(int i=0; i<oldFieldPath.length - 1; i++){
          fieldPath[i] = oldFieldPath[i];
        }
      }
    }
  }
  
  public boolean isEqualTo(FFieldPath otherFFieldPath){
    boolean equal = false;
    if(otherFFieldPath != null){
      int otherFieldPath[] = otherFFieldPath.getFieldPath();
      equal = fieldPath.length == otherFieldPath.length;
      
      for(int i = 0; i < fieldPath.length && equal; i++){
        if(fieldPath[i] != otherFieldPath[i]){
          equal = false;
        }
      }
    }
    return equal;
  }
  
  public void set(int at, int fieldId) {
    if (fieldPath != null) {
      fieldPath[at] = fieldId;
    }
  }

  public int get(int at) {
    return fieldPath != null ? fieldPath[at] : FField.NO_FIELD_ID;
  }

  public int size() {
    return fieldPath != null ? fieldPath.length : 0;
  }

  public static FFieldPath newFieldPath(int fieldId) {
    FFieldPath path = new FFieldPath(1);
    path.set(0, fieldId);
    return path;
  }

  public static FFieldPath newFieldPath(int fieldId1, int fieldId2) {
    FFieldPath path = new FFieldPath(2);
    path.set(0, fieldId1);
    path.set(1, fieldId2); 
    return path;
  }

  public static FFieldPath newFieldPath(int fieldId1, int fieldId2, int fieldId3) {
    FFieldPath path = new FFieldPath(3);
    path.set(0, fieldId1);
    path.set(1, fieldId2);
    path.set(2, fieldId3);    
    return path;
  }

  public static FFieldPath newFieldPath(int fieldId1, int fieldId2, int fieldId3, int fieldId4) {
    FFieldPath path = new FFieldPath(4);
    path.set(0, fieldId1);
    path.set(1, fieldId2);
    path.set(2, fieldId3);
    path.set(3, fieldId4);    
    return path;
  }
  
  public static FFieldPath newFieldPath(FocDesc focDesc, String dataPath) {
    FFieldPath fieldPath = null;
    
    if(focDesc != null && dataPath != null && !dataPath.isEmpty()){
      ArrayList<String> fieldNamesArray = new ArrayList<String>();
      
      StringTokenizer tokenizer = new StringTokenizer(dataPath, ".");
      while(tokenizer.hasMoreTokens()){
        String token = tokenizer.nextToken();
        fieldNamesArray.add(token);
      }
  
      if(fieldNamesArray.size() > 0){
        fieldPath = new FFieldPath(fieldNamesArray.size());
        
        FocDesc currDesc  = focDesc;
        FField  field     = null;
        boolean errorStop = false;
        for(int i=0; i<fieldNamesArray.size() && !errorStop; i++){
          String fieldName = fieldNamesArray.get(i);
          
          if(currDesc == null){
            errorStop = true;
          }
          
          if(!errorStop){
            field = currDesc.getFieldByName(fieldName);
            if(field == null){
              errorStop = true;
            }else{
              fieldPath.set(i, field.getID());
              currDesc = field.getFocDesc();
            }
          }
        }
        if(errorStop) fieldPath = null;
      }
    }
    
    return fieldPath;
  }
  
  public FField getFieldFromDesc(FocDesc desc) {
    FField field = null;
    FField[] fieldArray = getFieldArrayFromDesc(desc);
    if(size() > 0){
      field = fieldArray[size() - 1];
    }
    return field;
  }

  private FocDesc getNextDesc(FField fld){
    FocDesc focDesc = null;
    if(fld != null && fld.isObjectContainer()){
    	focDesc = fld.getFocDesc();
    }
    return focDesc ;
  }
  
  public FocDesc getDescFromDesc(FocDesc desc) {
    FField fld = getFieldFromDesc(desc);
    return (fld == null) ? desc : getNextDesc(fld);
  }
  
  public FField[] getFieldArrayFromDesc(FocDesc desc) {
    FField[] fieldList = null;
    
    if(desc != null){
      fieldList = new FField[size()];
  
      for (int i = 0; i < size(); i++) {
        fieldList[i] = null;
      }
      
      if (desc != null) {
        FocDesc currDesc = desc;
        int i;
        for (i = 0; i < size() && currDesc != null; i++) {
          int fieldId = get(i);
          fieldList[i] = currDesc.getFieldByID(fieldId);

          //BAntoine - The Unit field is otherwise without colour or normal renderer because the getFocDesc of the FFieldPath gives the TypeFocDesc...  
          //currDesc = getNextDesc(fieldList[i]);
          if(fieldList[i] instanceof FTypedObjectField){
          	FTypedObjectField             typedFld = (FTypedObjectField) fieldList[i];
          	Iterator<FMultipleChoiceItem> iter     = typedFld.newIteratorOnPossibleDescs(currDesc);
						if(iter != null && iter.hasNext()){
							FMultipleChoiceItem multiItem = (FMultipleChoiceItem) iter.next();
							if(multiItem != null){
								String  tableName   = multiItem.getTitle();
								currDesc = Globals.getApp().getFocDescByName(tableName);
							}
          	}
          }else{
          	currDesc = getNextDesc(fieldList[i]);
          }
          //EAntoine
        }
      }
    }
    return fieldList;
  }

  public FProperty getPropertyFromObject(FocObject focObject, int depth) {
    FProperty foundProp = null;
    if (focObject != null) {
      FProperty curProp = focObject.getFocProperty(get(0));
      int i ;
      for (i = 1; i <= depth && curProp != null; i++) {
        curProp = curProp.getFocProperty(get(i));
      }
      if (i == depth + 1) {
        foundProp = curProp;
      }
    }
    return foundProp;
  }
  
  public FProperty getPropertyFromObject(FocObject focObject) {
    return getPropertyFromObject(focObject, size()-1);
  }
  
  public String getFieldCompleteName(FocDesc desc){
    String name = "";
    if(desc != null){
      FField  curField = null;
      FocDesc currDesc = desc;
      for(int i = 0; i < size() && currDesc != null; i++){
        int fieldId = get(i);
        curField = currDesc.getFieldByID(fieldId);
        if(curField != null){
	        currDesc = getNextDesc(curField);
	        if(i == size() - 1){
	          name = name + curField.getName();
	        }else{
	        	if(i == size() - 2 && curField instanceof FObjectField && !Utils.isStringEmpty(((FObjectField)curField).getForcedDBName())){
	        		name = ((FObjectField)curField).getForcedDBName();
	        		break;
	        	}
	          name = name + curField.getKeyPrefix();
	        }
        }else{
        	currDesc = null;
        	name     = null;
        }
      }
    }
    return name;
  }

  private int[] getFieldPath() {
    return fieldPath;
  }
  
  public static FFieldPath newFFieldPath(FocDesc focDesc, String dataPath) {
  	FFieldPath fieldPath = new FFieldPath(0);
  	
  	StringTokenizer tokenizer = new StringTokenizer(dataPath, ".", false);
  	
  	while(tokenizer.hasMoreTokens() && focDesc != null){
  		String token = tokenizer.nextToken();
  		
  		FField fld = focDesc.getFieldByName(token);
  		if(fld != null){
  			fieldPath.add(fld.getID());
  			focDesc = fld.getFocDesc();
  		}else{
  			focDesc = null;
  		}
  	}
  	
  	return fieldPath;
  }

}
