/*
 * Created on Aug 4, 2005
 */
package com.foc.stringList;

import com.foc.*;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.gui.*;
import com.foc.list.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class StringList extends FocList{
  
  public static int VIEW_NO_VALIDATION = 1;
  
  public StringList() {
    super(new FocLinkSimple(StringListItem.getFocDesc()));
    FocListOrder order = new FocListOrder();
    order.addField(FFieldPath.newFieldPath(StringListItem.FLD_NAME));
    setListOrder(order);
    
    FObject selectionProperty = new FObject(null, 1, newEmptyItem());
    setSelectionProperty(selectionProperty);
    //constr = new FocConstructor(StringListItem.getFocDesc(), null, null, Globals.getApp().getSourceFactory().getById(Source.TYPE_MEMORY));
  }
  
  public void addString(String name){
    StringListItem strItem = (StringListItem) newEmptyItem();
    strItem.setString(name);
    add(strItem);
  }
  
  public FPanel newSelectionPanel(){
    return StringListItem.newBrowsePanel(this, 0);
  }

  public FPanel newSelectionPanel(int viewID){
    return StringListItem.newBrowsePanel(this, viewID);
  }

  public void addSelectionListener(FPropertyListener propListener){
    FObject selectionProperty = getSelectionProperty();
    selectionProperty.addListener(propListener);
  }
}
