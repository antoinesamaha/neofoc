/*
 * Created on Feb 23, 2006
 */
package com.foc.jasper;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class FocJRDataRow {
  private HashMap<String, Object> fieldsMap = null;
    
  public FocJRDataRow(){
    fieldsMap = new HashMap<String, Object>();
  }
  
  public Object getFieldData(String fieldName){
    return fieldsMap.get(fieldName);
  }
  
  public void setFieldData(String fieldName, Object data){
    fieldsMap.put(fieldName, data);
  }
  
  public void copy(FocJRDataRow other){
    Iterator iter = other.fieldsMap.keySet().iterator();
    while(iter != null && iter.hasNext()){
      String field = (String) iter.next();
      setFieldData(field, other.getFieldData(field));
    }
  }
}
