/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import java.sql.Types;

import com.foc.*;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.*;
import com.foc.gui.table.cellControler.*;
import com.foc.list.*;
import com.foc.list.filter.FilterCondition;
import com.foc.list.filter.FocDescForFilter;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FListField extends FField {
  private FocLink          focLink                     = null;
  private FocDescForFilter focDescForFilter            = null;
  private boolean          deleteListWhenMasterDeleted = true;
  private FocListOrder     listOrder                   = null;
  private boolean          directlyEditable            = true;
  
  public FListField(String name, String title, int id, FocLink focLink, FocDescForFilter focDescForFilter) {
    super(name, title, id, false, 0, 0);
    this.focLink = focLink;
    this.focDescForFilter = focDescForFilter;
  }

  public FListField(String name, String title, int id, FocLink focLink) {
    this(name, title, id, focLink, null);
  }

  public int getSqlType() {
    return Types.ARRAY;
  }

  public String getCreationString(String name) {
    return "";
  }

  public FocLink getLink() {
    return focLink;
  }

  public Object clone() throws CloneNotSupportedException {
    return null;
  }

  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FList(masterObj, getID(), (FocList)defaultValue);
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj){
  	FocList list = null;
  	FocLink link = getLink();
  	if(link instanceof FocLinkConditionalForeignKey){
  		FocLinkConditionalForeignKey conditionalLink = (FocLinkConditionalForeignKey) link;
  		link = conditionalLink.clone();
  	}
  	
    if(focDescForFilter != null){
      list = new FocListWithFilter(focDescForFilter, masterObj, link, null);
    }else{
      list = new FocList(masterObj, link, null);
    }
  	list.setDirectlyEditable(directlyEditable);
  	list.setDirectImpactOnDatabase(!directlyEditable);
  	if(link.getSlaveDesc() != null){
  		if(listOrder != null){
    		list.setListOrder(listOrder);
  		}else if(!link.getSlaveDesc().hasOrderField()){
    		list.setListOrder();
  		}
  	}
    return newProperty(masterObj, list);
  }

  public Component getGuiComponent(FProperty prop){
    FListPanel selPanel = null; 
    if(prop != null){
      FList listProp = (FList)prop;
      selPanel = new FListPanel(listProp.getList());
    }
    return selPanel;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    Globals.logString("LtFdGetCellEdit: Not implemented");
    //Not Implemented
    //Not Implemented
    //Not Implemented
    //Not Implemented    
    return null;
  }
  
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return focLink.getSlaveDesc();
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }  
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
  	return null;
  }

  public FocDescForFilter getFocDescForFilter() {
    return focDescForFilter;
  }

  public void setFocDescForFilter(FocDescForFilter focDescForFilter) {
    this.focDescForFilter = focDescForFilter;
  }

	public boolean isDeleteListWhenMasterDeleted() {
		return deleteListWhenMasterDeleted;
	}

	public void setDeleteListWhenMasterDeleted(boolean deleteListWhenMasterDeleted) {
		this.deleteListWhenMasterDeleted = deleteListWhenMasterDeleted;
	}

	public FocListOrder getListOrder() {
		return listOrder;
	}

	public void setListOrder(FocListOrder listOrder) {
		this.listOrder = listOrder;
	}

	public boolean isDirectlyEditable() {
		return directlyEditable;
	}

	public void setDirectlyEditable(boolean directlyEditable) {
		this.directlyEditable = directlyEditable;
	}
}
