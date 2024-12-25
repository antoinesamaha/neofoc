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

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.foc.gui.table.FTable;
import com.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;

@SuppressWarnings("serial")
public class FGanttChartResourceCellRenderer implements TableCellRenderer {
  
	public static final int COLUMN_WIDTH = 1000;
	private GanttChartResourceRowPanel gPanel = null;
	private BasicGanttScale gantScale = null;
	
	public FGanttChartResourceCellRenderer(BasicGanttScale gantScale){
		gPanel = new GanttChartResourceRowPanel(gantScale);
		this.gantScale = gantScale;
	}
	  
  public void dispose(){
  	if(this.gPanel != null){
  		this.gPanel.dispose();
  		this.gPanel = null;
  	}
  	
  	this.gantScale = null;
  }
  
  public BasicGanttScale getGantScale(){
  	return this.gantScale;
  }

  /*public class GanttScale{
  	public int NBR_PIXELS_PER_DAY = 100;
  	public double NBR_PIXELS_PER_MINUTE = NBR_PIXELS_PER_DAY / (24 * 60);
  	public Date startDate = null;
  	public Date endDate = null;
  	
  	public GanttScale(Date startDate, Date endDate, int totalPixels){
  		totalPixels = (int)(totalPixels);
  		
  		this.startDate = startDate;
  		this.endDate = endDate;
  		int nbrMinutes = (int)((endDate.getTime() - startDate.getTime()) / (60 * 1000));
  		NBR_PIXELS_PER_MINUTE = ((double)totalPixels) / nbrMinutes; 
  		NBR_PIXELS_PER_DAY = (int) NBR_PIXELS_PER_MINUTE * 24 * 60;
  	}

  	public int getPixelsForMinutes(double duration){
  		return (int)(duration * NBR_PIXELS_PER_MINUTE); 
  	}
  	
  	public int getPixelsForDate(Date date){
  		int sinceStartMilli = (int)(date.getTime()-startDate.getTime());
  		int sinceStartMinutes = sinceStartMilli / (60 * 1000);
  		return getPixelsForMinutes(sinceStartMinutes); 
  	}

		public Date getEndDate() {
			return endDate;
		}

		public Date getStartDate() {
			return startDate;
		}
  }*/
  
	/**
	 * TreeCellRenderer method. Overridden to update the visible row.
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		FTable fTable = (FTable)table;
		IGantChartResourceDrawingInfo drawingInfo = (IGantChartResourceDrawingInfo)fTable.getTableModel().getRowFocObject(row);
		gPanel.setTable(table);
    gPanel.setDrawingInfo(drawingInfo);
		return gPanel;
	}
  
	/*public class GanttRowPanel extends JPanel{
		private JTable table = null;
		private int row = 0, column = 0;		
		private IGantChartDrawingInfo drawingInfo = null;
		private GanttScale gantScale = null;
		
		public GanttRowPanel(GanttScale gantScale){
			this.gantScale = gantScale;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			if(gantScale == null){
				long minDate = System.currentTimeMillis();
				long maxDate = System.currentTimeMillis();
				//int activityCount = this.drawingInfo.getActivityCount();
				FTableModel tableModel = (FTableModel)this.table.getModel();
				int rowCount = tableModel.getRowCount();
				for(int rc = 0; rc < rowCount; rc++){
					IGantChartDrawingInfo drawingInfo = (IGantChartDrawingInfo)tableModel.getRowFocObject(rc);
					int activityCount = drawingInfo.getActivityCount();
					for(int i = 0; i < activityCount; i++){
						long s = drawingInfo.getActivityStartDateAt(i).getTime();
						long dur = (long)drawingInfo.getActivityDurationAt(i) * 60 * 1000;
						
						
						if(s < minDate){
							minDate = s;
						}
						if(s+dur > maxDate){
							maxDate = (long)(s+dur);
						}					
					}
				}
				maxDate += 86400000;
				gantScale = new GanttScale(new Date(minDate), new Date(maxDate), COLUMN_WIDTH );
			}
			
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, COLUMN_WIDTH, this.table.getRowHeight());
			
			Color[] color = new Color[5];
			color[0] = Color.RED;
			color[1] = Color.BLUE;
			color[2] = Color.ORANGE;
			color[3] = Color.YELLOW;
			color[4] = Color.GREEN;
			
			int activityCount = this.drawingInfo.getActivityCount();
			for(int i = 0; i < activityCount; i++){
				int activityX = gantScale.getPixelsForDate(this.drawingInfo.getActivityStartDateAt(i));				
				int activityWidth = gantScale.getPixelsForMinutes(this.drawingInfo.getActivityDurationAt(i));
				g.setColor(color[i%5]);
				
				Date lastDate = new Date(this.drawingInfo.getActivityStartDateAt(i).getTime() + ((long)drawingInfo.getActivityDurationAt(i) * 60 * 1000));
				int activityX2 = gantScale.getPixelsForDate(lastDate);
				//g.fillRect(activityX, 2, activityX2 - activityX, this.table.getRowHeight()-5);
				g.fillRect(activityX, 2, activityWidth, this.table.getRowHeight()-4);
				
				String label = this.drawingInfo.getActivityLabelAt(i);
				Graphics gForLabel = g.create();
				Font smalerFont = gForLabel.getFont();
				FontMetrics fontMetrics = g.getFontMetrics();
				int labelWidth = fontMetrics.stringWidth(label);
				while(labelWidth > activityWidth){
					float smalerFontSize = (float)(smalerFont.getSize() - 1);
					smalerFont = smalerFont.deriveFont(smalerFontSize);
					fontMetrics = gForLabel.getFontMetrics(smalerFont);
					labelWidth = fontMetrics.stringWidth(label);
				}
				
				gForLabel.setFont(smalerFont);
				gForLabel.setColor(Color.WHITE);
				int labelX = (activityWidth - labelWidth)/2;
				gForLabel.drawString(label, activityX + labelX, this.table.getRowHeight()-5);
			}

			
			g.setColor(Color.GRAY);
			Date date = (Date)gantScale.getStartDate().clone();
			Date lastDate = gantScale.getEndDate();
			while(date.getTime() < lastDate.getTime()){
				int x = gantScale.getPixelsForDate(date);
				g.drawLine(x, 0, x, table.getRowHeight());
				date.setTime(date.getTime()+86400000);
			}
		}
		
		public void set(JTable table, int row, int column, IGantChartDrawingInfo drawingInfo){
			this.table = table;
			this.row = row;
			this.column = column;		
			this.drawingInfo = drawingInfo;
		}
		
		public void dispose(){
			this.table = null;
		}
	}*/
}
