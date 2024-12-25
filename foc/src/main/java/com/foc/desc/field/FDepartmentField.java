/*
 * Created on Jan 17, 2014	
 */
package com.foc.desc.field;

import java.awt.Component;

import com.foc.business.department.DepartmentDesc;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.property.FDepartmentProperty;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FDepartmentField extends FObjectField {
  
  public FDepartmentField(boolean key, boolean mandatory) {
  	super(FField.FNAME_DEPARTMENT, "Department", FField.FLD_DEPARTMENT, key, DepartmentDesc.getInstance(), "DEPARTMENT_");
  	setNullValueMode(NULL_VALUE_ALLOWED_AND_SHOWN);
  	setSelectionList(DepartmentDesc.getList(FocList.NONE));
  	setComboBoxCellEditor(DepartmentDesc.FLD_NAME);
  	setDisplayField(DepartmentDesc.FLD_NAME);
  	if(mandatory){
  		setNullValueDisplayString("-select a Department-");
  	}else{
  		setNullValueDisplayString("-any-");
  	}
  	setMandatory(mandatory);
  }
    
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FDepartmentProperty(masterObj, getID(), (FocObject) defaultValue);
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
    return newProperty(masterObj, null);
  }

  @Override
  public Component getGuiComponent_ComboBox(FProperty prop){
  	Component comp = super.getGuiComponent_ComboBox(prop);
//  	Component comp   = null;
//  	FocObject focObj = prop.getFocObject();
//  	if(focObj != null){
//  		if(focObj.getCompany() == null || focObj.getCompany().getUserRights() == UserCompanyRightsDesc.ACCESS_RIGHT_READ_WRITE){
//  			comp = (FGObjectComboBox) super.getGuiComponent_ComboBox(prop);		
//  		}else{
//  			comp = new FGLabel(focObj.getCompany().getPropertyString(getDisplayField()));		
//  		}
//  	}
  	
    return comp;
  }
}
