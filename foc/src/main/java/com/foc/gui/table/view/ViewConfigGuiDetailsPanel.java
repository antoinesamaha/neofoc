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
package com.foc.gui.table.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.gui.FGLabel;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;
import com.foc.property.FProperty;

@SuppressWarnings("serial")
public class ViewConfigGuiDetailsPanel extends FPanel{
	public static final int VIEW_LABEL_ONLY   = 2;
	public static final int VIEW_FULL_EDIT    = 3;
	public static final int VIEW_FULL_NO_EDIT = 4;
	public static final int VIEW_CREATION     = 5;
	
	protected ViewConfig config = null;  
	private   int        view   = VIEW_LABEL_ONLY;

	public ViewConfigGuiDetailsPanel(FocObject obj, int viewID){
		config    = (ViewConfig) obj;
		this.view = viewID;
		if(view == FocObject.DEFAULT_VIEW_ID){
			if(config.isCreated()){
				view = VIEW_LABEL_ONLY;
			}else{
				view = VIEW_FULL_EDIT;
			}
		}
		
		if(view == VIEW_LABEL_ONLY){
			fillLabelOnlyPanel();
		}else if(view == VIEW_CREATION){
			fillCreationPanel();
		}else{
			fillEditPanel();
		}
		/*
		add(config, ViewConfigDesc.FLD_CODE, 0, 0);
		
		FGButton newButton = new FGButton("New");
		newButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				FocList list = (FocList) config.getFatherSubject();
				config = (ViewConfig) list.newEmptyItem();
				FPanel panel = new ViewConfigGuiDetailsPanel(config, FocObject.DEFAULT_VIEW_ID, getTableView());
				Globals.getDisplayManager().goBack();
				Globals.getDisplayManager().popupDialog(panel, "Table View", true);
			}
		});
		add(newButton, 2, 0);
		
		FocList list = config.getColumnsConfigList();
		ColumnsConfigGuiBrowsePanel browse = new ColumnsConfigGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID, tableView);
		add(browse, 0, 1, 3, 1);
		FValidationPanel validPanel = showValidationPanel(true);
		validPanel.addSubject(config);
		*/
	}
	
	public void dispose(){
		super.dispose();
		config    = null;
	}

	public FTableView getTableView() {
		return ((ViewFocList)config.getFatherSubject()).getTableView();
	}
	
	private void fillLabelOnlyPanel(){
		setMainPanelSising(FILL_NONE);
		FProperty   prop      = config.getFocProperty(ViewConfigDesc.FLD_CODE);
		FGTextField textField = (FGTextField) prop.getGuiComponent();
		textField.setEditable(false);
		Font font = Globals.getDisplayManager().getDefaultFont().deriveFont((float)9.0);
		textField.setFont(font);
		textField.setColumns(10);
		add(textField, 0, 0);
	}

	private void fillCreationPanel(){
		setMainPanelSising(FILL_NONE);
		add(config, ViewConfigDesc.FLD_CODE, 0, 0);
		FValidationPanel validPanel = showValidationPanel(true);
		validPanel.addSubject(config);
	}
	
	private void fillEditPanel(){
		setMainPanelSising(FILL_BOTH);
		
		FGLabel label = new FGLabel("Code");
		add(label, 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);

		FGTextField txtField = (FGTextField) config.getGuiComponent(ViewConfigDesc.FLD_CODE);
		Dimension dim = new Dimension(txtField.getMinimumSize());
		dim.width += 300;
		txtField.setMinimumSize(dim);
		add(txtField, 1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);
		if(view == VIEW_FULL_NO_EDIT){
			txtField.setEnabled(false);
		}

		label = new FGLabel(" ");
		add(label, 2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);

		//add(config, ViewConfigDesc.FLD_CODE, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL);
		
		FocList list = config.getColumnsConfigList_Full(getTableView());
		ColumnsConfigGuiBrowsePanel browse = new ColumnsConfigGuiBrowsePanel(list, ColumnsConfigGuiBrowsePanel.VIEW_MAIN, view != VIEW_FULL_NO_EDIT, getTableView());
		add(browse, 0, 3, 3, 1, 0.1, 0.1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
	}
}
