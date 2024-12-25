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
package com.foc.business.country.region;

import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class RegionGuiBrowsePanel extends FListPanel{
  
  public RegionGuiBrowsePanel(FocList list, int viewID){
    setFrameTitle("Region");
    setMainPanelSising(FPanel.MAIN_PANEL_FILL_VERTICAL);
    FocDesc desc = RegionDesc.getInstance();
    if(desc != null){
      if(list == null){
        list = RegionDesc.getList(FocList.FORCE_RELOAD);
      }
      if(list != null){
        setFocList(list);

        FTableView tableView = getTableView();       
        tableView.addColumn(desc, RegionDesc.FLD_REGION_NAME, false);
        //tableView.setDetailPanelViewID(viewID);
        
        construct();
        requestFocusOnCurrentItem();
        //FValidationPanel savePanel = showValidationPanel(true);
        //savePanel.addSubject(list);
        //setMainPanelSising(FPanel.MAIN_PANEL_FILL_VERTICAL);
        setFill(FPanel.FILL_VERTICAL);
                
        showEditButton(false);
        //tableView.setEditAfterInsertion(true);
      }
    }
  }
}
