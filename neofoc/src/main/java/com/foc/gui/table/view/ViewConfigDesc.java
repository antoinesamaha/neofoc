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

import com.foc.admin.FocUser;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class ViewConfigDesc extends FocDesc{

  public static final int FLD_CODE             = 1;
  public static final int FLD_USER             = 2;
  public static final int FLD_VIEW_KEY         = 3;
  public static final int FLD_COLUMN_ID_LIST   = 4;
  public static final int FLD_USER_CONFIG_LIST = 5;
  
	public ViewConfigDesc(){
    super(ViewConfig.class, DB_RESIDENT, "VIEW_CONFIG", true);
    setGuiBrowsePanelClass(ViewConfigGuiBrowsePanel.class);
    setGuiDetailsPanelClass(ViewConfigGuiDetailsPanel.class);
    FField focFld = addReferenceField();

    focFld = new FStringField("CODE", "Code", FLD_CODE, true, 20);
    focFld.setMandatory(true);
    //focFld.setLockValueAfterCreation(true);
    addField(focFld);

    focFld = new FObjectField("USER", "User", FLD_USER, false, FocUser.getFocDesc(), "USER_");
		addField(focFld);

		focFld = new FStringField("VIEW_ID", "View id", FLD_VIEW_KEY, true, 40);
		addField(focFld);
  }

	private static FocList list = null; 
	public static FocList getList(){
		if(list == null){
			list = new FocList(new FocLinkSimple(getInstance()));
			list.setListOrder(new FocListOrder(FLD_CODE));
		}
		return list;
	}
	
  private static ViewConfigDesc focDesc = null;
  public static ViewConfigDesc getInstance(){
    if(focDesc == null){
      focDesc = new ViewConfigDesc();
    }
    return focDesc;
  }
}
