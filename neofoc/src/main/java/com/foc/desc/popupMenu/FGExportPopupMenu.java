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
package com.foc.desc.popupMenu;

import java.awt.event.ActionEvent;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.gui.FAbstractListPanel;

@SuppressWarnings("serial")
public class FGExportPopupMenu extends FGAbstractImportExportPopupMenu {

	public FGExportPopupMenu(FAbstractListPanel listPanel) {
		super("Export", Globals.getApp().getFocIcons().getExportIcon(), listPanel);
	}

	public FocDesc getFocDesc(){
		return getListPanel().getFocList().getFocDesc();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

}
