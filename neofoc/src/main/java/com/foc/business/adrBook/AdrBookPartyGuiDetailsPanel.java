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
package com.foc.business.adrBook;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.gui.FGButton;
import com.foc.gui.FGObjectComboBox;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

@SuppressWarnings("serial")
public class AdrBookPartyGuiDetailsPanel extends FPanel {

	public static final int VIEW_DEFAULT          = FocObject.DEFAULT_VIEW_ID;
	public static final int VIEW_SELECTION        = 1;
	public static final int VIEW_SELECTION_SHORT  = 2;
	public static final int VIEW_IN_PARTY_DETAILS = 3;
	
	private AdrBookParty      party           = null;
	private FGObjectComboBox  regionCombo     = null;
	private FGObjectComboBox  cityCombo       = null;
	private FPropertyListener countryListener = null;
	
  public AdrBookPartyGuiDetailsPanel(FocObject price, int view){
    super("Adress Book Party", FPanel.FILL_NONE);   
    
    party = (AdrBookParty) price;
    
    int y = 0;
    
   	party.code_resetIfCreated();
    
    if(view == VIEW_SELECTION || view == VIEW_SELECTION_SHORT){
    	//setFill(FPanel.FILL_HORIZONTAL);
			setInsets(0, 0, 0, 0);
			FGTextField comp = (FGTextField) party.getGuiComponent(AdrBookPartyDesc.FLD_CODE_NAME);
			comp.setEnabled(false);
			//if(view == VIEW_SELECTION_SHORT) 
			comp.setColumns(25);
			
			//comp.setHorizontalAlignment(FGTextField.RIGHT);
			add(comp, 1, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
			/*comp = (FGTextField) party.getGuiComponent(AdrBookPartyDesc.FLD_NAME);
			comp.setEnabled(false);
			comp.setColumns(30);
			add(comp, 2, 0);*/
    }else{
	    add(party, AdrBookPartyDesc.FLD_CODE, 0, y++);
	    add(party, AdrBookPartyDesc.FLD_NAME, 0, y++);
	    add(party, AdrBookPartyDesc.FLD_EXTERNAL_CODE, 0, y++);
	    add(party, AdrBookPartyDesc.FLD_INDUSTRY, 0, y++);
	    
	    FPanel subPanel = new FPanel();
	    subPanel.add(party, FField.FLD_COMPANY, 2, 0);

	    add(subPanel, 0, y++, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);	    
	    
	    subPanel = new FPanel();
	    subPanel.add(party, AdrBookPartyDesc.FLD_PHONE_1, 0, 0);
	    subPanel.add(party, AdrBookPartyDesc.FLD_PHONE_2, 0, 1);
	    subPanel.add(party, AdrBookPartyDesc.FLD_MOBILE, 0, 2);
	    subPanel.add(party, AdrBookPartyDesc.FLD_FAX, 0, 3);
	    subPanel.add(party, AdrBookPartyDesc.FLD_EMAIL, 2, 0);
	    subPanel.add(party, AdrBookPartyDesc.FLD_WEB, 2, 1);
	    subPanel.add(party, AdrBookPartyDesc.FLD_PO_BOX, 2, 2);
	    subPanel.add(party, AdrBookPartyDesc.FLD_MOF_REG_NBR, 2, 3);
	    add(subPanel, 0, y++, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
	    
	    subPanel = new FPanel();
	    subPanel.add(party, AdrBookPartyDesc.FLD_COUNTRY, 0, 0);
	    regionCombo = (FGObjectComboBox) subPanel.add(party, AdrBookPartyDesc.FLD_REGION, 0, 1);
	    cityCombo   = (FGObjectComboBox) subPanel.add(party, AdrBookPartyDesc.FLD_CITY, 0, 2);	    
	    subPanel.add(party, AdrBookPartyDesc.FLD_LANGUAGE, 0, 3);	    
	    add(subPanel, 0, y++, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
	    
	    Component comp = party.getGuiComponent(AdrBookPartyDesc.FLD_DELIVERY_ADDRESS);
	    add(comp, 0, y++, 2, 1);
	    comp = party.getGuiComponent(AdrBookPartyDesc.FLD_INVOICE_ADDRESS);
	    add(comp, 0, y++, 2, 1);
	
	    if(view != VIEW_IN_PARTY_DETAILS){
		    FGButton contactsList = new FGButton("Contacts...");
		    add(contactsList, 0, y++, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		    contactsList.addActionListener(new AbstractAction(){
					public void actionPerformed(ActionEvent e) {
//						ContactGuiBrowsePanel contactBrowse = new ContactGuiBrowsePanel(party.getContactList(), FocObject.DEFAULT_VIEW_ID);
//						Globals.getDisplayManager().changePanel(contactBrowse);
					}
		    });
	    }
	
	    if(view != VIEW_IN_PARTY_DETAILS){
		    FValidationPanel savePanel = showValidationPanel(true);
		    if(savePanel != null){
		      savePanel.addSubject(party);
		    }
	    }
	    
	    FPropertyListener countryListener = new FPropertyListener() {
				@Override
				public void propertyModified(FProperty property) {
					if(regionCombo != null && property.getFocField().getID() != AdrBookPartyDesc.FLD_REGION){
						regionCombo.refreshList();
					}
					if(cityCombo != null) cityCombo.refreshList();
				}
				
				@Override
				public void dispose() {
				}
			};
	    
	    party.getFocProperty(AdrBookPartyDesc.FLD_COUNTRY).addListener(countryListener);
	    party.getFocProperty(AdrBookPartyDesc.FLD_REGION).addListener(countryListener);
    }
  }
  
  public void dispose(){
  	if(countryListener != null){
  		if(party != null){
  	    party.getFocProperty(AdrBookPartyDesc.FLD_COUNTRY).removeListener(countryListener);
  	    party.getFocProperty(AdrBookPartyDesc.FLD_REGION).removeListener(countryListener);
  		}
  		countryListener.dispose();
  		countryListener = null;
  	}
  	super.dispose();
  	party       = null;
  	cityCombo   = null;
  	regionCombo = null;
  }
  
	@Override
	public Dimension getPreferredSize(){
		return super.getPreferredSize();
	}
}
