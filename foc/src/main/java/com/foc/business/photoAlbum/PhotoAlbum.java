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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;

import com.foc.Globals;
import com.foc.admin.DocRightsGroup;
import com.foc.admin.DocRightsGroupUsers;
import com.foc.admin.DocRightsGroupUsersDesc;
import com.foc.admin.FocUser;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.util.ASCII;

@SuppressWarnings("serial")
public class PhotoAlbum extends FocObject{

	public PhotoAlbum(FocConstructor constr){
    super(constr);
    newFocProperties();
  }
  
	@Override
	public void dispose() {
		super.dispose();
	}
	
  /*public void setImage(InputStream inputStream){
  	Globals.getApp().getDataSource().focObject_addBlobFromInputStream(this, PhotoAlbumDesc.FLD_IMAGE, inputStream);
  }
  
  public InputStream getImage(){
  	return Globals.getApp().getDataSource().focObject_LoadInputStream(this, PhotoAlbumDesc.FLD_IMAGE);
  }*/
  
  public void setImageCloud(InputStream inputStream, String directoryName, boolean createDirIfNeeded, String fileName){
  	setPropertyCloudStorage(PhotoAlbumDesc.FLD_IMAGE, inputStream, directoryName, createDirIfNeeded, fileName);
  }
  
  public InputStream getImageCloud(){
  	return getPropertyCloudStorage(PhotoAlbumDesc.FLD_IMAGE);
  }
  
  public void setImageName(String imageName){
    setPropertyString(PhotoAlbumDesc.FLD_IMAGE_NAME, imageName);
  }
  
  public String getImageName(){
    return getPropertyString(PhotoAlbumDesc.FLD_IMAGE_NAME);
  }
  
  public void setTableName(String tableName){
    setPropertyString(PhotoAlbumDesc.FLD_TABLE_NAME, tableName);
  }
  
  public String getAllowedGroups(){
    return getPropertyString(PhotoAlbumDesc.FLD_ALLOWED_GROUPS);
  }
  
  public void setAllowedGroups(String allowedGroups){
    setPropertyString(PhotoAlbumDesc.FLD_ALLOWED_GROUPS, allowedGroups);
  }

  public String getURLKey(){
    return getPropertyString(PhotoAlbumDesc.FLD_URL_KEY);
  }
  
  public void setURLKey(String allowedGroups){
    setPropertyString(PhotoAlbumDesc.FLD_URL_KEY, allowedGroups);
  }

  public String getTableName(){
    return getPropertyString(PhotoAlbumDesc.FLD_TABLE_NAME);
  }
  
  public void setObjectRef(long objectRef){
    setPropertyLong(PhotoAlbumDesc.FLD_OBJECT_REF, objectRef);
  }
  
  public long getObjectRef(){
    return getPropertyLong(PhotoAlbumDesc.FLD_OBJECT_REF);
  }
  
  public void setUploadDate(Date uploadDate){
    setPropertyDate(PhotoAlbumDesc.FLD_UPLOAD_DATE, uploadDate);
  }
  
  public Date getUploadDate(){
    return getPropertyDate(PhotoAlbumDesc.FLD_UPLOAD_DATE);
  }
  
  public void setDescription(String description){
    setPropertyString(PhotoAlbumDesc.FLD_DESCRIPTION, description);
  }
  
  public String getDescription(){
    return getPropertyString(PhotoAlbumDesc.FLD_DESCRIPTION);
  }
  
  public FocList getPhotoAlbumAccessList(){
  	return getPropertyList(PhotoAlbumDesc.FLD_PHOTO_ALBUM_ACCESS_LIST);
  }
  
  public void setTransactionCode(String transactionCode){
    setPropertyString(PhotoAlbumDesc.FLD_TRANSACTION_CODE, transactionCode);
  }
  
  public String getTransactionCode(){
    return getPropertyString(PhotoAlbumDesc.FLD_TRANSACTION_CODE);
  }
  
  public FocUser getCreationUser(){
  	return (FocUser) getPropertyObject(PhotoAlbumDesc.FLD_CREATION_USER);
  }
	
	public void setCreationUser(FocUser user){
  	setPropertyObject(PhotoAlbumDesc.FLD_CREATION_USER, user);
  }
	
	public DocumentType getDocumentType(){
  	return (DocumentType) getPropertyObject(PhotoAlbumDesc.FLD_DOCUMENT_TYPE);
  }
	
	public void setDocumentType(DocumentType documentType){
  	setPropertyObject(PhotoAlbumDesc.FLD_DOCUMENT_TYPE, documentType);
  }
	
	public int getKeyWord(){
		return  getPropertyMultiChoice(PhotoAlbumDesc.FLD_KEYWORD);
	}
	
	public void setKeyWord(int keyWord){
		setPropertyMultiChoice(PhotoAlbumDesc.FLD_KEYWORD, keyWord);
	}
	
	public String getSection(){
		    return getPropertyString(PhotoAlbumDesc.FLD_SECTION);
	}
		  
	public void setSection(String section){
		    setPropertyString(PhotoAlbumDesc.FLD_SECTION, section);
	}
	
	public Boolean isVersioned(){
	    return getPropertyBoolean(PhotoAlbumDesc.FLD_VERSIONED);
	}
		  
	public void setVersioned(Boolean versioned){
		setPropertyBoolean(PhotoAlbumDesc.FLD_VERSIONED, versioned);
	}
	
	public void setVersionDate(Date versionDate){
	    setPropertyDate(PhotoAlbumDesc.FLD_VERSION_DATE_TIME, versionDate);
    }
  
    public Date getVersionDate(){
    	return getPropertyDate(PhotoAlbumDesc.FLD_VERSION_DATE_TIME);
    }
    
    
    public long getFileSize(){
  		return getPropertyLong(PhotoAlbumDesc.FLD_FILE_SIZE);
  	}
    
  	public void setFileSize(long fileSize){
  		setPropertyLong(PhotoAlbumDesc.FLD_FILE_SIZE, fileSize);
  	}

	
	public int hasAccess(){
		FocUser currentUser = Globals.getApp().getUser_ForThisSession();
		return hasAccess(currentUser);
	}
	
	public int hasAccess(FocUser currentUser){
		int include = PhotoAlbumAccessDesc.READ_ONLY;
		if(Globals.getApp() != null && currentUser != null){
			FocList photoAlbumAccessList = getPhotoAlbumAccessList();
			photoAlbumAccessList.loadIfNotLoadedFromDB();
			
			if(photoAlbumAccessList.size() == 0){
				include = PhotoAlbumAccessDesc.READ_WRITE;
			}else{
			  for(int t=0; t<photoAlbumAccessList.size() && (include == PhotoAlbumAccessDesc.READ_ONLY); t++){
			  	PhotoAlbumAccess access = (PhotoAlbumAccess) photoAlbumAccessList.getFocObject(t);
			  	DocRightsGroup group = access.getDocRightsGroup();
			  	if(group != null){
			  		FocList userList = group.getDocRightsGroupUsersList();
			  		DocRightsGroupUsers rightUser = (DocRightsGroupUsers) userList.searchByPropertyObjectReference(DocRightsGroupUsersDesc.FLD_USER, currentUser.getReferenceInt());
			  		include = rightUser != null ? PhotoAlbumAccessDesc.READ_WRITE : PhotoAlbumAccessDesc.READ_ONLY;
			  	}
			  }
			}
			
			if(include == PhotoAlbumAccessDesc.READ_WRITE && getDocumentType() != null){
				include = getDocumentType().hasAccess();
			}
		}
		return include;
	}
	
	public boolean isEditable(){
		boolean enabled = false;
		if(Globals.getApp() != null && Globals.getApp().getUser_ForThisSession() != null){
			if(getCreationUser() != null && getCreationUser().equalsRef(Globals.getApp().getUser_ForThisSession())){
				enabled = true;
			}else{
				enabled = hasAccess() == PhotoAlbumAccessDesc.READ_WRITE ? true : false;
			}
		}
		return enabled;
	}

//	public boolean hasAccess(){
//		FocUser currentUser = Globals.getApp().getUser_ForThisSession();
//		return hasAccess(currentUser);
//	}
//	
//	public boolean hasAccess(FocUser currentUser){
//		boolean include = false;
//		if(Globals.getApp() != null && currentUser != null){
//			FocList photoAlbumAccessList = getPhotoAlbumAccessList();
//			photoAlbumAccessList.loadIfNotLoadedFromDB();
//			
//			if(photoAlbumAccessList.size() == 0){
//				include = true;
//			}else{
//			  for(int t=0; t<photoAlbumAccessList.size() && !include; t++){
//			  	PhotoAlbumAccess access = (PhotoAlbumAccess) photoAlbumAccessList.getFocObject(t);
//			  	DocRightsGroup group = access.getDocRightsGroup();
//			  	if(group != null){
//			  		FocList userList = group.getDocRightsGroupUsersList();
//			  		DocRightsGroupUsers rightUser = (DocRightsGroupUsers) userList.searchByPropertyObjectReference(DocRightsGroupUsersDesc.FLD_USER, currentUser.getReferenceInt());
//			  		include = rightUser != null;
//			  	}
//			  }
//			}
//			
//			if(include && getDocumentType() != null){
//				include = getDocumentType().hasAccess();
//			}
//		}
//		return include;
//	}
	
	public class PhotoAlbumFileResource {

    private ByteArrayInputStream bais   = null;
    private PhotoAlbum           album  = null;
    
    public PhotoAlbumFileResource(File sourceFile, PhotoAlbum album){
//    	super(sourceFile);
    	this.album = album;
    }
    
    public void dispose(){
    	if(bais != null){
    		try{
					bais.close();
				}catch (IOException e){
					Globals.logException(e);
				}
    		bais =  null;	
    	}
    }
    
//    @Override
//    public DownloadStream getStream() {
//      DownloadStream downloadStream = null;
//      if(album.getImageCloud() instanceof FileInputStream){
//      	InputStream inputStream = album.getImageCloud();
//	      try{
//		      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		      int byt = 0;
//		      while((byt = inputStream.read()) != -1){
//		      	byteArrayOutputStream.write(byt);
//		      }
//		      inputStream.close();
//		      bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//	      }catch(Exception ex){
//	      	ex.printStackTrace();
//	      }
//      }else{
//      	bais = (ByteArrayInputStream) album.getImageCloud();
//      }
//      if(bais != null){
//      	String fileName = album.getImageName();
//        downloadStream = new DownloadStream(bais, "application/x-unknown", fileName);
//        downloadStream.setParameter("Content-Disposition", "attachment; filename=" + fileName);
//        downloadStream.setCacheTime(0);
//      }
//      return downloadStream;
//    }
  }
	
	public void fillURLKey(){
  	String randomKeyStringForURL = ASCII.generateRandomString(6, true, true).trim();
  	setURLKey(randomKeyStringForURL);
	}
}
