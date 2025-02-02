/*
 * Created on 19-May-2005
 */
package com.foc.menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.foc.Globals;
import com.foc.desc.*;
import com.foc.gui.*;
import com.foc.list.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public abstract class FAbstractMenuAction extends AbstractAction {
  protected FocDesc       focDesc = null;
  protected FocList       list = null;
  protected int           viewID = FocObject.DEFAULT_VIEW_ID;
  protected InternalFrame internalFrame = null;
  protected boolean       uniquePopup = false;

  public abstract FPanel generatePanel();
  
  public FAbstractMenuAction(FocDesc focDesc, boolean uniquePopup) {
    this.focDesc = focDesc;
    this.uniquePopup = uniquePopup;
    list = null;
    viewID = FocObject.DEFAULT_VIEW_ID;
  }

  public FAbstractMenuAction(FocDesc focDesc, FocList list, boolean uniquePopup) {
    this.focDesc = focDesc;
    this.uniquePopup = uniquePopup;
    this.list = list;
    viewID = FocObject.DEFAULT_VIEW_ID;
  }

  public FAbstractMenuAction(FocDesc focDesc, FocList list, int viewID, boolean uniquePopup) {
    this.focDesc = focDesc;
    this.uniquePopup = uniquePopup;
    this.list = list;
    this.viewID = viewID;
  }
  
  public void actionPerformed(ActionEvent evt) {
    try {
      if(!Globals.getDisplayManager().shouldLockFocus(true)){
        boolean restoreSuccesful = false;
        if(uniquePopup && internalFrame != null){
          restoreSuccesful = Globals.getDisplayManager().restoreInternalFrame(internalFrame);
        }
        if(!restoreSuccesful){
          FPanel panel = generatePanel();
          if(panel != null){
	          internalFrame = Globals.getDisplayManager().newInternalFrame(panel);
	          panel.setInternalFrame(internalFrame);
          }
        }
      }      
    } catch (Exception e) {
      Globals.logException(e);
    }
  }

  /**
   * @param viewID
   *          The viewID to set.
   */
  public void setViewID(int viewID) {
    this.viewID = viewID;
  }
}
