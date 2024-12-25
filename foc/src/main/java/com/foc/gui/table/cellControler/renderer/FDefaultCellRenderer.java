/*
 * Created on 24-Mar-2005
 */
package com.foc.gui.table.cellControler.renderer;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FNumField;
import com.foc.gui.StaticComponent;
import com.foc.gui.borders.FocCornerBorder;
import com.foc.gui.table.FAbstractTableModel;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.IFocCellPainter;
import com.foc.gui.table.view.ColumnsConfig;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FDefaultCellRenderer extends DefaultTableCellRenderer{

  //private static final Color COLOR_FOR_LINE_GROUPING_BY_3 = new Color(235, 235, 235);
  private static FocCornerBorder formulaBorder = null;
  private IFocCellPainter cellPainter   = null;

  public FDefaultCellRenderer(){
    super();
  }
  
  public void dispose(){
  	cellPainter   = null;
  }
  
  @Override
  protected void paintComponent(Graphics g) {
  	if(cellPainter != null){
  		cellPainter.beforePaint(this, g);
  	}
  	/*
  	g.setColor(Color.GREEN);
  	if(cellRectangle != null){
  		g.fillRect(0, 0, cellRectangle.width, cellRectangle.height);
  	}
    setOpaque(false);
    setBackground(null);
    */
 		super.paintComponent(g);
  	if(cellPainter != null){
  		cellPainter.afterPaint(this, g);
  	}
  }
  
  public static FocCornerBorder getFormulaBorder(){
  	if(formulaBorder == null){
  		formulaBorder = new FocCornerBorder(Color.GREEN, 6);
  	}
  	return formulaBorder;
  }

  /*
  protected void setToolTipTextAccordingToField(Component comp){
    try{
      JComponent jComp = (JComponent)comp;
      jComp.setToolTipText("werwdd");
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  */
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component comp = null;
    comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    setCellShape(comp, table, value, isSelected, hasFocus, row, column);
    //setToolTipTextAccordingToField(comp);    
    //setBackground(Color.RED);
    //Globals.logString(""+column);
    
    if(cellPainter != null){
    	cellPainter.clear();
    	cellPainter = null;
    }
    FocObject obj  = ((FTable)table).getTableModel().getRowFocObject(row);
    if(obj != null){
	    FProperty prop          = ((FTable)table).getTableModel().getFProperty(row, column);
	    Rectangle cellRectangle = table.getCellRect(row, column, true);
	    cellPainter = obj.getCellPainter((FTable)table, isSelected, hasFocus, row, column, prop, cellRectangle);
    }
    return comp;
  }
  
  public static void setCellColorStatic(Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){    
    FTable fTable = (FTable) table;
 
    Color cellColor = null;
    FAbstractTableModel model = (FAbstractTableModel) table.getModel();
    int modelCol = model.getTableView().getVisibleColumnIndex(column);
    
    boolean editable = model.isCellEditable(row, modelCol);
    /*if(modelCol == 2){
      Globals.logString("  -Table cell "+modelCol+" editable:"+editable);
    }*/

   	cellColor = model.getCellColor(row, modelCol);

    if(cellColor == null){
      if(isSelected){
        cellColor = table.getSelectionBackground();
        //Globals.logString("Red="+cellColor.getRed()+" Green="+cellColor.getGreen()+" Blue="+cellColor.getBlue());        
        if(hasFocus){

          int red = 255;
          int green = 255;
          int blue = 255;   
          if(!editable){
            int colorDiff = 20;
            red   = Math.min(255, cellColor.getRed()+colorDiff);
            green = Math.min(255, cellColor.getGreen()+colorDiff);
            blue  = Math.min(255, cellColor.getBlue()+colorDiff);   
          }
         
          cellColor = new Color(red, green, blue);
        }
      }else{
        cellColor = table.getBackground();
        
        FAbstractTableModel fModel = (FAbstractTableModel) table.getModel();
        cellColor = fModel.getDefaultBackgroundColor(cellColor, comp, table, value, isSelected, hasFocus, row, column);

//        if(row % 6 >= 3){
//          cellColor = COLOR_FOR_LINE_GROUPING_BY_3;          
//        }
      }
    }    
    
    if(fTable != null && fTable.getCurrentMouseRow() >= 0){
      if(fTable.getCurrentMouseRow() == row){
        cellColor = table.getSelectionBackground(); 
      }else{
        cellColor = table.getBackground(); 
      }
    }
    
    if(cellColor != null){
      comp.setBackground(cellColor);
    }

    if(row == table.getSelectedRow() && column == table.getSelectedColumn()){
    	comp.setBackground(Globals.getDisplayManager().getBarmajaOrange());
    	//comp.setBackground(Globals.getDisplayManager().getColumnTitleBackground());
    }
    
    StaticComponent.setEnabledNoBackground(comp, editable, false);
    
    FProperty prop = model.getFProperty(row, modelCol);
		//if(prop != null && prop.getFocField().isWithInheritance() && prop.isInherited()){
		//	comp.setForeground(Color.LIGHT_GRAY);
		//}

    if(prop != null){
	    FField field = prop.getFocField(); 
	    if(field instanceof FNumField && comp instanceof DefaultTableCellRenderer){
	    	((DefaultTableCellRenderer) comp).setHorizontalAlignment(JTextField.RIGHT);
	    }
	    
	    if(fTable.isShowFormulaCellTag() && (field.getFormulaString() != null || prop.getFormula() != null) && comp instanceof JComponent){
	    	((JComponent)comp).setBorder(getFormulaBorder());
	    }
    }
    
    if(comp != null){
	    FTableColumn  tableCol  = fTable.getTableView().getColumnAt(modelCol);
	    ColumnsConfig colConfig = tableCol != null ? tableCol.getColumnConfig() : null;
	    if(colConfig != null){
	    	if(colConfig.getForeground() != null){
	    		comp.setForeground(colConfig.getForeground());
	    	}
	    	if(colConfig.getFont() != null){
	    		comp.setFont(colConfig.getFont());
	    	}
	    }
    }
  }
  
  protected void setCellColor(Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
  	setCellColorStatic(comp, table, value, isSelected, hasFocus, row, column);
  }
  
  protected void setCellShape(Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
    if(comp != null){
      //FAbstractTableModel model = (FAbstractTableModel) table.getModel();
      /*
      if(!model.isCellEditable(row, column)){
        comp.setEnabled(false);
      }else{
        comp.setEnabled(true);      
      }*/
      setCellColor(comp, table, value, isSelected, hasFocus, row, column);      
    }
  }
}
