/*
 * Created on Jun 27, 2005
 */
package com.foc.desc.field;

import java.awt.Component;

import com.foc.*;
import com.foc.desc.*;
import com.foc.gui.table.cellControler.*;
import com.foc.list.filter.FilterCondition;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FFieldArray extends FField{
  private FField fieldArray[]= null;
  private FFieldArrayPlug plug = null;
  
  public FFieldArray(FField firstField, int size, FFieldArrayPlug plug){
    super(firstField.getName(), firstField.getTitle(), firstField.getID() - 1, false, size, 0);
    try{
      this.plug = plug;
      fieldArray = new FField[size];
      for(int i=0; i<size; i++){
        FField fld = null;      
        if(i == 0){
          fld = firstField;
        }else{
          fld = (FField) firstField.clone();  
        }      
        fieldArray[i] = fld ;
        
        fld.setName(name+i);
        fld.setId(firstField.getID() + i);
      }
      setLockValueAfterCreation(firstField.isLockValueAfterCreation());
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  
  public Object clone() throws CloneNotSupportedException {
    FFieldArray fldArray = (FFieldArray)super.clone();
    return fldArray;
  }
  /*
  public FField duplicate() {
    return new FFieldArray(fieldArray[0], size, plug);
  }
  */
  
  public String getCreationString(String name) {
    return null;
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return null;
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
    return null;
  }
  
  public Component getGuiComponent(FProperty prop) {
    FocObject obj = prop.getFocObject();
    FField currFld = getCurrentField();
    int currFldId = currFld.getID();
    FProperty currProp = obj.getFocProperty(currFldId);
    return currProp.getGuiComponent();
  }
  
  public int getSqlType() {
    return java.sql.Types.ARRAY;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop) {
    FocObject obj = prop.getFocObject();
    FField currFld = getCurrentField();
    int currFldId = currFld.getID();
    return currFld.getTableCellEditor(obj.getFocProperty(currFldId));
  }
  
  public FField getFieldAt(int i){
    return fieldArray != null && fieldArray.length > i ? fieldArray[i] : null;
  }
  
  public FField getCurrentField(){
    return plug != null ? getFieldAt(plug.getCurrentIndex()) : null;
  }

  public int getFieldDisplaySize() {
    FField field = getCurrentField();
    return field != null ? field.getFieldDisplaySize() : 0;
  }
  
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
  	return null;
  }
}
