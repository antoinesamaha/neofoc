/*
 * Created on 01-Feb-2005
 */
package com.foc.business.currency;

import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.list.*;

/**
 * @author 01Barmaja
 */
public class CurrencyDateDesc extends FocDesc {

  public static final int FLD_DATE      = 1;
  public static final int FLD_RATE_LIST = 2;
  
	public static final String DB_TABLE_NAME = "CURR_DATES";
	
	public CurrencyDateDesc(){
    super(CurrencyDate.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);

    FField focFld = addReferenceField();

    focFld = new FDateField("DATE", "Date", FLD_DATE, true);
    addField(focFld);
	}
	
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public FocList newFocList(){
  	FocList focList = super.newFocList();
    FocListOrder listOrder = new FocListOrder();
    listOrder.addField(FFieldPath.newFieldPath(FLD_DATE));
    focList.setListOrder(listOrder);
    
    return focList;
  }
  
	public static CurrencyDateDesc getInstance(){
		return (CurrencyDateDesc) getInstance(DB_TABLE_NAME, CurrencyDateDesc.class);
	}
}
