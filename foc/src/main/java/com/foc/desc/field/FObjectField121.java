/*
 * Created on Jul 07, 2008
 */
package com.foc.desc.field;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.property.FObject121;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FObjectField121 extends FObjectField {
  public FObjectField121(String name, String title, int id, boolean key, FocDesc focDesc, String keyPrefix) {
  	super(name, title, id, key, focDesc, keyPrefix);
  	//So that the FObject121 would create an Object if it does not find one.
  	setNullValueMode(FObjectField.NULL_VALUE_NOT_ALLOWED);
  	setWithList(false);
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FObject121(masterObj, getID(), (FocObject) defaultValue);
  }
}
