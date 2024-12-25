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

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

public abstract class FocDropTargetListener implements DropTargetListener {

	public void dispose(){
	}
	
  public void dragEnter(DropTargetDragEvent dtde) {
  }

  public void dragExit(DropTargetEvent dte) {
  }

  public void dragOver(DropTargetDragEvent dtde) {
  }

  public void dropActionChanged(DropTargetDragEvent dtde) {
  }
  
  public abstract void fillDropInfo(FocTransferable focTransferable, DropTargetDropEvent dtde);
  public abstract boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde);

}
