package com.foc.admin;

import java.sql.Date;

import com.foc.business.company.Company;
import com.foc.business.company.CompanyDesc;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.WFTitle;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.shared.json.B01JsonBuilder;

public class ActiveUserList extends FocList {

	public static final String PARAM_TABLE_LIST = "activeUsers";
	public static final String PARAM_USER_REF = "user_ref";
	public static final String PARAM_COMPANY_REF = "company_ref";
	public static final String PARAM_SITE_REF = "site_ref";
	public static final String PARAM_TITLE_REF = "title_ref";
	public static final String PARAM_LAST_HEART_BEAT = "last_heart_beat";

	public ActiveUserList() {
		super(new FocLinkSimple(ActiveUserDesc.getInstance()));
	}
	
	public void dispose() {
		super.dispose();
	}
	
	private long lastClean = 0;
	private void serviceSide_cleanActiveUsersList() {
		long now = System.currentTimeMillis();
		if((now - lastClean) > 5 * 60 * 1000) {//Every 5 minutes we refresh
			lastClean = now;
			long oldestToKeep = System.currentTimeMillis() - (1000 * 60 * 60 * 2);//2 hours
			for(int i = size()-1; i >= 0; i--) {
				ActiveUser curr = (ActiveUser) getFocObject(i);
				if(curr.getLastHeartBeat() == null || curr.getLastHeartBeat().getTime() < oldestToKeep) remove(curr);
			}
		}
	}
	
	//This is used in the Jetty service process. To update the user last heartbeat 
	public void serviceSide_updateHeartbeat(FocUser user) {
		if(user != null) {
			serviceSide_cleanActiveUsersList();
			ActiveUser activeUser = null;
			for(int i=0; i < size() && activeUser == null; i++) {
				ActiveUser curr = (ActiveUser) getFocObject(i);
				if(curr.getUser() != null && curr.getUser().equalsRef(user)) activeUser = curr;
			}
			if(activeUser == null) {
				addActiveUser(user, System.currentTimeMillis(), ActiveUserDesc.ORIGIN_MOBILE);
			} else {
				long lastHeartBeat = System.currentTimeMillis();
				Date lastHeartBeatDate = new Date(lastHeartBeat);
				activeUser.setLastHeartBeat(lastHeartBeatDate);
				activeUser.validate(false);				
			}
		}
	}
	
	public ActiveUser addActiveUser(FocUser user, long lastHeartBeat, String origin) {
		ActiveUser activeUser = null;
		if (user != null) {
			Company company = user.getCurrentCompany();
			WFSite site = user.getCurrentSite();
			WFTitle title = user.getCurrentTitle();
			activeUser = addActiveUser(user, lastHeartBeat, origin, company, site, title); 
		}
		return activeUser;
	}
	
	public ActiveUser addActiveUser(FocUser user, long lastHeartBeat, String origin, Company company, WFSite site, WFTitle title) {
		ActiveUser activeUser = null;
		if (user != null) {
			activeUser = (ActiveUser) newEmptyItem();
			activeUser.setUserCompany(company);
			activeUser.setUser(user);
			activeUser.setUserSite(site);
			activeUser.setUserTitle(title);
			activeUser.setOrigin(origin);

			Date lastHeartBeatDate = new Date(lastHeartBeat);
			activeUser.setLastHeartBeat(lastHeartBeatDate);

			add(activeUser);
		}
		return activeUser;
	}
	
	public ActiveUser addActiveUser(long userRef, long lastHeartbeat, String origin, long companyRef, long siteRef, long titleRef) {
		ActiveUser newItem = null;
		try{
			if(ActiveUserDesc.getInstance() != null && ActiveUserDesc.getInstance().getFocList() != null && userRef > 0 && lastHeartbeat > 0){
				FocUser user = null;
				Company company = null;
				WFSite site = null;
				WFTitle title = null;
				
				if(FocUserDesc.getInstance() != null && FocUserDesc.getInstance().getFocList() != null) {
					user = (FocUser) FocUserDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED).searchByRealReferenceOnly(userRef);
				}
				
				if(companyRef > 0) {
					if(CompanyDesc.getInstance() != null && CompanyDesc.getInstance().getFocList() != null) {
						company = (Company) CompanyDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED).searchByRealReferenceOnly(companyRef);
					}
				} else if(user != null) {
					company = user.getCurrentCompany();
				}
				
				if(siteRef > 0) {
					if(WFSiteDesc.getInstance() != null && WFSiteDesc.getInstance().getFocList() != null) {
						site = (WFSite) WFSiteDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED).searchByRealReferenceOnly(siteRef);
					}
				} else if(user != null) {
					site = user.getCurrentSite();
				}
				
				if(titleRef > 0) {
					if(WFTitleDesc.getInstance() != null && WFTitleDesc.getInstance().getFocList() != null) title = (WFTitle) WFTitleDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED).searchByRealReferenceOnly(titleRef);
				} else if(user != null) {
					title = user.getCurrentTitle();
				}
			
				addActiveUser(user, lastHeartbeat, origin, company, site, title);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return newItem;
	}
	
	public synchronized String toJson() {
  	B01JsonBuilder builder = new B01JsonBuilder();				
		builder.beginObject();
		builder.appendKey(PARAM_TABLE_LIST);
		builder.beginList();
		for(int i=0; i < size(); i++) {
			ActiveUser curr = (ActiveUser) getFocObject(i);
			builder.beginObject();
			builder.appendKeyValue(PARAM_USER_REF, curr.getUser().getReferenceInt());
			builder.appendKeyValue(PARAM_COMPANY_REF, curr.getCompanyRef());
			builder.appendKeyValue(PARAM_SITE_REF, curr.getUserSite().getReferenceInt());
			builder.appendKeyValue(PARAM_TITLE_REF, curr.getUserTitle().getReferenceInt());
			builder.appendKeyValue(PARAM_LAST_HEART_BEAT, curr.getLastHeartBeat().getTime());
			builder.endObject();
		}
		builder.endList();
		builder.endObject();
		return builder.toString();
	}
	
	private static ActiveUserList instance = null;
	public static ActiveUserList getInstance() {
		if(instance == null) {
			instance = new ActiveUserList();
		}
		return instance;
	}	
	
}
