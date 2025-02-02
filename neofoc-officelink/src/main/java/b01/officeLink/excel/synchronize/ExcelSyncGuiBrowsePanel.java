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
package b01.officeLink.excel.synchronize;

import java.awt.event.ActionEvent;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.dataModelTree.DataModelNodeList;
import com.foc.desc.field.FField;
import com.foc.event.FValidationListener;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FGPopupMenuItem;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class ExcelSyncGuiBrowsePanel extends FListPanel {
  
	private DataModelNodeList  dataModelNodelist    = null;
	private ExcelSync          selectedImportConfig = null;
	private FocList            targetList           = null;
	private boolean            ownerOfFocList       = true;//Always owner
	
	private FAbstractListPanel abstractListPanel    = null;
	
	public ExcelSyncGuiBrowsePanel(FAbstractListPanel abstractListPanel, FocList targetList, int viewID){
    super("Excel Synchronization Models", FPanel.FILL_BOTH);
    setWithScroll(false);
    
    this.targetList        = targetList;
    this.abstractListPanel = abstractListPanel;
    
    FocList list = null;
    FocDesc desc = ExcelSyncDesc.getInstance();

    if(desc != null && targetList != null){
    	list = ExcelSyncDesc.newList(targetList.getFocDesc().getStorageName());
    	list.loadIfNotLoadedFromDB();
     	setFocList(list);
      
      FTableView tableView = getTableView();   
      tableView.addColumn(desc, FField.FLD_NAME       , "Model Name", false);
      tableView.addColumn(desc, ExcelSyncDesc.FLD_FILE, "File"      , false);
      
      construct();
      tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
      
      showAddButton(true);
      showRemoveButton(true);
      showEditButton(true);
      showDuplicateButton(false);
      requestFocusOnCurrentItem();
      
      getTable().getPopupMenu().add(new FGPopupMenuItem("Synchronize", this) {
				@Override
				public void actionPerformed(ActionEvent e) {
					ExcelSync config = (ExcelSync) getSelectedObject();
					if(config != null){
						boolean backupRefresh = false;
						if(getAbstractListPanel() != null && getAbstractListPanel().getTableModel() != null){
							backupRefresh = getAbstractListPanel().getTableModel().isSuspendGuiRefresh();
							getAbstractListPanel().getTableModel().setSuspendGuiRefresh(true);
						}
						
						config.resetExcelFile();
						config.importFile(getTargetFocList());
						
						if(getAbstractListPanel() != null && getAbstractListPanel().getTableModel() != null){
							getAbstractListPanel().getTableModel().setSuspendGuiRefresh(backupRefresh);
						}

						/*
						BkdnTree tree = getWBS().getBkdnTree();
						((FAbstractTableModel) getTableModel()).setSuspendGuiRefresh(true);
				    config.importFile(tree, tree.getRoot());
				    ((FAbstractTableModel) getTableModel()).setSuspendGuiRefresh(false);
		    		getWBS().validate(true);
		    		*/
					}
				}
			});
    }

    setFill(FPanel.FILL_VERTICAL);
    
    /*
    FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(this, FocObject.DEFAULT_VIEW_ID);
    add(currentItemPanel, 2, 1);
    */
    
    FValidationPanel savePanel = showValidationPanel(true);
    if(savePanel != null){
      savePanel.addSubject(list);
      savePanel.setValidationListener(new FValidationListener(){
				@Override
				public void postCancelation(FValidationPanel panel) {
				}

				@Override
				public void postValidation(FValidationPanel panel) {
				}

				@Override
				public boolean proceedCancelation(FValidationPanel panel) {
					setSelectedImportConfig(null);
					return true;
				}

				@Override
				public boolean proceedValidation(FValidationPanel panel) {
					ExcelSync config = (ExcelSync) getSelectedObject();
					setSelectedImportConfig(config);
					return true;
				}
      });
    }
  }
	
	public void dispose(){
		if(ownerOfFocList && getFocList() != null){
			getFocList().dispose();
		}
		super.dispose();
		if(dataModelNodelist != null)	dataModelNodelist.dispose();
		dataModelNodelist = null;
		targetList = null;
		abstractListPanel = null;
	}
	
	public FocList getTargetFocList(){
		return targetList;
	}

	public FocDesc getTargetFocDesc(){
		return getTargetFocList() != null ? getTargetFocList().getFocDesc() : null;
	}

	public DataModelNodeList getDataModelNodeList(){
		if(dataModelNodelist == null){
			dataModelNodelist = new DataModelNodeList(getFocDesc(), 5);
		}
		return dataModelNodelist;
	}

	public ExcelSync getSelectedImportConfig() {
		return selectedImportConfig;
	}

	public void setSelectedImportConfig(ExcelSync selectedImportConfig) {
		this.selectedImportConfig = selectedImportConfig;
	}

	@Override
	protected FocObject newListItem() {
		ExcelSync sync = (ExcelSync) super.newListItem();
		sync.setStorageDesc(getTargetFocDesc());
		return sync;
	}

	public FAbstractListPanel getAbstractListPanel() {
		return abstractListPanel;
	}
}
