/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.util.HashMap;
import java.util.Iterator;

import com.foc.desc.*;
import com.foc.desc.field.FListField;
import com.foc.list.*;

/**
 * @author 01Barmaja
 */
public class FList extends FProperty {
  private FocList list = null;

  public FList(FocObject fatherObj, int fieldID, FocList list) {
    super(fatherObj, fieldID);
    this.list = list;
    list.setFatherSubject(fatherObj);
    list.setFatherProperty(this);
  }

  public void dispose(){
    FListField listField = (FListField)getFocField();
    if(listField != null){
      FocLink link = listField.getLink();
      if (link.disposeList(list)){
        list = null;
      }
    }
    super.dispose();
  }
  
  public void popupSelectionPanel() {
  }

  public String getString() {
    return "";
  }

  public void setString(String str) {
  }

  public void setInteger(int iVal) {
  }

  public int getInteger() {
    return 0;
  }

  public void setDouble(double dVal) {
  }

  public double getDouble() {
    return 0;
  }

  public void setObject(Object obj) {
    /*
     * focObjValue = (FocObject) obj; notifyListeners();
     */
  }

  public Object getObject() {
    FocList list = getList();
    return list != null ? (Object) list.getSingleTableDisplayObject() : null;
  }

  public FProperty getFocProperty(int fldId){
    FProperty prop = null;
    FocObject singleDisplayObj = (FocObject) getObject();
    
    if(singleDisplayObj != null){
      prop = singleDisplayObj.getFocProperty(fldId);
    }

    return prop;
  }  
  
  /*
   * public void setFocObject(FocObject obj) { focObjValue = obj;
   * notifyListeners(); }
   * 
   * public FocObject getFocObject() { return focObjValue; }
   */
  public void backup() {
  }

  public void restore() {
  }

  private boolean fillTheListFromAMasterListInTheFocObject(){
		boolean doLoadIfNotLoaded = true;
		if(getFocObject() != null && getFocField() != null){
			FocList sourceList = (FocList) getFocObject().getListPropertyInitialLoadedList(getFocField().getID());
			if(sourceList != null){
				list.setLoaded(true);
				doLoadIfNotLoaded = false;
				  
				for(int i=sourceList.size()-1; i>=0; i--){
					FocObject srcObj = sourceList.getFocObject(i);
					if(srcObj != null){
						boolean objectIncluded = true;
						
						HashMap<Integer, FocObject> foreignObjectsMap = list.getForeignObjectsMap();

			      Iterator iter = foreignObjectsMap.keySet().iterator();
			      while(iter != null && iter.hasNext()){
			        int key = (Integer)iter.next();
			        FocObject targetListForeignObject = foreignObjectsMap.get(key);
			        int       targetListForeignObject_Ref = targetListForeignObject.getReference().getInteger();
			        
			        FObject   sourceObjProp = (FObject) srcObj.getFocProperty(key);
			        long      sourceListForeignObject_Ref = sourceObjProp.getLocalReferenceInt();
			        
			        //ATTENTION!!! FocObject srcForeignObject        = srcObj.getPropertyObject(key);
			        
			        if(targetListForeignObject_Ref != sourceListForeignObject_Ref){
			        	objectIncluded = false;
			        }
			      }
			        
			      if(objectIncluded){
			      	sourceList.remove(srcObj);
			      	
				      iter = foreignObjectsMap.keySet().iterator();
				      while(iter != null && iter.hasNext()){
				        int       key           = (Integer)iter.next();
				        FocObject foreignObject = foreignObjectsMap.get(key);
				        srcObj.setPropertyObject(key, foreignObject);
				      }			      	
			      	
			      	list.add(srcObj);
			      }
					}
				}
			}
		}
		return doLoadIfNotLoaded;
  }
  
  /**
   * @return
   */
  public FocList getList() {
    if(list != null && getFocField() != null && getFocField().isDBResident()){
    	if(list.needsToBeLoaded()){
    		boolean doLoadIfNotLoaded = fillTheListFromAMasterListInTheFocObject();
    		if(doLoadIfNotLoaded){
    			list.loadIfNotLoadedFromDB();
    		}
    	}
    }
    return list;
  }
  
  public FocList getListWithoutLoad() {    
    return list;
  }
  
  public void copy(FProperty sourceProp){
    FList sourceListProp = (FList) sourceProp;
    FocList sourceList = sourceListProp.getList();
    FocList targetList = this.getList();
    if(sourceList != null && targetList != null){
      targetList.copy(sourceList);
    }
  }
}
