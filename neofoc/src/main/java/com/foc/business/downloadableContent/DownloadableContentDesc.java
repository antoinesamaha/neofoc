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
package com.foc.business.downloadableContent;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FCloudStorageField;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FReferenceField;
import com.foc.list.FocList;

public class DownloadableContentDesc extends FocDesc {

  public static final int FLD_DOCUMENT           = 1;
  public static final int FLD_DOCUMENT_NAME      = 2;
  public static final int FLD_UPLOAD_DATE        = 3;
  public static final int FLD_LAST_MODIFICATION_DATE = 4;
  public static final int FLD_DESCRIPTION        = 5;
  public static final int FLD_VISIBILITY         = 6;
  
  public static final String DB_TABLE_NAME = "DOWNLOADABLE_CONTENT";
  
  public static final int VISIBILITY_PUBLIC     = 0;
  public static final int VISIBILITY_USERS_ONLY = 1;
  
  public DownloadableContentDesc(){
    super(DownloadableContent.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    addReferenceField();
    
    FCloudStorageField imageTestField = new FCloudStorageField("DOCUMENT_CLOUD", "DocumentCloud", FLD_DOCUMENT, false, FLD_DOCUMENT_NAME);
    addField(imageTestField);
    
    FStringField imageName = new FStringField("DOCUMENT_NAME", "Document Name", FLD_DOCUMENT_NAME, false, 70);
    addField(imageName);
     
    FDateField uploadDate = new FDateField("UPLOAD_DATE", "Upload date", FLD_UPLOAD_DATE, false);
    addField(uploadDate);

    uploadDate = new FDateField("LAST_MODIFICATION_DATE", "Last modification date", FLD_LAST_MODIFICATION_DATE, false);
    addField(uploadDate);
    
    FBlobStringField description = new FBlobStringField("DESCRIPTION", "Description", FLD_DESCRIPTION, false,4,30);
    addField(description);
    
    FMultipleChoiceField mFld = new FMultipleChoiceField("VISIBILITY", "Visibility", FLD_VISIBILITY, false, 2);
    mFld.addChoice(VISIBILITY_PUBLIC, "Public");
    mFld.addChoice(VISIBILITY_USERS_ONLY, "Users only");
    addField(mFld);
  }
  
  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, DownloadableContentDesc.class);    
  }
  
  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    return list;
  }
}
