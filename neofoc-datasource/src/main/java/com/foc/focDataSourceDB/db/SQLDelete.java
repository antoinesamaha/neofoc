/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import com.foc.db.SQLFilter;
import com.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class SQLDelete extends SQLRequest {

  private FocObject focObj = null;
  
  public SQLDelete(FocObject focObj) {
    this(focObj.getThisFocDesc(), new SQLFilter(focObj, SQLFilter.FILTER_ON_IDENTIFIER));
    this.focObj = focObj;
  }
  
  public SQLDelete(FocDesc focDesc, SQLFilter filter) {
    super(focDesc, filter);
  }
  
  @Override
  protected int getSQLRequestType() {
    return TYPE_DELETE;
  }

  @Override
  protected FocObject getFocObject(){
    return this.focObj; 
  }

  public boolean buildRequest() {
    request = new StringBuffer("");

    if (focDesc != null && focDesc.isPersistent()) {
      request.append("DELETE ");
      addFrom(false);
      addWhere(false);
    }
    return false;
  }  
}
