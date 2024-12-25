/*
 * Created on Oct 14, 2004
 */
package com.foc.desc;

import java.util.*;

import com.foc.Globals;
import com.foc.desc.field.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FocFieldEnum implements Iterator {

  private FFieldPath currentFieldPath = null;
  private ArrayList<IterElem> fields = null;
  private FocObject focObj = null;
  private int category = 0;
  private int level = 0;

  private IterElem currentElement = null;
  private int currentIndex = 0;

  public static final int CAT_REF = 1;
  public static final int CAT_KEY = 2;
  public static final int CAT_ALL = 3;
  public static final int CAT_ALL_DB = 4;
  public static final int CAT_LIST = 5;

  public static final int LEVEL_PLAIN = 5;
  public static final int LEVEL_DB = 6;

  public class IterElem {
    FFieldPath fieldPath = null;
    FField     field     = null;

    public IterElem(FField field, FFieldPath currentFieldPath) {
      this.field = field;
      fieldPath = (FFieldPath) currentFieldPath.clone();
    }
    
    public void dispose(){
    	fieldPath = null;
    	field     = null;
    }
  }

  public void addFieldToPath(FField field){
    currentFieldPath.add(field.getID());
  }
  
  public void removeFieldFromPath(){
    currentFieldPath.removeLast();
  }

  public void addFieldToEnum(FField field){
    IterElem elem = new IterElem(field, currentFieldPath);
    fields.add(elem);
  }
  
  private void addLeafElement(FField field) {
    addFieldToPath(field);
    addFieldToEnum(field);
    removeFieldFromPath();
  }
  
  private void addElement(FField field) {
    if (field != null) {
      boolean doShowField = field.isDBResident() || category != CAT_ALL_DB;

      if (doShowField && field.isObjectContainer()){
        int newEnumFieldCategory = CAT_REF;
        if(category == CAT_LIST){
          newEnumFieldCategory = CAT_LIST;
        }else if(field.getClass() == FInLineObjectField.class){
          newEnumFieldCategory = (category == CAT_ALL_DB) ? CAT_ALL_DB : CAT_ALL ;
        }
        doShowField = level == LEVEL_PLAIN && field.getClass() != FInLineObjectField.class ;
        if (!doShowField) {
          addFieldToPath(field);
          if(field.getFocDesc() == null) {
          	Globals.logString("!! BAD MODEL !! Desc is null for field: "+field.getName()+" in table "+field.getFocDescParent().getStorageName());
          }
          new FocFieldEnum(field.getFocDesc(), null, fields, currentFieldPath, newEnumFieldCategory, this.level);
          removeFieldFromPath();
        }
      }
      
      if (category == CAT_LIST && field.getClass() != FListField.class) {
        doShowField = false;
      } else if (category != CAT_LIST && field.getClass() == FListField.class) {
        doShowField = false;
      }

      if (doShowField) {
        addLeafElement(field);
      }
    }
  }

  private void init(FocDesc focDesc, FocObject focObj, ArrayList<IterElem> paramFields, FFieldPath currentFieldPath, int category, int level) {
    this.focObj = focObj;
    this.category = category;
    this.level = level;
    this.currentFieldPath = currentFieldPath;
    if(this.currentFieldPath == null){
      this.currentFieldPath = new FFieldPath(0);
    }

    fields = paramFields;
    if (fields == null) {
      fields = new ArrayList<IterElem>();
    }
//    if(focDesc != null) {
	    if (category == CAT_REF && focDesc.getWithReference()) {
	      // If the category is reference and the ref field exists
	      // then, there is only one field.
	      // This is why we check the index == 0
	      FField field = focDesc.getFieldByID(FField.REF_FIELD_ID);
	      addElement(field);
	    } else if (category == CAT_REF || category == CAT_KEY) {
	      // If we are looking for keys we check the next key field
	      for (int i = 0; i < focDesc.getKeyFieldsSize(); i++) {
	        FField field = focDesc.getKeyFieldAt(i);
	        addElement(field);
	      }
	    } else {
	      // If we are looking for all the fields
	      for (int i = 0; i < focDesc.getFieldsSize(); i++) {
	        FField field = focDesc.getFieldAt(i);
	        addElement(field);
	      }
	    }
//    }
  }

  public FocFieldEnum(FocDesc focDesc, FocObject focObj, ArrayList<IterElem> fields, FFieldPath currentFieldPath, int category, int level) {
    init(focDesc, focObj, fields, currentFieldPath, category, level);
  }

  public FocFieldEnum(FocDesc focDesc, FocObject focObj, int category, int level) {
  	init(focDesc, focObj, null, null, category, level);
  }

  public FocFieldEnum(FocDesc focDesc, int category, int level) {
    init(focDesc, null, null, null, category, level);
  }

  public void dispose(){
  	if(fields != null){
  		for(int i=0; i<fields.size(); i++){
  			fields.get(i).dispose();
  		}
  		fields.clear();
  		fields = null;
  	}
  	currentFieldPath = null;
  	focObj           = null;
  }  
  
  public void reverseOrder(){
    ArrayList<IterElem> prevFields = fields;
    
    if(prevFields != null){
      fields = new ArrayList<IterElem>(prevFields.size());
      for(int i=prevFields.size()-1 ;i>=0; i--){
        fields.add(prevFields.get(i));
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext() {
    return (this.currentIndex < this.fields.size());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#next()
   */
  public Object next() {
    currentElement = (IterElem) fields.get(this.currentIndex++);
    return currentElement != null ? currentElement.field : null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#remove()
   */
  public void remove() {
  }

  public FField nextField() {
    return (FField) next();
  }

  public FProperty getProperty() {
  	return getProperty(focObj);
  }

  public FProperty getProperty(FocObject localFocObject) {
    FProperty prop = null;
    if (localFocObject != null && currentElement != null) {
      prop = currentElement.fieldPath.getPropertyFromObject(localFocObject);
    }
    return prop;
  }

  public FFieldPath getFieldPath() {
    FFieldPath fieldPath = null;
    if (currentElement != null) {
      fieldPath = currentElement.fieldPath;
    }
    return fieldPath;
  }
  
  public String getFieldCompleteName(FocDesc desc){
    return getFieldPath() != null ? getFieldPath().getFieldCompleteName(desc) : null;
  }
  
  public void reset() {
    this.currentIndex     = 0;
    this.currentFieldPath = null;
  }
}