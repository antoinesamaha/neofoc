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
package com.foc.gui;

import javax.swing.SwingUtilities;

import com.foc.desc.FocObject;

public class EditAfterCreationData {
	private FAbstractListPanel listPanel       = null ;
	private FocObject          focObject       = null ;
	private boolean            disposeAfterRun = false;
	
	public EditAfterCreationData(FAbstractListPanel listPanel, FocObject focObject){
		this.listPanel = listPanel;
		this.focObject = focObject;
	}
	
	public void dispose(){
		if(!disposeAfterRun){
			dispose_Internal();
		}
	}
	
	public void dispose_Internal(){
		listPanel = null;
		focObject = null;
	}
	
	public void editCreatedItem(){
		disposeAfterRun = true;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				if(focObject.hasRealReference()){//This condition is to disable edit popup if the user calcels the creation
					listPanel.setSelectedObject(focObject);
					listPanel.editCurrentItem(focObject);
				}
				dispose_Internal();
			}
		});
	}
}
