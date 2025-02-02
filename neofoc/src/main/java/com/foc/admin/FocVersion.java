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
// PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// LIST
// DESCRIPTION
// VERSION LIST

/*
 * Created on 20-May-2005
 */
package com.foc.admin;

import java.util.*;

import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.gui.*;
import com.foc.list.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FocVersion extends FocObject{

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // PROPERTIES
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private FocVersion dbVersion = null;  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public FocVersion(FocConstructor constr) {
    super(constr);

    new FString(this, FLD_JAR, "") ;
    new FString(this, FLD_NAME, "") ;    
    new FDouble(this, FLD_ID, 0) ;
  }

  public String getJar(){
    String jar = null;
    FString pJar = (FString) getFocProperty(FLD_JAR);
    if(pJar != null){
      jar = pJar.getString();
    }
    return jar;
  }

  public void setJar(String jar){
    FString pJar = (FString) getFocProperty(FLD_JAR);
    if(pJar != null){
      pJar.setString(jar);
    }
  }

  public String getName(){
    String name = null;
    FString pName = (FString) getFocProperty(FLD_NAME);
    if(pName != null){
      name = pName.getString();
    }
    return name;
  }

  public void setName(String name){
    FString pName = (FString) getFocProperty(FLD_NAME);
    if(pName != null){
      pName.setString(name);
    }
  }

  public int getId(){
    int id = 0;
    FDouble pId= (FDouble) getFocProperty(FLD_ID);
    if(pId != null){
      id = pId.getInteger();
    }
    return id;
  }

  public void setId(int id){
    setPropertyInteger(FLD_ID, id);
  }

  public FocVersion getDbVersion() {
    return dbVersion;
  }

  public void setDbVersion(FocVersion dbVersion) {
    this.dbVersion = dbVersion;
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    return null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static FocList getList(){
    FocLink link = new FocLinkSimple(focDesc);
    FocList list = new FocList(link);
    list.reloadFromDB();

    FocListOrder listOrder = new FocListOrder();
    listOrder.addField(FFieldPath.newFieldPath(FLD_JAR));
    list.setListOrder(listOrder);    
    
    return list;
  }
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    return null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static final String TABLE_NAME = "FVERSION";
  
  public static final int FLD_JAR = 1;
  public static final int FLD_NAME = 2;
  public static final int FLD_ID = 3;  

  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(FocVersion.class, FocDesc.DB_RESIDENT, TABLE_NAME, false, false, false);

      focFld = focDesc.addReferenceField();

      focFld = new FStringField("JAR", "Jar", FLD_JAR, true, FStringField.NAME_LEN);
      focDesc.addField(focFld);

      focFld = new FStringField("NAME", "Name", FLD_NAME, false, FStringField.NAME_LEN);
      focDesc.addField(focFld);

      focFld = new FNumField("ID", "Id", FLD_ID, false, 6, 0);
      focDesc.addField(focFld);
    }
    return focDesc;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // VERSION LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static FocList versionList = null;
  
  public static FocList getVersionList(){
    if(versionList == null){
      FocLink link = new FocLinkSimple(FocVersion.getFocDesc());
      versionList = new FocList(link);
    }
    return versionList;
  }

  public static FocVersion findVersion(String jar, String name, int id){
  	FocVersion foundVersion = null; 
    FocList versionList = getVersionList();
    if(versionList != null){
      for(int i=0; i<versionList.size(); i++){
      	FocVersion ver = (FocVersion) versionList.getFocObject(i);
      	if(ver != null){
      		if(ver.getJar().equals(jar) && ver.getName().equals(name)){
      			foundVersion = ver;
      		}
      	}
      }
    }
    return foundVersion;
  }
  
  public static void addVersion(String jar, String name, int id){
    FocList versionList = getVersionList();
    if(versionList != null){
    	FocVersion ver = findVersion(jar, name, id);
    	if(ver == null){
	      ver = (FocVersion) versionList.newEmptyItem();
	      if(ver != null){
	        ver.setJar(jar);
	        ver.setName(name);
	        versionList.add(ver);        
	      }
    	}
    	ver.setId(id);
    }
  }

  public static FocVersion getDBVersionForModule(String jar){
  	FocVersion ver           =  null;
    FocList    dbVersionList = getList();
    if(dbVersionList != null){
    	//versionList.loadIfNotLoadedFromDB();
      ver = (FocVersion) dbVersionList.searchByPropertyStringValue(FocVersion.FLD_JAR, jar);
    }
    return ver;
  }

  public static int compareWithDatabaseVersion(){
    int diff = 0;
    FocList dbVerList = getList();
    if(dbVerList != null && versionList != null){
      Iterator iter = versionList.focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocVersion ver = (FocVersion) iter.next();
        if(ver != null){
          FocVersion dbVer = (FocVersion) dbVerList.searchByUniqueKey(ver);
          if(dbVer == null){
            if(diff == 0) diff = 1;
          }else{
            ver.setDbVersion(dbVer);
            int localDiff = ver.getId() - dbVer.getId();
            if(localDiff < 0 || diff == 0){
              diff = localDiff; 
            }
          }
        }
      }
    }
    return diff;
  }
  
  public static void saveVersions(){
    if(versionList != null){
      Iterator iter = versionList.focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocVersion ver = (FocVersion) iter.next();
        if(ver != null){
          FocVersion verToSave = null;
          FocVersion dbVer = ver.getDbVersion();
          if(dbVer != null){
            verToSave = dbVer; 
          }else{
            verToSave = (FocVersion) versionList.newEmptyDisconnectedItem();
            verToSave.setCreated(true);
            ver.setDbVersion(verToSave);
          }
          verToSave.setJar(ver.getJar());
          verToSave.setName(ver.getName());
          verToSave.setId(ver.getId());
          verToSave.save();
        }
      }
    }
  }
}
