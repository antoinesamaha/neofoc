/*
 * Created on Oct 14, 2004
 */
package com.foc.list;

import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FocListElement implements Comparable {
  private FocObject focObject  = null;
  private FBoolean  selected   = null;
  private boolean   hide_Soft  = false;
  private boolean   hide_Hard  = false;

  /**
   * @param focObject
   * @param selected
   */
  public FocListElement(FocObject focObject, boolean selected) {
    this.focObject = focObject;
    this.selected = new FBoolean(null, FField.SELECTION_FIELD_ID, selected);
    this.selected.setFocObject(focObject);
  }

  public void dispose(){
    disposeLeaveFocObject();
    if(focObject != null){
      focObject.dispose();
      focObject = null;
    }
    if(selected != null){
    	selected.dispose();
    }
  }
  
  public void dispose(boolean isInCollectionBehaviorList ){
    if(isInCollectionBehaviorList){
      disposeLeaveFocObject();      
    }else{
      dispose();
    }          
  }
  
  public void disposeLeaveFocObject(){
    if(selected != null){
      selected.dispose();
      selected= null;      
    }
  }
  
  /**
   * @return
   */
  public FocObject getFocObject() {
    return focObject;
  }

  /**
   * @return
   */
  public boolean isSelected() {
    return selected.getBoolean();
  }

  /**
   * @param b
   */
  public void setSelected(boolean b) {
    selected.setBoolean(b);
  }
  
  /**
   * @return
   */
  public FBoolean getSelectedProperty() {
    return selected;
  }

  /**
   * @param object
   */
  public void setFocObject(FocObject object) {
    focObject = object;
  }

  public int compareTo(Object object) {
    FocListElement otherElement = (FocListElement) object;
    int compRes = 0;
    FocObject otherFocObj = null;
    boolean error = otherElement == null || this.focObject == null;

    if (!error) {
      otherFocObj = otherElement.getFocObject();
      error = otherFocObj == null;
    }

    if (!error) {
      compRes = focObject.compareTo(otherFocObj);
    }

    return compRes;
  }
  
  public boolean isHide() {
    return hide_Soft || hide_Hard;
  }
  
  public void setHide_Soft(boolean hide) {
    this.hide_Soft = hide;
  }
  
  public void setHide_Hard(boolean hide) {
    this.hide_Hard = hide;
  }
}
