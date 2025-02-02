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
package com.foc.gui.table.cellControler.renderer.gantChartActivityRenderer;

import java.awt.Color;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

public class GanttStyle extends FocObject{

  public GanttStyle(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public int getBarPositions(){
  	return getPropertyMultiChoice(GanttStyleDesc.FLD_BAR_POSITIONS);
  }
  
  public int getFirstBar(){
  	return getPropertyMultiChoice(GanttStyleDesc.FLD_FIRST_BAR);
  }

  public int getFirstBarFiller(){
  	return getPropertyMultiChoice(GanttStyleDesc.FLD_FIRST_BAR_FILLER);
  }

  public Color getFirstBarColor(){
  	return getPropertyColor(GanttStyleDesc.FLD_FIRST_BAR_COLOR);
  }

  public Color getFirstBarFillerColor(){
  	return getPropertyColor(GanttStyleDesc.FLD_FIRST_BAR_FILLER_COLOR);
  }
  
  public int getSecondBar(){
  	return getPropertyMultiChoice(GanttStyleDesc.FLD_SECOND_BAR);
  }

  public int getSecondBarFiller(){
  	return getPropertyMultiChoice(GanttStyleDesc.FLD_SECOND_BAR_FILLER);
  }

  public Color getSecondBarColor(){
  	return getPropertyColor(GanttStyleDesc.FLD_SECOND_BAR_COLOR);
  }

  public Color getSecondBarFillerColor(){
  	return getPropertyColor(GanttStyleDesc.FLD_SECOND_BAR_FILLER_COLOR);
  }

  public int getRelationshipMode(){
  	return getPropertyMultiChoice(GanttStyleDesc.FLD_RELATIONSHIP_MODE);
  }

  public Color getRelationshipColor(){
  	return getPropertyColor(GanttStyleDesc.FLD_RELATIONSHIP_COLOR);
  }
}
