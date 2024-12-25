/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import com.foc.Globals;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.formula.FocSimpleFormulaContext;
import com.foc.formula.Formula;
import com.foc.gui.table.*;
import com.foc.list.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGObjectComboBox extends FGAbstractComboBox {
  private int        displayField    = -99;
  private HashMap    stringObjectMap = null;
  private HashMap    refObjectMap    = null;
  private FTableView tableView       = null;
  private int        totalWidth      = 0;
  private boolean    browsePopup     = false;
  private boolean    isTableRenderer = false;
  //public static final String NONE_CHOICE = "-- NONE --";
  
  public FGObjectComboBox(int displayField) {
    init1(displayField, false);
    init2();
  }

  public FGObjectComboBox(/*FocList choicesObjects,*/ int displayField, FTableView tableView) {
  	this(displayField, tableView, false);
  }
  
  public FGObjectComboBox(/*FocList choicesObjects,*/ int displayField, FTableView tableView, boolean browsePopup) {
  	this(displayField, tableView, browsePopup, false);
  }
  
  public FGObjectComboBox(/*FocList choicesObjects,*/ int displayField, FTableView tableView, boolean browsePopup, boolean isTableRenderer) {
    this.tableView = tableView;
    setTableRenderer(isTableRenderer);
    init1(displayField, browsePopup);
    initListWidthCustomization(tableView);      
    init2();
  }

  public FGObjectComboBox(FProperty prop, int displayField) {
  	this(prop, displayField, false);
  }

  public FGObjectComboBox(FProperty prop, int displayField, boolean browsePopup) {
    init1(displayField, browsePopup);
    init2();    
    setProperty(prop);
    if(prop.getFocObject() != null && prop.getFocObject().getThisFocDesc() != null && prop.getFocObject().getThisFocDesc().getStorageName() != null && prop.getFocField() != null && prop.getFocField().getName() != null){
    	setName(prop.getFocObject().getThisFocDesc().getStorageName()+"."+prop.getFocField().getName());
    }
  }
  
  public FGObjectComboBox(FProperty prop, int displayField, FTableView tableView) {
  	this(prop, displayField, tableView, true);
  }
  
  public FGObjectComboBox(FProperty prop, int displayField, FTableView tableView, boolean browsePopup) {
    this(displayField, tableView, browsePopup);
    if(prop.getFocObject() != null && prop.getFocObject().getThisFocDesc() != null && prop.getFocObject().getThisFocDesc().getStorageName() != null && prop.getFocField() != null && prop.getFocField().getName() != null){
    	setName(prop.getFocObject().getThisFocDesc().getStorageName()+"."+prop.getFocField().getName());
    }
    setProperty(prop);

    int maxLength = 0;
    FObject objProp = (FObject) prop;
    if(objProp != null){
      FocList choicesObjects = objProp.getPropertySourceList();

      for(int i = 0; i < choicesObjects.size(); i++){
        FocListElement elmt = choicesObjects.getFocListElement(i);
        FocObject obj = elmt.getFocObject();
        if(obj != null){
          FProperty idProp = obj.getIdentifierProperty();
          FProperty displayProp = obj.getFocProperty(displayField);
          if(displayProp != null && idProp != null){
          	int len = displayProp.getString().length();
          	if(maxLength < len){
          		maxLength = len;
          	}
          }
        }
      }
    }
    
    Dimension dim = getPreferredSize();
    dim = new Dimension(dim);
    dim.width = (int) ((maxLength+5) * Globals.getDisplayManager().getCharWidth());
    if(totalWidth > 0){
    	dim.width = totalWidth;
    }
    setPreferredSize(dim);
  }

  public void dispose(){
    super.dispose();
    
    if(stringObjectMap != null){
      stringObjectMap.clear();
      stringObjectMap = null;
    }
    if(refObjectMap != null){
      refObjectMap.clear();
      refObjectMap = null;      
    }
    /*
    if(tableView != null){
      tableView.dispose();
      tableView = null;
    }
    */
  }
  
  public boolean isTableRenderer() {
		return isTableRenderer;
	}

	public void setTableRenderer(boolean isTableRenderer) {
		this.isTableRenderer = isTableRenderer;
	}
	
	private void init1(int displayField, boolean browsePopup) {
    this.displayField = displayField;
    this.browsePopup  = browsePopup;
  }
  
  private void init2() {
    this.addFocusListener(this);
    this.addActionListener(this);
  }
  
  private void initListWidthCustomization(FTableView tableView){
    if(tableView != null){
      totalWidth = tableView.getTotalWidth();
      //ComboBoxUI cbui = getUI();
      if(isTableRenderer()){
      	setUI(new FGComboBoxUI ());
    		addAncestorListener(new AncestorListener() {
    			public void ancestorAdded(AncestorEvent event) {
    				//make sure combobox handles key events
    				requestFocusInWindow();
    			}

    			public void ancestorMoved(AncestorEvent event) {
    			}

    			public void ancestorRemoved(AncestorEvent event) {
    			}
    		});
      }
      addPopupMenuListener(new FGComboBoxPopupListener(totalWidth));
    }
  }

  /*protected void fillChoices(FocList choicesObjects, int displayField, int nullValueMode) {
    if(stringObjectMap == null){
      stringObjectMap = new HashMap();
    }
    if(refObjectMap == null){
      refObjectMap = new HashMap();
    }
    stringObjectMap.clear();
    refObjectMap.clear();
    removeAllItems();

    if (choicesObjects != null) {
      choicesObjects.sort();
      
      if(nullValueMode == FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN){
        String str = NONE_CHOICE;
        stringObjectMap.put(str, null);
        refObjectMap.put("0", null);
        addItem(str);
      }      
      
      for (int i = 0; i < choicesObjects.size(); i++) {
        FocListElement elmt = choicesObjects.getFocListElement(i);
        FocObject obj = elmt.getFocObject();
        if (obj != null) {
          FProperty idProp = obj.getIdentifierProperty();
          FProperty displayProp = obj.getFocProperty(displayField);
          if (displayProp != null && idProp != null) {
            stringObjectMap.put(displayProp.getString(), obj);
            refObjectMap.put(idProp.getString(), obj);
            String str = displayProp.getString();
            if(!elmt.isHide()){
              addItem(str);
            }
          }
        }
      }
    }
  }*/
  
  private void fillChoices(FocList choicesObjects, String selectonFilterExpression, int displayField, FObject fObject) {
    if(stringObjectMap == null){
      stringObjectMap = new HashMap();
    }
    if(refObjectMap == null){
      refObjectMap = new HashMap();
    }
    stringObjectMap.clear();
    refObjectMap.clear();
    removeAllItems();

    if (choicesObjects != null) {
      choicesObjects.sort();
      int nullValueMode = fObject != null ? fObject.getNullValueMode() : -1;
      if(fObject != null && nullValueMode == FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN){
        String str = fObject.getNullValueDisplayString();
        stringObjectMap.put(str, null);
        refObjectMap.put("0", null);
        addItem(str);
      }
      
      FocSimpleFormulaContext filterFormulaContext = null;
      if(selectonFilterExpression != null && !selectonFilterExpression.isEmpty()){
	      Formula formula = new Formula(selectonFilterExpression);
	      filterFormulaContext = new FocSimpleFormulaContext(formula);
      }
            
      for (int i = 0; i < choicesObjects.size(); i++) {
        FocListElement elmt = choicesObjects.getFocListElement(i);
        FocObject obj = elmt.getFocObject();
        if (obj != null) {
        	if(filterFormulaContext == null || filterFormulaContext.computeBooleanValue(obj)){
	          FProperty idProp = obj.getIdentifierProperty();
	          FProperty displayProp = obj.getFocProperty(displayField);
	          if (displayProp != null && idProp != null) {
	            stringObjectMap.put(displayProp.getString(), obj);
	            refObjectMap.put(idProp.getString(), obj);
	            String str = displayProp.getString();
	            if(!elmt.isHide()){
	              addItem(str);
	            }
	          }
        	}
        }
      }
    }
  }

  public void refreshList(){
  	if(getProperty() != null){
  		refreshList((FObject) getProperty());
  	}
  }
  
  public void refreshList(FObject objProp){
    if(objProp != null){
      FocList choicesObjects           = objProp.getPropertySourceList();
      String  selectonFilterExpression = objProp.getSelectionFilterExpression();
      if(tableView != null){
        FGMultiColumnComboListRenderer tableListRenderer = new FGMultiColumnComboListRenderer(choicesObjects, displayField, tableView);
        setRenderer(tableListRenderer);
      }
      fillChoices(choicesObjects, selectonFilterExpression, displayField, objProp);
    }
  }
  
  protected void setPropertyStringValue(String strValue) {
    if (property != null && stringObjectMap != null) {
      FocObject obj = (FocObject) stringObjectMap.get(strValue);
      FObject objProperty = (FObject)property;
      objProperty.setObject_UserEditingEvent(obj);
    }
  }

  protected String getPropertyStringValue() {
    String displayString = null;
    FObject objProperty = (FObject)(property != null ? property.getInhenritanceSourceProperty() : null);
    FocObject obj = objProperty != null ? (FocObject) objProperty.getObject_CreateIfNeeded() : null;
    FProperty displayProp = obj != null ? obj.getFocProperty(displayField) : null;
    displayString = displayProp != null ? displayProp.getString() : null;
    if(displayString == null){
      //displayString = NONE_CHOICE;
    	displayString = objProperty != null ? objProperty.getNullValueDisplayString() : FObjectField.NONE_CHOICE;
    }
    return displayString;
  }

  @Override
  public void setEnabled(boolean b) {
  	if(tableView != null){
  		super.setEnabled_Super(b);
  	}else{
  		super.setEnabled(b);
  	}
  }

  public void setProperty(FProperty prop) {
    refreshList((FObject)prop);
    super.setProperty(prop);
    if(prop != null){
      setEnabled(!prop.isValueLocked());
    }
  }
  
  // NIMBUS
  public class FGComboBoxUI extends BasicComboBoxUI {
    private BasicComboPopup cpu   = null;
    private JScrollPane     sPane = null;

    FGComboBoxUI() {
      super();
    }

    protected ComboPopup createPopup() {
      cpu   = (BasicComboPopup) (super.createPopup());
      sPane = (JScrollPane) (cpu.getComponent(0)); //the popup container has 1 component (i.e. the scrollPane)
      sPane.setAlignmentX(0); //default is centered. make items left aligned
      sPane.setAlignmentY(0); //default is centered. make items left aligned 
      return cpu;
    }

    public JScrollPane getJScrollPane() {
      return sPane;
    }
    
    @Override
    public void setPopupVisible( JComboBox c, boolean v ){
    	super.setPopupVisible(c, v );
    }
  }

  public FTableView getTableView(){
  	return tableView;
  }
  
  public class FGComboBoxPopupListener implements PopupMenuListener {
    int totalWidth = 0;

    FGComboBoxPopupListener () {
      super();
    }
    
    FGComboBoxPopupListener (int totalWidth) {
      super();
      this.totalWidth = totalWidth;
    }

    public void popupMenuCanceled(PopupMenuEvent e) {
    }
    
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }
    
    public void popupMenuWillBecomeVisible (PopupMenuEvent e) {
      FGObjectComboBox cb = (FGObjectComboBox)(e.getSource());
      
      if(browsePopup){
      	/*
      	FocList    list      = cb.getSeSelectionList();
      	FListPanel listPanel = new FListPanel(list);
      	for(int i=0; i<getTableView().getColumnCount(); i++){
      		FTableColumn tCol = getTableView().getColumnAt(i);
      		listPanel.getTableView().addColumn(list.getFocDesc(), tCol.getFieldPath(), (i+1), false);
      	}
      	listPanel.construct();
      	listPanel.getTableView().setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
        listPanel.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        JPopupMenu popup = new JPopupMenu();
        popup.setLightWeightPopupEnabled(true);
        popup.add(listPanel);
        popup.setVisible(true);
        popup.setLocation(cb.getLocation());
        */
      }
      
      if(totalWidth > 0){
      	//NIMBUS
      	if(cb.getUI() instanceof FGComboBoxUI){
	        FGComboBoxUI cbUI = (FGComboBoxUI)(cb.getUI());
	        
	        JScrollPane sPane = cbUI.getJScrollPane();
	        sPane.setPreferredSize (new Dimension (totalWidth, 200));
	        sPane.setMaximumSize (new Dimension (totalWidth, 200)); //the layout manager sets the maximum size to size of comboBox so you need to change it here
      	}
      }
    }
  }
}
