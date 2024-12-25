/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import com.foc.desc.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FObjectSumaryCharField extends FStringField {

	private int objectFieldID = FField.NO_FIELD_ID;
	
  public FObjectSumaryCharField(String name, String title, int id, boolean key, int size, int objectFieldID) {
    super(name, title, id, key, size);
    this.objectFieldID = objectFieldID;    
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FObjectSumaryChar(masterObj, getID(), (String)defaultValue);
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj){
    return newProperty(masterObj, "");
  }
  
  public String summarizeObject(FocObject focObject){
  	String str = "";
  	return str;
  }

	public int getObjectFieldID() {
		return objectFieldID;
	}
}
