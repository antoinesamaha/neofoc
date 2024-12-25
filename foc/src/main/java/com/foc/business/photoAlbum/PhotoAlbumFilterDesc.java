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

import com.foc.desc.FocDesc;
import com.foc.desc.field.FFieldPath;
import com.foc.list.filter.BooleanCondition;
import com.foc.list.filter.FilterDesc;
import com.foc.list.filter.FocDescForFilter;
import com.foc.list.filter.LongCondition;
import com.foc.list.filter.ObjectCondition;
import com.foc.list.filter.StringCondition;

public class PhotoAlbumFilterDesc extends FocDescForFilter{

  public static final String TABLE_NAME_CONDITION = "TABLE_NAME";
  public static final String OBJECT_REF_CONDITION = "REF";
  public static final String URL_KEY_CONDITION    = "URL_KEY";
  public static final String SECTION_CONDITION    = "SECTION";
  public static final String CONDITION_TYPE       = "TYPE";
  public static final String VERSIONED_CONDITION  = "VERSIONED";
  
  public PhotoAlbumFilterDesc(){
    super(PhotoAlbumFilter.class, FocDesc.NOT_DB_RESIDENT, "IMAGE_FILTER", true);
  }
  
  @Override
  public FilterDesc getFilterDesc() {
    if(filterDesc == null){
      filterDesc = new FilterDesc(PhotoAlbumDesc.getInstance());

      StringCondition tableNameCondition = new StringCondition(FFieldPath.newFieldPath(PhotoAlbumDesc.FLD_TABLE_NAME), TABLE_NAME_CONDITION);
      filterDesc.addCondition(tableNameCondition);
      
      LongCondition refCondition = new LongCondition(FFieldPath.newFieldPath(PhotoAlbumDesc.FLD_OBJECT_REF), OBJECT_REF_CONDITION);
      filterDesc.addCondition(refCondition);

      StringCondition urlKeyCondition = new StringCondition(FFieldPath.newFieldPath(PhotoAlbumDesc.FLD_URL_KEY), URL_KEY_CONDITION);
      filterDesc.addCondition(urlKeyCondition);
      
      StringCondition sectionCondition = new StringCondition(FFieldPath.newFieldPath(PhotoAlbumDesc.FLD_SECTION), SECTION_CONDITION);
      filterDesc.addCondition(sectionCondition);
      
      BooleanCondition versionedCondition = new BooleanCondition(FFieldPath.newFieldPath(PhotoAlbumDesc.FLD_VERSIONED), VERSIONED_CONDITION);
      filterDesc.addCondition(versionedCondition);

      ObjectCondition typeCondition = new ObjectCondition(FFieldPath.newFieldPath(PhotoAlbumDesc.FLD_DOCUMENT_TYPE), CONDITION_TYPE);
      filterDesc.addCondition(typeCondition);
      
      filterDesc.setNbrOfGuiColumns(1);
    }
    return filterDesc;
  }
  
  private static PhotoAlbumFilterDesc focDesc = null;
  public static PhotoAlbumFilterDesc getInstance() {
    if(focDesc == null){
      focDesc = new PhotoAlbumFilterDesc();
    }
    return focDesc;
  } 
}
