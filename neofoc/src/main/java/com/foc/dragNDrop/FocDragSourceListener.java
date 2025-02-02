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
package com.foc.dragNDrop;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import com.foc.Globals;


public class FocDragSourceListener implements DragSourceListener {

  public void dragDropEnd(DragSourceDropEvent dsde) {
    Transferable transferable = dsde.getDragSourceContext().getTransferable();
    try{
      FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());
      focTransferable.dispose();
    }catch(Exception e){
      Globals.logException(e);
    }
  }

  public void dragEnter(DragSourceDragEvent dsde) {
    boolean allowDrop = true;
    DragSourceContext context =  dsde.getDragSourceContext();
    Transferable transferable = context.getTransferable();
    try{
      FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());
      allowDrop = focTransferable.isAllowDropInCurrentComponent();
    }catch(Exception e){
      Globals.logException(e);
    }
    if(allowDrop){
      context.setCursor(DragSource.DefaultCopyDrop);
    }else{
      context.setCursor(DragSource.DefaultCopyNoDrop);
    }
  }

  public void dragExit(DragSourceEvent dse) {
  }

  public void dragOver(DragSourceDragEvent dsde) {
  }

  public void dropActionChanged(DragSourceDragEvent dsde) {
  }

}
