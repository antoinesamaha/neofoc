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
package com.foc.admin;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.gui.table.view.ViewConfigDesc;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class GrpViewRightsDesc extends FocDesc{

  public static final int FLD_VIEW_KEY     = 1;
  public static final int FLD_VIEW_CONTEXT = 2;
  public static final int FLD_VIEW_RIGHT   = 3;
  public static final int FLD_GROUP        = 4;
  public static final int FLD_VIEW_CONFIG  = 5;
  
  public static final int ALLOW_CREATION    = 0;
  public static final int ALLOW_SELECTION   = 1;
  public static final int ALLOW_NOTHING     = 2;
  
  public static final String DB_TABLE_NAME = "GROUP_VIEW_RIGHTS";
  
  public GrpViewRightsDesc() {
    super(GrpViewRights.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    
    FField fField = addReferenceField();
    
    fField = new FStringField("VIEW_KEY", "View Key", FLD_VIEW_KEY, false, 20);
    addField(fField);

    fField = new FStringField("VIEW_CONTEXT", "View Context", FLD_VIEW_CONTEXT, false, 50);
    addField(fField);
    
    FMultipleChoiceField fMultipleField = new FMultipleChoiceField("ALLOW", "Allow", FLD_VIEW_RIGHT, false, 2);
    fMultipleField.addChoice(ALLOW_CREATION , "Creation");
    fMultipleField.addChoice(ALLOW_SELECTION, "Selection");
    fMultipleField.addChoice(ALLOW_NOTHING  , "Nothing");
    addField(fMultipleField);
    fMultipleField.addListener(new FPropertyListener(){
			@Override
			public void dispose() {
			}

			@Override
			public void propertyModified(FProperty property) {
				GrpViewRights grpViewRights = (GrpViewRights) property.getFocObject();
				if(grpViewRights != null){
					grpViewRights.adjustPropertyLock();
				}
			}
    });
    
    FObjectField objectField = new FObjectField("GROUP", "Group", FLD_GROUP, false, FocGroupDesc.getInstance(), "FOC_GROUP_", this, FocGroupDesc.FLD_VIEWS_RIGHTS_LIST);
    objectField.setDisplayField(FocGroupDesc.FLD_NAME);
    objectField.setComboBoxCellEditor(FocGroupDesc.FLD_NAME);
    objectField.setSelectionList(FocGroup.getList(FocList.NONE));
    objectField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(objectField);

    objectField = new FObjectField("VIEW_CONFIG", "View", FLD_VIEW_CONFIG, false, ViewConfigDesc.getInstance(), "VIEW_CONFIG_");
    objectField.setDisplayField(ViewConfigDesc.FLD_CODE);
    objectField.setComboBoxCellEditor(ViewConfigDesc.FLD_CODE);
    //objectField.setWithList(true);
    //objectField.setSelectionList(ViewConfigDesc.getList(FocList.NONE));
    objectField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(objectField);
  }
  

	@Override
	protected void afterConstruction() {
		super.afterConstruction();
		FObjectField fld = (FObjectField) getFieldByID(FLD_VIEW_CONFIG);
		fld.setSelectionList(ViewConfigDesc.getList());
	}
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static FocList getList(int mode){
  	return getInstance().getFocList(mode);
  }
  
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    if(list.getListOrder() == null){
      list.setListOrder(new FocListOrder(FLD_VIEW_KEY, FLD_VIEW_CONTEXT));
    }
    return list;
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
  	return getInstance(DB_TABLE_NAME, GrpViewRightsDesc.class); 
  }
}
