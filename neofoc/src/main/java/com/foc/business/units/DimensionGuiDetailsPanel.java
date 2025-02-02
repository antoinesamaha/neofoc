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

import com.foc.desc.FocObject;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class DimensionGuiDetailsPanel extends FPanel {

	public final static int VIEW_UNIT_LIST = 2; 
	
	private Dimension         dimension = null;

  public DimensionGuiDetailsPanel(FocObject d, int view){
  	super("Dimension", view == VIEW_UNIT_LIST ? FILL_VERTICAL : FILL_NONE);  	
  	this.dimension = (Dimension) d;
  	  	
  	if(view == VIEW_UNIT_LIST){
    	FGTextField textField = (FGTextField) add(dimension, DimensionDesc.FLD_NAME, 0, 0);
    	textField.setEnabled(false);
	  	FocList list = dimension.getUnitList(FocList.LOAD_IF_NEEDED);
	  	FPanel panel = new UnitGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID);
	  	add(panel, 0, 1, 2, 1);
  	}else{
    	FGTextField textField = (FGTextField) add(dimension, DimensionDesc.FLD_NAME, 0, 0);
    	textField.setEnabled(true);
  		
  		FValidationPanel validPanel = showValidationPanel(true);
  		validPanel.addSubject(dimension);
  	}
  }
  
  public void dispose(){
  	super.dispose();
 		dimension = null;
  }
}
