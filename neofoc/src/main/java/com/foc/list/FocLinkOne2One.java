/*
 * Created on Oct 14, 2004
 */
package com.foc.list;

import com.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class FocLinkOne2One extends FocLinkOne2N {
  private FocDesc linkTableDesc = null;

  public FocLinkOne2One(FocDesc masterDesc, FocDesc slaveDesc) {
    super(masterDesc, slaveDesc);
  }

  public FocObject getSingleTableDisplayObject(FocList list){    
    return list != null ? list.getOrInsertAnItem() : null;
  }

}
