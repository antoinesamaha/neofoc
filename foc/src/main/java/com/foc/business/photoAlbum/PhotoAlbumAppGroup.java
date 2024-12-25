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

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.admin.FocAppGroup;
import com.foc.admin.FocGroupDesc;
import com.foc.admin.FocUser;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;

@SuppressWarnings("serial")
public class PhotoAlbumAppGroup extends FocAppGroup {

  public PhotoAlbumAppGroup(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  private static FocDesc focDesc = null;  
  public static final int FLD_DOC_RIGHT               = 2;
  public static final int FLD_ACCESS_DETACHED_GALLERY = 3;
  
  private static int appGroupListFieldID = FField.NO_FIELD_ID;  

  private static final int DOC_RIGHT_NONE     = 0;
  private static final int DOC_RIGHT_DOWNLOAD = 1;
  private static final int DOC_RIGHT_UPLOAD   = 2;//Include download
  private static final int DOC_RIGHT_DELETE   = 3;//Include upload download
  
  public boolean isAllowDownload(){
  	return getPropertyInteger(FLD_DOC_RIGHT) >= DOC_RIGHT_DOWNLOAD ;
  }   

  public boolean isAllowUpload(){
  	return getPropertyInteger(FLD_DOC_RIGHT) >= DOC_RIGHT_UPLOAD ;
  }   

  public boolean isAllowDelete(){
  	return getPropertyInteger(FLD_DOC_RIGHT) >= DOC_RIGHT_DELETE;
  }   

  public boolean isAllowAccessToDetachedGallery(){
  	return getPropertyBoolean(FLD_ACCESS_DETACHED_GALLERY);
  }   

  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      focDesc = new FocDesc(PhotoAlbumAppGroup.class, FocDesc.DB_RESIDENT, "PHOTO_ALBUM_APP_GROUP", false);
      focDesc.addReferenceField();
      
      appGroupListFieldID = FocGroupDesc.getInstance().getAndIncrementNumberOfAppGroupListFieldID();
      FObjectField focGroupFld = new FObjectField("FOC_GROUP", "FocGroup", FLD_FOC_GROUP, false, FocGroupDesc.getInstance(), "FGRP_", focDesc, appGroupListFieldID);
      focDesc.addField(focGroupFld);     
      
      FMultipleChoiceField mFld = new FMultipleChoiceField("DOC_RIGHT", "", FLD_DOC_RIGHT, false, 2);
      mFld.addChoice(DOC_RIGHT_NONE, "- none -");
      mFld.addChoice(DOC_RIGHT_DOWNLOAD, "Download");
      mFld.addChoice(DOC_RIGHT_UPLOAD, "Download / Upload");
      mFld.addChoice(DOC_RIGHT_DELETE, "Download / Upload / Delete");
      focDesc.addField(mFld);
      
      FBoolField bFld = new FBoolField("ACCESS_DETACHED_GALLERY", "Access detached gallery", FLD_ACCESS_DETACHED_GALLERY, false);
      focDesc.addField(bFld);
    }
    return focDesc;
  }
  
  public static PhotoAlbumAppGroup getCurrentAppGroup(){
    PhotoAlbumAppGroup group = null;
    if(Globals.getApp() != null){
      FocUser user = Globals.getApp().getUser_ForThisSession();
      if(user != null && user.getGroup() != null){
        group = (PhotoAlbumAppGroup) user.getGroup().getAppGroupAt(appGroupListFieldID);
      }
    }
    return group;
  }

	@Override
	public String getTitle() {
		return ConfigInfo.isArabic() ? "الوثائق و الصور" : "Document Management";
	}
}
