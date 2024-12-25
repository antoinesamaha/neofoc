/*
 * Created on 07 July. 2008
 */
package com.foc.property;

import com.foc.desc.*;
import com.foc.list.FocList;

/**
 * @author 01Barmaja
 */
public class FObject121 extends FObject {
  public FObject121(FocObject fatherObj, int fieldID, FocObject focObj) {
    super(fatherObj, fieldID, focObj);
  }

  @Override
  protected FocObject getAnyObjectBecauseCannotRemainEmpty(){
    FocList focList = this.getPropertySourceList();
    FocObject obj = focList.newEmptyItem();
    focList.add(obj);
  	return obj;
  }

  public void copy(FProperty sourceProp){
  	super.copy(sourceProp);

  	/*
  	FObject sourceObjProp = (FObject)sourceProp;
    if(sourceObjProp != null){
	    FocList objList = getPropertySourceList();
	    //We make like this so if the objList == null we got into the else and we make setObject()
	    //if(objList != sourceObjProp.getPropertySourceList()){
	    if(objList != null && objList != sourceObjProp.getPropertySourceList()){
	      if(objList != null){
	        FocObject tarObj = objList.searchByUniqueKey((FocObject)sourceObjProp.getObject_CreateIfNeeded());
	        //FocObject tarObj = objList.findObject((FocObject)sourceObjProp.getObject());
	        if(tarObj != null){
	          setObject(tarObj);
	        }
	      }
	    }else{
	      this.setObject(sourceObjProp.getObject_CreateIfNeeded());      
	    }
    }
    */
  }  
}