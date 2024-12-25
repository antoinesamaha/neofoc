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
package com.foc.business.units;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.list.FocLink;
import com.foc.list.FocLinkForeignKey;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class DimensionDesc extends FocDesc {

	public static final int FLD_NAME      = 1;
	public static final int FLD_UNIT_LIST = 10;

  //public static final String DIMENSION_LABEL_OTHER     = "- not mandatory -";
  public static final String DIMENSION_LABEL_TIME      = "Time"; 
  //public static final String DIMENSION_LABEL_DISTANCE  = "Distance"; 
  //public static final String DIMENSION_LABEL_AREA      = "Area";
  public static final String DIMENSION_LABEL_WEIGHT    = "Weight";
  //public static final String DIMENSION_LABEL_VOLUME    = "Volume";
  public static final String DIMENSION_LABEL_COUNT    = "Count";

  //public static final String DIMENSION_OTHER           = DIMENSION_LABEL_OTHER; 
  public static final String DIMENSION_TIME            = DIMENSION_LABEL_TIME; 
  //public static final String DIMENSION_DISTANCE        = DIMENSION_LABEL_DISTANCE; 
  //public static final String DIMENSION_AREA            = DIMENSION_LABEL_AREA;
  public static final String DIMENSION_WEIGHT          = DIMENSION_LABEL_WEIGHT;
  //public static final String DIMENSION_VOLUME          = DIMENSION_LABEL_VOLUME;
  public static final String DIMENSION_COUNT          = DIMENSION_LABEL_COUNT;
  
  public static final String DB_TABLE_NAME = "DIM";

	public DimensionDesc(){
		super(Dimension.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		setGuiBrowsePanelClass(DimensionGuiBrowsePanel.class);	
		setGuiDetailsPanelClass(DimensionGuiDetailsPanel.class);
		
    FField focFld = addReferenceField();
    
    focFld = new FStringField("NAME", "Dimension", FLD_NAME,  true, 30);    
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
	}
	
	private static FocLink unitLink = null;
	public static FocLink getUnitLink(){
		if(unitLink == null){
			unitLink = new FocLinkForeignKey(UnitDesc.getInstance(), UnitDesc.FLD_DIMENSION, true);
		}
		return unitLink;
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_NAME);
      list.setListOrder(order);
    }
    return list;
  }
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, DimensionDesc.class);    
  }
}
