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
package com.fab.model.table;

import java.awt.GridBagConstraints;
import java.util.Iterator;

import com.fab.FocApplicationBuilder;
import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.event.FocEvent;
import com.foc.event.FocListener;
import com.foc.gui.FGCurrentItemPanel;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FieldDefinitionGuiBrowsePanel extends FListPanel {
	private FocList fieldDefinitionList = null;
	
	public FieldDefinitionGuiBrowsePanel(FocList list, int viewID){
		this(list, viewID, true);
	}
	
	public FieldDefinitionGuiBrowsePanel(FocList list, int viewID, boolean withDictionaryGroups){
		super("Fields defintion", FPanel.FILL_BOTH);
		boolean allowEdit = viewID != FocApplicationBuilder.VIEW_NO_EDIT;
		FocDesc desc = list != null ? list.getFocDesc() : FieldDefinitionDesc.getInstance();

    if (desc != null) {
      if(list == null){
      	list = FieldDefinitionDesc.getList(FocList.FORCE_RELOAD);
      }else{
      	list.loadIfNotLoadedFromDB();
      }
      if (list != null) {
      	this.fieldDefinitionList = list;
      	try{
      		setFocList(list);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
        FTableView tableView = getTableView();       
        tableView.setDetailPanelViewID(FieldDefinitionGuiDetailsPanel.VIEW_STAND_ALONE);
        
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_ID, 10, allowEdit);
        if(withDictionaryGroups){
        	tableView.addColumn(desc, FieldDefinitionDesc.FLD_DICTIONARY_GROUP, 20, allowEdit);
        }
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_NAME, 30, allowEdit);
        tableView.addColumn(desc, FField.FLD_FAB_OWNER, false);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_DB_RESIDENT, 30, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_TITLE, 30, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_PREDEFINED_FIELD, 20, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_SQL_TYPE, 20, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_IS_KEY, 10, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_LENGTH, 10, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_DECIMALS, 10, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_WITH_INHERITANCE, 6, allowEdit);
        tableView.addColumn(desc, FieldDefinitionDesc.FLD_DEFAULT_VALUE, 6, allowEdit);
        
        addAdditionalColumns(allowEdit);
        
        construct();
        
        addFilterExpressionPanel();
        
        FGCurrentItemPanel currPanel = new FGCurrentItemPanel(this, viewID); 
        add(currPanel, 0, 3, 1, 1, 0.1, 0.1, GridBagConstraints.SOUTH, GridBagConstraints.NONE);
        
        requestFocusOnCurrentItem();
        showEditButton(allowEdit);
        showDuplicateButton(false);
        showAddButton(allowEdit);
        showRemoveButton(allowEdit);
        if(allowEdit){
        	plugListenerToFieldDefinitionList();
        	plugListenersToAllFieldDefinition();
        }
      }
    }
	}
	
	public void dispose(){
		unplugListenersToAllFieldDefinition();
		if(this.fieldDefinitionList != null){
			if(fieldDefinitionListListener != null){
				fieldDefinitionList.removeFocListener(fieldDefinitionListListener);
			}
			this.fieldDefinitionList = null;
		}
		super.dispose();
	}	
	
	private FocListener fieldDefinitionListListener = null;
	private FocListener getFieldDefinitionListListener(){
		if(fieldDefinitionListListener == null){
			fieldDefinitionListListener = new FocListener(){

				public void focActionPerformed(FocEvent evt) {
					if(evt.getID() == FocEvent.ID_ITEM_ADD){
						FieldDefinition definition = (FieldDefinition) evt.getEventSubject();
						if(definition != null){
							definition.plugListenersToFieldDefinition();
						}
					}
				}
				
				public void dispose() {
				}
			};
		}
		return fieldDefinitionListListener;
	}
	
	@SuppressWarnings("unchecked")
	private void unplugListenersToAllFieldDefinition(){
		/*if(fieldDefinitionList != null){
			Iterator<FieldDefinition> iter = fieldDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext()){
				FieldDefinition definition = iter.next();
				if(definition != null){
					unplugListenersFromFocObject(definition);
				}
			}
		}*/
		plugUnplugListenersToAllFieldDefinition(false);
	}
	
	private void plugListenersToAllFieldDefinition(){
		plugUnplugListenersToAllFieldDefinition(true);
	}
	
	@SuppressWarnings("unchecked")
	private void plugUnplugListenersToAllFieldDefinition(boolean plug){
		if(fieldDefinitionList != null){
			Iterator<FieldDefinition> iter = fieldDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext()){
				FieldDefinition definition = iter.next();
				if(definition != null){
					if(plug){
						definition.plugListenersToFieldDefinition();
					}else{
						definition.unplugListenersFromFocObject();
					}
				}
			}
		}
	}
	
	private void plugListenerToFieldDefinitionList(){
		if(fieldDefinitionList != null){
			//fieldDefinitionList.removeFocListener(getFieldDefinitionListListener());
			fieldDefinitionList.addFocListener(getFieldDefinitionListListener());
		}
	}
	
	protected void addAdditionalColumns(boolean allowEdit){
		
	}
}
