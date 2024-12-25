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

import com.foc.business.country.CountryConst;
import com.foc.business.country.city.CityGuiBrowsePanel;
import com.foc.desc.FocObject;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class RegionGuiDetailsPanel extends FPanel implements CountryConst {

	public static final int VIEW_SELECTION = 1;
	
	public RegionGuiDetailsPanel(FocObject obj, int viewID){		
		if(viewID == VIEW_SELECTION){
			setInsets(0, 0, 0, 0);
			FGTextField comp = (FGTextField) obj.getGuiComponent(RegionDesc.FLD_REGION_NAME);
			comp.setEditable(false);
			add(comp, 0, 0);
		}else{
			Region region = (Region) obj;
			add(region, FLD_REGION_NAME, 0, 0);
			if(!region.isCreated()){
				FocList            list   = region.getCityList();
				CityGuiBrowsePanel browse = new CityGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID);
				add(browse, 0, 1, 2, 1);
			}else{
				FValidationPanel vPanel = showValidationPanel(true);
				vPanel.addSubject(obj);
			}
		}
	}
}
