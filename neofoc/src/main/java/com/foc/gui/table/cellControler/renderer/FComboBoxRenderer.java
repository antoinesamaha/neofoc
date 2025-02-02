/*
 * Created on 24-Mar-2005
 */
package com.foc.gui.table.cellControler.renderer;

import java.awt.*;

import java.util.*;
import javax.swing.*;

import com.foc.Globals;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FMultipleChoiceItem;
import com.foc.gui.DisplayManager;
import com.foc.gui.table.FAbstractTableModel;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FComboBoxRenderer extends FDefaultCellRenderer{

  private FGComboBoxInTable box = null;
  private HashMap map = null;
  
  public void dispose(){
  	if(box != null){
  		box.dispose();
  	}
    box = null;
    map = null;
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    JComponent comp = null;
    FAbstractTableModel model = (FAbstractTableModel) table.getModel();
    int modelCol = model.getTableView().getVisibleColumnIndex(column);
    if(model.isCellEditable(row, modelCol)){
      if(map == null){
        map = new HashMap();
      }
      if(box == null){
        box = new FGComboBoxInTable(table);
        //box.setBorder(null);
        /*
        ComboBoxUI ui = (ComboBoxUI)box.getUI();
        ui.
        */
        //box.setUI(new javax.swing.plaf.metal.MetalComboBoxUI());
        
        //javax.swing.plaf.metal.MetalLookAndFeel metal = new javax.swing.plaf.metal.MetalLookAndFeel();
        //metal.getC
        //UIManager.getLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel());
        //javax.swing.plaf.metal.MetalLookAndFeel
        //box.setUI()
      }
      if(map.get(value) == null){
        map.put(value, value);
        box.addItem(value);
      }
      box.setSelectedItem(value);      
      comp = box;
    }else{
      comp = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      FProperty prop  = model.getFProperty(row, modelCol);
      if(prop != null){
	      FField field = prop.getFocField();
	      if(field != null && field instanceof FMultipleChoiceField){
	      	FMultipleChoiceField multiFld = (FMultipleChoiceField) field;
	      	FMultipleChoiceItem item = multiFld.getChoiceItemForKey(prop.getInteger());
	      	if(item != null && item.getImageIcon() != null){
	      		FComboBoxRenderer comboRenderer = (FComboBoxRenderer) comp;
	      		comboRenderer.setIcon(item.getImageIcon());
	      	}
	      }
      }
    }
    
    /*
    if(isSelected){
      comp.setBackground(table.getSelectionBackground());
    }else{
      comp.setBackground(table.getBackground());
    }
    */
    setCellColor(comp, table,value,isSelected,hasFocus,row,column);
    if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_NIMBUS){
    	//Otherwize we get a white uncolored component
    	comp.setOpaque(true);
    }
    return comp;
  }
}
