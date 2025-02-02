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

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.menu.FMenuAction;
import com.foc.menu.FMenuItem;
import com.foc.menu.FMenuList;

public class Dimension extends FocObject {
	
	public Dimension(FocConstructor constr){
    super(constr);
 	  newFocProperties();
  }
	
	public void setName(String name){
		setPropertyString(DimensionDesc.FLD_NAME, name);
	}

	public String getName(){
		return getPropertyString(DimensionDesc.FLD_NAME);
	}

  public static Dimension getDimension(String type){
    FocList dimensionList = DimensionDesc.getList(FocList.LOAD_IF_NEEDED);
    return (Dimension)dimensionList.searchByPropertyStringValue(DimensionDesc.FLD_NAME, type);
  }
  
  public FocList getUnitList(int mode){
    FocList unitList = getPropertyList(DimensionDesc.FLD_UNIT_LIST);
    unitList.setDirectlyEditable(true);
    unitList.setDirectImpactOnDatabase(false);
    if(mode == FocList.LOAD_IF_NEEDED){
      unitList.loadIfNotLoadedFromDB();  
    }else if(mode == FocList.FORCE_RELOAD){
      unitList.reloadFromDB();
    }
		return unitList;
	}
  
  public Unit pushUnit(String name, String symbol, double factor){
  	FocList list = getUnitList(FocList.LOAD_IF_NEEDED);
  	Unit    unit = (Unit) list.searchByPropertyStringValue(UnitDesc.FLD_NAME, name);
  	if(unit == null) unit = (Unit) list.searchByPropertyStringValue(UnitDesc.FLD_SYMBOL, symbol);
  	if(unit == null){
  		unit = (Unit) list.newEmptyItem();
  		unit.setName(name);
  		unit.setSymbol(symbol);
  		unit.setFactor(factor);
  		list.add(unit);
  		list.validate(true);
  	}
    return unit;
  }
  
  public static void declareMenus(FMenuList fatherMenu){
  	FMenuItem menuItem = new FMenuItem("Units & Dimensions", 'D', new FMenuAction(DimensionDesc.getInstance(), true)); 
    fatherMenu.addMenu(menuItem);
  }
  
  public boolean isTime(){
  	return getName().equals(DimensionDesc.DIMENSION_TIME);
  }
  
  public boolean isWeight(){
  	return getName().equals(DimensionDesc.DIMENSION_WEIGHT);
  }
}
