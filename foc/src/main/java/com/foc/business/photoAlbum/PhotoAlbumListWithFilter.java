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

import java.io.InputStream;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocListOrder;
import com.foc.list.FocListWithFilter;
import com.foc.list.filter.BooleanCondition;
import com.foc.list.filter.FilterDesc;
import com.foc.list.filter.FocListFilter;
import com.foc.list.filter.LongCondition;
import com.foc.list.filter.ObjectCondition;
import com.foc.list.filter.StringCondition;
import com.foc.util.Utils;

@SuppressWarnings("serial")
public class PhotoAlbumListWithFilter extends FocListWithFilter {

	private FocObject focObject = null;
	private String    tableName = null;
	private long      objRef    = 0;
	
  public PhotoAlbumListWithFilter() {
    super(PhotoAlbumFilterDesc.getInstance(), new FocLinkSimple(PhotoAlbumDesc.getInstance()));
    getFocListFilter().setActive(this, false);
    setDirectlyEditable(false);
    setDirectImpactOnDatabase(true);
    FocListOrder order = new FocListOrder(PhotoAlbumDesc.FLD_UPLOAD_DATE);
    order.setReverted(true);
    setListOrder(order);
  }

  public PhotoAlbumListWithFilter(String tableName, int objRef) {
    this();
    setPhotoSubject(tableName, objRef); 
    applyFilterOnTableName(tableName);
    applyFilterOnObjectReference(objRef);
  }

  public PhotoAlbumListWithFilter(FocObject focObject){
  	this();
  	this.focObject = focObject;
  	if(focObject != null && focObject.hasRealReference()){
  	  setPhotoSubject(focObject.getThisFocDesc().getStorageName(), focObject.getReference().getInteger());
      applyFilterOnTableName(focObject.getThisFocDesc().getStorageName());
      applyFilterOnObjectReference(focObject.getReference().getInteger());
  	}
  }

  public PhotoAlbumListWithFilter(String urlKey){
  	this();
  	if(!Utils.isStringEmpty(urlKey)){
      applyFilterOnURLKey(urlKey);
  	}
  }
  
  public void dispose(){
  	super.dispose();
  }

  public void setPhotoSubject(String tableName, long objRef) {
    this.objRef    = objRef;
    this.tableName = tableName;
  }
  
  public long getObjectRef(){
  	return focObject != null ? focObject.getReference().getLong() : objRef;
  }
  
  public String getTableName(){
  	return focObject != null ? focObject.getThisFocDesc().getStorageName() : tableName;
  }
  
  public String getTransactionCode(){
  	return focObject != null ? focObject.code_getCode() : "";
  }

  public void applyFilterOnURLKey(String urlKey){
    if(urlKey != null && !urlKey.isEmpty()){
      FilterDesc filterDesc = getFocListFilter().getThisFilterDesc();
      StringCondition mcCond = (StringCondition) filterDesc.findConditionByFieldPrefix(PhotoAlbumFilterDesc.URL_KEY_CONDITION);
      mcCond.forceToValue((FocListFilter) getFocListFilter(), StringCondition.OPERATION_EQUALS, urlKey);
    }
  }
  
  public void applyFilterOnSection(String section){
	    if(section != null && !section.isEmpty()){
	      FilterDesc filterDesc = getFocListFilter().getThisFilterDesc();
	      StringCondition mcCond = (StringCondition) filterDesc.findConditionByFieldPrefix(PhotoAlbumFilterDesc.SECTION_CONDITION);
	      mcCond.forceToValue((FocListFilter) getFocListFilter(), StringCondition.OPERATION_EQUALS, section);
	    }
	  }
  
  public void applyFilterOnVersion() {
	  FilterDesc filterDesc = getFocListFilter().getThisFilterDesc();
	  BooleanCondition mcCond = (BooleanCondition) filterDesc.findConditionByFieldPrefix(PhotoAlbumFilterDesc.VERSIONED_CONDITION);
	  mcCond.forceToValue((FocListFilter) getFocListFilter(), BooleanCondition.VALUE_FALSE);
  }

  public void applyFilterOnTableName(String tableName){
    if(tableName != null && !tableName.isEmpty()){
      FilterDesc filterDesc = getFocListFilter().getThisFilterDesc();
      StringCondition mcCond = (StringCondition) filterDesc.findConditionByFieldPrefix(PhotoAlbumFilterDesc.TABLE_NAME_CONDITION);
      mcCond.forceToValue((FocListFilter) getFocListFilter(), StringCondition.OPERATION_EQUALS, tableName);
    }
  }

  public void applyFilterOnObjectReference(long objRef){
    if(objRef > 0){
      FilterDesc filterDesc = getFocListFilter().getThisFilterDesc();
      LongCondition mcCond = (LongCondition) filterDesc.findConditionByFieldPrefix(PhotoAlbumFilterDesc.OBJECT_REF_CONDITION);
      mcCond.forceToValue((FocListFilter) getFocListFilter(), LongCondition.OPERATOR_EQUALS, objRef, objRef);
    }
  }
  
  public void applyFilterOnObjectReference(int objRef){
    if(objRef > 0){
      FilterDesc filterDesc = getFocListFilter().getThisFilterDesc();
      LongCondition mcCond = (LongCondition) filterDesc.findConditionByFieldPrefix(PhotoAlbumFilterDesc.OBJECT_REF_CONDITION);
      mcCond.forceToValue((FocListFilter) getFocListFilter(), LongCondition.OPERATOR_EQUALS, objRef, objRef);
    }
  }
  
  public void applyFilterOnUnrelatedObjects(){
  	FilterDesc filterDesc = getFocListFilter().getThisFilterDesc();
  	LongCondition mcCond = (LongCondition) filterDesc.findConditionByFieldPrefix(PhotoAlbumFilterDesc.OBJECT_REF_CONDITION);
    mcCond.forceToValue((FocListFilter) getFocListFilter(), LongCondition.OPERATOR_EQUALS, 0, 0);
  }
  
  public void applyFilterOnDocumentType(DocumentType type){
  	FilterDesc filterDesc = getFocListFilter().getThisFilterDesc();
  	ObjectCondition mcCond = (ObjectCondition) filterDesc.findConditionByFieldPrefix(PhotoAlbumFilterDesc.CONDITION_TYPE);
    mcCond.forceToValue((FocListFilter) getFocListFilter(), LongCondition.OPERATOR_EQUALS, type);
  }

	public FocObject getFocObject() {
		return focObject;
	}
  
	public PhotoAlbum addPhotoAlbum(String fileName, InputStream inputStream){
		PhotoAlbum photoAlbum = (PhotoAlbum) newEmptyItem();
		if(photoAlbum != null){
		  photoAlbum.setImageName(fileName);
		  if(Globals.getApp() != null){
		  	photoAlbum.setCreationUser(Globals.getApp().getUser_ForThisSession());
		  	photoAlbum.setUploadDate(Globals.getApp().getSystemDate());
		  }
	  	photoAlbum.setTransactionCode(getTransactionCode());
	  	photoAlbum.fillURLKey();
	  	
	  	photoAlbum.setTableName(getTableName());
	  	photoAlbum.setObjectRef(getObjectRef());
		  
	  	photoAlbum.validate(true);
		  add(photoAlbum);
//		  photoAlbum.setImage(inputStream);
		  photoAlbum.setImageCloud(inputStream, Globals.getApp().getCloudStorageDirectory(), true, fileName);
		  photoAlbum.validate(true);
//		  validate(true);
		}
	  return photoAlbum;
	}
}
