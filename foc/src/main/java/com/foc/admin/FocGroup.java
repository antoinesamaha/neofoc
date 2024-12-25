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
// EXTERNAL PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// LIST
// DESCRIPTION

/*
 * Created on 20-May-2005
 */
package com.foc.admin;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.business.workflow.WFTitle;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FListField;
import com.foc.event.FValidationListener;
import com.foc.gui.FGTabbedPane;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.StaticComponent;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableView;
import com.foc.link.FocLinkInRights;
import com.foc.link.FocLinkOutRights;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FBoolean;
import com.foc.property.FInt;
import com.foc.property.FList;
import com.foc.property.FMultipleChoice;
import com.foc.property.FString;
import com.foc.shared.IFocMobileModule;
import com.foc.shared.IFocWebModuleShared;
import com.foc.shared.json.B01JsonBuilder;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FocGroup extends FocObject{
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // EXTERNAL PROPERTIES
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo  
  
  private static FocDesc applicationGroupFocDesc = null;
  public static final String STANDARD = "Standard"; 
  
  private MenuRightsDisplayList  menuRightsDisplayList  = null;
  private MenuRightsGuiTreePanel menuRightsGuiTreePanel = null;
  
  private GrpViewRightsDisplayList    viewRightsDisplayList    = null;
  private GrpViewRightsGuiBrowsePanel viewRightsGuiBrowsePanel = null;
  
  public static void setApplicationGroup(FocDesc focDesc, int displayField){
    applicationGroupFocDesc = focDesc;
  }  
  
  public static FocDesc getApplicationGroup(){
    return applicationGroupFocDesc;
  }  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static final int VIEW_READ_ONLY = 2;  
  
  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public FocGroup(FocConstructor constr) {
    super(constr);
    
    new FString(this, FocGroupDesc.FLD_NAME, "") ;
    new FString(this, FocGroupDesc.FLD_DESCRIPTION, "") ;
    new FString(this, FocGroupDesc.FLD_STARTUP_MENU, "") ;
    new FString(this, FocGroupDesc.FLD_DASHBOARD_CONTEXT, "") ;
    new FBoolean(this, FocGroupDesc.FLD_ALLOW_NAMING_MODIF, false) ;
//    new FBoolean(this, FocGroupDesc.FLD_ALLOW_STATUS_MANUAL_MODIF, false) ;
    new FBoolean(this, FocGroupDesc.FLD_ALLOW_AREA_MODIF, false) ;
    new FBoolean(this, FocGroupDesc.FLD_ALLOW_INSERT_IN_COMBOBOX, false) ;
    if(FocGroupDesc.getInstance().getLink_AppGroup() != null){
    	new FList(this, FocGroupDesc.FLD_APP_GROUP, new FocList(this, FocGroupDesc.getInstance().getLink_AppGroup(), null));
    }
    if(Globals.getApp().isCurrencyModuleIncluded()){
      new FBoolean(this, FocGroupDesc.FLD_ALLOW_CURRENCY_RATES_MODIF, false) ;    
    }
    if(Globals.getApp().isWithReporting()){
      new FBoolean(this, FocGroupDesc.FLD_ALLOW_REPORT_ACCESS, false) ;    
    }
    new FBoolean(this, FocGroupDesc.FLD_ALLOW_DATABASE_BACKUP, false) ;
    new FBoolean(this, FocGroupDesc.FLD_ALLOW_DATABASE_RESTORE, false) ;    
    
    if(Globals.getApp().isCashDeskModuleIncluded()){
      new FMultipleChoice(this, FocGroupDesc.FLD_CASH_DESKS_ACCESS, FocGroupDesc.CASH_ACCESS_NONE);
    }
    if(Globals.getApp().isWithRightsByLevel()){
      new FMultipleChoice(this, FocGroupDesc.FLD_RIGHTS_LEVEL, 1);
    }
    
    FocGroupDesc focDesc = FocGroupDesc.getInstance();
    for(int i=0; i< focDesc.getNumberOfAppGroupListFieldID(); i++){
      FListField listField = (FListField) focDesc.getFieldByID(FocGroupDesc.FLD_START_APP_GROUPS + i);
      if(listField != null){
      	listField.newProperty(this);
      }
    }
    
    //I NEED IT FOR THE PROPERTY OF MENU LIST TO NOT BE NULL
    newFocProperties();
  }

  public void dispose(){
    super.dispose();
  }
  
  public String getName(){
    FString nameProp = (FString) getFocProperty(FocGroupDesc.FLD_NAME);
    return (nameProp != null) ? nameProp.getString() : (String)null;
  }
  
  public void setName(String groupName){
  	setPropertyString(FocGroupDesc.FLD_NAME, groupName);
  }
  
  public String getDashboardContext(){
    return getPropertyString(FocGroupDesc.FLD_DASHBOARD_CONTEXT);
  }
  
  public void setDashboardContext(String context){
    setPropertyString(FocGroupDesc.FLD_DASHBOARD_CONTEXT, context);
  }
    
  public String getStartupMenu(){
    FString startupMenuProp = (FString) getFocProperty(FocGroupDesc.FLD_STARTUP_MENU);
    return (startupMenuProp != null) ? startupMenuProp.getString() : (String)null;
  }
  
  public void setStartupMenu(String startupMenu){
  	setPropertyString(FocGroupDesc.FLD_STARTUP_MENU, startupMenu);
  }

  public FocObject getAppGroup(){
  	FocList groupList = null;
  	if(applicationGroupFocDesc != null){
	    FList pGroupList = (FList) getFocProperty(FocGroupDesc.FLD_APP_GROUP);
	    groupList = pGroupList.getList();
  	}
    return groupList != null ? groupList.getOrInsertAnItem() : null;
  }

  public boolean allowNamingModif(){
    FBoolean pAllowNaingModif = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_NAMING_MODIF);
    return pAllowNaingModif != null ? pAllowNaingModif.getBoolean() : false;
  }

//  public boolean allowStatusManualModif(){
//    return getPropertyBoolean(FocGroupDesc.FLD_ALLOW_STATUS_MANUAL_MODIF);
//  }

  public boolean allowAreaManualModif(){
    return getPropertyBoolean(FocGroupDesc.FLD_ALLOW_AREA_MODIF);
  }

  public boolean allowInsertInCombobox(){
    return getPropertyBoolean(FocGroupDesc.FLD_ALLOW_INSERT_IN_COMBOBOX);
  }

  public boolean allowImport(){
    return getPropertyBoolean(FocGroupDesc.FLD_ALLOW_IMPORT);
  }

  public boolean allowExport(){
    return getPropertyBoolean(FocGroupDesc.FLD_ALLOW_EXPORT);
  }
  
  public boolean isGuestApplicable(){
    return getPropertyBoolean(FocGroupDesc.FLD_GUEST_APPLICABLE);
  }
  
  public void setGuestApplicable(boolean guestApplicable){
    setPropertyBoolean(FocGroupDesc.FLD_GUEST_APPLICABLE, guestApplicable);
  }
  
  public boolean allowCurrencyRateModif(){
    FBoolean pAllowNaingModif = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_CURRENCY_RATES_MODIF);
    return pAllowNaingModif != null ? pAllowNaingModif.getBoolean() : false;
  }

  public boolean allowReportAccess(){
    FBoolean pAllowNaingModif = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_REPORT_ACCESS);
    return pAllowNaingModif != null ? pAllowNaingModif.getBoolean() : false;
  }
  
  public boolean allowReportCreation(){
    FBoolean allowReportCreation = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_REPORT_CREATION);
    return allowReportCreation != null ? allowReportCreation.getBoolean() : false;
  }

  public boolean allowDatabaseBackup(){
    FBoolean pAllowNaingModif = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_DATABASE_BACKUP);
    return pAllowNaingModif != null ? pAllowNaingModif.getBoolean() : false;
  }

  public boolean allowDatabaseRestore(){
    FBoolean pAllowNaingModif = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_DATABASE_RESTORE);
    return pAllowNaingModif != null ? pAllowNaingModif.getBoolean() : false;
  }

  public boolean allowDeleteUnusedUsers(){
    FBoolean prop = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_DELETE_ALL_UNUSED_USERS);
    return prop != null ? prop.getBoolean() : false;
  }
  
  public boolean accessDBLog(){
    FBoolean prop = (FBoolean) getFocProperty(FocGroupDesc.FLD_ACCESS_DB_LOG);
    return prop != null ? prop.getBoolean() : false;
  }
  
  public int getCashDeskAccess(){
    FInt pCashDeskAccess = (FInt) getFocProperty(FocGroupDesc.FLD_CASH_DESKS_ACCESS);
    return pCashDeskAccess != null ? pCashDeskAccess.getInteger() : 0;
  }

  public int getRightsLevel(){
    FInt pCashDeskAccess = (FInt) getFocProperty(FocGroupDesc.FLD_RIGHTS_LEVEL);
    return pCashDeskAccess != null ? pCashDeskAccess.getInteger() : 0;
  }
  
  public int getViewsRight(){
  	return getPropertyInteger(FocGroupDesc.FLD_VIEWS_RIGHT);
  }
  
  public void setViewsRight(int viewsRight){
  	setPropertyInteger(FocGroupDesc.FLD_VIEWS_RIGHT, viewsRight);
  }

  public FocObject getAppGroupAt(int fieldID){
  	FocList list = getPropertyList(fieldID);
    return list.getOrInsertAnItem();
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newGeneralFocPanel(int viewID){
    FPanel panel = new FPanel();
    
    int x = 0;
    int y = 0;
    
    Component comp = null;
    
    if(Globals.getApp().isWithRightsByLevel()){
      comp = panel.add(this, FocGroupDesc.FLD_RIGHTS_LEVEL, x, y++);
      if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);
    }
    
    if(Globals.getApp().isCashDeskModuleIncluded()){
      comp = getGuiComponent(FocGroupDesc.FLD_CASH_DESKS_ACCESS);
      if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);
      panel.add("Cash desk access", comp, x, y++);
    }

    FPanel checkFlagPanel = new FPanel();
    int yy = 0;
    comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_NAMING_MODIF, 0, yy++, 2, 1, GridBagConstraints.NONE);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

//    comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_STATUS_MANUAL_MODIF, 0, yy++, 2, 1, GridBagConstraints.NONE);
//    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_AREA_MODIF, 0, yy++, 2, 1, GridBagConstraints.NONE);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_IMPORT, 0, yy++, 2, 1, GridBagConstraints.NONE);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_EXPORT, 0, yy++, 2, 1, GridBagConstraints.NONE);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    if(Globals.getApp().isCurrencyModuleIncluded()){
      comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_CURRENCY_RATES_MODIF, 0, yy++, 2, 1, GridBagConstraints.NONE);
      if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);
    }

    if(Globals.getApp().isWithReporting()){
      comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_REPORT_ACCESS, 0, yy++, 2, 1, GridBagConstraints.NONE);
      if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);
    }
    
    comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_DATABASE_BACKUP, 0, yy++, 2, 1, GridBagConstraints.NONE);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_DATABASE_RESTORE, 0, yy++, 2, 1, GridBagConstraints.NONE);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    panel.add(checkFlagPanel, x, y++, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    FocObject focAppGroup = getAppGroup();
    if(focAppGroup != null){
	    comp = focAppGroup.newDetailsPanel(viewID);
	    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);
	    panel.add(comp, x, y++, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
	 
	    focAppGroup.forceControler(true);
    }
    return panel;
  }
  
  public FPanel newDetailsPanel(int viewID) {
    FPanel panel = new FPanel();
    
    int x = 0;
    int y = 0;
    
    Component comp = panel.add(this, FocGroupDesc.FLD_NAME, x, y++);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    comp = panel.add(this, FocGroupDesc.FLD_DESCRIPTION, x, y++);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    FGTabbedPane tabbedPanel = new FGTabbedPane();
    FPanel generalPanel = newGeneralFocPanel(viewID);
         
    tabbedPanel.add("General", generalPanel);
    
    if(FocGroupDesc.getInstance().getNumberOfAppGroupListFieldID() > 0){
	    for(int i=0; i< FocGroupDesc.getInstance().getNumberOfAppGroupListFieldID(); i++){
	      FocObject appGroup = getAppGroupAt(FocGroupDesc.FLD_START_APP_GROUPS + i);
	      
	      FPanel groupPanelAtI = appGroup.newDetailsPanel(viewID);
	      String title = groupPanelAtI.getFrameTitle();
	      if(title == null || title.compareTo("") == 0){
	      	title = "Group "+i;
	      }
	      if(viewID == VIEW_READ_ONLY) groupPanelAtI.setEnabled(false);
	      tabbedPanel.add(title, groupPanelAtI);
	   
	      appGroup.forceControler(true);
	    }
    }

    menuRightsDisplayList  = new MenuRightsDisplayList(getMenuRightsList(), this);
    menuRightsGuiTreePanel = new MenuRightsGuiTreePanel(menuRightsDisplayList.getDisplayList(), FocObject.DEFAULT_VIEW_ID, viewID == VIEW_READ_ONLY);
    tabbedPanel.add("Menu Rights", menuRightsGuiTreePanel);

    viewRightsDisplayList    = new GrpViewRightsDisplayList(getViewRightsList(), this);
    viewRightsGuiBrowsePanel = new GrpViewRightsGuiBrowsePanel(viewRightsDisplayList.getDisplayList(), FocObject.DEFAULT_VIEW_ID, viewID == VIEW_READ_ONLY);
    if(viewID == VIEW_READ_ONLY) StaticComponent.setEnabled(viewRightsGuiBrowsePanel, false, false);
    tabbedPanel.add("Views Rights", viewRightsGuiBrowsePanel);

    panel.add(tabbedPanel, x, y++, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH);//GridBagConstraints.NONE
    
    forceControler(true);
    if(viewID != VIEW_READ_ONLY){
      FValidationPanel validPanel = panel.showValidationPanel(true);
      validPanel.addSubject(this);
      FocObject focAppGroup = getAppGroup();
      if(focAppGroup != null){
      	validPanel.addSubject(focAppGroup);
      }
      validPanel.setValidationListener(new FValidationListener(){

        public void postCancelation(FValidationPanel panel) {
          
        }

        public void postValidation(FValidationPanel panel) {
          
        }

        public boolean proceedCancelation(FValidationPanel panel) {
          return true;
        }

        public boolean proceedValidation(FValidationPanel panel) {
          if(menuRightsDisplayList != null){
            menuRightsDisplayList.updateRealList();
          }
          if(viewRightsDisplayList != null){
          	viewRightsDisplayList.updateRealList();
          }
          return true;
        }
      });
      
    }
    panel.setMainPanelSising(FPanel.FILL_BOTH);
    return panel;
  }

  public FocList getMenuRightsList(){
    FocList menuRightsList = getPropertyList(FocGroupDesc.FLD_MENU_RIGHTS_LIST);
    return menuRightsList;
  }

  public FocList getMenuAccessRightWebList(){
    FocList menuRightsList = getPropertyList(FocGroupDesc.FLD_MENU_ACCESS_RIGHTS_WEB_LIST);
    return menuRightsList;
  }

  public FocList getViewRightsList(){
    FocList viewRightsList = getPropertyList(FocGroupDesc.FLD_VIEWS_RIGHTS_LIST);
    return viewRightsList;
  }
  
  public FocList getXmlViewRightsList(){
    FocList xmlViewRightsList = getPropertyList(FocGroupDesc.FLD_XML_VIEW_RIGHTS_LIST);
    return xmlViewRightsList;
  }
  
  public FocList getWebModuleRightsList(){
    FocList viewRightsList = getPropertyList(FocGroupDesc.FLD_WEB_MODULE_RIGHTS_LIST);
    if(viewRightsList != null){
	    viewRightsList.loadIfNotLoadedFromDB();
	    if(viewRightsList.getListOrder() == null){
	    	viewRightsList.setListOrder(new FocListOrder(GrpWebModuleRightsDesc.FLD_IS_ADMIN, GrpWebModuleRightsDesc.FLD_MODULE_TITLE));
	    }
    }
    return viewRightsList;
  }
  
  public FocList getMobileModuleRightsList(){
    FocList viewRightsList = getPropertyList(FocGroupDesc.FLD_MOBILE_MODULE_RIGHTS_LIST);
    if(viewRightsList != null){
	    viewRightsList.loadIfNotLoadedFromDB();
	    if(viewRightsList.getListOrder() == null){
	    	viewRightsList.setListOrder(new FocListOrder(GrpMobileModuleRightsDesc.FLD_MODULE_TITLE));
	    }
    }
    return viewRightsList;
  }
   
  public GrpWebModuleRights getWebModuleRightsObject(String webModuleName){
  	FocList list = getWebModuleRightsList();
  	GrpWebModuleRights found = list != null ? (GrpWebModuleRights) list.searchByPropertyStringValue(GrpWebModuleRightsDesc.FLD_MODULE_NAME, webModuleName) : null;
  	return found;
  }
  
  public GrpMobileModuleRights getMobileModuleRightsObject(String mobileModuleName){
  	FocList list = getMobileModuleRightsList();
  	GrpMobileModuleRights found = (GrpMobileModuleRights) list.searchByPropertyStringValue(GrpMobileModuleRightsDesc.FLD_MODULE_NAME, mobileModuleName);
  	return found;
  }
  
  public int getWebModuleRights(String webModuleName){
  	int right = GrpWebModuleRightsDesc.ACCESS_NONE;
  	if(webModuleName != null && !webModuleName.isEmpty()){
	  	GrpWebModuleRights found = getWebModuleRightsObject(webModuleName);
	  	if(found != null){
	  		right = found.getRight();
	  	}
  	}
  	return right;
  }
  
  public void addWebModule(String webModuleName, String webModuleTitle, boolean adminModule){
  	GrpWebModuleRights right = getWebModuleRightsObject(webModuleName);
  	if(right == null && getWebModuleRightsList() != null){
  		right = (GrpWebModuleRights) getWebModuleRightsList().newEmptyItem();
  		right.setModuleName(webModuleName);
  	}
  	if(right != null){
	  	right.setAdminModule(adminModule);
	  	right.setModuleTitle(webModuleTitle);
  	}
  }
  
  public void addMobileModule(String mobileModuleName, String mobileModuleTitle){
  	GrpMobileModuleRights right = getMobileModuleRightsObject(mobileModuleName);
  	if(right == null){
  		right = (GrpMobileModuleRights) getMobileModuleRightsList().newEmptyItem();
  		right.setModuleName(mobileModuleName);
  	}
  	right.setModuleTitle(mobileModuleTitle);
  }
  
  public FocLinkOutRights getLinkOutRights(){
  	return (FocLinkOutRights) getPropertyObject(FocGroupDesc.FLD_LINK_OUT_RIGHTS);
  }
  
  public void setLinkOutRights(FocLinkOutRights lor){
  	setPropertyObject(FocGroupDesc.FLD_LINK_OUT_RIGHTS, lor);
  }
  
  public FocLinkInRights getLinkInRights(){
  	return (FocLinkInRights) getPropertyObject(FocGroupDesc.FLD_LINK_IN_RIGHTS);
  }
  
  public void setLinkInRights(FocLinkInRights lir){
  	setPropertyObject(FocGroupDesc.FLD_LINK_IN_RIGHTS, lir);
  }
  
	public void scanAndAddWebModulesToGroup(){
    Iterator<IFocWebModuleShared> iter = Globals.getIFocNotification().newWebModuleIterator();
    while(iter != null && iter.hasNext()){
    	IFocWebModuleShared focWebModule = iter.next();
      if(focWebModule.getName() != null && !focWebModule.getName().isEmpty()){
        addWebModule(focWebModule.getName(), focWebModule.getTitle(), focWebModule.isAdminConsole());
      }
    }
	}
	
	public void scanAndAddMobileModulesToGroup(){
    Iterator<IFocMobileModule> iter = Globals.getIFocNotification().newMobileModuleIterator();
    while(iter != null && iter.hasNext()){
      IFocMobileModule focWebModule = iter.next();
      if(focWebModule.getName() != null && !focWebModule.getName().isEmpty()){
        addMobileModule(focWebModule.getName(), focWebModule.getTitle());
      }
    }
	}
	
	public GroupXMLView adjustXMLViewRight(String storageName, String context, int type, String userView){
		GroupXMLView grpXMlView = findXMLView(storageName, context, type);
		if(grpXMlView == null){
			grpXMlView = addXMLView(storageName, context, type);
		}
		if(grpXMlView != null){
			grpXMlView.setView(userView);
			grpXMlView.setRight(GroupXMLViewDesc.ALLOW_NOTHING);
			grpXMlView.validate(true);
		}
		return grpXMlView;
	}
	
	public GroupXMLView findXMLView(String storageName, String context, int type){
		GroupXMLView result = null;
    FocList xmlViewRightsList = getXmlViewRightsList();

    if(xmlViewRightsList != null){
      for(int i=0; i<xmlViewRightsList.size(); i++){
        GroupXMLView xmlView = (GroupXMLView) xmlViewRightsList.getFocObject(i);
        
        if(xmlView.getStorageName().equals(storageName) && xmlView.getContext().equals(context) && xmlView.getType() == type){
          result = xmlView;
          break;
        }
      }
    }
    return result;
	}

	public GroupXMLView addXMLView(String storageName, String context, int type){
		GroupXMLView xmlView = null;
    FocList xmlViewRightsList = getXmlViewRightsList();

    if(xmlViewRightsList != null){
      xmlView = (GroupXMLView) xmlViewRightsList.newEmptyItem();
      xmlView.setStorageName(storageName);
      xmlView.setContext(context);
      xmlView.setType(type);
    }
    return xmlView;
	}

  public WFTitle getDefaultTitle(){
  	return (WFTitle) getPropertyObject(FocGroupDesc.FLD_DEFAULT_TITLE);
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static final int COL_NAME = 1;  
  public static final int COL_DESCRIPTION = 2;  
  
  public static FocList getList(int loadMode){
    return FocGroupDesc.getInstance().getFocList(loadMode);
  }
  
  public static FocGroup getAnyGroup(){
    FocList list = getList(FocList.LOAD_IF_NEEDED);
    return (FocGroup)list.getAnyItem();
  }
  
  public static FocGroup createIfNotExist(String groupName){
  	FocList list = getList(FocList.LOAD_IF_NEEDED);
  	FocGroup group = list != null ? (FocGroup) list.searchByPropertyStringValue(FocGroupDesc.FLD_NAME, groupName) : null;
  	if(group == null){
  		group = (FocGroup) list.newEmptyItem();
	    group.setName(groupName);
	    list.add(group);
	    group.validate(true);
	    list.validate(true);
  	}
  	return group;
  }
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    FocDesc desc = getFocDesc();
    FListPanel selectionPanel = null;
    if (desc != null) {
      if(list == null){
        list = getList(FocList.LOAD_IF_NEEDED);
      }
      if (list != null) {
        list.setDirectImpactOnDatabase(false);

        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        
        FTableColumn col = null;

        col = new FTableColumn(desc, FFieldPath.newFieldPath(FocGroupDesc.FLD_NAME), COL_NAME, "Name", false);
        tableView.addColumn(col);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(FocGroupDesc.FLD_DESCRIPTION), COL_DESCRIPTION, "Description", false);
        tableView.addColumn(col);
        
        selectionPanel.construct();

        selectionPanel.setDirectlyEditable(false);

        FValidationPanel savePanel = selectionPanel.showValidationPanel(true);
        if (savePanel != null) {
          list.forceControler(true);
          savePanel.addSubject(list);
          savePanel.setValidationListener(new FValidationListener(){
            public boolean proceedValidation(FValidationPanel panel) {
              return true;
            }

            public boolean proceedCancelation(FValidationPanel panel) {
              return true;
            }

            public void postValidation(FValidationPanel panel) {
              getList(FocList.FORCE_RELOAD);
            }

            public void postCancelation(FValidationPanel panel) {
              getList(FocList.FORCE_RELOAD);              
            }
          });
        }

        selectionPanel.requestFocusOnCurrentItem();
      }
    }
    selectionPanel.setFrameTitle("Group");
    
    return selectionPanel;
  }

  public static void printDebug(String title){
    FocList list = getList(FocList.LOAD_IF_NEEDED);
    if(list != null){
      Globals.logString("");
      Globals.logString(title);
      Iterator iter = list.focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocGroup group = (FocGroup)iter.next();
        if(group != null){
          Globals.logString("Group "+group.toString()+" "+group.getReference().getInteger()+" : "+group.getName());
        }
      }
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getFocDesc() {
    return FocGroupDesc.getInstance();
  }
  
  @Override
  public void toJson_Embedded(B01JsonBuilder builder) {
    builder.beginObject();
    appendKeyValueForFieldName(builder, null, "NAME");
    appendKeyValueForFieldName(builder, null, "DESCRIP");
    builder.endObject();
  }
  
  @Override
  public void toJson_InList(B01JsonBuilder builder) {
  	toJson_Embedded(builder);
  }
}
