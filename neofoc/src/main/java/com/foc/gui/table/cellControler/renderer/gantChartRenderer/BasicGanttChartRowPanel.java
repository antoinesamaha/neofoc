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
package com.foc.gui.table.cellControler.renderer.gantChartRenderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.sql.Date;
import java.util.Calendar;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.foc.business.calendar.FCalendar;
import com.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;

@SuppressWarnings("serial")
public class BasicGanttChartRowPanel extends JPanel {
	protected JTable table = null;
  protected BasicGanttScale gantScale = null;
  private static final Color CUSTOM_LIGHT_GRAY = new Color(218, 218, 218);
	
	public BasicGanttChartRowPanel(BasicGanttScale gantScale){
		this.gantScale = gantScale;
	}
	
	public void dispose(){
		this.table = null;
		this.gantScale = null;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(gantScale.getGanttColumnsView() == BasicGanttScale.VIEW_WEEKLY){
			fillWeekDelimeters(g);
		}else	if(gantScale.getGanttColumnsView() == BasicGanttScale.VIEW_MONTHLY){
			fillMonthDelimeters(g);
		}else{
			fillGrayForHolyDaysAndWhiteElseWhere(g);
		}
	}
	
	private void fillGrayForHolyDaysAndWhiteElseWhere(Graphics g){
		Date date = (Date)gantScale.getStartDate().clone();
		Date lastDate = gantScale.getEndDate();
		while(date.getTime() < lastDate.getTime()){
			int x1 = gantScale.getPixelsForDate(date);
			int x2 = gantScale.getPixelsForDate(new Date(date.getTime() + FCalendar.MILLISECONDS_IN_DAY));
			int rectangleWidth = x2 - x1;
			
			boolean workingDay = gantScale.getCalandar().isWorkingDay(date);
			Color color = workingDay ? Color.WHITE : /*Color.LIGHT_GRAY*/CUSTOM_LIGHT_GRAY;
			g.setColor(color);
			g.fillRect(x1, 0, rectangleWidth, this.table.getRowHeight());
			date.setTime(date.getTime()+FCalendar.MILLISECONDS_IN_DAY);
		}
	}

	private void fillWeekDelimeters(Graphics g){
		int rowHeight = this.table.getRowHeight();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, gantScale.getTotalNumberOFPixelsForColumn(), rowHeight);
    
    Date date = (Date)gantScale.getStartDate().clone();
    Calendar calendar = FCalendar.getInstanceOfJavaUtilCalandar();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
    date = new Date(calendar.getTimeInMillis());
    Date lastDate = gantScale.getEndDate();

    Graphics gForDate = g.create();
    Font smalerFont = gForDate.getFont();
    smalerFont = smalerFont.deriveFont((float)12);
    gForDate.setFont(smalerFont);
    gForDate.setColor(Color.BLACK);
    Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
    
    while(date.getTime() < lastDate.getTime()){
    	int x = gantScale.getPixelsForDate(date);
      g.setColor(Color.LIGHT_GRAY);
      g.drawLine(x, 0, x, rowHeight);
      
      cal.setTime(date);
      cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
      
      date.setTime(date.getTime()+ 7 * FCalendar.MILLISECONDS_IN_DAY);
    }
	}

	private void fillMonthDelimeters(Graphics g){
		int rowHeight = this.table.getRowHeight();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, gantScale.getTotalNumberOFPixelsForColumn(), rowHeight);
    
    Date     date     = (Date)gantScale.getStartDate().clone();
    Date     lastDate = gantScale.getEndDate();
    Calendar cal      = FCalendar.getInstanceOfJavaUtilCalandar();
    
    while(date.getTime() < lastDate.getTime()){
    	int x = gantScale.getPixelsForDate(date);
      g.setColor(Color.LIGHT_GRAY);
      g.drawLine(x, 0, x, rowHeight);
      
      cal.setTime(date);
      FCalendar.rollTheCalendar_Month(cal);
      date.setTime(cal.getTimeInMillis());
    }
	}

	public void setTable(JTable table){
    this.table = table;
  }
}
