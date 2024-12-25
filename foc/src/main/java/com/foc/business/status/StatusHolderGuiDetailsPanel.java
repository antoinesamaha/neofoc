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
package com.foc.business.status;

import java.awt.Component;
import java.awt.GridBagConstraints;

import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.gui.StaticComponent;

@SuppressWarnings("serial")
public class StatusHolderGuiDetailsPanel extends FPanel{
  
  private FocObject focObject = null;
    
	public StatusHolderGuiDetailsPanel(FocObject focObj, int view, boolean editable){
		super("", FPanel.FILL_NONE);

    this.focObject = focObj;
    Component comp = null;

    int y = 0;
    
    //add(statusButtonsPanel, 1, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);
    FPanel datesPanel = new FPanel();
    add(datesPanel, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);

    new StatusFieldPanel(focObj, view, editable, false, datesPanel, 0, y++);
    comp = datesPanel.add(focObject, getIStatusHolderDesc().getFLD_CREATION_USER(), 0, y++);
    comp.setEnabled(false);
    comp = datesPanel.add(focObject, getIStatusHolderDesc().getFLD_CREATION_DATE(), 0, y++);
    comp.setEnabled(false);
    comp = datesPanel.add(focObject, getIStatusHolderDesc().getFLD_VALIDATION_DATE(), 0, y++);
    comp.setEnabled(false);
    comp = datesPanel.add(focObject, getIStatusHolderDesc().getFLD_CLOSURE_DATE(), 0, y++);
    comp.setEnabled(false);
    
    if(!editable){
      StaticComponent.setAllComponentsEnable(this, false, true);
    }
  }
  
  public void dispose(){
    super.dispose();
    focObject = null;
  }
    
  public IStatusHolderDesc getIStatusHolderDesc(){
  	return (IStatusHolderDesc)focObject.getThisFocDesc();
  }

  public IStatusHolder getIStatusHolder(){
  	return (IStatusHolder)focObject;
  }

  public StatusHolder getStatusHolder(){
  	return getIStatusHolder().getStatusHolder();
  }
  
  public FocObject getFocObject(){
  	return focObject;
  }
}
