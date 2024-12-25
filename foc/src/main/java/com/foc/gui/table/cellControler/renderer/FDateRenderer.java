/*
 * Created on 24-Mar-2005
 */
package com.foc.gui.table.cellControler.renderer;

import java.awt.*;
import java.util.HashMap;

import javax.swing.*;

import com.foc.gui.table.FAbstractTableModel;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FDateRenderer extends FDefaultCellRenderer{

  private HashMap map = null;
  
  public void dispose(){
    super.dispose();
    if( map != null ){
      map.clear();
      map = null;
    }
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component comp = null;
    FAbstractTableModel model = (FAbstractTableModel) table.getModel();
    int modelCol = model.getTableView().getVisibleColumnIndex(column);
    if(model.isCellEditable(row, modelCol)){
      FProperty property = model.getFProperty(row, modelCol);
      
      if(map == null){
        map = new HashMap();
      }
      
      String key = /*""+row+*/""+modelCol;
      
      if(map.get(key) == null){
        comp = property.getGuiComponent();
        map.put(key, comp);
      }else{
       comp = (Component) map.get(key);
      }
      
    }else{
      comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    setCellColor(comp, table,value, isSelected, hasFocus, row, column);    
    return comp;
  }
}
