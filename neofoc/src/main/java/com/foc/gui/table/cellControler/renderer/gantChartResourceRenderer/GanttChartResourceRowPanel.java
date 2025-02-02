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
package com.foc.gui.table.cellControler.renderer.gantChartResourceRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.sql.Date;

import com.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttChartRowPanel;
import com.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;

@SuppressWarnings("serial")
public class GanttChartResourceRowPanel extends BasicGanttChartRowPanel {
	private IGantChartResourceDrawingInfo drawingInfo = null;
	
	public GanttChartResourceRowPanel(BasicGanttScale gantScale){
		super(gantScale);
	}
	
	public void dispose(){
		super.dispose();
		this.drawingInfo = null;
	}
	
/*	private ArrayList<Color> getColorArrayForActiviyAt(int i){
		return this.drawingInfo.getColorArrayForActivityAt(i);
	}*/
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		fillActivities(g);
	}

	private void fillActivities(Graphics g){
    double complete = (double)(this.table.getRowHeight()*0.75);
    
    if(drawingInfo != null){
	    for(int i = 0; i < drawingInfo.getActivityIntervalCount(); i++){
	    	//We add a DAY_TIME to the end because it is included in the interval we don't want the graphics to stop at midnight before.
	    	Date endDate = (Date) drawingInfo.getActivityIntervalEndDateAt(i).clone();
	    	//endDate.setTime(endDate.getTime() + Globals.DAY_TIME);
	    	
	      int activityStart = gantScale.getPixelsForDate(drawingInfo.getActivityIntervalStartDateAt(i));
	      int activityEnd   = gantScale.getPixelsForDate(endDate);
	      int activityWidth = activityEnd - activityStart;
	      
	      int cumul = 0;
	      int resourceTaskOccupationCount = drawingInfo.getResourceTaskOccupationCountAt(i);
	      for(int j = 0; j < resourceTaskOccupationCount; j++){
	      	int occupationHeight = (int) (drawingInfo.getPercentageOccupationAt(i, j) * complete / (double)100.0);
	      	g.setColor(drawingInfo.getColorForActivityIntervalAt(i, j));
	      	g.fillRect(activityStart, table.getRowHeight()-(cumul+occupationHeight), activityWidth, occupationHeight/*table.getRowHeight()-cumul*/);
	      	g.setColor(Color.DARK_GRAY);
	      	g.drawRect(activityStart, table.getRowHeight()-(cumul+occupationHeight), activityWidth, occupationHeight/*table.getRowHeight()-cumul*/);
	      	cumul += occupationHeight;
	      }
	    }
    }      
    g.setColor(Color.BLACK);
    g.drawLine(0, (int)(table.getRowHeight() - complete), gantScale.getTotalNumberOFPixelsForColumn(), (int)(table.getRowHeight() - complete));
	}

	/*
	private void fillActivities_Old(Graphics g){
    int complete = (int)(this.table.getRowHeight()*0.75);
    
    for(int i = 0; i < drawingInfo.getActivityIntervalCount(); i++){
      Date startDate = drawingInfo.getActivityIntervalStartDateAt(i);
      Date endDate = drawingInfo.getActivityIntervalEndDateAt(i);
      g.setColor(drawingInfo.getColorForActivityIntervalAt(i, 0));
      
      int activityStart = gantScale.getPixelsForDate(startDate);
      int activityEnd   = gantScale.getPixelsForDate(endDate);
      
      int activityWidth = activityEnd - activityStart;
      
      int y      = 0;
      int height = 0;
      
      int resourceTaskOccupationCount = drawingInfo.getResourceTaskOccupationCountAt(i);
      if( resourceTaskOccupationCount == 1 ){
        double percentageOccupation = drawingInfo.getPercentageOccupationAt(i, 0);
        y = table.getRowHeight() - (int)(complete * percentageOccupation);
        height = (int)(complete * percentageOccupation);
        g.fillRect(activityStart, y, activityWidth, height);
      }else if( resourceTaskOccupationCount > 1 ){
        double percentageOccupation = drawingInfo.getPercentageOccupationAt(i, 0);
        double sum = 0;
        for( int j = 1; j < resourceTaskOccupationCount; j++){
          sum += drawingInfo.getPercentageOccupationAt(i, j);
        }
        height = (int)(complete * percentageOccupation);
        y = table.getRowHeight() - (int)(complete * (sum+percentageOccupation));
        g.fillRect(activityStart, y+1, activityWidth, height);
      }
    }
    
    g.setColor(Color.BLACK);
    g.drawLine(0, table.getRowHeight() - complete, gantScale.getTotalNumberOFPixelsForColumn(), table.getRowHeight() - complete);
	}
	
	private void drawActivitiesLabel(Graphics g){
		int activityCount = this.drawingInfo.getActivityCount();
		for(int i = 0; i < activityCount; i++){
			int activityX = gantScale.getPixelsForDate(this.drawingInfo.getActivityStartDateAt(i));				
			
			Date lastDate = new Date(this.drawingInfo.getActivityStartDateAt(i).getTime() + ((long)drawingInfo.getActivityDurationAt(i) * 60 * 1000));
			int activityX2 = gantScale.getPixelsForDate(lastDate);
			int activityWidth = activityX2 - activityX;
			
			String label = this.drawingInfo.getActivityLabelAt(i);
			Graphics gForLabel = g.create();
			Font smalerFont = gForLabel.getFont();
			FontMetrics fontMetrics = gForLabel.getFontMetrics(smalerFont);
			int labelWidth = fontMetrics.stringWidth(label);
			while(labelWidth > activityWidth){
				float smalerFontSize = (float)(smalerFont.getSize() - 1);
				smalerFont = smalerFont.deriveFont(smalerFontSize);
				fontMetrics = gForLabel.getFontMetrics(smalerFont);
				labelWidth = fontMetrics.stringWidth(label);
			}
			
			gForLabel.setFont(smalerFont);
			gForLabel.setColor(Color.RED);
			double labelHeigtDouble = fontMetrics.getStringBounds(label, gForLabel).getHeight();
			labelHeigtDouble = Math.ceil(labelHeigtDouble);
			int labelHeight = (int)labelHeigtDouble;
			int labelY = (table.getRowHeight() + labelHeight)/2 - 4;
			//int labelY = table.getRowHeight()/2 +  5;
			int labelX = (activityWidth - labelWidth)/2;
			gForLabel.drawString(label, activityX + labelX, labelY);
		}
	}
	
	private void drawWhiteAreasForNonWorkingTimesAndGrayForNonWorkingDays(Graphics g){
		Date date = (Date)gantScale.getStartDate().clone();
		Date lastDate = gantScale.getEndDate();
		while(date.getTime() < lastDate.getTime()){
			int x = gantScale.getPixelsForDate(date);
			int x24 = gantScale.getPixelsForDate(new Date(date.getTime() + ((FCalendar.MILLISECONDS_IN_DAY / 24) * 24)));
			if(gantScale.getCalandar().isWorkingDay(date)){
				g.setColor(Color.WHITE);
				int x8 = gantScale.getPixelsForDate(new Date(date.getTime() + ((FCalendar.MILLISECONDS_IN_DAY / 24) * 8)));
				g.fillRect(x, 0                         , x8 - x, 2*(table.getRowHeight()/5) + 1);
				g.fillRect(x, 3*(table.getRowHeight()/5), x8 - x, 2*(table.getRowHeight()/5) + 1);
				
				int x17 = gantScale.getPixelsForDate(new Date(date.getTime() + ((FCalendar.MILLISECONDS_IN_DAY / 24) * 17)));
				
				g.fillRect(x17, 0                         , x24 - x17, 2*(table.getRowHeight()/5) + 1);
				g.fillRect(x17, 3*(table.getRowHeight()/5), x24 - x17, 2*(table.getRowHeight()/5) + 1);
				
				int xVar = x8;
				int xStep = (x17 - x8) / 9;
				g.setColor(new Color(192, 192, 192, 100));
				while(xVar < x17){
					g.drawLine(xVar, 0, xVar, table.getRowHeight());
					xVar += xStep;
				}
			}else{
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(x, 0, x24 - x, 2*(table.getRowHeight()/5) + 1);
				g.fillRect(x, 3*(table.getRowHeight()/5), x24 - x, 2*(table.getRowHeight()/5) + 1);
			}
			g.setColor(Color.GRAY);
			g.drawLine(x, 0, x, table.getRowHeight());
			
			date.setTime(date.getTime()+FCalendar.MILLISECONDS_IN_DAY);
		}
	}*/
	
  public void setDrawingInfo(IGantChartResourceDrawingInfo drawingInfo) {
    this.drawingInfo = drawingInfo;
  }
  
  public Dimension getPreferredSize(){
    Dimension dimension = super.getPreferredSize();
    dimension.height = gantScale.getRowHeight();
    
    return dimension;
  }
}
