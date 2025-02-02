/*
 * Created on 24-May-2005
 */
package com.foc.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

import com.foc.desc.*;
import com.foc.gui.table.*;
import com.foc.list.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGMultiColumnComboListRenderer extends JLabel implements ListCellRenderer {

  private int keyFieldId = -99;  
  private FocList focList = null;
  private FTableView tableView = null;
  private JList list = null;
  private ListHighLightListener listener = null;
  
  private FListPanel listPanel = null;
  //private FTable fTable = null;
  
  public FGMultiColumnComboListRenderer(FocList focList, int keyFieldId, FTableView tableView) {
    super();
    setOpaque(true); //MUST do this for background to show up.
    this.focList = focList;
    this.keyFieldId = keyFieldId; 
    this.tableView = tableView;
  }

  public void dispose(){
  	focList = null;
  	tableView = null;
  	list = null;
  	listener = null;
  	if(listPanel != null){
  		listPanel.dispose();
  		listPanel = null;
  	}
  }
  
  public FListPanel getListPanel(){
  	if(listPanel == null){
  		listPanel = new FListPanel(focList);
  		listPanel.getTableModel().setTableView(tableView);
      
  		listPanel.construct();
      
      tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
      
      listPanel.showEditButton(false);
      listPanel.showDuplicateButton(false);
      //tableView.setColumnResizingMode(FTableView.COLUMN_WIDTH_FACTOR_MODE);
      listPanel.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      //requestFocusOnCurrentItem();      
  	}
  	return listPanel;
  }
  
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    if(this.list == null){
      this.list = list;
      if(this.list != null){
        listener = new ListHighLightListener(list);
        list.addMouseMotionListener(listener);
      }
    }

    /*
    if(fTable == null){
      FTableModel tableModel = new FTableModel(focList, null);
      tableModel.setTableView(tableView);
      fTable = new FTable(tableModel);

      tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
      tableModel.fireTableStructureChanged();
      fTable.autoResizeColumns();
      fTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      tableView.setColumnResizingMode(FTableView.COLUMN_WIDTH_FACTOR_MODE);
    }
    */

    FTable fTable = getListPanel().getTable();
    
    if(index == listener.getHighLightedRow()){
      fTable.setCurrentMouseRow(0);
    }else{
      fTable.setCurrentMouseRow(-1);
    }
     
    FocObject obj = focList.searchByPropertyStringValue(keyFieldId, (String)value);
    if(obj == null){
      /*if(focList.size() > 0){
        obj = focList.getFocObject(0);
      }else{*/
        obj = focList.newEmptyItem();
      //}
    }
    FocList singleItemFocList = new FocList(new FocLinkSimple(focList.getFocDesc()));
    singleItemFocList.setCollectionBehaviour(true);
    singleItemFocList.add(obj);

    ((FTableModel)fTable.getTableModel()).setFocList(singleItemFocList);
    
    /*
    tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
    FTableModel tableModel = new FTableModel(singleItemFocList, null);
    tableModel.setTableView(tableView);
    fTable = new FTable(tableModel);
    if(index == listener.getHighLightedRow()){
      fTable.setCurrentMouseRow(0);
    }else{
      fTable.setCurrentMouseRow(-1);
    }
    tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
    //fTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    tableModel.fireTableStructureChanged();
    tableModel.setFocList(focList);
    fTable.autoResizeColumns();
    tableModel.setFocList(singleItemFocList);
    fTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    tableView.setColumnResizingMode(FTableView.COLUMN_WIDTH_FACTOR_MODE);
    //fTable.setCurrentMouseRow(0);
    //fTable.addMouseListener(getMouseListener());
    //fTable.attachMouseListener();
     */
    return fTable;
  }
  
  public class ListHighLightListener implements MouseMotionListener{
    private JList list = null;
    private int highLightedRow = -1;
    
    public ListHighLightListener(JList list){
      this.list = list;
    }
    
    public int getHighLightedRow() {
      return highLightedRow;
    }
    
    public void setHighLightedRow(int highLightedRow) {
      this.highLightedRow = highLightedRow;
    }

    private void newPoint(Point p){     
      int newRow = list.locationToIndex(p);
      if(newRow >= 0 && newRow != highLightedRow){
        highLightedRow = newRow;
        list.repaint();
      }
    }

    public void mouseDragged(MouseEvent e) {
      newPoint(e.getPoint());
    }

    public void mouseMoved(MouseEvent e) {
      newPoint(e.getPoint());
    }    
  }
}
