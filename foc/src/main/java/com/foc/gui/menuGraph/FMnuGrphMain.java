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
package com.foc.gui.menuGraph;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.AbstractAction;

import com.fab.FabModule;
import com.fab.parameterSheet.ParameterSheetFactory;
import com.foc.*;
import com.foc.admin.*;
import com.foc.admin.defaultappgroup.DefaultAppGroupDesc;
import com.foc.business.BusinessModule;
import com.foc.business.multilanguage.AppLanguage;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.business.units.UnitModule;
import com.foc.business.workflow.WorkflowModule;
import com.foc.db.migration.MigrationModule;
import com.foc.gui.DisplayManager;
import com.foc.gui.FDesktop;
import com.foc.gui.FGButton;
import com.foc.gui.FGLabel;
import com.foc.gui.FPanel;
import com.foc.gui.Navigator;
import com.foc.gui.borders.FocRoundBorder;
import com.foc.menu.FMenu;

/**
 * @author 01Barmaja
 */
public class FMnuGrphMain {

	private FPanel 						desktopPanel       = null; 
	private FPanel 						cyclesPanel        = null;
	private int    						cycleButtonPanel_Y = 0;
	private ArrayList<FPanel> cyclesPanelsArray  = null;
	
	public FMnuGrphMain(String[] args){
  	Navigator navigator = Globals.getDisplayManager().getNavigator();
  	FDesktop  desktop   = (FDesktop) navigator;
  	
  	desktop.setLayout(new GridBagLayout());
	  Insets insets = new Insets(0, 0, 0, 0);
	  GridBagConstraints constr = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0);
	  
	  desktopPanel = new FPanel();
	  desktop.add(desktopPanel, constr);
	  
	  FPanel cyclesPanel = new FPanel();
	  cyclesPanel.setVisible(true);
	  desktopPanel.add(cyclesPanel, 0, 0);
	  
	  cyclesPanelsArray = new ArrayList<FPanel>();
  }
	
	public void dispose(){
		cyclesPanel = null;
		if(cyclesPanelsArray != null){
			cyclesPanelsArray.clear();
		}
	}

	public ArrayList<FPanel> getCyclesPanelsArray() {
		return cyclesPanelsArray;
	}

	public void addCycleButton(String buttonLabel, String menuCode, FPanel cyclePanel, String cyclePanelTitle){
	  FMenu appMenu = Globals.getApp().getMainAppMenu();
		
	  FMenu menu = appMenu.findMenuForCode(menuCode);
  	if(menu != null && menu.isEnabled()){
  		FGButton butt = new FGButton(buttonLabel);
  		cyclesPanel.add(butt, 0, cycleButtonPanel_Y++, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
  		
		  desktopPanel.add(cyclePanel, 1, 0);
			
		  cyclePanel.setBorder(cyclePanelTitle);
		  cyclePanel.setInsets(2, 2, 2, 2);
		  
		  butt.addActionListener(new CycleButtonListener(butt, cyclePanel));
  	}
	}
	
	public class CycleButtonListener extends AbstractAction{

		private FGButton button     = null;
		private FPanel   cyclePanel = null;
		
		public CycleButtonListener(FGButton button, FPanel cyclePanel){
			this.button     = button;
			this.cyclePanel = cyclePanel;
		}
		
		public void dispose(){
			button     = null;
			cyclePanel = null;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<FPanel> array = getCyclesPanelsArray();
			for(int i=0; i<array.size(); i++){
				FPanel pnl = array.get(i);
				if(pnl == cyclePanel){
					pnl.setVisible(true);
				}else{
					pnl.setVisible(false);
				}
			}
		}
	}
	
	/*
	public void addStepToCyclePanel(String stepTitle, ){
		
	}
	
  protected FPanel salesCyclePanel       = null;
  protected FPanel procurementCyclePanel = null;
  protected FPanel productionCyclePanel  = null;
  
  private void fillDesktopGraphicalMenu(){
    salesCyclePanel       = new FPanel();
    procurementCyclePanel = new FPanel();
    productionCyclePanel  = new FPanel();
  	
  	Navigator navigator = Globals.getDisplayManager().getNavigator();
  	FDesktop  desktop   = (FDesktop) navigator;
  	
  	desktop.setLayout(new GridBagLayout());
	  Insets insets = new Insets(0, 0, 0, 0);
	  GridBagConstraints constr = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0);
	  
	  FMenu appMenu = Globals.getApp().getMainAppMenu();
	  FMenu menu    = null;
	  
	  FPanel desktopPanel = new FPanel();
	  desktop.add(desktopPanel, constr);
	  
	  FPanel cyclesPanel = new FPanel();
	  cyclesPanel.setVisible(true);
	  desktopPanel.add(cyclesPanel, 0, 0);

	  //Cycles Panel
	  //------------
	  {
	  	cyclesPanel.setBorder("Cycles");
	  	int y = 0;
	  	
	  	menu = appMenu.findMenuForCode(BudgetMenu.MENU_CODE_MAIN_SALES);
	  	if(menu != null && menu.isEnabled()){
	  		FGButton butt = new FGButton("Sales");
	  		butt.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						salesCyclePanel.setVisible(true);
						procurementCyclePanel.setVisible(false);
						productionCyclePanel.setVisible(false);
					}
				});
	  		cyclesPanel.add(butt, 0, y++, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
	  	}

	  	menu = appMenu.findMenuForCode(BudgetMenu.MENU_CODE_MAIN_PROCUREMENT);
	  	if(menu != null && menu.isEnabled()){
	  		FGButton butt = new FGButton("Procurement");
	  		butt.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						salesCyclePanel.setVisible(false);
						procurementCyclePanel.setVisible(true);
						productionCyclePanel.setVisible(false);
					}
				});
	  		cyclesPanel.add(butt, 0, y++, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
	  	}

	  	menu = appMenu.findMenuForCode(BudgetMenu.MENU_CODE_MAIN_PRODUCTION);
	  	if(menu != null && menu.isEnabled()){
	  		FGButton butt = new FGButton("Production");
	  		butt.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						salesCyclePanel.setVisible(false);
						procurementCyclePanel.setVisible(true);
						productionCyclePanel.setVisible(false);
					}
				});
	  		cyclesPanel.add(butt, 0, y++, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);	  	
	  	}
	  }

	  {
		  salesCyclePanel.setVisible(true);
		  desktopPanel.add(salesCyclePanel, 1, 0);
		
		  salesCyclePanel.setBorder("Sales cycle");
		  salesCyclePanel.setInsets(2, 2, 2, 2);
		  
		  FPanel orderPanel = new FPanel();
		  orderPanel.setBorder(new FocRoundBorder(Color.ORANGE, 3, 5));
		  orderPanel.add(new FGLabel("Order"), 0, 0);
		  orderPanel.add(new FGButton("New"), 0, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		  orderPanel.add(new FGButton("View list"), 0, 2, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		  salesCyclePanel.add(orderPanel, 0, 0);

		  FPanel delInvPanel = new FPanel();
		  delInvPanel.setBorder(new FocRoundBorder(Color.ORANGE, 3, 5));
		  delInvPanel.add(new FGLabel("Delivery & Invoice"), 0, 0);
		  delInvPanel.add(new FGButton("New"), 0, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		  salesCyclePanel.add(delInvPanel, 1, 0, 2, 1);
		  
		  FPanel recPanel = new FPanel();
		  recPanel.setBorder();
		  recPanel.add(new FGLabel("Receipt"), 0, 0);
		  recPanel.add(new FGButton("New"), 0, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		  salesCyclePanel.add(recPanel, 3, 0);

		  FPanel delPanel = new FPanel();
		  delPanel.setBorder();
		  delPanel.add(new FGLabel("Delivery\nOnly"), 0, 0);
		  delPanel.add(new FGButton("New"), 0, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		  salesCyclePanel.add(delPanel, 1, 1);

		  FPanel invPanel = new FPanel();
		  invPanel.setBorder();
		  invPanel.add(new FGLabel("Invoice\nOnly"), 0, 0);
		  invPanel.add(new FGButton("New"), 0, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		  salesCyclePanel.add(invPanel, 2, 1);
		  
	  }
	  

	  
	//  cyclesPanel.doLayout();
	//  cyclesPanel.validate();
	  
	//  FPanel invOnly = new FPanel();
	//  invOnly.setBorder();
	//  invOnly.add(new JButton("Invoice\nOnly"), 0, 0);
	//  invOnly.setLightWeightPopupMenu();
	//  salesPanel.add(invOnly, 2, 1);
	//  
	//  invOnly.addActionListener(new ActionListener() {
	//		
	//		@Override
	//		public void actionPerformed(ActionEvent e) {
	//			FPanel tpnl = new FPanel();
	//			tpnl.add(new FGLabel("Hi"), 0, 0);
	//			tpnl.setLightWeight(true);
	//			Globals.getDisplayManager().popupDialog(tpnl, "Choose", true, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
	//			
	//		}
	//	});
	//  salesPanel.add(invOnly, 2, 1);
  }
  */
}
 
