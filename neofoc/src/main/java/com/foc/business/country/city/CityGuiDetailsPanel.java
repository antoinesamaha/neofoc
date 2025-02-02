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

import com.foc.desc.FocObject;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;

@SuppressWarnings("serial")
public class CityGuiDetailsPanel extends FPanel{

	public CityGuiDetailsPanel(FocObject obj, int viewID){
		setInsets(0, 0, 0, 0);
		FGTextField comp = (FGTextField) obj.getGuiComponent(CityDesc.FLD_CITY_NAME);
		comp.setEditable(false);
		add(comp, 0, 0);
	}
}
