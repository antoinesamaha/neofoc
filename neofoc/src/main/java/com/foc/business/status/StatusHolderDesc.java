/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.business.status;

import com.foc.ConfigInfo;
import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FMultipleChoiceItem;
import com.foc.desc.field.FObjectField;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class StatusHolderDesc {
	
	public static final int STATUS_NONE     =  0;
	public static final int STATUS_SYSTEM   =  5;
	public static final int STATUS_PROPOSAL = 10;
	public static final int STATUS_APPROVED = 30;
	public static final int STATUS_CANCELED = 40;
	public static final int STATUS_CLOSED   = 50;	

	public static final String FNAME_STATUS = "STATUS";
	public static final String FNAME_CREATION_USER = "CREATION_USER";
	
	public static String PRINTED_LABEL_FOR_PROPOSAL = "Proposal";
	
	private FocDesc           focDesc           = null;
	private IStatusHolderDesc iStatusHolderDesc = null;
	
	public StatusHolderDesc(FocDesc focDesc){
		this.focDesc = focDesc;
		iStatusHolderDesc = (IStatusHolderDesc) focDesc;
	}
	
	public void dispose(){
		iStatusHolderDesc = null;
		focDesc = null;
	}
		
	public void addFields(){
		addFields(false);
	}
	
	public void addFields(boolean reflectionFields){
		if(focDesc.getFieldByID(iStatusHolderDesc.getFLD_STATUS()) == null){
			FField fFld = newStatusField(iStatusHolderDesc.getFLD_STATUS());
			fFld.setAllwaysLocked(true);
			fFld.setReflectingField(reflectionFields);
			focDesc.addField(fFld);
			if(!reflectionFields){
				fFld.addListener(new FPropertyListener() {
					@Override
					public void propertyModified(FProperty property) {
						if(property != null && property.getFocObject() != null && !property.isLastModifiedBySetSQLString()){
							//20150908 - Begin - Change the Status only if we are moving to approved.
							boolean doTheChange = true;
							if(property.getFocObject() instanceof IStatusHolder){
								if(((IStatusHolder) property.getFocObject()).getStatusHolder().getStatus() != STATUS_APPROVED){
									doTheChange = false;
								}
							}
							
							if(doTheChange){
								//20150908 - End - Change the Status only if we are moving to approved.								
								property.getFocObject().code_resetCodeIfPrefixHasChanged();
							}
						}
					}
					
					@Override
					public void dispose() {
					}
				});
			}
		}
	
		if(focDesc.getFieldByID(iStatusHolderDesc.getFLD_CREATION_DATE()) == null){
			FDateTimeField focFld = new FDateTimeField("CREATTION_DATE", "Creation Date", iStatusHolderDesc.getFLD_CREATION_DATE(), false);
	    focFld.setAllwaysLocked(true);
	    focFld.setTimeRelevant(true);
	    focFld.setReflectingField(reflectionFields);
	    focDesc.addField(focFld);
		}

		if(focDesc.getFieldByID(iStatusHolderDesc.getFLD_VALIDATION_DATE()) == null){
			FDateTimeField focFld = new FDateTimeField("VALIDATION_DATE", "Approval Date", iStatusHolderDesc.getFLD_VALIDATION_DATE(), false);
	    focFld.setAllwaysLocked(true);
	    focFld.setTimeRelevant(true);
	    focFld.setReflectingField(reflectionFields);	    
	    focDesc.addField(focFld);
		}
    
		if(focDesc.getFieldByID(iStatusHolderDesc.getFLD_CLOSURE_DATE()) == null){		
			FDateTimeField focFld = new FDateTimeField("CLOSURE_DATE", "Closure Date", iStatusHolderDesc.getFLD_CLOSURE_DATE(), false);
	    focFld.setAllwaysLocked(true);
	    focFld.setTimeRelevant(true);
	    focFld.setReflectingField(reflectionFields);
	    focDesc.addField(focFld);
		}
		
		if(focDesc.getFieldByID(iStatusHolderDesc.getFLD_CREATION_USER()) == null){
	    FObjectField fObjectFld = new FObjectField(FNAME_CREATION_USER, "Creation User", iStatusHolderDesc.getFLD_CREATION_USER(), false, FocUser.getFocDesc(), FNAME_CREATION_USER+"_");
	    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
	    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
	    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
	    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
	    fObjectFld.setAllwaysLocked(true);
	    fObjectFld.setReflectingField(reflectionFields);	    
	    focDesc.addField(fObjectFld);
		}
	}
	
	public void addColumns(FTableView tableView){
		if(tableView != null){
			/*
			FocDesc focDesc = (FocDesc) pricingDataDesc;
			FTableColumn tCol = tableView.addColumn(focDesc, pricingDataDesc.getFLD_UNIT_PRICE(), true);
			tCol.setTitle("Unit|Price");
			tCol = tableView.addColumn(focDesc, pricingDataDesc.getFLD_PRICE(), false);
			tCol.setTitle("Price");
			tCol = tableView.addColumn(focDesc, pricingDataDesc.getFLD_PRICE_DISCOUNT_PERCENTAGE(), true);
			tCol.setTitle("Disc|(%)");			
			tCol = tableView.addColumn(focDesc, pricingDataDesc.getFLD_PRICE_DISCOUNT(), true);
			tCol.setTitle("Disc");
			tCol = tableView.addColumn(focDesc, pricingDataDesc.getFLD_PRICE_AFTER_DISCOUNT(), true);
			tCol.setTitle("Price|-Disc");
			tCol = tableView.addColumn(focDesc, pricingDataDesc.getFLD_PRICE_TAX_PERCENTAGE(), true);
			tCol.setTitle("Tax|(%)");
			tCol = tableView.addColumn(focDesc, pricingDataDesc.getFLD_PRICE_TAX(), false);
			tCol.setTitle("Tax");
			tCol = tableView.addColumn(focDesc, pricingDataDesc.getFLD_PRICE_AFTER_DISCOUNT_AND_VAT(), false);
			tCol.setTitle("Price|-Disc|+Tax");
			*/
		}		
	}

	public static FMultipleChoiceField newStatusField(int fldID){
		return newStatusField(fldID, FNAME_STATUS);
	}
	
	public static FMultipleChoiceField newStatusField(int fldID, String fldName){
		boolean arabic = ConfigInfo.isArabic();
	  
		FMultipleChoiceField multipleChoice = new FMultipleChoiceField(fldName, "Status", fldID, false, 3);
	  FMultipleChoiceItem item = null;
	  item = multipleChoice.addChoice(StatusHolderDesc.STATUS_SYSTEM  , "System");
	  item.setIconFontAwesomeName(getFontAwesomeIconNameForValue(StatusHolderDesc.STATUS_SYSTEM));
	  
	  item = multipleChoice.addChoice(StatusHolderDesc.STATUS_PROPOSAL, arabic ? "مسودة" : PRINTED_LABEL_FOR_PROPOSAL/*"Proposal"*/);
	  item.setIconFontAwesomeName(getFontAwesomeIconNameForValue(StatusHolderDesc.STATUS_PROPOSAL));
	  
	  item = multipleChoice.addChoice(StatusHolderDesc.STATUS_APPROVED, arabic ? "موافقة" : "Approved");
	  item.setIconFontAwesomeName(getFontAwesomeIconNameForValue(StatusHolderDesc.STATUS_APPROVED));
	  
	  item = multipleChoice.addChoice(StatusHolderDesc.STATUS_CANCELED, arabic ? "ملغاة" : "Canceled");
	  item.setIconFontAwesomeName(getFontAwesomeIconNameForValue(StatusHolderDesc.STATUS_CANCELED));
	  
	  item = multipleChoice.addChoice(StatusHolderDesc.STATUS_CLOSED  , arabic ? "مغلقة" : "Closed");
	  item.setIconFontAwesomeName(getFontAwesomeIconNameForValue(StatusHolderDesc.STATUS_CLOSED));
	  
	  multipleChoice.setSortItems(false);
	  
	  return multipleChoice;
	}
	
	public static String getFontAwesomeIconNameForValue(int value) {
		String icon = null;
		switch(value) {
		case StatusHolderDesc.STATUS_SYSTEM:
			icon = "ERASER";
			break;
		case StatusHolderDesc.STATUS_PROPOSAL:
			icon = "ERASER";
			break;
		case StatusHolderDesc.STATUS_APPROVED:
			icon = "CHECK_SQUARE";
			break;
		case StatusHolderDesc.STATUS_CLOSED:
			icon = "LOCK";
			break;
		case StatusHolderDesc.STATUS_CANCELED:
			icon = "BAN";
			break;
		}
		return icon;
	}
}
