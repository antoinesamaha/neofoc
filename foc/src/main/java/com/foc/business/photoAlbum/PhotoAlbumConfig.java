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
package com.foc.business.photoAlbum;

import com.foc.Globals;
import com.foc.admin.UserSession;
import com.foc.business.company.Company;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class PhotoAlbumConfig extends FocObject {

	public PhotoAlbumConfig(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  public boolean isSingleGroup(){
  	return getPropertyBoolean(PhotoAlbumConfigDesc.FLD_SINGLE_GROUP);
  }
  
  public boolean isUseKeywords(){
  	return getPropertyBoolean(PhotoAlbumConfigDesc.FLD_USE_KEYWORDS);
  }

	public static PhotoAlbumConfig getOrCreateForCompany(Company company){
		PhotoAlbumConfig foundConfig = null; 
				
    FocList basicsConfigList = PhotoAlbumConfigDesc.getList(FocList.LOAD_IF_NEEDED);
    for(int i=0; i<basicsConfigList.size() && foundConfig == null; i++){
    	PhotoAlbumConfig cfg = (PhotoAlbumConfig) basicsConfigList.getFocObject(i);
    	if(cfg != null && cfg.isForCompany(company)){
    		foundConfig = cfg;
    	}
    }
    if(foundConfig == null){
    	foundConfig = (PhotoAlbumConfig) basicsConfigList.newEmptyItem();
    	foundConfig.setCompany(company);
    	basicsConfigList.add(foundConfig);
    }
    
    return foundConfig;
	}
	
//  private static BusinessConfig generalConfig = null;
  public synchronized static PhotoAlbumConfig getInstance(){
  	PhotoAlbumConfig generalConfig = (PhotoAlbumConfig) UserSession.getParameter("PHOTO_ALBUM_CONFIG");
  	if(generalConfig == null || !generalConfig.isForCurrentCompany()){
  		generalConfig = getOrCreateForCompany(Globals.getApp().getCurrentCompany());
  		if(generalConfig != null){
  			UserSession.putParameter("PHOTO_ALBUM_CONFIG", generalConfig);
  		}
  	}
  	return generalConfig;
  }
}
