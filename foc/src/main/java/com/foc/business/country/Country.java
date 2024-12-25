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

import com.foc.business.country.region.RegionDesc;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

@SuppressWarnings("serial")
public class Country extends FocObject implements CountryConst {

  public Country(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  public String getCountryName(){
  	return getPropertyString(FLD_COUNTRY_NAME);
  }

  public void setCountryName(String name){
  	setPropertyString(FLD_COUNTRY_NAME, name);
  }

  @Override
	public FocList getListPropertyInitialLoadedList(int fieldID) {
  	FocList list = super.getListPropertyInitialLoadedList(fieldID);
  	if(list == null && fieldID == FLD_REGION_LIST){
  		list = RegionDesc.getList(FocList.LOAD_IF_NEEDED);
  	}
		return list;
	}

	public FocList getRegionList(){
  	FocList list = getPropertyList(FLD_REGION_LIST);
  	if(list != null && list.getListOrder() == null){
  		list.setDirectlyEditable(false);
  		list.setDirectImpactOnDatabase(true);
  		list.setListOrder(new FocListOrder(FLD_REGION_NAME));
  	}
  	return list;
  }
}
