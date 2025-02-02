/*
 * Created on Oct 14, 2004
 */
package com.foc.list;

import java.util.*;

import com.foc.*;
import com.foc.db.*;
import com.foc.desc.*;

/**
 * @author 01Barmaja
 */
public abstract class FocLink {
  private FocDesc masterDesc = null;
  private FocDesc slaveDesc = null;

  public abstract FocDesc getLinkTableDesc();

  public abstract boolean saveDB(FocList focList);
  public abstract boolean loadDB(FocList focList);
  public abstract boolean loadDB(FocList focList, long refToUpdateIncementally);
  public abstract boolean deleteDB(FocList focList);

  public abstract void copy(FocList targetList, FocList sourceList);
  
  public abstract void adaptSQLFilter(FocList list, SQLFilter filter);

  public abstract FocObject getSingleTableDisplayObject(FocList list);
  
  public abstract boolean disposeList(FocList list);
  
  public FocLink(FocDesc masterDesc, FocDesc slaveDesc) {
    this.masterDesc = masterDesc;
    this.slaveDesc = slaveDesc;
  }
  
  protected boolean loadDBDefault(FocList focList, long refToUpdateIncementally) {
    boolean loaded = false;
    FocDesc slaveDesc = getSlaveDesc();
    if (slaveDesc != null) {
      if(Globals.getApp() != null){
        loaded = !Globals.getApp().getDataSource().focList_Load(focList, refToUpdateIncementally);
      }
    }
    return loaded;
  }
  
  public void copyDefault(FocList targetList, FocList sourceList, boolean copyObjects){
  	copyDefault(targetList, sourceList, copyObjects, null);
  }
  
  public void copyDefault(FocList targetList, FocList sourceList, boolean copyObjects, int[] excludedFields){
    try{
      if(targetList != null && sourceList != null){
        Iterator iter = sourceList.focObjectIterator();
        while(iter != null && iter.hasNext()){
          FocObject sourceObj = (FocObject) iter.next();
          if(sourceObj != null){
            FocObject targetObj = null;
            if(copyObjects){              
              targetObj = targetList.newEmptyItem();
              targetObj = sourceObj.duplicate(targetObj, targetList.getMasterObject(), false, false, excludedFields);
              //targetObj = sourceObj.duplicateNoValidate(targetObj, targetList.getMasterObject(), false);
            }else{
              targetObj = sourceObj;
              targetList.add(targetObj);
            }
          }
        }
      }
    } catch(Exception e){
      Globals.logException(e);
    }
  }

  /**
   * @return
   */
  public FocDesc getMasterDesc() {
    return masterDesc;
  }

  /**
   * @return
   */
  public FocDesc getSlaveDesc() {
    return slaveDesc;
  }
  
  public void setSlaveDesc(FocDesc focDesc) {
    this.slaveDesc = focDesc;
  }
}
