/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import com.foc.desc.FocObject;
import com.foc.property.FCurrRate;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FCurrRateField extends FNumField {
	  
  public FCurrRateField(String name, String title, int id, boolean key, int size, int decimals) {
    super(name, title, id, key, size, decimals, true);
    //setMinusIsUsedToReverse(true);
  }

  public FCurrRateField(String name, String title, int id, boolean key, int size, int decimals, boolean groupingUsed) {
    super(name, title, id, key, size, decimals);
    //setMinusIsUsedToReverse(true);
  }

  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FCurrRate(masterObj, getID(), defaultValue != null ? ((Double)defaultValue).doubleValue() : 0);
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
    return new FCurrRate(masterObj, getID(), 0);
  }
}
