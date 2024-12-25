/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import com.foc.db.SQLFilter;
import com.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class SQLSelectDistinct extends SQLSelectFields {
  
  public SQLSelectDistinct(FocDesc focDesc, int fieldID, SQLFilter filter) {
    super(focDesc, fieldID, filter);
  }
  
  public boolean buildRequest() {
    return buildRequest("SELECT DISTINCT");
  }
}
