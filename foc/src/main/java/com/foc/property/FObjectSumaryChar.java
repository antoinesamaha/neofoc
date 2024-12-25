/*
 * Created on 17 Oct. 2009 By Antoine SAMAHA 01Barmaja
 */
package com.foc.property;

import com.foc.desc.*;
import com.foc.desc.field.FObjectSumaryCharField;

/**
 * @author Standard
 */
public class FObjectSumaryChar extends FString implements Cloneable{
  
  public FObjectSumaryChar(FocObject focObj, int fieldID, String str) {
    super(focObj, fieldID, str);
  }

  public void compute(){
  	FObjectSumaryCharField field      = (FObjectSumaryCharField) getFocField();
  	int                    objFieldID = field.getObjectFieldID();
  	FocObject              bigFocObj  = getFocObject();

  	FocObject objectToSumarise = bigFocObj.getPropertyObject(objFieldID);
  	String summary = field.summarizeObject(objectToSumarise);
  	if(summary.length() > field.getSize()){
  		summary = summary.substring(0, field.getSize());
  	}
  	setString(summary);
  }
}
