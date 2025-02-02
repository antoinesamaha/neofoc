/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
// LOCKABLE DESC LIST
// LOCKABLE DESCRIPTION

package com.foc.db.lock;

import java.util.ArrayList;
import java.util.Iterator;

import com.foc.*;
import com.foc.desc.*;
import com.foc.gui.FGTabbedPane;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

/**
 * @author 01Barmaja
 */
public class LockManager {
  
  ArrayList<LockableDescription> lockableDescriptions = null;
  
  public LockManager(){
    
  }
  
  public void dispose(){
    listOfLockableDescriptions_dispose();
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LOCKABLE DESC LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private void listOfLockableDescriptions_dispose(){
    if(lockableDescriptions != null){
      for(int i=0; i<lockableDescriptions.size(); i++){
        LockableDescription lockDesc = lockableDescriptions.get(i);
        if(lockDesc != null){
          lockDesc.dispose();
        }
      }
      lockableDescriptions.clear();
      lockableDescriptions = null;
    }
  }
  
  private ArrayList listOfLockableDescriptions_getList(){
    if(lockableDescriptions == null){
      lockableDescriptions = new ArrayList<LockableDescription>();
      Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
      while(iter != null && iter.hasNext()){
      	IFocDescDeclaration focDescDecalaration = iter.next();
      	if(focDescDecalaration != null){
      		FocDesc desc = focDescDecalaration.getFocDescription();
      		if(desc != null && desc.isConcurrenceLockEnabled()){
      			lockableDescriptions.add(new LockableDescription(desc));
      		}
      	}
      }
    }
    return lockableDescriptions;
  }
  
  public int listOfLockableDescriptions_Size(){
    ArrayList list = listOfLockableDescriptions_getList();
    return list != null ? list.size() : 0;
  }

  public LockableDescription listOfLockableDescriptions_get(int i){
    ArrayList list = listOfLockableDescriptions_getList();
    return list != null ? (LockableDescription) list.get(i) : null;
  }

  public void unlockAllObjectsForAllDescriptions(){
    for(int i=0; i<listOfLockableDescriptions_Size(); i++){
      LockableDescription lockableDesc = listOfLockableDescriptions_get(i);
      if(lockableDesc != null){
        lockableDesc.unlockAll();
      }
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LOCKABLE RECORD LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public FPanel newDetailsPanel(){
    FPanel panel = new FPanel();
    FGTabbedPane tabbed = new FGTabbedPane();
    panel.add(tabbed, 0, 0);
    
    for(int i=0; i<listOfLockableDescriptions_Size(); i++){
      LockableDescription lockDesc = listOfLockableDescriptions_get(i);
      if(lockDesc != null){
        FPanel descPanel = lockDesc.newRecordBrowsePanel();
        tabbed.add(lockDesc.getTitle(), descPanel);
      }
    }
    
    FValidationPanel validPanel = (FValidationPanel) panel.showValidationPanel(true);
    validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    
    return panel;
  }
}
