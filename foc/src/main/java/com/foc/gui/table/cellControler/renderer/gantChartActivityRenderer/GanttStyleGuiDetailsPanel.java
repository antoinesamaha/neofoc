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

import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class GanttStyleGuiDetailsPanel extends FPanel{

	public GanttStyleGuiDetailsPanel(FocObject obj, int viewID){
		super("Gantt Style", FILL_NONE);
		GanttStyle ganttStyle = (GanttStyle) obj;
		
		int x = 0;
		int y = 0;
		
		add(ganttStyle, GanttStyleDesc.FLD_NAME         , x, y++);
		add(ganttStyle, GanttStyleDesc.FLD_BAR_POSITIONS, x, y++);
		
		add(ganttStyle, GanttStyleDesc.FLD_FIRST_BAR, x, y);
		addField(ganttStyle, GanttStyleDesc.FLD_FIRST_BAR_COLOR, x+2, y++);
		
		add(ganttStyle, GanttStyleDesc.FLD_FIRST_BAR_FILLER, x, y);
		addField(ganttStyle, GanttStyleDesc.FLD_FIRST_BAR_FILLER_COLOR, x+2, y++);

		add(ganttStyle, GanttStyleDesc.FLD_SECOND_BAR, x, y);
		addField(ganttStyle, GanttStyleDesc.FLD_SECOND_BAR_COLOR, x+2, y++);
		
		add(ganttStyle, GanttStyleDesc.FLD_SECOND_BAR_FILLER, x, y);
		addField(ganttStyle, GanttStyleDesc.FLD_SECOND_BAR_FILLER_COLOR, x+2, y++);

		add(ganttStyle, GanttStyleDesc.FLD_RELATIONSHIP_MODE, x, y);
		addField(ganttStyle, GanttStyleDesc.FLD_RELATIONSHIP_COLOR, x+2, y++);

		FValidationPanel vPanel = showValidationPanel(true);
		vPanel.addSubject(ganttStyle);
	}
}
