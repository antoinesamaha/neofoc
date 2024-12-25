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
package com.foc.business.notifier;

import com.foc.Globals;
import com.foc.admin.FocVersion;
import com.foc.db.DBManager;
import com.foc.desc.FocModule;
import com.foc.list.FocList;
import com.foc.menu.FMenuList;

public class NotifierModule extends FocModule {
	
	public static final String MODULE_NAME = "NOTIFIER";
	public static final int VERSION = 1004;
	
	public static final int VERSION_MULTI_REPORT_IN_TRIGGER = 1002;
	public static final int VERSION_ID_MIGRATE_TO_CLOB = 1003;
	
	private boolean copyMonoReportToSlaveTable = false;
	private boolean migrateToCLOB = false;
	
	public NotifierModule(){
	  FocVersion.addVersion(MODULE_NAME, "notfier 1.0", VERSION);
	}
	
	public void dispose(){
		
	}
	
	@Override
	public void declareFocObjectsOnce() {
//		declareFocDescClass(FNotifTriggerDesc.class);
		declareFocDescClass(FocNotificationEmailTemplateDesc.class);
		declareFocDescClass(FocNotificationEmailDesc.class);
		declareFocDescClass(FocPageLinkDesc.class);
		declareFocDescClass(DocMsgContentDesc.class);
		declareFocDescClass(DocMsgDesc.class);
		
		scanModelPackage("com.foc.business.notifier");
	}

	public void addApplicationMenu(FMenuList menuList) {
	}

	public void addConfigurationMenu(FMenuList menuList) {
	}

	public void afterApplicationEntry() {
	}

	public void afterApplicationLogin() {
	}

	public void beforeAdaptDataModel() {
		super.beforeAdaptDataModel();
		copyMonoReportToSlaveTable = false;
		FocVersion version = FocVersion.getDBVersionForModule(MODULE_NAME);
		if (version != null && version.getId() < VERSION_MULTI_REPORT_IN_TRIGGER) {
			copyMonoReportToSlaveTable = true;
		}
		
		if (version == null || version.getId() < VERSION_ID_MIGRATE_TO_CLOB) {
			if(Globals.getDBManager() != null && Globals.getDBManager().getProvider(null) == DBManager.PROVIDER_ORACLE) {
				migrateToCLOB = true;
				StringBuffer buffer = new StringBuffer();
				buffer.append("ALTER TABLE \"NOTIF_EMAIL\" RENAME COLUMN \"TEXT\" TO \"TEXTOLD\"");
				Globals.getApp().getDataSource().command_ExecuteRequest(buffer);
			}
		} else {
			migrateToCLOB = false;
		}
	}

	public void afterAdaptDataModel() {
		if(copyMonoReportToSlaveTable) {
			FocList list = FNotifTrigger.getFocDesc().getFocList();
			list.loadIfNotLoadedFromDB();
			for(int i=0; i<list.size(); i++) {
				FNotifTrigger trigger = (FNotifTrigger) list.getFocObject(i);
				if(trigger.getAction() == FNotifTrigger.ACTION_EXECUTE_REPORT) {
					FocList repList = trigger.getReportList();
					FNotifTrigReport report = (FNotifTrigReport) repList.newEmptyItem();
					report.setReportTableName(trigger.getReportTableName());
					report.setReportReference(trigger.getReportReference());
					report.setReportLayout(trigger.getReportLayout());
					repList.add(report);
					report.validate(true);
				}
				trigger.validate(true);
			}
		}
		
		if (migrateToCLOB) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("UPDATE \"NOTIF_EMAIL\" SET \"TEXT\" = \"TEXTOLD\"");
			Globals.getApp().getDataSource().command_ExecuteRequest(buffer);
		}
	}

	private static NotifierModule notifierModule = null;
	public static NotifierModule getInstance(){
		if(notifierModule == null){
			notifierModule = new NotifierModule();
		}
		return notifierModule;
	}
}
