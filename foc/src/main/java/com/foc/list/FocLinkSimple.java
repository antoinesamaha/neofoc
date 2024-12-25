/*
 * Created on Oct 14, 2004
 */
package com.foc.list;

import com.foc.db.*;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;

/**
 * @author 01Barmaja
 */
public class FocLinkSimple extends FocLink {

  public FocLinkSimple(FocDesc slaveDesc) {
    super(null, slaveDesc);
  }

  public FocDesc getLinkTableDesc() {
    return null;
  }

  public void adaptSQLFilter(FocList list, SQLFilter filter) {
  }

  public boolean saveDB(FocList focList) {
    /*
     * Iterator iter = focList.focObjectIterator(); while(iter.hasNext()){
     * FocObject obj = (FocObject)iter.next(); if(obj != null){ obj.save(); } }
     */

    return false;
  }

  public boolean canDeleteDB(FocList focList) {
    return true;
  }

  public boolean deleteDB(FocList focList) {
    return false;
  }

	@Override
	public boolean loadDB(FocList focList, long refToUpdateIncementally) {
		return loadDBDefault(focList, refToUpdateIncementally);
	}
	
  public boolean loadDB(FocList focList) {
    return loadDB(focList, 0);
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
