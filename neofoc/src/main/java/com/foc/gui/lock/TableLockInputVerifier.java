/*
 * Created on Sep 12, 2005
 */
package com.foc.gui.lock;

import javax.swing.InputVerifier;
import javax.swing.JComponent;

import com.foc.*;

/**
 * @author 01Barmaja
 */
public class TableLockInputVerifier extends InputVerifier{

  public TableLockInputVerifier(){
    
  }
  
  /* (non-Javadoc)
   * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
   */
  public boolean verify(JComponent input) {
    
    boolean verify = true;
 
    /*Transported inside the shouldLockFocus itself
    if(Globals.getDisplayManager().shouldLockFocus(false)){
      Globals.getDisplayManager().stopEditingLockFocus();
    }
    */
    verify = !Globals.getDisplayManager().shouldLockFocus(true);
    
    /*
    FocusLock focusLock = Globals.getDisplayManager().getFocusLock();
    if(focusLock != null){
      //If we are editing and the field in mandatory, when trying to change the focus it would be good to stop editing first so that the data is validated
      //But in case of a combo box selection on the line, the stop editing would forbid the drill down of thee list. This is why we only do the stopEditing() if the lock is locking the value 
      
      if(focusLock.shouldHoldFocus(false)) focusLock.stopEditing();
      //focusLock.stopEditing();
      verify = !focusLock.shouldHoldFocus(true);
      Globals.logString("Verify (Lock != NULL)= "+verify);
    }else{
      //verify = false;
      Globals.logString("Verify (Lock = NULL)= "+verify);
    }
    */
    return verify;
  }
}
