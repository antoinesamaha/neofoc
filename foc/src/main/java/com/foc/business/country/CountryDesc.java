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
package com.foc.business.country;

import java.util.Locale;

import com.foc.desc.AutoPopulatable;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class CountryDesc extends FocDesc implements AutoPopulatable, CountryConst {
  
  public static final String DB_TABLE_NAME = "COUNTRY";
  
  public CountryDesc() {
    super(Country.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    setGuiBrowsePanelClass(CountryGuiBrowsePanel.class);
    setGuiDetailsPanelClass(CountryGuiDetailsPanel.class);
    FField focFld = addReferenceField();
    
    focFld = new FStringField("COUNTRY", "Country", FLD_COUNTRY_NAME, false, 30);
    focFld.setMandatory(true);
    focFld.setLockValueAfterCreation(true);
    addField(focFld);
  }

  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_COUNTRY_NAME);
      list.setListOrder(order);
    }
    return list;
  }
  
  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, CountryDesc.class);    
  }

	public String getAutoPopulatableTitle() {
		return "Countries";
	}

	public boolean populate() {
		FocList list = getList(FocList.FORCE_RELOAD);
		
		for(int i=0; i<Locale.getAvailableLocales().length; i++){
			Locale l = Locale.getAvailableLocales()[i];
			String   countryDisplay = l.getDisplayCountry();
			if(countryDisplay != null && !countryDisplay.isEmpty() && !countryDisplay.startsWith("Isra")){
				Country lang = (Country) list.searchByPropertyStringValue(FLD_COUNTRY_NAME, countryDisplay);
				if(lang == null){
					lang = (Country) list.newEmptyItem();
					lang.setCountryName(countryDisplay);
				}
			}
		}
		
		list.validate(true);
		return false;
	}
}
