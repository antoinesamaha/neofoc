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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Date;

import com.foc.Globals;
import com.foc.business.calendar.FCalendar;
import com.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttChartRowPanel;
import com.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;

@SuppressWarnings("serial")
public class FGanttActivityRowPanel extends BasicGanttChartRowPanel {
	private IGanttChartObjectInfo drawingInfo = null;
  //private static final int BOTTOM_UP_EDGES = 1;
  //private static final int TOP_DOWN_EDGES  = 2;
  
  private static final Color COLOR_CRITICAL_ACTIVITY       = Color.RED;
  private static final Color COLOR_FORWARD_ACTIVITY        = new Color(191,	255,  128);//Color.GREEN;
  private static final Color COLOR_BACKWARD_ACTIVITY       = new Color(128,	255,	159); //Color.BLUE;
  private static final Color COLOR_FORWARD_NON_ACTIVITY    = new Color(204, 227,  255);
  private static final Color COLOR_BACKWARD_NON_ACTIVITIES = new Color(166, 193,  221);

  private static final Color COLOR_FORWARD_ACTIVITY_FORECAST = new Color(161,	225,  98);//Color.GREEN;

  public static final int BAR_POS_LARGE = 0;
  public static final int BAR_POS_THIN  = 1;
  public static final int BAR_POS_UP    = 2;
  public static final int BAR_POS_DOWN  = 3;
  
	public FGanttActivityRowPanel(BasicGanttScale gantScale){
		super(gantScale);
  }
	
	public void dispose(){
		super.dispose();
		this.drawingInfo = null;
	}
  
  @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(drawingInfo.getGanttStyle() == null){
	    drawActivities(g);
	    drawNoneActivities(g);
			drawCurrentDate(g);
		}else{
			GanttStyle ganttStyle = drawingInfo.getGanttStyle();
			Date    start  = null;
			Date    end    = null;
			Color   color  = null;
			int     barPos = BAR_POS_LARGE;
			
			boolean withText = true;
			
			if(ganttStyle.getFirstBar() != GanttStyleDesc.BAR_TYPE_NONE){
				if(ganttStyle.getFirstBar() == GanttStyleDesc.BAR_TYPE_MINIMUM){
					start = drawingInfo.getMinimumStartDate();
					end   = drawingInfo.getMinimumEndDate();
				}else if(ganttStyle.getFirstBar() == GanttStyleDesc.BAR_TYPE_MAXIMUM){
					start = drawingInfo.getMaximumStartDate();
					end   = drawingInfo.getMaximumEndDate();
				}else if(ganttStyle.getFirstBar() == GanttStyleDesc.BAR_TYPE_FORECAST){
					start = drawingInfo.getForecastStartDate();
					end   = drawingInfo.getForecastEndDate();
				}else if(ganttStyle.getFirstBar() == GanttStyleDesc.BAR_TYPE_ACTUAL){
					start = drawingInfo.getActualStartDate();
					end   = drawingInfo.getActualEndDate();
					if(FCalendar.isDateZero(end) && !FCalendar.isDateZero(start)){
						end = Globals.getApp().getSystemDate();
					}
				}
				
				color = ganttStyle.getFirstBarColor();
				if(ganttStyle.getBarPositions() == GanttStyleDesc.BAR_POSITION_OUTER_INNER || ganttStyle.getBarPositions() == GanttStyleDesc.BAR_POSITION_SINGLE_CENTERED){
					barPos = BAR_POS_LARGE;
				}else{
					barPos = BAR_POS_UP;
				}
				drawBar(g, start, end, barPos, color, withText);
				withText = false;
			}

			if(ganttStyle.getSecondBar() != GanttStyleDesc.BAR_TYPE_NONE && ganttStyle.getBarPositions() != GanttStyleDesc.BAR_POSITION_SINGLE_CENTERED){
				if(ganttStyle.getSecondBar() == GanttStyleDesc.BAR_TYPE_MINIMUM){
					start = drawingInfo.getMinimumStartDate();
					end   = drawingInfo.getMinimumEndDate();
				}else if(ganttStyle.getSecondBar() == GanttStyleDesc.BAR_TYPE_MAXIMUM){
					start = drawingInfo.getMaximumStartDate();
					end   = drawingInfo.getMaximumEndDate();
				}else if(ganttStyle.getSecondBar() == GanttStyleDesc.BAR_TYPE_FORECAST){
					start = drawingInfo.getForecastStartDate();
					end   = drawingInfo.getForecastEndDate();
				}else if(ganttStyle.getSecondBar() == GanttStyleDesc.BAR_TYPE_ACTUAL){
					start = drawingInfo.getActualStartDate();
					end   = drawingInfo.getActualEndDate();
					if(FCalendar.isDateZero(end) && !FCalendar.isDateZero(start)){
						end = Globals.getApp().getSystemDate();
					}
				}
				
				color = ganttStyle.getSecondBarColor();
				if(ganttStyle.getBarPositions() == GanttStyleDesc.BAR_POSITION_OUTER_INNER || ganttStyle.getBarPositions() == GanttStyleDesc.BAR_POSITION_SINGLE_CENTERED){
					barPos = BAR_POS_THIN;
				}else{
					barPos = BAR_POS_DOWN;
				}
				drawBar(g, start, end, barPos, color, withText);
				withText = false;
			}
		}
	}
  
  private void drawBar(Graphics g, Date start, Date end, int barPos, Color color, boolean withText){
  	if(drawingInfo.getGanttNodeType() == IGanttChartObjectInfo.GANTT_NODE_TYPE_ACTIVITY){
  		drawActivityBar(g, start, end, barPos, color, withText);
  	}else if(drawingInfo.getGanttNodeType() == IGanttChartObjectInfo.GANTT_NODE_TYPE_SUPER_ACTIVITY){
  		drawSuperActivityBar(g, start, end, barPos, color);
  	}
  }
  
  private void drawActivityBar(Graphics g, Date start, Date end, int barPos, Color color, boolean withText){
    g.setColor(color);
    
    int midY      = this.table.getRowHeight() / 2;
    int height    = 0;
    int y         = 0;
    
    switch(barPos){
    case BAR_POS_LARGE:
      height = midY ;
      y = midY - (height/2);
    	break;
    case BAR_POS_THIN:
      height = midY/2 ;
      y = midY - (height/2);
    	break;
    case BAR_POS_UP:
      height = midY/2 ;
      y = midY - (height+1);
    	break;
    case BAR_POS_DOWN:
      height = midY/2 ;
      y = midY + 1;
    	break;    	
    }
    
    int startPx = gantScale.getPixelsForDate(start);        
    int endPx   = gantScale.getPixelsForDate(end);
        
    Graphics2D g2 = (Graphics2D) g;
  
    RoundRectangle2D r = new RoundRectangle2D.Double(startPx, y, endPx - startPx, height, height/2, height/2);
    //g2.setPaint(new GradientPaint(startPx, y, Color.lightGray, endPx, y, color, false));
    Color[] cArray = {color, Color.white, color};
    float[] fArray = {0.1f, 0.7f, 0.9f};
    Point p1 = new Point(startPx, y);
    Point p2 = new Point(startPx, y+height);
    g2.setPaint(new LinearGradientPaint(p1, p2, fArray, cArray));
    g2.fill(r);
    g2.setColor(color);
    g2.draw(r);
    
    int textMargin = 5;
    
    if(withText){
	    g2.setColor(Color.BLACK);
	    g2.setFont(getFont().deriveFont(9));
	    g2.drawString(drawingInfo.getTextOnBarRightSide(), endPx+textMargin, y+height);
	    String str = drawingInfo.getTextOnBarLeftSide();
	    FontMetrics metrics = getFontMetrics(g2.getFont());
	    Rectangle2D rect = metrics.getStringBounds(str, g2);
	    g2.drawString(str, startPx-((int)rect.getWidth() + textMargin), y+height);
    }
  }  

  private void drawSuperActivityBar(Graphics g, Date start, Date end, int barPos, Color color){
  	if(barPos == BAR_POS_LARGE || barPos == BAR_POS_UP){
  		color = Color.DARK_GRAY;
	    g.setColor(color);
	    
	    int midY        = this.table.getRowHeight() / 2;
	    int height      = midY/2;
	    int y           = midY - height;
	    int triHalfBase = 4;
	    int triHeight   = 7;
	    
	    int startPx = gantScale.getPixelsForDate(start) - triHalfBase;        
	    int endPx   = gantScale.getPixelsForDate(end)   + triHalfBase;
	        
	    Graphics2D g2 = (Graphics2D) g;
	  
	    Rectangle2D r = new Rectangle2D.Double(startPx, y, endPx - startPx, height);
	    //g2.setPaint(new GradientPaint(startPx, y, Color.lightGray, endPx, y, color, false));
	    g2.setColor(color);
	    g2.fill(r);
	    int xPoints[] = {startPx, startPx+2*triHalfBase, startPx+triHalfBase};
	    int yPoints[] = {y+height, y+height, y+height+triHeight};
	    g2.fillPolygon(xPoints, yPoints, 3);

	    int xPoints2[] = {endPx, endPx-2*triHalfBase, endPx-triHalfBase};
	    int yPoints2[] = {y+height, y+height, y+height+triHeight};
	    g2.fillPolygon(xPoints2, yPoints2, 3);
  	}
  }  

  private void drawForwardActivities(Graphics g){
  	drawForwardActivities(g, COLOR_FORWARD_ACTIVITY);
  }
  
  private void drawForwardActivities(Graphics g, Color color){
    g.setColor(color);
    int activityStart = gantScale.getPixelsForDate(this.drawingInfo.getMinimumStartDate());        
    int activityEnd = gantScale.getPixelsForDate(this.drawingInfo.getMinimumEndDate());
    int activityWidth = activityEnd - activityStart;
    if(this.drawingInfo.isInCriticalPath()){
      g.setColor(COLOR_CRITICAL_ACTIVITY);
    }
    if(this.drawingInfo.getMinimumStartDate().getTime() == this.drawingInfo.getMinimumEndDate().getTime()){
    	int xPoints[] = new int[4];
    	int yPoints[] = new int[4];
    	xPoints[0] = activityStart - 6; yPoints[0] = this.table.getRowHeight() / 2;
    	xPoints[1] = activityStart    ; yPoints[1] = this.table.getRowHeight() / 2 - 6;
    	xPoints[2] = activityStart + 6; yPoints[2] = this.table.getRowHeight() / 2;
    	xPoints[3] = activityStart    ; yPoints[3] = this.table.getRowHeight() / 2 + 6;
    	g.fillPolygon(xPoints, yPoints, 4);
	    g.setColor(Color.BLACK);
    	g.drawPolygon(xPoints, yPoints, 4);
    }else{
	    g.fillRect(activityStart, 1, activityWidth, (int)(this.table.getRowHeight()-4));
	    g.setColor(Color.BLACK);
	    g.drawRect(activityStart, 1, activityWidth, (int)(this.table.getRowHeight()-4));

	    //Forecast Drawing
	    int forecastActivityStart = gantScale.getPixelsForDate(this.drawingInfo.getForecastStartDate());        
	    int forecastActivityEnd = gantScale.getPixelsForDate(this.drawingInfo.getForecastEndDate());
	    int forecastActivityWidth = forecastActivityEnd - forecastActivityStart;
	    if(forecastActivityWidth > 0){
		    g.setColor(COLOR_FORWARD_ACTIVITY_FORECAST);
		    g.fillRect(forecastActivityStart, 5, forecastActivityWidth, (int)(this.table.getRowHeight()-12));
		    g.setColor(Color.BLACK);
		    g.drawRect(forecastActivityStart, 5, forecastActivityWidth, (int)(this.table.getRowHeight()-12));
	    }
	    

	    //Acctual Drawing
	    Date actualStartDate = this.drawingInfo.getActualStartDate();
	    if(!FCalendar.isDateZero(actualStartDate)){
	    	boolean drawEnd = true;
	    	Date actualEndDate = this.drawingInfo.getActualEndDate();
	    	if(FCalendar.isDateZero(actualEndDate)){
	    		actualEndDate = gantScale.getCurrentDate();
	    		drawEnd = false;
	    	}
	    	
		    int actualActivityStart = gantScale.getPixelsForDate(actualStartDate);        
		    int actualActivityEnd   = gantScale.getPixelsForDate(actualEndDate);
		    int actualActivityWidth = actualActivityEnd - actualActivityStart;
		    
		    g.setColor(Color.RED);
		    g.drawLine(actualActivityStart, 2, actualActivityStart, this.table.getRowHeight() - 2);
		    if(actualActivityWidth > 0){
			    g.drawLine(actualActivityStart, this.table.getRowHeight() / 2, actualActivityEnd, this.table.getRowHeight() / 2);
			    if(drawEnd){
			    	g.drawLine(actualActivityEnd, 2, actualActivityEnd, this.table.getRowHeight() - 2);
			    }
		    }
			  g.setColor(Color.BLACK);
	    }
    }
    //drawActivityEdges(g, activityStart, activityEnd, TOP_DOWN_EDGES);
  }
  
  private void drawReverseActivities(Graphics g){
  	drawReverseActivities(g, COLOR_BACKWARD_ACTIVITY);
  }
  
  private void drawReverseActivities(Graphics g, Color color){
    int activityStart = gantScale.getPixelsForDate(this.drawingInfo.getMaximumStartDate());        
    int activityEnd = gantScale.getPixelsForDate(this.drawingInfo.getMaximumEndDate());
    int activityWidth = activityEnd - activityStart;
    g.setColor(color);
    if(this.drawingInfo.getMaximumStartDate().getTime() == this.drawingInfo.getMaximumEndDate().getTime()){
    	int xPoints[] = new int[4];
    	int yPoints[] = new int[4];
    	xPoints[0] = activityStart - 3; yPoints[0] = this.table.getRowHeight() / 2;
    	xPoints[1] = activityStart    ; yPoints[1] = this.table.getRowHeight() / 2 - 3;
    	xPoints[2] = activityStart + 3; yPoints[2] = this.table.getRowHeight() / 2;
    	xPoints[3] = activityStart    ; yPoints[3] = this.table.getRowHeight() / 2 + 3;
    	g.fillPolygon(xPoints, yPoints, 4);
	    g.setColor(Color.BLACK);
    	g.drawPolygon(xPoints, yPoints, 4);
    }else{    
	    g.fillRect(activityStart, (int)(this.table.getRowHeight()*0.3), activityWidth, (int)(this.table.getRowHeight()*0.4)-2);
	    g.setColor(Color.BLACK);
	    g.drawRect(activityStart, (int)(this.table.getRowHeight()*0.3), activityWidth, (int)(this.table.getRowHeight()*0.4)-2);
    }
    //drawActivityEdges(g, activityStart, activityEnd, BOTTOM_UP_EDGES);
  }
  
  private void drawCurrentDate(Graphics g){
    int currentPixel = gantScale.getPixelsForDate(gantScale.getCurrentDate());        
    g.setColor(Color.RED);
    g.fillRect(currentPixel, 0, 2, this.table.getRowHeight());
  }
  
  private void drawActivities(Graphics g) {
		if(this.drawingInfo.getGanttNodeType() == IGanttChartObjectInfo.GANTT_NODE_TYPE_ACTIVITY){
			int schedMode = ((GanttActivityScale) gantScale).getSchedulingMode();
			if(this.drawingInfo.getMinimumStartDate() != null && this.drawingInfo.getMinimumEndDate() != null){
				if(schedMode == GanttActivityScale.MODE_FORWARD || schedMode == GanttActivityScale.MODE_BOTH || schedMode == GanttActivityScale.MODE_NONE){
					drawForwardActivities(g);
				}
			}

			if(this.drawingInfo.getMaximumStartDate() != null && this.drawingInfo.getMaximumEndDate() != null){
				if(schedMode == GanttActivityScale.MODE_REVERSE || schedMode == GanttActivityScale.MODE_BOTH){
					drawReverseActivities(g);
				}
			}
		}
	}
  
  private void drawNoneActivities(Graphics g){
    if(this.drawingInfo.getGanttNodeType() == IGanttChartObjectInfo.GANTT_NODE_TYPE_SUPER_ACTIVITY){
    	int schedMode = ((GanttActivityScale) gantScale).getSchedulingMode();
  		if(this.drawingInfo.getMinimumStartDate() != null && this.drawingInfo.getMinimumEndDate() != null){
				if(schedMode == GanttActivityScale.MODE_FORWARD || schedMode == GanttActivityScale.MODE_BOTH || schedMode == GanttActivityScale.MODE_NONE){
					drawForwardActivities(g, COLOR_FORWARD_NON_ACTIVITY);
				}
			}

			if(this.drawingInfo.getMaximumStartDate() != null && this.drawingInfo.getMaximumEndDate() != null){
				if(schedMode == GanttActivityScale.MODE_REVERSE || schedMode == GanttActivityScale.MODE_BOTH){
					drawReverseActivities(g, COLOR_BACKWARD_NON_ACTIVITIES);
				}
			}
    	
    	/*
      int activityMinStart = 0;        
      int activityMinEnd   = 0;
      int activityMinWidth = 0;
      
      if( this.drawingInfo.getMinimumStartDate() != null && this.drawingInfo.getMinimumEndDate() != null ){
        if( ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_FORWARD ||
            ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_BOTH){
          activityMinStart = gantScale.getPixelsForDate(this.drawingInfo.getMinimumStartDate());        
          activityMinEnd   = gantScale.getPixelsForDate(this.drawingInfo.getMinimumEndDate());
          activityMinWidth = activityMinEnd - activityMinStart;
          g.setColor(COLOR_NONE_ACTIVITY);
          g.fillRect(activityMinStart, 0, activityMinWidth, (int)(this.table.getRowHeight()));
          //drawActivityEdges(g, activityMinStart, activityMinEnd, TOP_DOWN_EDGES);  
        }
      }
      
      int activityMaxStart = 0;        
      int activityMaxEnd   = 0;
      int activityMaxWidth = 0;
      
      if( this.drawingInfo.getMaximumStartDate() != null && this.drawingInfo.getMaximumEndDate() != null ){
        if( ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_REVERSE ||
            ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_BOTH){
          activityMaxStart = gantScale.getPixelsForDate(this.drawingInfo.getMaximumStartDate());        
          activityMaxEnd   = gantScale.getPixelsForDate(this.drawingInfo.getMaximumEndDate());
          activityMaxWidth = activityMaxEnd - activityMaxStart;
          g.setColor(COLOR_NONE_ACTIVITY);
          g.fillRect(activityMaxStart, (int)(this.table.getRowHeight() * 0.3), activityMaxWidth, (int)(this.table.getRowHeight()*0.4));
          //drawActivityEdges(g, activityMaxStart, activityMaxEnd, TOP_DOWN_EDGES);  
        }
      }
      
      if( ((GanttActivityScale)gantScale).getSchedulingMode() == GanttActivityScale.MODE_BOTH ){
        g.setColor(COLOR_OVERLAPPING_NONE_ACTIVITIES);
        if( activityMaxStart < activityMinStart && activityMaxEnd > activityMinEnd ){
          g.fillRect(activityMinStart, 0, (activityMinEnd-activityMinStart), (int)(this.table.getRowHeight()*0.5));
        }else if( activityMinStart < activityMaxStart && activityMinEnd > activityMaxStart ){
          g.fillRect(activityMaxStart, 0, (activityMinEnd-activityMaxStart), (int)(this.table.getRowHeight()*0.5));
        }else if( activityMaxStart < activityMinStart && activityMaxEnd > activityMinStart ){
          g.fillRect(activityMinStart, 0, (activityMaxEnd-activityMinStart), (int)(this.table.getRowHeight()*0.5));
        }
      }
      */
    }
  }

  /*
  private void drawActivityEdges(Graphics g, int activityStart, int activityEnd, int type){
    int edgeWidth = 2;
    if( type == BOTTOM_UP_EDGES ){
      int length = (int)(this.table.getRowHeight()*0.6);
      g.fillRect(activityStart, length, edgeWidth, this.table.getRowHeight());
      g.fillRect(activityEnd-edgeWidth, length, edgeWidth, this.table.getRowHeight());  
      Polygon triangle = new Polygon(new int[]{activityStart-edgeWidth,activityStart+(edgeWidth*2),activityStart+(edgeWidth/2)}, new int[]{length,length,(int)(this.table.getRowHeight()*0.2)}, 3);  
      g.fillPolygon(triangle);
      triangle.translate((activityEnd-activityStart)-edgeWidth, 0);
      g.fillPolygon(triangle);
    }else if( type == TOP_DOWN_EDGES ){
      int length = (int)(this.table.getRowHeight()*0.4);
      g.fillRect(activityStart, 0, edgeWidth, length);
      g.fillRect(activityEnd-edgeWidth, 0, edgeWidth, length);
      Polygon triangle = new Polygon(new int[]{activityStart-edgeWidth,activityStart+(edgeWidth*2),activityStart+(edgeWidth/2)}, new int[]{length,length,(int)(this.table.getRowHeight()*0.8)}, 3);  
      g.fillPolygon(triangle);
      triangle.translate((activityEnd-activityStart)-edgeWidth, 0);
      g.fillPolygon(triangle);
    }
    
  }
  */
  
  public Dimension getPreferredSize(){
    Dimension dimension = super.getPreferredSize();
    dimension.width = gantScale.getTotalNumberOFPixelsForColumn();
    return dimension;
  }

  public void setDrawingInfo(IGanttChartObjectInfo drawingInfo) {
    this.drawingInfo = drawingInfo;
  }
}
