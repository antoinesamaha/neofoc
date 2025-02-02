/*
 * Created on Sep 7, 2005
 */
package com.foc.list;

import java.util.Iterator;

import com.foc.desc.FocObject;
/**
 * @author 01Barmaja
 */
public abstract class SelectionDisplayList extends DisplayList{

  public SelectionDisplayList(FocList focList) {
    super(focList);
    FocList displayList = getDisplayList();
    Iterator iter = displayList.listElementIterator();
    while(iter != null && iter.hasNext()){
    	FocListElement elmt = (FocListElement) iter.next();
    	elmt.setSelected(true);
    }
    //setDoNotRemoveRealItems(true);
  }

  /* (non-Javadoc)
   * @see b01.foc.list.DisplayList#isDisplayItemToBeSaved(b01.foc.desc.FocObject)
   */
  public boolean isDisplayItemToBeSaved(FocObject object) {
  	FocListElement listElement = getDisplayList().getFocListElement(object);
    return listElement.isSelected();
  }
}