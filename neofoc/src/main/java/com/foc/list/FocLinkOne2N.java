/*
 * Created on Oct 14, 2004
 */
package com.foc.list;

import com.foc.Globals;
import com.foc.db.*;
import com.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class FocLinkOne2N extends FocLink {
  private FocDesc linkTableDesc = null;

  public FocLinkOne2N(FocDesc masterDesc, FocDesc slaveDesc) {
    super(masterDesc, slaveDesc);
    slaveDesc.addMasterReferenceField(masterDesc);
  }

  public void adaptSQLFilter(FocList list, SQLFilter filter) {
    if (list != null && filter != null) {
      filter.setMasterObject(list.getMasterObject());
    }
  }

  public FocDesc getLinkTableDesc() {
    return null;
  }

  public boolean saveDB(FocList focList) {
    return  false;
  }

  public boolean loadDB(FocList focList, long refToUpdateIncementally) {
    boolean bool = false;
    if(focList.getMasterObject() != null && !focList.getMasterObject().isCreated() && focList.getMasterObject().getThisFocDesc().isPersistent()){
      bool = loadDBDefault(focList, refToUpdateIncementally);
    }
    return bool ;
  }
  
  public boolean loadDB(FocList focList) {
    return loadDB(focList, 0);
  }

  public boolean canDeleteDB(FocList focList) {
    return true;
  }

  public boolean deleteDB(FocList focList) {
  	return !Globals.getApp().getDataSource().focList_Delete(focList);
  }
  
  public boolean disposeList(FocList list){
    list.dispose();
    return true;
  }
  
  /* (non-Javadoc)
   * @see b01.foc.list.FocLink#copy(b01.foc.list.FocList, b01.foc.list.FocList)
   */
  public void copy(FocList targetList, FocList sourceList) {
    super.copyDefault(targetList, sourceList, true);
  }
  
  public FocObject getSingleTableDisplayObject(FocList list){
    return null;
  }
}
