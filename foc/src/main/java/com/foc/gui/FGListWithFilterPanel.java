/*
 * Created on 24 fevr. 2004
 */
package com.foc.gui;

import com.foc.list.FocList;
import com.foc.list.FocListWithFilter;
import com.foc.list.filter.FocListFilter;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGListWithFilterPanel extends FListPanel {
	
  public FGListWithFilterPanel() {
		super();
	}

	public FGListWithFilterPanel(FocList focList, int ddw) {
		super(focList, ddw);
	}

	public FGListWithFilterPanel(FocList focList) {
		super(focList);
	}

	public FGListWithFilterPanel(String frameTitle, int frameSizing, int panelFill) {
		super(frameTitle, frameSizing, panelFill);
	}

	public FGListWithFilterPanel(String panelTitle, int panelFill) {
		super(panelTitle, panelFill);
	}

	public void setFocList(FocList focList){
  	super.setFocList(focList);
  	FocListWithFilter lwf = (FocListWithFilter) focList; 
    this.fTableModel.getTableView().setFilter((FocListFilter) lwf.getFocListFilter(), false);
  }

	@Override
	public void construct() {
		super.construct();
		showFilterButton(true);
	}
}