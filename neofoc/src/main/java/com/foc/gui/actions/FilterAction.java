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
package com.foc.gui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.foc.gui.FAbstractListPanel;
import com.foc.gui.findObject.FindObjectGuiDetailsPanel_ForFooter;

@SuppressWarnings("serial")
public class FilterAction extends AbstractAction {

  protected FAbstractListPanel abstractListPanel = null;
  
  public FilterAction( FAbstractListPanel abstractListPanel ){
    this.abstractListPanel = abstractListPanel;
  }
  
  public void dispose(){
    abstractListPanel = null;
  }
  
  public void actionPerformed(ActionEvent e) {
  	FindObjectGuiDetailsPanel_ForFooter footerFilterPanel = abstractListPanel.getFilterExpressionPanel();
  	footerFilterPanel.requestFocusForExpression();  	
  	
  	/*
    FindObject findObject = FindObject.getFindObject();
    if( findObject != null ){
      FPanel findObjectPanel = new FindObjectGuiDetailsPanel(findObject, FocObject.DEFAULT_VIEW_ID, abstractListPanel);
      if( findObjectPanel != null ){
        Globals.getDisplayManager().popupDialog(findObjectPanel, findObjectPanel.getFrameTitle(), true);  
      }  
    }
    */
  }
}
