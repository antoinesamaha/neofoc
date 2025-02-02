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

import javax.swing.Icon;

import com.foc.desc.FocDesc;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.table.FGPopupMenuItem;

@SuppressWarnings("serial")
public abstract class FGAbstractImportExportPopupMenu extends FGPopupMenuItem {

	private boolean                  forTree          = false;
	private AbstractImportExportFile importExportFile = null ;
	
	public FGAbstractImportExportPopupMenu(String text, Icon icon, FAbstractListPanel listPanel) {
		super(text, icon, listPanel);
		setForTree(listPanel instanceof FTreeTablePanel);
	}
	
	public void dispose(){
		super.dispose();
		if(importExportFile != null){
			importExportFile.dispose();
			importExportFile = null;
		}
	}

	public FocDesc getFocDesc(){
		return getListPanel().getFocList().getFocDesc();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	public boolean isForTree() {
		return forTree;
	}

	public void setForTree(boolean forTree) {
		this.forTree = forTree;
	}
}
