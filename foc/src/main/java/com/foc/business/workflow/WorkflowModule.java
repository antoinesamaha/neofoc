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
package com.foc.business.workflow;

import com.foc.Application;
import com.foc.Globals;
import com.foc.admin.FocVersion;
import com.foc.business.workflow.implementation.ILoggableDesc;
import com.foc.business.workflow.implementation.LoggableDesc;
import com.foc.business.workflow.implementation.WFLogDesc;
import com.foc.business.workflow.map.WFMapDesc;
import com.foc.business.workflow.map.WFSignatureDesc;
import com.foc.business.workflow.map.WFStageDesc;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.business.workflow.rights.RightLevelDesc;
import com.foc.business.workflow.rights.UserTransactionRightDesc;
import com.foc.db.DBManager;
import com.foc.desc.FocModule;
import com.foc.menu.FMenuList;

public class WorkflowModule extends FocModule {
  
	public static final String MODULE_NAME = "WORKFLOW";
	
	public static final int VERSION_ID = 160;
	
	public static final int VERSION_ID_LAST_BEFORE_FLD_NAME_CUT = 152;
	public static final int VERSION_ID_CHANGE_CLOB_TO_SECURED_FILE_SYSTEM_IN_ORACLE = 158;

	private boolean changeCLOB2SecuredFileSystem_Oracle = false;
	
	private WorkflowModule(){
	}
  
	public void dispose(){
	}
	
  public void declare(){
    Application app = Globals.getApp();
    FocVersion.addVersion(MODULE_NAME, "workflow 1.0", VERSION_ID);
    app.declareModule(this);
  }

  public void addApplicationMenu(FMenuList menuList) {
  }

  public void addConfigurationMenu(FMenuList menuList) {
  }
  
  public void afterApplicationEntry() {
  }

  public void afterApplicationLogin() {
  }

	public void declareFocObjectsOnce() {
		declareFocDescClass(WFSiteDesc.class);		
		declareFocDescClass(WFTitleDesc.class);
		declareFocDescClass(WFOperatorDesc.class);
		declareFocDescClass(WFMapDesc.class);
		declareFocDescClass(WFSignatureDesc.class);
		declareFocDescClass(WFStageDesc.class);
		declareFocDescClass(WFTransactionConfigDesc.class);
		declareFocDescClass(WFFieldLockStageDesc.class);
		//declareFocDescClass(WFSuperUserDesc.class);
		
		declareFocDescClass(RightLevelDesc.class);
		declareFocDescClass(UserTransactionRightDesc.class);
		//declareFocDescClass(WFAssignFunctionalStageCorrespondanceDesc.class);
	}

	@Override
	public void beforeAdaptDataModel() {
		FocVersion dbVersion = FocVersion.getDBVersionForModule(MODULE_NAME);
		if (		dbVersion != null 
				&& 	dbVersion.getId() < VERSION_ID_CHANGE_CLOB_TO_SECURED_FILE_SYSTEM_IN_ORACLE
				&&  Globals.getApp() != null 
				&&  Globals.getApp().getDBManager() != null 
				&&  Globals.getApp().getDBManager().getProvider() == DBManager.PROVIDER_ORACLE) {
			changeCLOB2SecuredFileSystem_Oracle = true;
		}
		super.beforeAdaptDataModel();
	}

	private StringBuffer newAlterTableMoveLOB2Secure(String tableName, String fieldName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ALTER TABLE \""+tableName+"\" MOVE LOB(\""+fieldName+"\") STORE AS SECUREFILE");
		return buffer;
	}
	
	@Override
	public void afterAdaptDataModel() {
		super.afterAdaptDataModel();
		if (changeCLOB2SecuredFileSystem_Oracle) {
			for(int i=0; i<LoggableFactory.getInstance().getFocDescCount(); i++) {
				ILoggableDesc iWorkflowDesc = LoggableFactory.getInstance().getIWorkflowDesc(i);
				LoggableDesc loggableDesc = (iWorkflowDesc != null) ? iWorkflowDesc.iWorkflow_getWorkflowDesc() : null;
				WFLogDesc logDesc = loggableDesc != null ? loggableDesc.getWFLogDesc() : null;
					
				if(logDesc != null) {
					StringBuffer buffer = null;
					if(logDesc.getFieldByName(WFLogDesc.FNAME_CHANGES) != null) {
						buffer = newAlterTableMoveLOB2Secure(logDesc.getStorageName_ForSQL(), WFLogDesc.FNAME_CHANGES);
						Globals.getApp().getDataSource().command_ExecuteRequest(buffer);
					}
					if(logDesc.getFieldByName(WFLogDesc.FNAME_DocZip) != null) {
						buffer = newAlterTableMoveLOB2Secure(logDesc.getStorageName_ForSQL(), WFLogDesc.FNAME_DocZip);
						Globals.getApp().getDataSource().command_ExecuteRequest(buffer);
					}
				}
			}
			Globals.getApp().getDataSource().command_AdaptDataModel_Reindex();
		}
	}
	
  private static WorkflowModule module = null;
  public static WorkflowModule getInstance(){
    if(module == null){
      module = new WorkflowModule();
    }
    return module;
  }
}
