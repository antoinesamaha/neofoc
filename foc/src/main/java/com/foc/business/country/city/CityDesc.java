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
package com.foc.business.country.city;

import com.foc.business.country.CountryConst;
import com.foc.business.country.region.RegionDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class CityDesc extends FocDesc implements CountryConst {
  
  public static final String DB_TABLE_NAME = "COUNTRY_CITY";
  
  public CityDesc() {
    super(City.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    setGuiBrowsePanelClass(CityGuiBrowsePanel.class);
    setGuiDetailsPanelClass(CityGuiDetailsPanel.class);
    FField focFld = addReferenceField();
    
    focFld = new FStringField("CITY", "City/Village", FLD_CITY_NAME, false, 30);
    focFld.setMandatory(true);
    focFld.setLockValueAfterCreation(true);
    addField(focFld);
    
    FObjectField oFld = new FObjectField("REGION", "Region", FLD_REGION, true, RegionDesc.getInstance(), "REGION_", this, FLD_CITY_LIST);
    oFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    oFld.setSelectionList(RegionDesc.getList(FocList.NONE));
    oFld.setComboBoxCellEditor(FLD_REGION_NAME);
    addField(oFld);
  }
  
  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_CITY_NAME);
      list.setListOrder(order);
    }
    return list;
  }
  
  public static City getCityByName(String cityName){
  	City city = null;
  	FocList list = getList(FocList.LOAD_IF_NEEDED);
  	city = (City) list.searchByPropertyStringValue(CityDesc.FLD_CITY_NAME, cityName);
  	return city;
  }

  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, CityDesc.class);    
  }
}
