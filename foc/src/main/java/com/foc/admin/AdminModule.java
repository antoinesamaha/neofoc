/*
 * Created on Oct 14, 2004
 */
package com.foc.admin;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;

import com.fab.gui.xmlView.UserXMLViewDesc;
import com.fab.gui.xmlView.XMLViewDefinitionDesc;
import com.foc.Application;
import com.foc.FocLangKeys;
import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.business.BusinessMenu;
import com.foc.business.BusinessModule;
import com.foc.business.multilanguage.LanguageDesc;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.business.printing.PrnLayoutDefinitionDesc;
import com.foc.db.lock.LockManager;
import com.foc.db.migration.MigrationMenu;
import com.foc.desc.AutoPopulatable;
import com.foc.desc.FocDesc;
import com.foc.desc.FocModule;
import com.foc.desc.FocObject;
import com.foc.event.FValidationListener;
import com.foc.gui.FGOptionPane;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.view.ColumnsConfigDesc;
import com.foc.gui.table.view.UserViewDesc;
import com.foc.gui.table.view.ViewConfigDesc;
import com.foc.list.FocList;
import com.foc.menu.FAbstractMenuAction;
import com.foc.menu.FMenu;
import com.foc.menu.FMenuAction;
import com.foc.menu.FMenuItem;
import com.foc.menu.FMenuList;
import com.foc.saas.manager.SaaSConfigDesc;

/**
 * @author 01Barmaja
 */
public class AdminModule extends FocModule {
 
  public static final String ADMIN_USER   = "ADMIN";
  
  private boolean activateLogin = false;
  
  public AdminModule(boolean activateLogin) {
    this.activateLogin = activateLogin;
    
    scanModelPackage("com.foc.admin");
  }
  
  public void checkTables() {
  	Globals.getApp().getDataSource().command_CheckTables();
  }
  
  @SuppressWarnings("serial")
	public FMenu getAdminMenu(){
    FMenuList mainMenu = new FMenuList(FocLangKeys.MENU_ADMIN_MENU);

    //User
    FMenuList userMenu = new FMenuList(FocLangKeys.MENU_USER);
    mainMenu.addMenu(userMenu);
    
    FMenuItem userItem = new FMenuItem(FocLangKeys.MENU_USER, new FMenuAction(FocUser.getFocDesc(), true));
    userMenu.addMenu(userItem);
    
    //Group
    FMenuList groupMenu = new FMenuList(FocLangKeys.MENU_GROUP);
    mainMenu.addMenu(groupMenu);

    FMenuItem menuRightsItem = new FMenuItem("Global Menu Rights", 'M', new FAbstractMenuAction(MenuRightsDesc.getInstance(), true) {
    	MenuRightsDisplayList menuRightsDisplayList = null;
    	
    	@Override
			public FPanel generatePanel() {
				FocList menuRightsList = MenuRightsDesc.getGlobalMenuRightsList(FocList.FORCE_RELOAD);
				menuRightsDisplayList  = new MenuRightsDisplayList(menuRightsList, null);				
				MenuRightsGuiTreePanel treePanel = new MenuRightsGuiTreePanel(menuRightsDisplayList.getDisplayList(), FocObject.DEFAULT_VIEW_ID, false);
				FValidationPanel vPanel = treePanel.showValidationPanel(true);
				vPanel.setValidationListener(new FValidationListener() {
					@Override
					public boolean proceedValidation(FValidationPanel panel) {
						return true;
					}
					
					@Override
					public boolean proceedCancelation(FValidationPanel panel) {
						return true;
					}
					
					@Override
					public void postValidation(FValidationPanel panel) {
	          if(menuRightsDisplayList != null){
	            menuRightsDisplayList.updateRealList();
	            menuRightsDisplayList.getRealList().validate(true);
	            menuRightsDisplayList.dispose();
	            menuRightsDisplayList = null;
	          }
					}
					
					@Override
					public void postCancelation(FValidationPanel panel) {
					}
				});
				
				return treePanel;
			}
		});
    groupMenu.addMenu(menuRightsItem);
    
    FMenuItem groupItem = new FMenuItem(FocLangKeys.MENU_GROUP, new FMenuAction(FocGroup.getFocDesc(), true));
    groupMenu.addMenu(groupItem);

    //Company
    if(BusinessModule.getInstance().isMultiCompany()){
      FMenuList companyMenu = new FMenuList("Company", 'C');
      BusinessMenu.addCompanyMenu(companyMenu);
      //WorkflowMenu.addGeneralMenus(companyMenu);

      mainMenu.addMenu(companyMenu);
    }
    
    //Cash
    /*
    if(Globals.getApp().isCashDeskModuleIncluded()){
      FMenuList cashDeskMenu = CashDeskModule.newAdminMenuList();
      mainMenu.addMenu(cashDeskMenu);
    }
    */
    
    //Adapt data model
    //Data Model List
    FMenuList dataModelMenu = new FMenuList("Data Model", 'D');
    mainMenu.addMenu(dataModelMenu);
    
    //Adapt Data Model Item    
    AbstractAction adaptAction = new AbstractAction(){
      public void actionPerformed(ActionEvent e){
        Application app = Globals.getApp();
        app.declareFocObjects();
        app.adaptDataModel(false, false);
      }
    };
    FMenuItem adaptItem = new FMenuItem(FocLangKeys.MENU_ADAPT_DATA_MODEL, adaptAction);
    dataModelMenu.addMenu(adaptItem);
    
    //Adapt Data Model Item    
    adaptAction = new AbstractAction(){
      public void actionPerformed(ActionEvent e){
        Application app = Globals.getApp();
        app.declareFocObjects();
        app.adaptDataModel(true, false);
      }
    };
    adaptItem = new FMenuItem("Force Alter On All Tables", 'F', adaptAction);
    dataModelMenu.addMenu(adaptItem);    
    
    addAutoPopulateMenuList(dataModelMenu);
    
    FMenuList migrationMenu = new FMenuList("Migration", 'M', "MIGRATION_MAIN");
    MigrationMenu.addMigrationMenus(migrationMenu);    
    mainMenu.addMenu(migrationMenu);
    
    //Check table locks
    FMenuList checkLocksMenu = new FMenuList(FocLangKeys.MENU_CHECK_LOCKS);
    AbstractAction checkLockAction = new AbstractAction(){
      public void actionPerformed(ActionEvent e){
        LockManager lockManager = new LockManager();
        FPanel panel = lockManager.newDetailsPanel();
        Globals.getDisplayManager().popupDialog(panel, MultiLanguage.getString(FocLangKeys.MENU_CHECK_LOCKS), true);
      }
    };
    FMenuItem lockItem = new FMenuItem(FocLangKeys.MENU_CHECK_LOCKS, checkLockAction);
    checkLocksMenu.addMenu(lockItem);
    mainMenu.addMenu(checkLocksMenu);
    
    return mainMenu;
  }

	@Override
	public void afterConstruction() {
	}
	
  public void addAutoPopulateMenuList(FMenuList dataModelMenu){
  	ArrayList<AutoPopulatable> array = new ArrayList<AutoPopulatable>();
  	
    Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
    while(iter != null && iter.hasNext()){
    	IFocDescDeclaration focDescDeclaration = iter.next();
    	if(focDescDeclaration != null){
    		FocDesc focDesc = focDescDeclaration.getFocDescription();
    		if(focDesc instanceof AutoPopulatable){
    			array.add((AutoPopulatable)focDesc);
    		}
    	}
    }
    
    if(array.size() > 0){
    	FMenuList populatableList = new FMenuList("Auto Populate", 'A');
    	dataModelMenu.addMenu(populatableList);
    	
    	for(int i=0; i<array.size(); i++){
    		AutoPopulatable autoPopulatable = array.get(i);
    		if(autoPopulatable != null){
  	  	  AbstractAction populateAction = new AutoPopulateMenuAction(autoPopulatable);
  	  	  FMenuItem populateItem = new FMenuItem(autoPopulatable.getAutoPopulatableTitle(), autoPopulatable.getAutoPopulatableTitle().charAt(0), populateAction);
  	  	  populatableList.addMenu(populateItem);
    		}
    	}
    }
  }
  
  @SuppressWarnings("serial")
	private class AutoPopulateMenuAction extends AbstractAction {
  	
  	private AutoPopulatable autoPopulatable = null;
  	
  	public AutoPopulateMenuAction(AutoPopulatable autoPopulatable){
  		this.autoPopulatable = autoPopulatable;
  	}
  	
  	public void dispose(){
  		autoPopulatable = null;
  	}
  	
		public void actionPerformed(ActionEvent arg0) {
			if(autoPopulatable != null && !FGOptionPane.popupOptionPane_YesNo("Database Automatic Population", "Are you sure you want the software to automatically create new "+autoPopulatable.getAutoPopulatableTitle())){
				autoPopulatable.populate();
			}
		}
  }

	@Override
	public void declare() {
		Globals.getApp().declareModule(this);
	}

	@Override
	public void declareFocObjectsOnce() {
    declareFocDescClass(FocVersion.class);    
    if(activateLogin){
    	declareFocDescClass(FocUser.class);
    	declareFocDescClass(FocGroup.class);
    	declareFocDescClass(UserViewDesc.class);
    	declareFocDescClass(ViewConfigDesc.class);
      declareFocDescClass(ColumnsConfigDesc.class);
      declareFocDescClass(MenuRightsDesc.class);
      declareFocDescClass(GrpViewRightsDesc.class);
      declareFocDescClass(GrpMobileModuleRightsDesc.class);
      declareFocDescClass(GrpWebModuleRightsDesc.class);
      declareFocDescClass(MenuAccessRightWebDesc.class);      
      declareFocDescClass(LanguageDesc.class);
      declareFocDescClass(GroupXMLViewDesc.class);
      declareFocDescClass(ActiveUserDesc.class);
      declareFocDescClass(DocRightsGroupDesc.class);
      declareFocDescClass(DocRightsGroupUsersDesc.class);
      declareFocDescClass(SaaSConfigDesc.class);
      
      declareFocDescClass(XMLViewDefinitionDesc.class);
      declareFocDescClass(PrnLayoutDefinitionDesc.class);
      declareFocDescClass(UserXMLViewDesc.class);
      declareFocDescClass(FocUserHistoryDesc.class);
    }
	}

	//--------------------------------------------------------------
	// STATIC INSTANCE
	//--------------------------------------------------------------
	
  private static AdminModule instance = null;
  
  public static AdminModule getInstance(){
  	if(instance == null){
  		instance = new AdminModule(true);
  	}
  	return instance;
  }
  
  public static AdminModule getInstance(boolean withLogin){
  	if(instance == null){
  		instance = new AdminModule(withLogin);
  	}
  	return instance;
  }
}
