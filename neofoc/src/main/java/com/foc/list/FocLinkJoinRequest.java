/*
 * Created on Oct 14, 2004
 */
package com.foc.list;

import com.foc.Globals;
import com.foc.db.*;
import com.foc.desc.*;
import com.foc.join.*;

/**
 * @author 01Barmaja
 */
public class FocLinkJoinRequest extends FocLinkSimple {
  private FocRequestDesc requestDesc = null;
  
  public FocLinkJoinRequest(FocRequestDesc requestDesc) {
    super(requestDesc.getFocDesc());
    this.requestDesc = requestDesc;
  }

  public FocDesc getLinkTableDesc() {
    return null;
  }

  public void adaptSQLFilter(FocList list, SQLFilter filter) {
  }

  public FocRequestDesc getRequestDesc(){
  	return requestDesc;
  }
  
	@Override
	public boolean loadDB(FocList focList, long refToUpdateIncementally) {
    boolean loaded = Globals.getApp().getDataSource().focList_Join_Load(focList, refToUpdateIncementally);
    return loaded;		
	}
	
  public boolean loadDB(FocList focList) {
    boolean loaded = Globals.getApp().getDataSource().focList_Join_Load(focList, 0);
    return loaded;
  }
}
