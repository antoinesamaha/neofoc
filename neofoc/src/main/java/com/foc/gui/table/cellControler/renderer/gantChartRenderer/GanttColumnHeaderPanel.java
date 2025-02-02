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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.foc.Globals;
import com.foc.business.calendar.FCalendar;
import com.foc.desc.field.FField;
import com.foc.gui.table.FAbstractTableModel;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableView;

@SuppressWarnings("serial")
public class GanttColumnHeaderPanel extends JPanel {
	private BasicGanttScale gantScale        = null;
	private JTable          table            = null;
  private JButton         hideOrShowButton = null;
  
  private static final String COLLAPSE = "<<";
  private static final String EXPAND   = ">>";
  private static final String [] weekDayNames = new DateFormatSymbols().getWeekdays();
  
	public GanttColumnHeaderPanel(BasicGanttScale gantScale){
    this.gantScale = gantScale;
		setBackground(Globals.getDisplayManager().getColumnTitleBackground());
	}
  
	public void dispose(){
		gantScale        = null;
    table            = null;
    hideOrShowButton = null;
	}
	
	private BasicGanttScale getGantScale(){
		return this.gantScale;
	}
	
	public void set(JTable table){
		this.table = table;
		int headerHeight = table.getTableHeader().getHeight();
		Rectangle oldBound = getBounds();
		setBounds(oldBound.x, oldBound.y, oldBound.width, headerHeight);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
    if(gantScale.getGanttColumnsView() == BasicGanttScale.VIEW_HOURLY){
      paintHourlyView(g);  
    }else if(gantScale.getGanttColumnsView() == BasicGanttScale.VIEW_DAILY){
      paintDailyView(g);
    }else if(gantScale.getGanttColumnsView() == BasicGanttScale.VIEW_WEEKLY){
      paintWeeklyView(g);
    }else if(gantScale.getGanttColumnsView() == BasicGanttScale.VIEW_MONTHLY){
      paintMonthlyView(g);  
    }
	}
  
  private void paintHourlyView(Graphics g){
    int headerHeight = this.table.getTableHeader().getHeight();
    Date date = (Date)getGantScale().getStartDate().clone();
    Date lastDate = getGantScale().getEndDate();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
    while(date.getTime() < lastDate.getTime()){
      int x = getGantScale().getPixelsForDate(date);
      g.setColor(Color.GRAY);
      g.drawLine(x, 0, x, headerHeight);
      String dateStr = dateFormat.format(date);
      
      Graphics gForDate = g.create();
      int columnWidth = getGantScale().getPixelsForDate(new Date(date.getTime() + FCalendar.MILLISECONDS_IN_DAY)) - getGantScale().getPixelsForDate(date);
      Font smalerFont = getSuitableFont(gForDate, dateStr, columnWidth);
      FontMetrics fontMetrics = gForDate.getFontMetrics();
      
      int dateStrHeight = fontMetrics.getHeight();
      gForDate.setFont(smalerFont);
      
      gForDate.setColor(Color.BLACK);
      int dateStrX = x + (columnWidth - fontMetrics.stringWidth(dateStr))/2;
      gForDate.drawString(dateStr, dateStrX, ((headerHeight/2)+(dateStrHeight/2))-3);
      date.setTime(date.getTime()+FCalendar.MILLISECONDS_IN_DAY);
    }
  }
  
  private void paintDailyView(Graphics g){
    int headerHeight = this.table.getTableHeader().getHeight();
    
    g.setColor(Color.GRAY);
    g.drawLine(0, headerHeight/2, gantScale.getTotalNumberOFPixelsForColumn(), headerHeight/2);
    
    Date date = (Date)getGantScale().getStartDate().clone();
    Date lastDate = getGantScale().getEndDate();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
    while(date.getTime() < lastDate.getTime()){
      int x = getGantScale().getPixelsForDate(date);
      g.setColor(Color.GRAY);
      g.drawLine(x, headerHeight/2, x, headerHeight);
      String dateStr = dateFormat.format(date);

      Graphics gForDate = g.create();
      int columnWidth = getGantScale().getPixelsForDate(new Date(date.getTime() + FCalendar.MILLISECONDS_IN_DAY)) - getGantScale().getPixelsForDate(date);
      
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
      String dayOfWeekSymbol = ""+weekDayNames[dayOfWeek].charAt(0); 
      Font smalerFont = getSuitableFont(gForDate, dayOfWeekSymbol, columnWidth);
      
      FontMetrics fontMetrics = gForDate.getFontMetrics();
      gForDate.setFont(smalerFont);
      gForDate.setColor(Color.BLACK);
      int dateStrX = x + (columnWidth - fontMetrics.stringWidth(dayOfWeekSymbol))/2;
      gForDate.drawString(dayOfWeekSymbol, dateStrX, (int)((headerHeight*0.75)+(fontMetrics.getHeight()/2))-3);
      
      if( dayOfWeek == Calendar.MONDAY ){
        g.drawLine(x, 0, x, headerHeight/2);
        gForDate.drawString(dateStr, x == 0 ? x+30 : x+5, (int)(headerHeight*0.25)+(fontMetrics.getHeight()/2));
      }

      FCalendar.rollTheCalendar_Day(cal);
      date.setTime(cal.getTimeInMillis());
    }
    if(headerHeight < 50){
      int width = table.getTableHeader().getPreferredSize().width;
      JComponent comp = (JComponent)table.getTableHeader().getDefaultRenderer();
      comp.setPreferredSize(new Dimension(width, 50));
    }
  }

  private void paintWeeklyView(Graphics g){
    int headerHeight = this.table.getTableHeader().getHeight();
    
    g.setColor(Color.GRAY);
    g.drawLine(0, headerHeight/2, gantScale.getTotalNumberOFPixelsForColumn(), headerHeight/2);
    
    Date lastDate = getGantScale().getEndDate();

    Graphics gForDate = g.create();
    Font smalerFont = gForDate.getFont().deriveFont(Font.BOLD, (float)12);
    gForDate.setFont(smalerFont);
    gForDate.setColor(Color.BLACK);
    FontMetrics fontMetrics = gForDate.getFontMetrics();
    int dateY = fontMetrics.getHeight() + ((headerHeight / 2) - fontMetrics.getHeight())/2 ;
    
    Calendar calendar = FCalendar.getInstanceOfJavaUtilCalandar();
    
    //Drawing the Monthly Line
    Date date      = (Date)getGantScale().getStartDate().clone();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yy");
    while(date.getTime() <= lastDate.getTime()){
 			//Draw the month separation line
      int x = getGantScale().getPixelsForDate(date);
      g.setColor(Color.GRAY);
      g.drawLine(x, 0, x, headerHeight/2);

      //write the month string
      String dateStr = dateFormat.format(date);
      int stringWidth = fontMetrics.stringWidth(dateStr);      
      int columnWidth = getGantScale().getPixelsForDate(new Date(date.getTime() +  30 * FCalendar.MILLISECONDS_IN_DAY)) - x;
      int dateX = x + (columnWidth - stringWidth)/2;
      gForDate.drawString(dateStr, dateX, dateY);
      
    	//Increment one month
    	calendar.setTime(date);
    	FCalendar.rollTheCalendar_Month(calendar);
    	date.setTime(calendar.getTimeInMillis());
    }
    
    //Drawing the Weekly Line    
    date = (Date)getGantScale().getStartDate().clone();
    if(getFocCalendar() != null){
    	date = getFocCalendar().getFirstDayOfWeek(date);
    }else{
      calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
      date = new Date(calendar.getTimeInMillis());
  	}
    //calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
    //date = new Date(calendar.getTimeInMillis());
    lastDate = getGantScale().getEndDate();
    int dayY = (headerHeight / 2) + fontMetrics.getHeight() + ((headerHeight / 2) - fontMetrics.getHeight())/2 ;
    while(date.getTime() < lastDate.getTime()){
    	int x = getGantScale().getPixelsForDate(date);
      g.setColor(Color.GRAY);
      g.drawLine(x, headerHeight/2, x, headerHeight);
      
      String dayStr = null;
      if(getFocCalendar() != null){
      	date = getFocCalendar().getFirstDayOfWeek(date);
        Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
        cal.setTime(date);
        dayStr = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
      }else{
        Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        dayStr = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
    	}
      
      int columnWidth = getGantScale().getPixelsForDate(new Date(date.getTime() +  7 * FCalendar.MILLISECONDS_IN_DAY)) - getGantScale().getPixelsForDate(date);
      
      int stringWidth = fontMetrics.stringWidth(dayStr);
      int dayX = x+ (columnWidth - stringWidth)/2;
      gForDate.drawString(dayStr, dayX, dayY);
      
      date.setTime(date.getTime()+ 7 * FCalendar.MILLISECONDS_IN_DAY);
    }
    
    //System.out.println("Table Header BEFORE"+table.getTableHeader().getHeight());
    if( headerHeight < 50 ){
      int width = table.getTableHeader().getPreferredSize().width;
      JComponent comp = (JComponent)table.getTableHeader().getDefaultRenderer();
      comp.setPreferredSize(new Dimension(width, 50));
    }
    //System.out.println("Table Header AFTER"+table.getTableHeader().getHeight());
  }

  private void paintMonthlyView(Graphics g){
    int headerHeight = this.table.getTableHeader().getHeight();
    
    g.setColor(Color.GRAY);
    g.drawLine(0, headerHeight/2, gantScale.getTotalNumberOFPixelsForColumn(), headerHeight/2);
    
    Date lastDate = getGantScale().getEndDate();

    Graphics gForDate = g.create();
    Font smalerFont = gForDate.getFont().deriveFont(Font.BOLD, (float)12);
    gForDate.setFont(smalerFont);
    gForDate.setColor(Color.BLACK);
    FontMetrics fontMetrics = gForDate.getFontMetrics();
    int dateY = fontMetrics.getHeight() + ((headerHeight / 2) - fontMetrics.getHeight())/2 ;
    
    Calendar calendar = FCalendar.getInstanceOfJavaUtilCalandar();

    //Drawing the Yearly Line
    Date date      = (Date)getGantScale().getStartDate().clone();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
    while(date.getTime() <= lastDate.getTime()){
 			//Draw the month separation line
      int x = getGantScale().getPixelsForDate(date);
      g.setColor(Color.GRAY);
      g.drawLine(x, 0, x, headerHeight/2);

      //write the month string
      String dateStr     = dateFormat.format(date);
      int    stringWidth = fontMetrics.stringWidth(dateStr);      
      int    columnWidth = getGantScale().getPixelsForDate(new Date(date.getTime() +  30 * FCalendar.MILLISECONDS_IN_DAY)) - x;
      int    dateX       = x + stringWidth + 5;
      gForDate.drawString(dateStr, dateX, dateY);
      
    	//Increment one year
    	calendar.setTime(date);
    	int year = calendar.get(Calendar.YEAR);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	calendar.set(Calendar.MONTH, 1);
    	calendar.set(Calendar.YEAR, year+1);    	
    	//FCalendar.rollTheCalendar_Year(calendar);
    	date.setTime(calendar.getTimeInMillis());
    }

    int monthY = (headerHeight / 2) + fontMetrics.getHeight() + ((headerHeight / 2) - fontMetrics.getHeight())/2 ;
    
    //Drawing the Monthly Line
    date      = (Date)getGantScale().getStartDate().clone();
    dateFormat = new SimpleDateFormat("MMM");
    while(date.getTime() <= lastDate.getTime()){
 			//Draw the month separation line
      int x = getGantScale().getPixelsForDate(date);
      g.setColor(Color.GRAY);
      g.drawLine(x, headerHeight/2, x, headerHeight);

      //write the month string
      String dateStr = dateFormat.format(date);
      int stringWidth = fontMetrics.stringWidth(dateStr);      
      int columnWidth = getGantScale().getPixelsForDate(new Date(date.getTime() +  30 * FCalendar.MILLISECONDS_IN_DAY)) - x;
      int dateX = x + (columnWidth - stringWidth)/2;
      gForDate.drawString(dateStr, dateX, monthY);
      
    	//Increment one month
    	calendar.setTime(date);
    	FCalendar.rollTheCalendar_Month(calendar);
    	date.setTime(calendar.getTimeInMillis());
    }
    
    //System.out.println("Table Header BEFORE"+table.getTableHeader().getHeight());
    if(headerHeight < 50){
      int width = table.getTableHeader().getPreferredSize().width;
      JComponent comp = (JComponent)table.getTableHeader().getDefaultRenderer();
      comp.setPreferredSize(new Dimension(width, 50));
    }
    //System.out.println("Table Header AFTER"+table.getTableHeader().getHeight());
  }

  private Font getSuitableFont(Graphics gForDate, String text, int columnWidth ){
    Font smalerFont = gForDate.getFont();
    smalerFont = smalerFont.deriveFont(Font.BOLD);
    FontMetrics fontMetrics = gForDate.getFontMetrics();
    int strWidth = fontMetrics.stringWidth(text);
    
    while(strWidth > columnWidth){
      float smalerFontSize = (float)(smalerFont.getSize() - 1);
      smalerFont = smalerFont.deriveFont(smalerFontSize);
      fontMetrics = gForDate.getFontMetrics(smalerFont);
      strWidth = fontMetrics.stringWidth(text);
    }
    return smalerFont;
  }
  
  public Dimension getPreferredSize(){
    Dimension dimension = super.getPreferredSize();
    dimension.width = gantScale.getTotalNumberOFPixelsForColumn();
    
    //System.out.println("HEIGHT HEIGHT HEIGHT "+dimension.height);
    //System.out.println("Table Header "+table.getTableHeader().getHeight());
    
    return dimension;
  }
  
  public JButton getHideOrShowButton() {
    if(hideOrShowButton == null){
      hideOrShowButton = new JButton(COLLAPSE);
      final ArrayList<FTableColumn> tableColumnList = new ArrayList<FTableColumn>();
      hideOrShowButton.addActionListener(new ActionListener(){

        public void actionPerformed(ActionEvent e) {
          FAbstractTableModel tableModel = (FAbstractTableModel)table.getModel();
          FTableView tableView = tableModel.getTableView();
          if( tableView != null ){
            if( hideOrShowButton.getText().equals(COLLAPSE)){
              tableColumnList.clear();
              for( int i = tableView.getColumnCount()-1; i >= 0; i--){
                int col = tableView.getVisibleColumnIndex(i);
                FTableColumn ftableCol = tableView.getColumnAt(col);
                if( ftableCol.getID() != FField.TREE_FIELD_ID && ftableCol.getID() != FField.FLD_ID_GANTT_CHART ){
                  ftableCol.setShow(false);
                  tableColumnList.add(ftableCol);
                }
              }
              hideOrShowButton.setText(EXPAND);
            }else{
              for( int i = 0; i < tableColumnList.size(); i++){
                FTableColumn ftableCol = tableColumnList.get(i);
                ftableCol.setShow(true);
              }
              hideOrShowButton.setText(COLLAPSE);
            }
            tableView.adjustColumnVisibility();
          }
        }
        
      });
      
      hideOrShowButton.setMargin(new Insets(0, 0, 0, 0));
      hideOrShowButton.setBackground(getBackground());
      
      hideOrShowButton.setPreferredSize(new Dimension(20, 7));
      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(hideOrShowButton);
    }
    
    
    return hideOrShowButton;
  }

	public FCalendar getFocCalendar() {
		return gantScale != null ? gantScale.getCalandar() : null;
	}
}
