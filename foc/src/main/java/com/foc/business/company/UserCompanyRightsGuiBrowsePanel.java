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
package com.foc.business.company;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class UserCompanyRightsGuiBrowsePanel extends FListPanel{

  /**
   * @author 01Barmaja
   */
  
  private UserCompanyRightsDisplayList displayList = null;
  
  public UserCompanyRightsGuiBrowsePanel(FocList realList, int viewID){
    FocDesc desc = UserCompanyRightsDesc.getInstance();
    if(desc != null){
      realList.loadIfNotLoadedFromDB();
      displayList = new UserCompanyRightsDisplayList(realList);
      FocList list = displayList.getDisplayList();
      try {
        setFocList(list);
      } catch (Exception e) {
        Globals.logException(e);
      }
      FTableView tableView = getTableView();       
      tableView.addColumn(desc, UserCompanyRightsDesc.FLD_USER, false);
      tableView.addColumn(desc, UserCompanyRightsDesc.FLD_ACCESS_RIGHT, true);
      
      construct();
      //tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);

      showModificationButtons(false);
      showEditButton(false);
      setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
      setFill(FPanel.FILL_BOTH);
    }
  }
  
  public void updateRealList(){
    displayList.updateRealList(); 
  }
  
  public void dispose(){
    super.dispose();
    if(displayList != null){
      displayList.dispose();
      displayList = null;
    }
  }
}
