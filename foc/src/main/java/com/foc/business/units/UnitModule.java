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

import com.foc.desc.FocModule;
import com.foc.menu.FMenuAction;
import com.foc.menu.FMenuItem;
import com.foc.menu.FMenuList;

public class UnitModule extends FocModule {

  public static FMenuItem addDimensionMenu(FMenuList list){
		FMenuItem dimensionItem = new FMenuItem("Dimension", 'D', new FMenuAction(DimensionDesc.getInstance(), true)); 
    list.addMenu(dimensionItem);
    return dimensionItem;
	}
  
  public static UnitModule module = null;
  public static UnitModule getInstance(){
  	if(module == null){
  		module = new UnitModule();
  	}
  	return module;
  }
  
	@Override
	public void declareFocObjectsOnce() {
		declareFocDescClass(DimensionDesc.class);
		declareFocDescClass(UnitDesc.class);
	}
}
