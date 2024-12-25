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
package com.foc.db.migration;

import java.awt.GridBagConstraints;

import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

@SuppressWarnings("serial")
public class MigrationSourceGuiDetailsPanel extends FPanel{

	public final static int VIEW_SELECTION = 2; 
	
	private FPropertyListener sourceTypeListener = null;
	private MigrationSource   migSource          = null;
	
	private FPanel databasePanel  = null;
	private FPanel directoryPanel = null;
	
	public MigrationSourceGuiDetailsPanel(FocObject obj, int viewID){
		migSource = (MigrationSource) obj;
		if(viewID == VIEW_SELECTION){
			setInsets(0, 0, 0, 0);
			FGTextField comp = (FGTextField) obj.getGuiComponent(MigrationSourceDesc.FLD_DESCRIPTION);
			comp.setEditable(false);
			add(comp, 1, 0);
		}else{
			setMainPanelSising(FPanel.FILL_BOTH);
			if(migSource.isCreated()){
				setFrameTitle("New Migration Source");
				init_New();
			}else{
				setFrameTitle("Migration Source : "+migSource.getName());
				init_Edit();
			}
			FValidationPanel vPanel = showValidationPanel(true);
			vPanel.addSubject(migSource);
		}
	}

	private void init_New(){
		int y = 0;
		
		add(migSource, FField.FLD_NAME, 0, y++);
		add(migSource, MigrationSourceDesc.FLD_DESCRIPTION, 0, y++);
		add(migSource, MigrationSourceDesc.FLD_DESTINATION_TABLE, 0, y++);
	}	
	
	private void init_Edit(){
		int y = 0;
		
		add(migSource, FField.FLD_NAME, 0, y++);
		add(migSource, MigrationSourceDesc.FLD_DESCRIPTION, 0, y++);
		add(migSource, MigrationSourceDesc.FLD_DESTINATION_TABLE, 0, y++);
		add(migSource, MigrationSourceDesc.FLD_SOURCE_TYPE, 0, y++);
		
		databasePanel  = new FPanel();
		databasePanel.add(migSource, MigrationSourceDesc.FLD_DATABASE, 0, 0);
		databasePanel.add(migSource, MigrationSourceDesc.FLD_TABLE_NAME, 0, 1);
		add(databasePanel, 0, y, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);

		directoryPanel = new FPanel();
		directoryPanel.add(migSource, MigrationSourceDesc.FLD_DIRECTORY, 0, 0);
		directoryPanel.add(migSource, MigrationSourceDesc.FLD_FILE_NAME, 0, 1);
		add(directoryPanel, 0, y++, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);
   
		sourceTypeListener = new FPropertyListener(){
			public void dispose() {
			}

			public void propertyModified(FProperty property) {
				databasePanel.setVisible(migSource.getSourceType() == MigrationSourceDesc.SOURCE_TYPE_DATABASE_TABLE);
				directoryPanel.setVisible(migSource.getSourceType() == MigrationSourceDesc.SOURCE_TYPE_COMMA_SEPARATED_FILE);
			}
		};
		
		migSource.getFocProperty(MigrationSourceDesc.FLD_SOURCE_TYPE).addListener(sourceTypeListener);
		sourceTypeListener.propertyModified(migSource.getFocProperty(MigrationSourceDesc.FLD_SOURCE_TYPE));
		
		MigFieldMapGuiBrowsePanel fldMapGuiDetails = new MigFieldMapGuiBrowsePanel(migSource.getMapFieldList(), FocObject.DEFAULT_VIEW_ID);
		add(fldMapGuiDetails, 0, y++, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	}
	
	public void dispose(){
		super.dispose();

		if(sourceTypeListener != null && migSource != null){
			migSource.getFocProperty(MigrationSourceDesc.FLD_SOURCE_TYPE).removeListener(sourceTypeListener);
			sourceTypeListener.dispose();
		}		
		migSource          = null;			
		sourceTypeListener = null;
		
		databasePanel  = null;
		directoryPanel = null;
	}
}

