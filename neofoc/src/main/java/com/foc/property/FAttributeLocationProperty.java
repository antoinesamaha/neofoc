/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.text.Format;
import java.util.StringTokenizer;

import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.list.FocList;
import com.foc.property.validators.PropertyAndFieldPath;
import com.foc.util.FocMath;

/**
 * @author Standard
 */
public class FAttributeLocationProperty extends FString implements Cloneable{
  private FFieldPath fieldPath       = null;
  private FFieldPath backupFieldPath = null;
  private FocDesc    localFocDesc    = null;

  public FAttributeLocationProperty(FocObject focObj, int fieldID, FFieldPath fieldPath) {
    super(focObj, fieldID, "");  
    this.fieldPath = fieldPath;
  }
  
  public void dispose(){
  	super.dispose();
  	fieldPath = null;
    backupFieldPath = null;
    localFocDesc = null;
  }

  protected Object clone() throws CloneNotSupportedException {
    FAttributeLocationProperty zClone = (FAttributeLocationProperty)super.clone();
    zClone.fieldPath = fieldPath;
    return zClone;
  }
  
  private String getString(boolean forSQL){
    StringBuffer str = new StringBuffer();
    
    if(fieldPath != null){      
      FocDesc currDesc = getBaseFocDesc();
      FField[] fieldArray = fieldPath.getFieldArrayFromDesc(currDesc);
      
      if(currDesc != null){
        if(!forSQL){
          str.append(currDesc.getStorageName() + ":");
        }
        
        for(int i=0; i<fieldPath.size(); i++){
          if(fieldArray[i] != null){
            if(i>0){
              str.append(".");
            }
            if(forSQL){
              str.append(fieldArray[i].getID());
            }else{
              str.append(fieldArray[i].getName());
            }
          }
        }
      }
    }
    return str.toString();
  }

  // Let the function call the static function that does the same work
  // so the static function will fill the fieldPath
  
  /*private void setString(boolean forSQL, String str){
    if(str != null && getDescProperty()!= null){
      StringTokenizer strTok = new StringTokenizer(str, ":.");    
      FFieldPath fieldPath = new FFieldPath(strTok.countTokens());
      
      int index = 0;
      FocDesc currDesc = getDescProperty().getSelectedFocDesc();
      FField currField = null;
      while(strTok.hasMoreTokens()){
        String nT = strTok.nextToken();
        int fieldId = FField.NO_FIELD_ID;
        if(forSQL){
          fieldId = Integer.valueOf(nT).intValue();
        }else{
          currField = currDesc.getFieldByName(nT);
          if(currField != null){
          	fieldId = currField.getID();
          	if(index < strTok.countTokens()-1){
              currDesc = currField.getFocDesc();
          	}
          }
        }
        fieldPath.set(index++, fieldId);        
      }
      
      setFieldPath(fieldPath);
    }
  }*/
  
  private void setString(boolean forSQL, String str){
		setFieldPath(FAttributeLocationProperty.newFieldPath(forSQL, str, getBaseFocDesc()));
  }

  public static FFieldPath newFieldPathUpToOneLevel(boolean forSQL, String str, FocDesc initialFocDesc){
  	return newFieldPath(forSQL, str, initialFocDesc, null, true);
  }
  
  public static FFieldPath newFieldPath(boolean forSQL, String str, FocDesc initialFocDesc){
  	return newFieldPath(forSQL, str, initialFocDesc, null, false);
  }

  public static FFieldPath newFieldPath(boolean forSQL, String str, FocDesc initialFocDesc, FocObject initialObject, boolean upToOneLevel){
  	PropertyAndFieldPath propertyAndPath = newFieldPath_PropertyAndField(forSQL, str, initialFocDesc, initialObject, upToOneLevel);
  	return propertyAndPath != null ? propertyAndPath.getFieldPath() : null;
  }
  
  public static PropertyAndFieldPath newFieldPath_PropertyAndField(boolean forSQL, String str, FocDesc initialFocDesc, FocObject initialObject, boolean upToOneLevel){
  	FFieldPath fieldPath = null;
  	FProperty  currProp  = null;
  	if(str != null && initialFocDesc!= null){
      StringTokenizer strTok = new StringTokenizer(str, ":.");
      int tokensCount = strTok.countTokens();
      if(upToOneLevel && tokensCount > 1) tokensCount = 1;
      fieldPath = new FFieldPath(tokensCount);
      
      int index = 0;
      FocDesc   currDesc   = initialFocDesc;
      FField    currField  = null;
      FocObject currObject = initialObject;
      while(strTok.hasMoreTokens() && index < tokensCount){
        String nT = strTok.nextToken();
        int fieldId = FField.NO_FIELD_ID;
        if(forSQL){
          fieldId = Integer.valueOf(nT).intValue();
        }else{
        	if(currDesc == null){
        		fieldPath = null;
        		break;
        	}else{
        		String fieldName = nT;
        		//Special treatment for list element
        		//----------------------------------
        		int crochetIdx = nT.indexOf('[');
        		if(crochetIdx > 0){
        			fieldName = nT.substring(0, crochetIdx);
        		}
        		//----------------------------------
	          currField = currDesc.getFieldByName(fieldName);
	          if(currField != null){
	          	fieldId    = currField.getID();
	          	currProp   = currObject != null ? currObject.getFocProperty(fieldId) : null;
	          	
	          	if(index < tokensCount - 1){
	          		//Special treatment for list element
	          		//----------------------------------
	          		if(currObject != null && currField instanceof FListField && crochetIdx > 0){
	          			int closeCrochetIdx = nT.indexOf(']');
	          			if(closeCrochetIdx > crochetIdx){
		          			String objectIndexStr = nT.substring(crochetIdx+1, closeCrochetIdx);
		          			int    objectIndex    = FocMath.parseInteger(objectIndexStr);
		          			FocList propsertyList = ((FList) currProp).getList();
		          			currObject = propsertyList.getFocObject(objectIndex);
		          			currDesc   = propsertyList.getFocDesc();
	          			}
		          	//----------------------------------
	          		//Was Under Comment
	              }else if(currObject != null && currField instanceof FTypedObjectField){
	              	currObject = (currProp != null && currProp.isObjectProperty()) ? ((FObject) currProp).getObject_CreateIfNeeded() : null;
	              	//currObject = ((FocTypedObject) currObject).getFocObject_CreateIfNeeded();
	              	currDesc   = currObject != null ? currObject.getThisFocDesc() : null;
	              }else{
	              	currDesc   = currField.getFocDesc();
	              	currObject = (currProp != null && currProp.isObjectProperty()) ? ((FObject) currProp).getObject_CreateIfNeeded() : null;
	              }
	          	}
	          }else{
	          	currDesc   = null;
	          	currObject = null;
	          	currProp   = null;
	          }
          }
        }
        fieldPath.set(index++, fieldId);        
      }
      
      //setFieldPath(fieldPath);
    }
  	
    return new PropertyAndFieldPath(fieldPath, currProp);
  }

  public static FProperty newFieldPathReturnProperty(boolean forSQL, String str, FocDesc initialFocDesc, FocObject initialObject){
  	FFieldPath fieldPath = null;
    FProperty currProp   = null;  	
  	if(str != null && initialFocDesc!= null){
      StringTokenizer strTok = new StringTokenizer(str, ":.");
      int tokensCount = strTok.countTokens();
      fieldPath = new FFieldPath(tokensCount);
      
      int index = 0;
      FocDesc   currDesc   = initialFocDesc;
      FField    currField  = null;
      FocObject currObject = initialObject;

      while(strTok.hasMoreTokens()){
        String nT = strTok.nextToken();
        if(currDesc != null){
	        int fieldId = FField.NO_FIELD_ID;
	        if(forSQL){
	          fieldId = Integer.valueOf(nT).intValue();
	        }else{
	          currField = currDesc.getFieldByName(nT);
	          if(currField != null){
	          	fieldId    = currField.getID();
	          	currProp   = currObject != null ? currObject.getFocProperty(fieldId) : null;
	          	
	          	if(index < tokensCount - 1){
	              /*if(initialObject != null && currField instanceof FTypedObjectField){
	              	currObject = (currProp != null && currProp.isObjectProperty()) ? ((FObject) currProp).getObject_CreateIfNeeded() : null;
	              	if(currObject != null){
		              	currObject = ((FocTypedObject) currObject).getFocObject_CreateIfNeeded();
		              	currDesc   = currObject.getThisFocDesc();
	              	}
	              }else{*/
	              	currDesc   = currField.getFocDesc();
	              	currObject = (currProp != null && currProp.isObjectProperty()) ? ((FObject) currProp).getObject_CreateIfNeeded() : null;
	              //}
	          	}
	          }else{
	          	currObject = null;
	          	currDesc = null;
	          	currProp = null;
	          }
	        }
	        fieldPath.set(index++, fieldId);
      	}
      }
      
      //setFieldPath(fieldPath);
    }
    return currProp;
  }

  public String getString() {
    return getString(false);
  }

  public void setString(String str) {
    setString(false, str);
  }

  public String getSqlString() {    
    return "\"" + getString(true) + "\"";
  }

  public void setSqlString(String str) {    
    setString(true, str);
  }
  
  public void setObject(Object obj) {
    setFieldPath((FFieldPath) obj);
  }

  public Object getObject() {
    return (Object) getFieldPath();
  }
  
  public void setLocalFocDesc(FocDesc localFocDesc){
  	this.localFocDesc = localFocDesc;
  }
  
  private FocDesc getLocalFocDesc(){
  	return this.localFocDesc;
  }
  
  public FFieldPath getFieldPath(){
    return fieldPath;
  }
  
  public void setFieldPath(FFieldPath fieldPath){
    this.fieldPath = fieldPath;
    notifyListeners();
  }  
  
	@Override
	public Object getTableDisplayObject(Format format) {
		return getString();
	}

	@Override
	public void setTableDisplayObject(Object obj, Format format) {
		setString((String)obj);
	}
  
  private IFDescProperty getDescProperty() {
    FAttributeLocationField attLocField = (FAttributeLocationField) getFocField();
    FFieldPath fieldPath = attLocField.getDescPropertyFieldPath();
    IFDescProperty descProperty = null;
    if(fieldPath != null){
    	descProperty = (IFDescProperty) fieldPath.getPropertyFromObject(getFocObject());
    }
    return descProperty;
  }
  
  public FocDesc getBaseFocDesc(){
  	FocDesc focDesc = getLocalFocDesc();
  	if(focDesc == null){
  		IFDescProperty descProperty = getDescProperty();
  		if(descProperty != null){
  			focDesc = descProperty.getSelectedFocDesc();
  		}
  	}
  	return focDesc;
  }
  
  public void backup() {
    backupFieldPath = fieldPath; 
  }
  
  public void restore() {
    setFieldPath(backupFieldPath);    
  }
}
