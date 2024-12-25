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
// INSTANCE
//    MAIN
//    PANEL
// LIST
// LOGIN
// DESCRIPTION

/*
 * Created on 20-May-2005
 */
package com.foc.admin;

import java.awt.image.BufferedImage;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.business.adrBook.Contact;
import com.foc.business.adrBook.ContactDesc;
import com.foc.business.company.Company;
import com.foc.business.company.CompanyDesc;
import com.foc.business.company.UserCompanyRights;
import com.foc.business.company.UserCompanyRightsDesc;
import com.foc.business.department.Department;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.business.workflow.WFOperator;
import com.foc.business.workflow.WFOperatorDesc;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.WFTitle;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.business.workflow.rights.RightLevel;
import com.foc.business.workflow.rights.RightLevelDesc;
import com.foc.business.workflow.rights.UserTransactionRight;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.ecomerce.EComConfiguration;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.FocListOrder;
import com.foc.property.FImageProperty;
import com.foc.property.FMultipleChoice;
import com.foc.property.FObject;
import com.foc.property.FPassword;
import com.foc.property.FProperty;
import com.foc.property.FString;
import com.foc.shared.json.B01JsonBuilder;
import com.foc.util.ASCII;
import com.foc.util.Encryptor;
import com.foc.util.IFocIterator;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FocUser extends FocObject {

  public static final int LOGIN_VIEW_ID                    = 2;
  public static final int SET_PASSWORD_VIEW_ID             = 3;
  public static final int CHANGE_PASSWORD_VIEW_ID          = 4;
  public static final int CHANGE_LANGUAGE_VIEW_ID          = 5;
  public static final int USER_GROUP_INFO_VIEW_ID          = 6;
  public static final int VIEW_CHANGE_MULTI_COMPANY_FILTER = 7;
  public static final String EMPTY_USER = "username";

  public static final int PASSWORD_POLICY_NONE                           = 0; 
  public static final int PASSWORD_POLICY_SIX_LETTERS_NUMBER             = 1;
  public static final int PASSWORD_POLICY_TWELVE_UPPLER_LOWER_NBR_SYMBOL = 2;

//USERREFACTOR  
//  private FocList companyList   = null;
//  private FocList titlesList    = null;
//  private FocList operatorsList = null;
//  private FocList sitesList     = null;
    private ArrayList<WFOperator> operatorsArrayForThisUser_AllAreas = null;

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------


  public FocUser(FocConstructor constr) {
    super(constr);

    newFocProperties();

    setPropertyString(FocUserDesc.FLD_NAME, "");
    setPropertyString(FocUserDesc.FLD_PASSWORD, "");
    setPropertyMultiChoice(FocUserDesc.FLD_FONT_SIZE, 0);
    setPropertyMultiChoice(FocUserDesc.FLD_LANGUAGE, MultiLanguage.getCurrentLanguage().getId());
    
  }

  @Override
  public void dispose(){
  	super.dispose();
  //USERREFACTOR
  	/*
  	dispose_TitlesList();
  	dispose_SitesList();
  	dispose_CompanyList();
 	  */
  	dispose_OperatorsArrayForThisUserAllAreas();
  }
  
//USERREFACTOR
  /*
	public void dispose_TitlesList(){
		if(titlesList != null){
			titlesList.dispose();
			titlesList = null;
		}
		if(operatorsList != null){
			operatorsList.dispose();
			operatorsList = null;
		}
	}
  
	public void dispose_SitesList(){
		if(sitesList != null){
			sitesList.dispose();
			sitesList = null;
		}
	}

	public void dispose_CompanyList(){
		if(companyList != null){
			companyList.dispose();
			companyList = null;
		}
	}
  */

	public void dispose_OperatorsArrayForThisUserAllAreas(){
		if(operatorsArrayForThisUser_AllAreas != null){
			for(int i=0; i<operatorsArrayForThisUser_AllAreas.size(); i++){
				WFOperator operator = operatorsArrayForThisUser_AllAreas.get(i);
				if(operator != null){
					operator.dispose();
				}
			}
			operatorsArrayForThisUser_AllAreas.clear();
			operatorsArrayForThisUser_AllAreas = null;
		}
	}
	
  @Override
	public FProperty getFocProperty(int fieldID) {
		FProperty prop = super.getFocProperty(fieldID);
		return prop;
	}
  
  @Override
  public String getSelectionFilterExpressionFor_ObjectProperty(int fieldID) {
    String expression = super.getSelectionFilterExpressionFor_ObjectProperty(fieldID);
    if(fieldID == FocUserDesc.FLD_GROUP && isGuest()){
      FField fld = FocGroupDesc.getInstance().getFieldByID(FocGroupDesc.FLD_GUEST_APPLICABLE);
      expression = fld.getName()+"=true";
    }
    return expression;
  }

	@Override
	public FocList getObjectPropertySelectionList(int fieldID) {
		FocList list = null;
//USERREFACTOR
		/*
		if(fieldID == FocUserDesc.FLD_CURRENT_COMPANY){
		  list = getCompanyList();
		}else if(fieldID == FocUserDesc.FLD_CURRENT_SITE){
			list = getSitesList();
		}else if(fieldID == FocUserDesc.FLD_CURRENT_TITLE){
			list = getTitlesList();
		}
		*/
		return list;
	}

//USERREFACTOR
	/*
	public void clearCompanyList(){
	  companyList = null;
	}
	
  public FocList getCompanyList(){
    if(companyList == null){
      buildCompanyList();
    }
    return companyList;
  }
  
  private FocList buildCompanyList(){
    companyList = new FocList(new FocLinkSimple(CompanyDesc.getInstance()));
    companyList.setCollectionBehaviour(true);
    companyList.setDbResident(false);
    
    FocList companyRightsList = getCompanyRightsList();
    if(companyRightsList != null){
      companyRightsList.reloadFromDB();
      
      for(int i=0; i<companyRightsList.size(); i++){
        UserCompanyRights userCompanyRights = (UserCompanyRights) companyRightsList.getFocObject(i);
        int accessRight = userCompanyRights.getAccessRight();
        
        if(accessRight != UserCompanyRightsDesc.ACCESS_RIGHT_NONE){
          Company company = userCompanyRights.getCompany();
          if(company != null){
            companyList.add(company);
          }
        }
      }
    }
    return companyList;
  }
  
  public synchronized FocList re_buildCompanyList(){
  	if(companyList == null) {
  		return buildCompanyList();
  	} else {
  		HashMap<Long, Company> toRemove = new HashMap<Long, Company>();
  		for(int i=0; i<companyList.size(); i++) {
  			Company comp = (Company) companyList.getFocObject(i);
  			toRemove.put(comp.getReferenceInt(), comp);
  		}
  		
	    FocList companyRightsList = getCompanyRightsList();
	    if(companyRightsList != null){
	      companyRightsList.reloadFromDB();
	      
	      for(int i=0; i<companyRightsList.size(); i++){
	        UserCompanyRights userCompanyRights = (UserCompanyRights) companyRightsList.getFocObject(i);
	        int accessRight = userCompanyRights.getAccessRight();
	        
	        if(accessRight != UserCompanyRightsDesc.ACCESS_RIGHT_NONE){
	          Company company = userCompanyRights.getCompany();
	          if(company != null){
	          	if(toRemove.containsKey(company.getReferenceInt())) {
	          		toRemove.remove(company.getReferenceInt());
	          	} else {
	          		companyList.add(company);
	          	}
	          }
	        }
	      }
	    }
	    
	    Iterator<Company> iter = toRemove.values().iterator();
	    while (iter != null && iter.hasNext()) {
	    	Company company = iter.next();
	    	companyList.remove(company);
	    }
	    
	    return companyList;
  	}
  }
	
  public void re_buildSitesList() {
  	if(sitesList == null) {
			sitesList = new FocList(new FocLinkSimple(WFSiteDesc.getInstance()));
			sitesList.setCollectionBehaviour(true);
			sitesList.setDbResident(false);
  	}
  	
		HashMap<Long, WFSite> toRemove = new HashMap<Long, WFSite>();
		for(int i=0; i<sitesList.size(); i++) {
			WFSite site = (WFSite) sitesList.getFocObject(i);
			toRemove.put(site.getReferenceInt(), site);
		}
	
		Company company = getCurrentCompany();
		if(company != null){
	//		FocList listAll = company.getSiteList();
			FocList listAll = WFSiteDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
			listAll.iterate(new IFocIterator() {
				@Override
				public boolean treatElement(Object element) {
					FocListElement listElem = (FocListElement) element;
					WFSite currSite = (WFSite) listElem.getFocObject();
					if(currSite != null && currSite.getCompany() != null && currSite.getCompany().equalsRef(company)){//20160107
						FocList listOpperators = currSite.getOperatorList();
						if(listOpperators.searchByPropertyObjectReference(WFOperatorDesc.FLD_USER, getReference().getInteger()) != null){
							if(toRemove.containsKey(currSite.getReferenceInt())) {
								toRemove.remove(currSite.getReferenceInt());
							} else {
								sitesList.add(currSite);
							}
						}
					}
					return false;
				}
			});
			
	    Iterator<WFSite> iter = toRemove.values().iterator();
	    while (iter != null && iter.hasNext()) {
	    	WFSite site = iter.next();
	    	sitesList.remove(site);
	    }
		}
  }
  
	public FocList getSitesList(){
		if(sitesList == null){
			re_buildSitesList();
		}
		
		return sitesList;
	}

	public FocList re_buildTitlesList() {
		if(titlesList == null){
			titlesList = new FocList(new FocLinkSimple(WFTitleDesc.getInstance()));
			titlesList.setCollectionBehaviour(true);
			titlesList.setDbResident(false);
		}

		HashMap<Long, WFTitle> toRemove = new HashMap<Long, WFTitle>();
		for(int i=0; i<titlesList.size(); i++) {
			WFTitle title = (WFTitle) titlesList.getFocObject(i);
			toRemove.put(title.getReferenceInt(), title);
		}

		WFSite site = getCurrentSite();
		while(site != null){
			FocList operatorList = site.getOperatorList();
			if(operatorList != null){
				for(int i=0; i<operatorList.size(); i++){
					WFOperator operator = (WFOperator) operatorList.getFocObject(i);
					if(operator.getUser() != null && operator.getUser().equalsRef(Globals.getApp().getUser_ForThisSession())){
						WFTitle title = operator.getTitle();
						if(title != null && titlesList.searchByReference(title.getReference().getInteger()) == null){
							if(toRemove.containsKey(title.getReferenceInt())) {
								toRemove.remove(title.getReferenceInt());
							} else {
								titlesList.add(title);
							}
						}
					}
				}
			}
			
			WFSite fatherSite = (WFSite) site.getFatherObject();
			if(fatherSite == null || fatherSite == site) break;
			site = fatherSite;
		}

    Iterator<WFTitle> iter = toRemove.values().iterator();
    while (iter != null && iter.hasNext()) {
    	WFTitle title = iter.next();
    	titlesList.remove(title);
    }
		
    return titlesList;
	}
	
	public FocList getTitlesList(){
		if(titlesList == null){
			re_buildTitlesList();
		}
		return titlesList;
	}
	
	public FocList getOperatorList(){
		if(operatorsList == null){
			operatorsList = new FocList(new FocLinkSimple(WFTitleDesc.getInstance()));
			operatorsList.setCollectionBehaviour(true);
			operatorsList.setDbResident(false);

			WFSite site = getCurrentSite();
			while(site != null){
				FocList operatorList = site.getOperatorList();
				for(int i=0; i<operatorList.size(); i++){
					WFOperator operator = (WFOperator) operatorList.getFocObject(i);
					if(operator.getUser().equalsRef(FocUser.this)){
						operatorsList.add(operator);
					}
				}
				site = (WFSite) site.getFatherObject();
			}
		}
		return operatorsList;
	}
*/	
	public FocUser getReplacementUserActingAs(){
		return (FocUser) getPropertyObject(FocUserDesc.FLD_REPLACEMENT_USER_ACTING_AS);
	}
	
	public synchronized boolean hasTitle(WFSite area, WFTitle title, Department department){
		boolean has = false;
		
		userSites_RebuildIfNeeded();
		
		while(!has && area != null) {
			
			if (userSites != null && area != null) {
				UserSite userSite = userSites.get(area.getReferenceInt());
				if(userSite != null) {
					UserOperator uo = userSite.getTitle(title, department);
					has = uo != null;
				}
			}
			
			area = (WFSite) area.getFatherObject();
		}
		
		/*
		if(getOperatorList() != null){
			FocList operatorList = getOperatorList();
			for(int i=0; i<operatorList.size() && !has; i++){
				WFOperator operator = (WFOperator) operatorList.getFocObject(i);
				if(operator != null && operator.getArea() != null && operator.getTitle() != null){
					if(FocObject.equal(operator.getTitle(), title) && (operator.getDepartment() == null || operator.getDepartment().equalsRef(department))){
						
						do{
							if(operator.getArea().equalsRef(area)){
								has = true;
							}
							area = area != null ? (WFSite) area.getFatherObject() : null;
						}while(!has && area != null);
						
					}						
				}
			}
		}
		*/
		return has;
	}

	public boolean hasTitle_InActingAs(WFSite area, WFTitle title, Department department){
		boolean has = false;
		
		FocList userList = FocUserDesc.getList();
		if(userList != null){
			for(int i=0; i<userList.size() && !has; i++){
				FocUser user = (FocUser) userList.getFocObject(i);
				FocUser actingAsUser = user.getReplacementUserActingAs();
				if(FocObject.equal(actingAsUser, this)){
					has = user.hasTitle(area, title, department);
				}
			}
		}
		
		return has;
	}
	
	public boolean isAdmin(){
  	return getName().equals(AdminModule.ADMIN_USER);
  }

	public boolean isSuspended(){
  	return getPropertyBoolean(FocUserDesc.FLD_SUSPENDED);
  }
	
	public void setSuspended(boolean suspended){
  	setPropertyBoolean(FocUserDesc.FLD_SUSPENDED, suspended);
  }
	
	public boolean isGuest(){
  	return getPropertyBoolean(FocUserDesc.FLD_IS_GUEST);
  }
	
	public void setGuest(boolean guest){
  	setPropertyBoolean(FocUserDesc.FLD_IS_GUEST, guest);
  }
	
	public boolean isSaasAdmin(){
  	return getPropertyBoolean(FocUserDesc.FLD_IS_SAAS_ADMIN);
  }
	
	public void setSaasAdmin(boolean admin){
  	setPropertyBoolean(FocUserDesc.FLD_IS_SAAS_ADMIN, admin);
  }

	public boolean isTransactionOrderIncremental(){
  	return getPropertyBoolean(FocUserDesc.FLD_TRANSACTION_SORTING_INCREMENTAL);
  }

	public boolean isPrintUponSave(){
  	return getPropertyBoolean(FocUserDesc.FLD_PRINT_UPON_SAVE);
  }

  public int getMultiCompanyMode(){
  	return getPropertyMultiChoice(FocUserDesc.FLD_MULTI_COMPANY_MODE);
  }
  
  public int getSaasApplicationRole(){
  	return getPropertyInteger(FocUserDesc.FLD_SAAS_APPLICATION_ROLE);
  }
  
  public void setSaasApplicationRole(int applicationRole){
  	setPropertyInteger(FocUserDesc.FLD_SAAS_APPLICATION_ROLE, applicationRole);
  }
  
  public void setSaasApplicationRole(String applicationRole){
  	FMultipleChoice objProp = (FMultipleChoice) getFocProperty(FocUserDesc.FLD_SAAS_APPLICATION_ROLE);
  	if(objProp != null){
  		objProp.setString(applicationRole);
  	}
  }

  public Company getCurrentCompany(){
  	Company company = (Company) getPropertyObject(FocUserDesc.FLD_CURRENT_COMPANY);
  	return company;
  }

  public void setCurrentCompany(Company company){
  	setPropertyObject(FocUserDesc.FLD_CURRENT_COMPANY, company);
  }

  public WFSite getCurrentSite(){
  	WFSite site = (WFSite) getPropertyObject(FocUserDesc.FLD_CURRENT_SITE);
  	return site;
  }

  public void setCurrentSite(WFSite site){
  	setPropertyObject(FocUserDesc.FLD_CURRENT_SITE, site);
  }

  public WFTitle getCurrentTitle(){
  	WFTitle site = (WFTitle) getPropertyObject(FocUserDesc.FLD_CURRENT_TITLE);
  	return site;
  }

  public void setCurrentTitle(WFTitle title){
  	setPropertyObject(FocUserDesc.FLD_CURRENT_TITLE, title);
  }
  
  public boolean isSimulationActive(){
  	return getPropertyBoolean(FocUserDesc.FLD_CURRENT_SIMULATION_ACTIVE);
  }

  public void setSimulationActive(boolean active){
  	setPropertyBoolean(FocUserDesc.FLD_CURRENT_SIMULATION_ACTIVE, active);
  }
  
  public String getFullName(){
  	return getPropertyString(FocUserDesc.FLD_FULL_NAME);
  }
  
  public void setFullName(String fullName){
    FString pFullName = (FString) getFocProperty(FocUserDesc.FLD_FULL_NAME);
    pFullName.setString(fullName);
  }
  
  public String getName() {
    FString pName = (FString) getFocProperty(FocUserDesc.FLD_NAME);
    return pName.getString();
  }

  public void setName(String name) {
    FString pName = (FString) getFocProperty(FocUserDesc.FLD_NAME);
    pName.setString(name);
  }

  public String getPassword() {
    FPassword pPass = (FPassword) getFocProperty(FocUserDesc.FLD_PASSWORD);
    return pPass.getString();
  }

  public void setPassword(String password) {
    FPassword pPass = (FPassword) getFocProperty(FocUserDesc.FLD_PASSWORD);
    pPass.setString(password);
  }

  public static String newRandomPassword(){
  	String newPassStr = ASCII.generateRandomString(6);
  	return newPassStr;
  }
  
  public String resetPassword(){
		String newPassStr = newRandomPassword(); 
		String newPassEncripted = Encryptor.encrypt_MD5(String.valueOf(newPassStr));
		setPassword(newPassEncripted);
		validate(true);
		return newPassStr;
  }
  
  public int getPageWidth(){
  	return getPropertyInteger(FocUserDesc.FLD_PAGE_WIDTH);	
  }
  
  public FocGroup getGroup() {
    FObject pGroup = (FObject) getFocProperty(FocUserDesc.FLD_GROUP);
    return pGroup != null ? (FocGroup) pGroup.getObject_CreateIfNeeded() : null;
  }

  public void setGroup(FocGroup group) {
    setPropertyObject(FocUserDesc.FLD_GROUP, group);
  }
  
  public Contact getContact() {
    return (Contact) getPropertyObject(FocUserDesc.FLD_CONTACT);
  }
  
  public void setContact(Contact contact) {
    setPropertyObject(FocUserDesc.FLD_CONTACT, contact);
    if(contact != null){
    	setFullName(contact.getFullName());
    }
  }
  
  public void createContactIfNotExist(){
		Contact contact = getContact();
		if(contact == null){
			FocList contacList = ContactDesc.getList(FocList.LOAD_IF_NEEDED);
			contact = (Contact) contacList.newEmptyItem();
			
			if(Globals.getApp() != null && Globals.getApp().getCurrentCompany() != null){
				contact.setAdrBookParty(Globals.getApp().getCurrentCompany().getAdrBookParty());
			}
			
			setContact(contact);
		}
  }
  
  public FocObject getAppGroup() {
    FocGroup group = getGroup();
    return group != null ? group.getAppGroup() : null;
  }

  public BufferedImage getSignature(){
  	FImageProperty imageProp = (FImageProperty) getFocProperty(FocUserDesc.FLD_SIGNATURE_IMAGE);
  	return imageProp != null ? imageProp.getImageValue() : null;
  }
  
  public int getFontSize() {
    int size = ConfigInfo.getFontSize();
    FMultipleChoice choice = (FMultipleChoice) getFocProperty(FocUserDesc.FLD_FONT_SIZE);
    if(choice != null){
      size = choice.getInteger();
    }
    return size;
  }

  public void setFontSize(int fontSize) {
    FMultipleChoice choice = (FMultipleChoice) getFocProperty(FocUserDesc.FLD_FONT_SIZE);
    if(choice != null){
      choice.setInteger(fontSize);
    }
  }

  public boolean isEnableToolTipText(){
  	return getPropertyBoolean(FocUserDesc.FLD_ENABLE_TOOL_TIP_TEXT);
  }

  public void setEnableToolTipText(boolean enableToolTipText){
  	setPropertyBoolean(FocUserDesc.FLD_ENABLE_TOOL_TIP_TEXT, enableToolTipText);
  }

  public String getSendEmailCommandLine(){
  	return getPropertyString(FocUserDesc.FLD_EMAIL_SEND_COMMAND_LINE);
  }

  public void setSendEmailCommandLine(String email){
  	setPropertyString(FocUserDesc.FLD_EMAIL_SEND_COMMAND_LINE, email);
  }
  
  public boolean isContextHelpActivated(){
    return getPropertyBoolean(FocUserDesc.FLD_CONTEXT_HELP_ACTIVATION);
  }
  
  public void setContextHelpActivated(boolean mode){
    setPropertyBoolean(FocUserDesc.FLD_CONTEXT_HELP_ACTIVATION, mode);
  }
  
	public int getFaliedLoginAttempts() {
		return getPropertyInteger(FocUserDesc.FLD_FAILED_LOGIN_ATTEMPTS);
	}

	public void setFaliedLoginAttempts(int val) {
		setPropertyInteger(FocUserDesc.FLD_FAILED_LOGIN_ATTEMPTS, val);
	}

	public boolean isLocked() {
		return getPropertyBoolean(FocUserDesc.FLD_LOCKED);
	}

	public void setLocked(boolean mode) {
		setPropertyBoolean(FocUserDesc.FLD_LOCKED, mode);
	}
	
	public Date getLockDateTime(){
    return (Date) getPropertyDate(FocUserDesc.FLD_LOCK_DATETIME);
  }

  public void setLockDateTime(Date obj){
    setPropertyDate(FocUserDesc.FLD_LOCK_DATETIME, obj);
  }
  
	public void updateFailedAttempts() {
		setFaliedLoginAttempts(getFaliedLoginAttempts() + 1);
		checkAndLockAccount();
	}

	public void checkAndLockAccount() {
		int accountLockThreshold = ConfigInfo.getAccountLockThreshold();
		if (accountLockThreshold > 0 && getFaliedLoginAttempts() >= accountLockThreshold && !isLocked()) {
			setLocked(true);
			setLockDateTime(Globals.getDBManager().getCurrentDate());
		}
	}

	public void unLockAccount() {
		setFaliedLoginAttempts(0);
		setLocked(false);
		setLockDateTime(new Date(0));
	}
  
  public int getRightsLevel() {
    int lvl = 1;
    FocGroup group = getGroup();
    if (group != null) {
      lvl = group.getRightsLevel();
    }
    return lvl;
  }

  public FocList getCompanyRightsList(){
  	FocList companyList = getPropertyList(FocUserDesc.FLD_COMPANY_RIGHTS_LIST);
  	if(companyList != null){
  		companyList.setListOrder(new FocListOrder(CompanyDesc.FLD_NAME));
  	}
  	return companyList;
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public static int getLoginStatus_ForUsernamePassword(String userName, String encryptedPassword) {
  	FocLoginAccess userAccess = new FocLoginAccess();
  	int           status     = userAccess.checkUserPassword(userName, encryptedPassword);
  	userAccess.dispose();
  	return status;
  }

  public static void userLoginCheck(String userName, String encryptedPassword) {
  	int status = getLoginStatus_ForUsernamePassword(userName, encryptedPassword);
  	Globals.getApp().setLoginStatus(status);
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static void printDebug(FocList list) {
    if (list != null) {
      Globals.logString("Users list ......");
      Iterator iter = list.focObjectIterator();
      while (iter != null && iter.hasNext()) {
        FocUser user = (FocUser) iter.next();
        FObject groupProp = (FObject) user.getFocProperty(FocUserDesc.FLD_GROUP);

        FocGroup group = user.getGroup();
        Globals.logString("User " + user.getName() + " group " + groupProp.getLocalReferenceToString() + " " + group.toString_Super() + " " + groupProp.getLocalReferenceInt() + " " + group.getReference().getInteger() + " " + group.getName());
      }
    }
  }

  public static FocUser newUser(String name) {
    FocUser focUser = new FocUser(new FocConstructor(FocUser.getFocDesc(), null, null));
    focUser.setName(name);
    return focUser;
  }

  public static FocUser findUser(String name) {
    FocUser foundUser = null;
    
    FocList listOfUsers = FocUserDesc.getList();
    for(int i=0; i<listOfUsers.size() && foundUser == null; i++){
      FocUser currentUser = (FocUser) listOfUsers.getFocObject(i);
      currentUser.getGroup();
      if(currentUser.getName().equals(name)){
        foundUser = currentUser;
        foundUser.getGroup();
      }
    }
    return foundUser;
  }

  public boolean isThirdParty(){
  	return getContact() != null && getGroup() == null;
  }
  
  public static FocUser findUser(Contact contact) {
  	FocList userList = FocUserDesc.getList();
  	FocUser user     = (FocUser) userList.searchByPropertyObjectValue(FocUserDesc.FLD_CONTACT, contact);
  	return user;
  }

//USERREFACTOR
/*
  public void companyChanged(){
  	dispose_SitesList();
		FocList sitesListApplicable = getSitesList();
		if(sitesListApplicable != null){
			if(getCurrentSite() == null || !sitesListApplicable.containsObject(getCurrentSite())){
				setCurrentSite((WFSite) sitesListApplicable.getAnyItem());
				siteChanged();			
			}
		}
  }

  public void siteChanged(){
  	dispose_TitlesList();
		FocList titlesListApplicable = getTitlesList();
		if(titlesListApplicable != null){
			if(getCurrentTitle() == null || !titlesListApplicable.containsObject(getCurrentTitle())){
				setCurrentTitle((WFTitle) titlesListApplicable.getAnyItem());
			}
		}
  }
*/
  
	public UserCompanyRights addCompanyRights(Company comp, int rights) {
		UserCompanyRights userCompany = null;
		FocList list = getCompanyRightsList();
		if(list != null){
			userCompany = (UserCompanyRights) list.searchByPropertyObjectValue(UserCompanyRightsDesc.FLD_COMPANY, comp);
			if(userCompany == null){
				userCompany = (UserCompanyRights) list.newEmptyItem();
				userCompany.setCompany(comp);
			}
			if(userCompany != null){
				userCompany.setAccessRight(rights);
				userCompany.validate(true);
			}
			list.validate(true);
		}
		return userCompany;
	}
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LOGIN
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FLoginPanel getLoginPanel() {
    return FLoginPanel.getInstance();
  }

  public static FocUser createIfNotExist(String userName, String password, FocGroup group){
  	FocUser user = findUser(userName);
  	if(user == null){
  		FocList focList = FocUserDesc.getList();
  	
  		user = (FocUser) focList.newEmptyItem();
	    user.setName(userName);
	    user.setPassword(Encryptor.encrypt_MD5(password));
	    user.setGroup(group);
	    focList.add(user);
	    user.validate(true);
	    focList.validate(true);
  	}
  	return user;
  }
  
  /**
   * Static method that functions somewhat similarly to the method createIfNotExist(String, String)
   * with the difference being that this new method will not return null if the user already exists,
   * rather it will append an integer to the end of that userName and keep incrementing it
   * until it finds an available name and then it will return the new user.
   * 
   * @param userName The user name.
   * @param password The password.
   * @return The new user created.
   * 
   */
  
  
  public static FocUser createAndAppendNumberIfExists(String userName, String password){
    FocUser user = findUser(userName);
    String appendedString = "";
    int app = 0;
    while( user != null){
      app++;
      appendedString = Integer.toString(app);
      user = findUser(userName+appendedString);
    }
      FocList focList = FocUserDesc.getList();
      user = (FocUser) focList.newEmptyItem();
      user.setName(userName+appendedString);
      user.setPassword(Encryptor.encrypt_MD5(password));
      focList.add(user);
      user.validate(true);
      focList.validate(true);
      return user;    
  }
  
  /**
   * Static method that functions somewhat similarly to the method createIfNotExist(String, String)
   * with the difference being that this new method will not return null if the user already exists,
   * rather it will append an integer to the end of that userName and keep incrementing it
   * until it finds an available name and then it will return the new user.
   * This method also sets the password as the first user name found that is available.
   * 
   * @param contact The contact to create a user from.
   * @return The new user created. Null if no user was able to be created.
   */
  
  public static FocUser createUserForContact(Contact contact){
  	return createUserForContact(contact, null, null, null, null);
  }

  public static FocUser createUserForContact_UserNameIsEmail(Contact contact, String password){
  	FocUser user = null;
  			
		WFSite   site  = EComConfiguration.getInstance() != null ? EComConfiguration.getInstance().getGuestUserSite() : null;
		FocGroup group = EComConfiguration.getInstance() != null ? EComConfiguration.getInstance().getGuestUserGroup() : null;

		user = createUserForContact(contact, password, group, site, null);
  	
  	return user;
  }
  
  public static FocUser createUserForContact(Contact contact, String password, FocGroup guestGroup, WFSite site, WFTitle defaultTitle){
    FocUser user = null;
    if(contact != null){

      FocGroup anyGuestGroup = guestGroup;

      if(anyGuestGroup == null){
        FocList groupList = FocGroupDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
	      for(int i=0; i<groupList.size(); i++){
	        FocGroup group = (FocGroup) groupList.getFocObject(i);
	
	        if(group.isGuestApplicable()){
	          anyGuestGroup = group;
	          break;
	        }
	      }
      }

      if(anyGuestGroup != null){
      	String name = contact.getEMail();
      	if(Utils.isStringEmpty(name)){
      		name = contact.getFirstName()+contact.getFamilyName();
      	}
      	if(name != null && !name.isEmpty()){
	        user = findUser(name);
	        String appendedString = "";
	        int app = 0;
	        while( user != null){
	          app++;
	          appendedString = Integer.toString(app);
	          user = findUser(name+appendedString);
	        }
	        FocList focList = FocUserDesc.getList();
	        user = (FocUser) focList.newEmptyItem();
	        name += appendedString;
	        user.setName(name);
	        if(password != null && !password.isEmpty()){
	        	user.setPassword(password);
	        }else{
	        	user.setPassword(name);
      		}	        	
	        user.setContact(contact);
	        user.setGroup(anyGuestGroup);
	        user.setGuest(true);
	        
	        focList.add(user);
	        user.validate(true);
	        focList.validate(true);
	        
          FocUserHistoryList historyList = (FocUserHistoryList) FocUserHistoryDesc.getInstance().getFocList();
          if(historyList != null){
	          historyList.loadIfNotLoadedFromDB();              
	          FocUserHistory userHistory = historyList.getOrCreateUserHistory(user);
	          if(userHistory != null){
	          	userHistory.setFullscreen(FocUserHistoryConst.FLD_FULLSCREEN);
	          }
          }
	        
	        Company company = Globals.getApp().getCurrentCompany();
	        if(company != null){
	        	//USERRIGHTS
	        	FocList compRightsList = user.getCompanyRightsList();
	        	UserCompanyRights userRight = (UserCompanyRights) compRightsList.newEmptyItem();
	        	userRight.setCompany(company);
	        	userRight.setAccessRight(UserCompanyRightsDesc.ACCESS_RIGHT_READ_WRITE);
	        	userRight.validate(true);
	        	compRightsList.add(userRight);
	        	compRightsList.validate(true);
	        	user.validate(true);
	        	//END USERRIGHTS
	        	
	        	if(site == null) site = Globals.getApp().getCurrentSite();
	        	
	        	WFTitle guestTitle = defaultTitle;
	        	
	        	if(guestTitle == null){
		        	FocList titleList = WFTitleDesc.getInstance().getFocList();
		        	titleList.loadIfNotLoadedFromDB();
		        	for(int i=0; i<titleList.size(); i++){
		        		WFTitle title = (WFTitle) titleList.getFocObject(i);
		        		if(title.getName().toUpperCase().equals("GUEST")){
		        			guestTitle = title;
		        		}
		        	}
	        	}
	        	
	        	if(guestTitle != null){	        		
		        	FocList operatorList = site.getOperatorList();
		        	WFOperator operator = (WFOperator) operatorList.newEmptyItem();
	        		operator.setUser(user);
	        		operator.setTitle(guestTitle);
	        		
	        		FocList userTransactionRighsList = site.getUserTransactionRightsList();
	        		UserTransactionRight rightToCheck = null;
	        		UserTransactionRight rightToAdd   = null;
	        		RightLevel guestRightLevel = RightLevelDesc.getGuestRightLevelOrCreateIfNotExist();
	        		for(int k=0; k<userTransactionRighsList.size();k++){
	        			rightToCheck = (UserTransactionRight) userTransactionRighsList.getFocObject(k);
	        			if(rightToCheck.getTitle() == guestTitle && rightToCheck.getWFRightsLevel() == guestRightLevel){
	        				rightToAdd = rightToCheck;
	        			}
	        		}
	        		if(rightToAdd == null){
		        		rightToAdd = (UserTransactionRight) userTransactionRighsList.newEmptyItem();
		        		rightToAdd.setTitle(guestTitle);
		        		rightToAdd.setWFRightsLevel(guestRightLevel);
		        		rightToAdd.validate(true);
		        		userTransactionRighsList.validate(true);
	        		}
	        		site.validate(true);
	        	}else{
	        		Globals.showNotification("A 'Guest' title  not available", "please check with 01BARMAJA to prepare a guest title.", IFocEnvironment.TYPE_WARNING_MESSAGE);
	        	}
        		
	        }
      	}
      }else{
        Globals.showNotification("Could not create user", "No guest-applicable groups found.", IFocEnvironment.TYPE_ERROR_MESSAGE);
      }
    }
    return user;
  }
  
  public GroupXMLView findXMLView(String storageName, String context, int  type){
    GroupXMLView result = null;

    FocGroup group = getGroup();

    if(group != null){
    	result = group.findXMLView(storageName, context, type);
    }     
    return result;
  }
  
//USERREFACTOR
  private HashMap<Long, UserSite> userSites = null;
  private class UserSite {
  	private long companyRef = 0;
  	private long siteRef    = 0;
  	HashMap<String, UserOperator> operators = null;
  	
  	public UserSite(WFSite site) {
  		siteRef = site != null ? site.getReferenceInt() : 0;
  		companyRef = site != null && site.getCompany() != null ? site.getCompany().getReferenceInt() : 0;
  	}
  	
  	public void pushTitle(WFOperator operator) {
  		if (operator != null) {
  			UserOperator userOperator = getTitle(operator.getTitle(), operator.getDepartment()); 

  			if (userOperator == null) {
    			String key = buildKey(operator.getTitle(), operator.getDepartment());
    			
    			userOperator = new UserOperator(operator);
    			if(operators == null) {
    				operators = new HashMap<String, UserOperator>(); 
    			}
  				operators.put(key, userOperator);
  			}
  			
  		}
  	}
  	
  	public synchronized boolean hasTitle(WFTitle title) {
  		boolean has = false;
  		
  		if (operators != null) {
  			Iterator<UserOperator> iter = operators.values().iterator();
  			while(iter != null && iter.hasNext() && !has){
  				UserOperator operator = iter.next();
  				if(operator.getTitleRef() == title.getReferenceInt()) {
  					has = true;
  				}
  			}
  		}

			return has;
  	}
  	
  	public synchronized UserOperator getTitle(WFTitle title, Department department) {
  		UserOperator userOperator = null;
  		
  		if (operators != null) {
  			String key = buildKey(title, department);
				userOperator = operators.get(key);
  		}

			return userOperator;
  	}
  	
  	public String buildKey(WFTitle title, Department department) {
  		String key = null;
  		
			long titleRef      = title != null ? title.getReferenceInt() : 0;
			long departmentRef = department != null ? department.getReferenceInt() : 0;
			key = titleRef+"|"+departmentRef;

			return key;
  	}
  	
  	public Iterator<UserOperator> operatorIterator(){
  		return operators != null && operators.values() != null ? operators.values().iterator() : null;
  	}

		public long getCompanyRef() {
			return companyRef;
		}

		public long getSiteRef() {
			return siteRef;
		}
  }
  
  private class UserOperator {
  	private long operatorRef   = 0;
  	private long titleRef      = 0;
  	private long departmentRef = 0;
  	
  	public UserOperator(WFOperator operator) {
  		if(operator != null) {
  			operatorRef   = operator.getReferenceInt();
	  		titleRef      = operator.getTitle() != null ? operator.getTitle().getReferenceInt() : 0;
	  		departmentRef = operator.getDepartment() != null ? operator.getDepartment().getReferenceInt() : 0;
  		}
  	}

		public long getOperatorRef() {
			return operatorRef;
		}

		public long getTitleRef() {
			return titleRef;
		}

		public long getDepartmentRef() {
			return departmentRef;
		}
  }
  
  private synchronized void userSites_RebuildIfNeeded() {
  	if (userSites == null) {
  		userSites_Rebuild();
  	}
  }
  
//  private HashMap<Long, UserSite> userSites_Get(boolean createIfNeeded){
//  	if(userSites == null && createIfNeeded) {
//  		userSites_Rebuild();
//  	}
//  	return userSites;
//  }
  
  public synchronized void userSites_Rebuild() {
		userSites = null;
		userSites = new HashMap<Long, UserSite>();
		
  	FocList siteList = WFSiteDesc.getInstance().getFocList();
  	if (siteList != null) {
  		siteList.loadIfNotLoadedFromDB();
  		 
  		siteList.iterate(new IFocIterator() {
				@Override
				public boolean treatElement(Object element) {
					if(element != null && element instanceof FocListElement) {
						WFSite site = (WFSite) ((FocListElement)element).getFocObject();
						
						if(site != null && site.getReferenceInt() > 0) {
							FocList operatorsList = site.getOperatorList();
							if(operatorsList != null) {
								operatorsList.iterate(new IFocIterator() {
									@Override
									public boolean treatElement(Object element) {
										if(element != null && element instanceof FocListElement) {
											WFOperator operator = (WFOperator) ((FocListElement)element).getFocObject();
											
											if(operator != null && operator.getUser() != null && operator.getUser().equalsRef(FocUser.this)) {
												UserSite userSite = userSites.get(site.getReferenceInt());
												if(userSite == null) {
													userSite = new UserSite(site);
													userSites.put(site.getReferenceInt(), userSite);
												}
												
												userSite.pushTitle(operator);
											}
											
										}									
										return false;
									}
								});
							}
						}
					}
					return false;
				}
			});
  	}
  }
  
  //USERREFACTOR
  private synchronized boolean hasCompany(Company company) {
  	
  	userSites_RebuildIfNeeded();
  	
  	boolean has = false;
  	if(company != null) {
  		long companyRef = company.getReferenceInt();
  		Iterator<UserSite> siteIter = userSites.values().iterator();
  		while (siteIter != null && siteIter.hasNext() && !has) {
  			UserSite userSite = siteIter.next();
  			if (userSite != null) {
  				has = userSite.getCompanyRef() == companyRef;
  			}
  		}
  	}
  	return has;
  }
  
  public synchronized boolean hasSiteTitle(WFSite site, WFTitle title) {
  	userSites_RebuildIfNeeded();
  	
  	boolean has = false;
  	if (site != null) {
  		UserSite us = getUserSite(site);
  		if (us != null) {
  			Iterator<UserOperator> iter = us.operatorIterator();
  			while (iter != null && iter.hasNext() && !has) {
  				UserOperator operator = iter.next();
  				if(operator != null && operator.getTitleRef() == title.getReferenceInt()) {
  					has = true;
  				}
  			}
  		}
  	}
  	return has;
  }
  
  public boolean hasSite(WFSite site) {
  	return getUserSite(site) != null;
  }
  
  private synchronized UserSite getUserSite(WFSite site) {
  	
  	userSites_RebuildIfNeeded();
  	
  	UserSite found = null;
  	
  	if(site != null) {
  		long siteRef = site.getReferenceInt();
  		Iterator<UserSite> siteIter = userSites.values().iterator();
  		while (siteIter != null && siteIter.hasNext() && found == null) {
  			UserSite userSite = siteIter.next();
  			if (userSite != null) {
  				if(userSite.getSiteRef() == siteRef) {
  					found = userSite; 
  				}
  			}
  		}
  	}
  	
  	return found;
  }
  
  public synchronized void copyCompanySelectionFromUser (UserSession userSession) {
  	Company cComp = getCurrentCompany();
  	
  	userSites_RebuildIfNeeded();
  	
  	//If has a selected company and it is still valid then pass
  	if(cComp == null || !hasCompany(cComp)) {
  		cComp = null;
  		//Here we need to get any company 
  		FocList companylist = CompanyDesc.getInstance() != null ? CompanyDesc.getInstance().getFocList() : null;
			
  		if(companylist != null) {
	  		//In this case get the first one.
	  		Iterator<UserSite> siteIter = userSites.values().iterator();
	  		while (siteIter != null && siteIter.hasNext() && cComp == null) {
	  			UserSite userSite = siteIter.next();
	  			if (userSite != null && userSite.getCompanyRef() > 0) {
	  				cComp = (Company) companylist.searchByReference(userSite.getCompanyRef());
	  			}
	  		}
  		}
  	}
  	
  	if(cComp != null) {
  		setCurrentCompany(cComp);
  		userSession.setCompany(cComp);
  		
  		WFSite cSite = getCurrentSite();
  		if(cSite == null || cSite.getCompanyRef() != cComp.getReferenceInt() || !hasSite(cSite)) {
  			cSite = null;
    		//In this case get the first one that matches the Company
    		FocList sitelist = WFSiteDesc.getInstance() != null ? WFSiteDesc.getInstance().getFocList() : null;
    		
    		if(sitelist != null) {
	    		Iterator<UserSite> siteIter = userSites.values().iterator();
	    		while (siteIter != null && siteIter.hasNext() && cSite == null) {
	    			UserSite userSite = siteIter.next();
	    			if (userSite != null && userSite.getCompanyRef() == cComp.getReferenceInt()) {
	    				cSite = (WFSite) sitelist.searchByReference(userSite.getSiteRef());
	    			}
	    		}
    		}
  		}
  		
  		//Here the Site is good
  		if(cSite != null) {
    		setCurrentSite(cSite);
    		userSession.setSite(cSite);
    		
    		WFTitle cTitle = getCurrentTitle();
    		if(cTitle == null || !hasSiteTitle(cSite, cTitle)) {
    			cTitle = null;
    			
    			UserSite us = getUserSite(cSite);
    			if(us != null) {
    				Iterator<UserOperator> operatorIter = us.operatorIterator();
    				while (operatorIter != null && operatorIter.hasNext() && cTitle == null) {
    					UserOperator userOperator = operatorIter.next();
    					if (userOperator != null && userOperator.getTitleRef() > 0) {
    						FocList titleList = WFTitleDesc.getList(FocList.LOAD_IF_NEEDED);
    						if(titleList != null) {
    							cTitle = (WFTitle) titleList.searchByReference(userOperator.getTitleRef());
    						}
    					}
    				}
    			}
    		}
    		
    		if(cTitle != null) {
      		setCurrentTitle(cTitle);
      		userSession.setTitle(cTitle);
    		} else {
    			setCurrentTitle(null);
    			userSession.setTitle(null);
    		}
  		} else {
    		setCurrentSite(null);
    		setCurrentTitle(null);
    		userSession.setSite(null);
    		userSession.setTitle(null);
  		}
  	} else {
  		setCurrentCompany(null);
  		setCurrentSite(null);
  		setCurrentTitle(null);
  		userSession.setCompany(null);
  		userSession.setSite(null);
  		userSession.setTitle(null);
  	}
  }
  
  public synchronized ArrayList<WFOperator> getOperatorsArray_AllAreas(boolean createIfNeeded){
		if(operatorsArrayForThisUser_AllAreas == null && createIfNeeded){
			operatorsArrayForThisUser_AllAreas = new ArrayList<WFOperator>();
			
			FocList operatorsList = WFOperatorDesc.getInstance().getFocList();
			
			if (operatorsList != null) {
				Iterator<UserSite> iter = userSites != null ? userSites.values().iterator() : null;
				while(iter != null && iter.hasNext()) {
					UserSite usersite = iter.next();
					if (usersite != null) {
						Iterator<UserOperator> operatorIterator = usersite.operatorIterator();
						while (operatorIterator != null && operatorIterator.hasNext()) {
							UserOperator userOperator = operatorIterator.next();
							if (userOperator != null) {
								long operatorRef = userOperator.getOperatorRef();
								WFOperator operator = (WFOperator) operatorsList.searchByRealReferenceOnly(operatorRef);
								if(operator != null) {
									operatorsArrayForThisUser_AllAreas.add(operator);
								}
							}
						}
					}
				}
			}
		}
		return operatorsArrayForThisUser_AllAreas;
  }
  /*
  public ArrayList<WFOperator> getOperatorsArray_AllAreas(boolean createIfNeeded){
		if(operatorsArrayForThisUser_AllAreas == null && createIfNeeded){
			operatorsArrayForThisUser_AllAreas = new ArrayList<WFOperator>(); 
			
			WFSiteTree areaTree = WFSiteTree.newInstance();
			
			String sessionUsername = (Globals.getApp() != null && Globals.getApp().getUser_ForThisSession() != null) ? Globals.getApp().getUser_ForThisSession().getName() : null;
			if(areaTree != null && !Utils.isStringEmpty(sessionUsername)) {
				//We scan the saved Operators list. 
				//Then for each operator that matches the user 
				//1- We add its 
				FocList operatorList = WFOperatorDesc.getList(FocList.FORCE_RELOAD);
				for(int i=0; i<operatorList.size(); i++){
					WFOperator operator = (WFOperator) operatorList.getFocObject(i);
					if(			operator != null
							&& 	operator.getUser() != null
							&&  operator.getUser().getName() != null
							&&  operator.getUser().getName().equals(sessionUsername)
							&&  operator.getArea() != null
							&&  operator.getArea().getReference() != null){
						FNode areaNode = areaTree.findNode(operator.getArea().getReference().getInteger());
						if(areaNode != null){
							//Scan the are node tree and copies of this operator with these areas
							areaNode.scan(new AreaOperatorTreeScanner(operator));
						}
					}
				}
			}
		}		
		return operatorsArrayForThisUser_AllAreas;
	}
  
	private class AreaOperatorTreeScanner implements TreeScanner<FNode>{
		private WFOperator operator = null;

		public AreaOperatorTreeScanner(WFOperator operator){
			this.operator = operator;
		}
		
		public void dispose(){
			operator = null;
		}
		
		public void afterChildren(FNode node) {
		}

		public boolean beforChildren(FNode node) {
			WFSite area = (WFSite) node.getObject();
			
			WFOperator newOperator = new WFOperator();
			newOperator.setDuplicatedForUserArrayOfTitles(true);
			newOperator.copyPropertiesFrom(operator);
			newOperator.setArea(area);
			operatorsArrayForThisUser_AllAreas.add(newOperator);
			
			return true;
		}
	}
	*/
  
	public static ArrayList<WFOperator> getOperatorsArrayForThisUser_AllAreas(boolean createIfNeeded){
		FocUser user = Globals.getApp().getUser_ForThisSession();
		return user != null ? user.getOperatorsArray_AllAreas(createIfNeeded) : null;
	}

	public int getTheme(){
		return getPropertyInteger(FocUserDesc.FLD_THEME);
	}
	
	public void saveFullScreenSettings(int fullScreenMode){
    FocUserHistoryList historyList = (FocUserHistoryList) FocUserHistoryDesc.getInstance().getFocList();
    if(historyList != null){
	    historyList.loadIfNotLoadedFromDB();              
	    FocUserHistory userHistory = historyList.getOrCreateUserHistory(FocUser.this);
	    if(userHistory != null){
	    	historyList.saveFullscreenSettings(userHistory, FocUser.this, fullScreenMode);
	    }
    }
	}
	
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static final String DB_TABLE_NAME = FocUserDesc.DB_TABLE_NAME;

  public static final String FLDNAME_NAME = FocUserDesc.FLDNAME_NAME;

  public static final int FLD_NAME = FocUserDesc.FLD_NAME;

  public static final int FLD_PASSWORD = FocUserDesc.FLD_PASSWORD;

  public static final int FLD_GROUP = FocUserDesc.FLD_GROUP;

  public static final int FLD_LANGUAGE = FocUserDesc.FLD_LANGUAGE;

  public static final int FLD_FONT_SIZE = FocUserDesc.FLD_FONT_SIZE;

  public static FocDesc getFocDesc() {
    return FocUserDesc.getInstance();
  }
  
  public ArrayList<DocRightsGroup> newUserDocRightsGroupArraylist(){
		ArrayList<DocRightsGroup> docRighArrayList = new ArrayList<DocRightsGroup>();
    FocList docRightsGroupList = DocRightsGroupDesc.getInstance().getFocList();
    FocList docRightsGroupUsersList = null;
    for(int x=0;x<docRightsGroupList.size();x++){
    	DocRightsGroup group = (DocRightsGroup) docRightsGroupList.getFocObject(x);
    	docRightsGroupUsersList = group.getDocRightsGroupUsersList();
    	for(int s=0;s<docRightsGroupUsersList.size();s++){
    		DocRightsGroupUsers user = (DocRightsGroupUsers) docRightsGroupUsersList.getFocObject(s);
    		if(user.getUser().equalsRef(this)){
    			docRighArrayList.add(group);
    		}
    	}
    }
    return docRighArrayList;
  }
  
  public boolean hasRightsToAddItemsFor(FocDesc focDesc){
  	//ConfigInfo.isAllowAddInsideComboBox();
  	FocGroup group = getGroup();
  	return group != null ? group.allowInsertInCombobox() : false;
  	//return ConfigInfo.isAllowAddInsideComboBox();
  }
    
	public static String checkPasswordPolicyAbidance(String password) {
		String errorText = null;
		
		int policy = ConfigInfo.getPasswordPolicy();
		if(policy == 1) {
			boolean error = false;
			if (Utils.isStringEmpty(password)) {
				error = true;
			} else if(password.length() < 6) {
				error = true;
			} else {
				boolean hasLetter = false;
				boolean hasDigit  = false;
				
			  for (int i = 0; i < password.length(); i++) {
	        char x = password.charAt(i);
	        if (Character.isLetter(x)) {
	          hasLetter = true;
	        } else if (Character.isDigit(x)) {
	          hasDigit = true;
	        }
			  }
			  
	      if(!hasLetter){
	      	error = true;
	      }
	      if(!hasDigit){
	      	error = true;
	      }
	
	    }
			
			if (error) {
				errorText = "Password should be at least 6 characters with a letters and numbers.";
				if (ConfigInfo.isArabic()) {
					errorText = "يجب على كلمة السر ان تكون اكبر من 6 احرف و ان تكون مكونة من احرف و ارقام";
				}
			}
		} else if(policy == 2) {
			boolean error = false;
			if (Utils.isStringEmpty(password)) {
				error = true;
			} else if(password.length() < 12) {
				error = true;
			} else {
				boolean hasUpperLetter = false;
				boolean hasLowerLetter = false;
				boolean hasDigit  = false;
				boolean hasSymbol = false;
			  for (int i = 0; i < password.length(); i++) {
	        char x = password.charAt(i);
	        if(Character.isLetter(x)) {
	        	if(!ConfigInfo.isArabic()) {
		        	if (Character.isUpperCase(x)) {
		        		hasUpperLetter = true;
		        	} else if (Character.isLowerCase(x)) {
		        		hasLowerLetter = true;
		        	}
	        	} else if(Character.isLetter(x)) {
	        		hasUpperLetter = true;
	        		hasLowerLetter = true;
		        } 
	        } else if (Character.isDigit(x)) {
	          hasDigit = true;
	        } else if (containsSpecialCharacter(String.valueOf(x))) {
	        	hasSymbol = true;
	        }
			  }
			  
	      if(!hasUpperLetter){
	      	error = true;
	      } else if(!hasLowerLetter){
	      	error = true;
	      } else if(!hasDigit){
	      	error = true;
	      } else if(!hasSymbol){
	      	error = true;
	      }
	    }
			if (error) {
				errorText = "Password should be at least 12 characters containing at least a symbol, an upper and lower case letter and a number.";
				if (ConfigInfo.isArabic()) {
					errorText = "يجب على كلمة السر ان تكون اكبر من 12 حرف و ان تكون مكونة من احرف، ارقام و رموز";
				}
			}
		}
			
		return errorText;
	}
	
	private static boolean containsSpecialCharacter(String s) {
		String specialChars = "/*!@#$%^&*()\"{}_[]|\\?/<>,.";
    return (s == null) ? false : specialChars.contains(s);
	}

  public String canChangePassword(String oldPassStr, String newPassStr, String confPassStr, boolean checkOldPassword){
  	String errorMessage = null;
  	
  	//If the old password is null this means the administrator is recreating the password. 
    if(checkOldPassword){
    	if(oldPassStr == null) errorMessage = "Old password is not available";
    	if (errorMessage == null) {
    		oldPassStr = Encryptor.encrypt_MD5(String.valueOf(oldPassStr));
        boolean oldPasswordMatches = false;
        if(getPassword().isEmpty()){
        	oldPasswordMatches = Encryptor.encrypt_MD5("").equals(oldPassStr);
        }else{
        	oldPasswordMatches = getPassword().equals(oldPassStr);
        }
        //If the password was never set it is still empty in the FocUser
        if(!oldPasswordMatches){
        	errorMessage = "Old password is incorrect!";
        }
    	}
    }
    
    if(errorMessage == null){
	    if(!newPassStr.equals(confPassStr)){
	    	errorMessage = "Passwords do not match!";
	    }

	    if(errorMessage == null) {
	    	errorMessage = FocUser.checkPasswordPolicyAbidance(newPassStr);
	    }
    }
    
    return errorMessage;
  }
  
  public void changePassword(String newPassStr) {
		Globals.logString(" = Username "+getName()+" password encrypted "+Encryptor.encrypt_MD5(String.valueOf(newPassStr)));
		newPassStr = Encryptor.encrypt_MD5(String.valueOf(newPassStr));
		setPassword(newPassStr);
		validate(true);
  }

	@Override
	public void toJson_Detailed(B01JsonBuilder builder) {
    builder.beginObject();
    appendKeyValueForFieldName(builder, null, FField.REF_FIELD_NAME);
    appendKeyValueForFieldName(builder, null, FocUserDesc.FLDNAME_NAME);
    appendKeyValueForFieldName(builder, null, FocUserDesc.FNAME_SUSPENDED);
    appendKeyValueForFieldName(builder, null, FocUserDesc.FNAME_GROUP);
    builder.endObject();
	}
	
}
