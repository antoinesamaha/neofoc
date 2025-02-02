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

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;

import com.foc.Globals;

public class FocDefaultDropTargetListener extends FocDropTargetListener implements KeyEventDispatcher {
	private boolean rPressed = false;
	private boolean iPressed = false;
  
	public void dispose(){
		super.dispose();
		setKeyboardListener(false);
	}

	public void setKeyboardListener(boolean listen){
		if(listen){
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
		}else{
			KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
		}
	}
	
	public boolean isRKeyPressed(){
  	return rPressed;
  }

  public boolean isIKeyPressed(){
  	return iPressed;
  }

	public void drop(DropTargetDropEvent dtde) {
    try{
	    Transferable transferable = dtde.getTransferable();
	    FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());
	    drop(focTransferable, dtde);
    }catch(Exception e){
    	Globals.logException(e);
    }
  }
  
  public void drop(FocTransferable focTransferable, DropTargetDropEvent dtde){
    boolean accepted = false;
    FocDropable targetComponent = (FocDropable)dtde.getDropTargetContext().getComponent();
    /*Component component = (Component)targetComponent;
    Point point = component.getMousePosition();*/
    try{
	    fillDropInfo(focTransferable, dtde);
	    if(shouldExecuteDrop(focTransferable, dtde)){
	    	accepted = targetComponent.drop(focTransferable, dtde);
	    }
	    if(!accepted){
	      dtde.rejectDrop();
	    }
    }catch(Exception e){
    	Globals.logException(e);
    }
  }
  
	@Override
	public void fillDropInfo(FocTransferable focTransferable, DropTargetDropEvent dtde){
		FocDropable targetComponent = (FocDropable)dtde.getDropTargetContext().getComponent();
		targetComponent.fillDropInfo(focTransferable, dtde);
		if(focTransferable != null){
	    Component component = (Component)targetComponent;
	    Point point = component.getMousePosition();
	    if(point != null){
		    focTransferable.setTargetX(point.x);
		    focTransferable.setTargetY(point.y);
	    }
		}
	}
	
	@Override
	public boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
		FocDropable targetComponent = (FocDropable)dtde.getDropTargetContext().getComponent();
		return targetComponent.shouldExecuteDrop(focTransferable, dtde);
	}
  
	public boolean executeDrop(FocTransferable focTransferable, DropTargetDropEvent dtde){
		FocDropable targetComponent = (FocDropable)dtde.getDropTargetContext().getComponent();
		return targetComponent.drop(focTransferable, dtde);
	}
	
  private static FocDefaultDropTargetListener focDefaultDropTargetListener = null;
  public static FocDefaultDropTargetListener getInstance(){
    if(focDefaultDropTargetListener == null){
      focDefaultDropTargetListener = new FocDefaultDropTargetListener();
    }
    return focDefaultDropTargetListener;
  }

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_R){
			if(e.getID() == KeyEvent.KEY_PRESSED){
				rPressed = true;
			}else if(e.getID() == KeyEvent.KEY_RELEASED){
				rPressed = false;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_I){
			if(e.getID() == KeyEvent.KEY_PRESSED){
				iPressed = true;
			}else if(e.getID() == KeyEvent.KEY_RELEASED){
				iPressed = false;
			}
		}
		
		return false;
	}
}
