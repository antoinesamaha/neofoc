/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;

import com.foc.business.company.CompanyDesc;
import com.foc.business.company.UserCompanyRightsDesc;
import com.foc.desc.*;
import com.foc.gui.FGLabel;
import com.foc.gui.FGObjectComboBox;
import com.foc.list.*;
import com.foc.property.FCompanyProperty;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FCompanyField extends FObjectField {
  
  public FCompanyField(boolean key, boolean mandatory) {
  	super(FField.FNAME_COMPANY, "Company", FField.FLD_COMPANY, key, CompanyDesc.getInstance(), "COMPANY_");
  	setNullValueMode(NULL_VALUE_ALLOWED_AND_SHOWN);
  	setSelectionList(CompanyDesc.getList(FocList.NONE));
  	setComboBoxCellEditor(CompanyDesc.FLD_NAME);
  	setDisplayField(CompanyDesc.FLD_NAME);
  	if(mandatory){
  		setNullValueDisplayString("-select a Company-");
  	}else{
  		setNullValueDisplayString("-all-");
  	}
  	setMandatory(mandatory);
  }
    
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FCompanyProperty(masterObj, getID(), (FocObject) defaultValue);
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
    return newProperty(masterObj, null);
  }

  @Override
  public Component getGuiComponent_ComboBox(FProperty prop){
  	Component comp   = null;
  	FocObject focObj = prop.getFocObject();
  	if(focObj != null){
  		//USERRIGHTS
//  		if(focObj.getCompany() == null || focObj.getCompany().getUserRights() == UserCompanyRightsDesc.ACCESS_RIGHT_READ_WRITE){
  			comp = (FGObjectComboBox) super.getGuiComponent_ComboBox(prop);		
//  		}else{
//  			comp = new FGLabel(focObj.getCompany().getPropertyString(getDisplayField()));		
//  		}
  	}
  	
    return comp;
  }
}
