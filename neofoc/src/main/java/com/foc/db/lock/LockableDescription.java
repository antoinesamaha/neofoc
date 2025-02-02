/*
 * Created on Nov 28, 2005
 */
package com.foc.db.lock;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;

import com.foc.db.SQLFilter;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.gui.FGButton;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableView;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.property.FObject;

/**
 * @author 01Barmaja
 */
public class LockableDescription {

  private FocDesc desc = null;
  private FocList listOfRecords = null;
  private FListPanel selectionPanel = null;
  
  public LockableDescription(FocDesc desc){
    this.desc = desc;
  }

  public void dispose(){
    desc = null;
    if(listOfRecords != null){
      listOfRecords = null;      
    }
    selectionPanel = null;
  }
  
  public FocList getListOfRecords(){
    if(listOfRecords == null){
      SQLFilter filter = new SQLFilter(null, SQLFilter.FILTER_ON_SELECTED);
      filter.setAdditionalWhere(new StringBuffer(FField.CONCURRENCY_LOCK_USER_FIELD_PREFIX + FField.REF_FIELD_NAME +">0"));
      listOfRecords = new FocList(null, new FocLinkSimple(desc), filter);
    }
    listOfRecords.loadIfNotLoadedFromDB();
    return listOfRecords;
  }
    
  public void unlockAll(){
    FocList list = getListOfRecords();
    Iterator iter = list.focObjectIterator();
    while(iter != null && iter.hasNext()){
      FocObject obj = (FocObject) iter.next();
      ((FObject)obj.getFocProperty(FField.LOCK_USER_FIELD_ID)).getObject_CreateIfNeeded();
      obj.unlock();
      obj.load();
    }
    
    iter = list.focObjectIterator();
    while(iter != null && iter.hasNext()){
      FocObject obj = (FocObject) iter.next();
      if(!obj.isLockedByConcurrence()){
        getListOfRecords().removeCurrentObjectFromIterator(iter);
      }
    }
  }
  
  @SuppressWarnings("serial")
  public FPanel newRecordBrowsePanel(){
    FPanel panel = new FPanel();
        
    selectionPanel = new FListPanel(getListOfRecords());
    panel.add(selectionPanel, 0, 0);      
    
    FTableView tableView = selectionPanel.getTableView();
    
    for(int i=0; i<desc.concurrenceLockView_FieldNumber(); i++){
      FField field = desc.getFieldByID(desc.concurrenceLockView_FieldAt(i));
      if(field != null){
        FTableColumn col = tableView.addColumn(desc, desc.getFieldByID(field.getID()));
        col.setEditable(false);
      }
    }
    
    FTableColumn col = tableView.addColumn(desc, desc.getFieldByID(FField.LOCK_USER_FIELD_ID));
    col.setEditable(false);
    
    selectionPanel.construct();
    selectionPanel.setDirectlyEditable(false);
    selectionPanel.requestFocus();
    selectionPanel.showEditButton(false);
    selectionPanel.showModificationButtons(false);
    
    FPanel totalsPanel = selectionPanel.getTotalsPanel();
    
    FGButton unlockButton = new FGButton("Unlock");
    totalsPanel.add(unlockButton, 0, 0, GridBagConstraints.WEST);
    unlockButton.addActionListener(new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
        FocObject obj = selectionPanel.getSelectedObject();
        obj.unlock();
        obj.load();
        if(!obj.isLockedByConcurrence()){
        	getListOfRecords().remove(obj);
        }
      }
    });

    FGButton unlockAllButton = new FGButton("Unlock all");
    totalsPanel.add(unlockAllButton, 1, 0, GridBagConstraints.WEST);
    unlockAllButton.addActionListener(new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
        unlockAll();
      }
    });
    
    return panel;
  }
  
  public String getTitle(){
    String title = desc.getTitle();
    if(title.compareTo("") == 0){
      title = desc.getStorageName();
    }else{
      title += " ("+desc.getStorageName()+")";
    }
    return title;
  }
}
