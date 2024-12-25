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
package com.foc.gui.table;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import com.foc.Globals;
import com.foc.gui.DisplayManager;

import javax.swing.table.TableCellRenderer;

public class AutofitTableColumns {
  
  private static final int DEFAULT_COLUMN_PADDING = 5;
  
  /*
   * @param JTable aTable, the JTable to autoresize the columns on @param
   * boolean includeColumnHeaderWidth, use the Column Header width as a minimum
   * width @returns The table width, just in case the caller wants it...
   */
  
  public static Dimension autoResizeTable(JTable aTable, boolean includeColumnHeaderWidth, boolean applyResizeToColumns) {
    return (autoResizeTable(aTable, includeColumnHeaderWidth, DEFAULT_COLUMN_PADDING, applyResizeToColumns));
  }
  
  /*
   * @param JTable aTable, the JTable to autoresize the columns on @param
   * boolean includeColumnHeaderWidth, use the Column Header width as a minimum
   * width @param int columnPadding, how many extra pixels do you want on the
   * end of each column @returns The table width, just in case the caller wants
   * it...
   */
  public static Dimension autoResizeTable(JTable aTable, boolean includeColumnHeaderWidth, int columnPadding, boolean applySizeToColumns) {
    int columnCount = aTable.getColumnCount();
    Dimension dim = null; 
    
    if (columnCount > 0) // must have columns !
    {
      // STEP ONE : Work out the column widths
      
      int columnWidth[] = new int[columnCount];
      
      dim = getTableDimesion(aTable, columnWidth, columnPadding);
      
      // STEP TWO : Dynamically resize each column
      
      /* DANGER DANGER DANGER
       * We have noticed very bad paint behaviour when these are added

      // try changing the size of the column names area
      JTableHeader tableHeader = aTable.getTableHeader();
      Dimension dim = tableHeader.getPreferredSize();
      dim.width = tableWidth;
      tableHeader.setPreferredSize(dim);
      
      dim = aTable.getPreferredSize();
      dim.width = tableWidth;
      aTable.setPreferredSize(dim);
      aTable.setw*/
      
      if(applySizeToColumns){
	      FAbstractTableModel tableModel = (FAbstractTableModel)aTable.getModel();
	      FTableView tableView = tableModel.getTableView();
	      if( tableView != null ){
	        for( int i = 0; i < columnCount; i++){
            int col = tableView.getVisibleColumnIndex(i);
            FTableColumn ftableCol = tableView.getColumnAt(col);
            
	          int width = columnWidth[i];
	          if(!ftableCol.isAllowAutoResizing()){
	            width = ftableCol.getPreferredWidth();
	          }
	            
	          ftableCol.setPreferredWidth(width);
	        }
	      }
	      aTable.doLayout();
      }     
    }
    
    return dim != null ? dim : getMaxDimensionForString("A");
  }
  
  private static Dimension getTableDimesion(JTable aTable, int [] columnWidth, int columnPadding){
    int tableWidth = 0;
    int columnCount = aTable.getColumnCount();
    Dimension cellSpacing = aTable.getIntercellSpacing();
    
    int rowHeight = 1;
    
    for (int i = 0; i < columnCount; i++) {
    	Dimension dim = getMaxColumnCellDimension(aTable, i, true, columnPadding);
      columnWidth[i] = dim.width;
      if(rowHeight < dim.height) rowHeight = dim.height;
      	
      tableWidth += columnWidth[i];
    }
    
    // account for cell spacing too
    tableWidth += ((columnCount - 1) * cellSpacing.width);
    return new Dimension(tableWidth, rowHeight);
  }
  
  private static Dimension getMaxDimensionForString(Component defaultLabel, String text){
    int maxWidth  = 0;
    int maxHeight = 0;
    if(text != null){
      Font font = defaultLabel.getFont();
      FontMetrics fontMetrics = defaultLabel.getFontMetrics(font);
      
	    maxWidth  = SwingUtilities.computeStringWidth(fontMetrics, text);
	    maxHeight = fontMetrics.getHeight();
	    //Globals.logString(" Ascent = " + fontMetrics.getAscent() + " Descent = " + fontMetrics.getDescent() + " Leading = " + fontMetrics.getLeading() + " Leading = " + fontMetrics.getHeight());
    }
    return new Dimension(maxWidth, maxHeight);
  }

  private static Dimension getMaxDimensionForString(String text){
  	return getMaxDimensionForString(new JLabel(text), text);
  }

  private static Dimension getMaxDimensionForString(JLabel defaultLabel){
  	return getMaxDimensionForString(defaultLabel, defaultLabel.getText());
  }
  
  private static Dimension getMaxDimensionForComponent(Component comp, boolean headerCalculation){
  	Dimension dim = null;
    if (comp instanceof JLabel) {
      JLabel jtextComp = (JLabel) comp;
      
      dim = getMaxDimensionForString(jtextComp);
    } else if (comp instanceof JComboBox) {
    	JComboBox jtextComp = (JComboBox) comp;
      
      dim = getMaxDimensionForString(jtextComp, (String)jtextComp.getSelectedItem());
      dim.width  += 25;
      dim.height += 2;

    }else if(comp instanceof JTree || comp instanceof JCheckBox ){
      Dimension preffDim = comp.getPreferredSize();
      int w = preffDim.width;
      int h = 0;
      if(headerCalculation){
      	h = new JLabel("AnyThing").getPreferredSize().height;
      }
      dim = new Dimension(w, h);
    }else {
    	Dimension preffDim = comp.getPreferredSize();
    	dim = new Dimension(preffDim);
    }
    return dim;
  }
  
  private static Dimension getMaxDimensionForMultiLineString(JTable aTable, int columnNo, String strVal, Dimension maxDimension){
  	if(strVal != null){
	  	TableColumn column = aTable.getColumnModel().getColumn(columnNo);
	  	TableCellRenderer headerRenderer = column.getHeaderRenderer();
	  	//Scan the multi lines of the title and get the max for each line
			//Otherwise we get a table row as high as 3 lines...
			StringTokenizer tokenizer = new StringTokenizer(strVal, ""+Globals.RC);
			while(tokenizer.hasMoreTokens()){
				String sub = tokenizer.nextToken();
				
				Component comp = headerRenderer.getTableCellRendererComponent(aTable, sub, false, false, 0, columnNo);
				Dimension subDimension = comp != null ? getMaxDimensionForComponent(comp, true) : new Dimension(0, 0);
	
				//Dimension subDimension = getMaxDimensionForString(sub);
				if(maxDimension == null){
					maxDimension = subDimension;
				}else{
					getMaxDimension(maxDimension, maxDimension, subDimension);
				}
			}
  	}
		return maxDimension;
  }
  
  /*
   * @param JTable aTable, the JTable to autoresize the columns on @param int
   * columnNo, the column number, starting at zero, to calculate the maximum
   * width on @param boolean includeColumnHeaderWidth, use the Column Header
   * width as a minimum width @param int columnPadding, how many extra pixels do
   * you want on the end of each column @returns The table width, just in case
   * the caller wants it...
   */

  private static Dimension getMaxColumnCellDimension(JTable aTable, int columnNo, boolean includeColumnHeaderWidth, int columnPadding) {
    TableColumn column       = aTable.getColumnModel().getColumn(columnNo);
    Component   comp         = null;
    Dimension   maxDimension = null;
    
    if(includeColumnHeaderWidth){
      TableCellRenderer headerRenderer = column.getHeaderRenderer();
      if(headerRenderer != null){
      	Object headerValue = column.getHeaderValue();
      	if(headerValue instanceof String){
      		boolean treated = false;
      		if(aTable instanceof FTable){
      			FTable fTable = (FTable) aTable;
      			if(fTable.getTableHeader() instanceof FColumnGroupTableHeader){
      				FColumnGroupTableHeader colGroupTableHeader = (FColumnGroupTableHeader) fTable.getTableHeader();

              ArrayList<FColumnGroup> columnGroupList = colGroupTableHeader.getGroupsForColumn(fTable.getTableView().getFTableColumn(column));
              Iterator<FColumnGroup> iter = columnGroupList.iterator();
              while (iter != null && iter.hasNext()) {
                FColumnGroup columnGroup = iter.next();
                if(columnGroup.getHeaderValue() instanceof String){
	                treated = true;
	
	                maxDimension = getMaxDimensionForMultiLineString(aTable, columnNo, (String) columnGroup.getHeaderValue(), maxDimension);
                }
              }

      			}
      		}
      		
      		maxDimension = getMaxDimensionForMultiLineString(aTable, columnNo, (String) headerValue, maxDimension);
      		
      	}else{
      		
      		maxDimension = getMaxDimensionForMultiLineString(aTable, columnNo, (String) column.getHeaderValue(), maxDimension);
      		
	        /*comp = headerRenderer.getTableCellRendererComponent(aTable, column.getHeaderValue(), false, false, 0, columnNo);
	        if(comp != null) maxDimension = getMaxDimensionForComponent(comp, true);*/
      	}        
      }else{
        try{
          String headerText = (String) column.getHeaderValue();
          maxDimension = getMaxDimensionForString(headerText);
        }catch (ClassCastException ce){
          // Can't work out the header column width..
        }
      }
    }
    
    if(maxDimension == null) maxDimension = getMaxDimensionForString("A");;
    
    TableCellRenderer tableCellRenderer = null;
    
    for(int i = 0; i < aTable.getRowCount(); i++){
      tableCellRenderer = aTable.getCellRenderer(i, columnNo);
      
      comp = tableCellRenderer.getTableCellRendererComponent(aTable, aTable.getValueAt(i, columnNo), false, false, i, columnNo);
      
      Dimension currDim = comp != null ? getMaxDimensionForComponent(comp, false) : new Dimension(0, 0);
      getMaxDimension(maxDimension, maxDimension, currDim);
    }
    
    maxDimension.width += columnPadding;
    if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_NIMBUS){
    	maxDimension.width += Globals.getDisplayManager().getCharWidth();
    }
    
    return maxDimension;
  }
  
  private static void getMaxDimension(Dimension result, Dimension d1, Dimension d2){
  	if(result != null && d1 != null && d2 != null){
      result.width  = Math.max(d1.width , d2.width );
      result.height = Math.max(d1.height, d2.height);
  	}
  }
}
