/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import com.foc.desc.*;
import com.foc.list.filter.FocDescForFilter;

/**
 * @author 01Barmaja
 */
public class FConditionalObjectField extends FObjectField {
	private FFieldPath pathToDescProperty = null;
	
	public FConditionalObjectField(String name, String title, int id, FocDesc focDesc, String keyPrefix, FocDesc slaveFocDesc, int listFieldIdInMaster, FFieldPath pathToDescProperty){
		super(name, title, id, false, focDesc, keyPrefix, slaveFocDesc, listFieldIdInMaster, null);
		this.pathToDescProperty = pathToDescProperty;
	}

	public void dispose(){
    super.dispose();
    pathToDescProperty = null;
  }
  
  protected FListField setListFieldInMaster(int listFieldIdInMaster, FocDesc slaveFocDesc, FocDescForFilter focDescForFilter){
  	FListField listField = null;
  	if(isMasterDetailsLink()){
      FocDesc masterDesc = getFocDesc();
      if(masterDesc != null){
      	listField = masterDesc.addListField(pathToDescProperty, listFieldIdInMaster, null);      	
      }
  	}
  	return listField;
  }
}
