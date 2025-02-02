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
package com.foc.business.adrBook;

import com.foc.desc.FocObject;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;

@SuppressWarnings("serial")
public class PositionGuiDetailsPanel extends FPanel{

	public final static int VIEW_SELECTION = 2; 
	
	public PositionGuiDetailsPanel(FocObject obj, int viewID){
		if(viewID == VIEW_SELECTION){
			setInsets(0, 0, 0, 0);
			FGTextField comp = (FGTextField) obj.getGuiComponent(PositionDesc.FLD_NAME);
			comp.setEditable(false);
			add(comp, 1, 0);
		}
	}
}
