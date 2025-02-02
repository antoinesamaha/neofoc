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
package com.foc.db.migration;

import java.util.ArrayList;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField121;
import com.foc.list.FocList;

public class MigrationSource extends FocObject {

	private boolean           mapFieldListConstructed = false;
	private ArrayList<String> columnTitleArray        = null;
	
  public MigrationSource(FocConstructor constr) {
    super(constr);
    mapFieldListConstructed = false;
    newFocProperties();
  }
  
  public void dispose(){
  	super.dispose();
  	if(columnTitleArray != null){
	  	columnTitleArray.clear();
	  	columnTitleArray = null;
  	}
  }

  public String getName(){
    return getPropertyString(FField.FLD_NAME);
  }
  
  public String getDescription(){
    return getPropertyString(MigrationSourceDesc.FLD_DESCRIPTION);
  }
  
  public String getFileName(){
    return getPropertyString(MigrationSourceDesc.FLD_FILE_NAME);
  }

  public String getTableName(){
    return getPropertyString(MigrationSourceDesc.FLD_TABLE_NAME);
  }

  public MigDataBase getDatabase(){
    return (MigDataBase) getPropertyObject(MigrationSourceDesc.FLD_DATABASE);
  }
  
  public MigDirectory getDirectory(){
    return (MigDirectory) getPropertyObject(MigrationSourceDesc.FLD_DIRECTORY);
  }  
  
  public int getSourceType(){
  	return getPropertyMultiChoice(MigrationSourceDesc.FLD_SOURCE_TYPE);
  }

  public FocDesc getDestinationFocDesc(){
  	return getPropertyDesc(MigrationSourceDesc.FLD_DESTINATION_TABLE);
  }

  public FocList getMapFieldList(){
  	return getMapFieldList(false);
  }
  
  public FocList getMapFieldList(boolean reload){
  	FocList list = getPropertyList(MigrationSourceDesc.FLD_FIELD_LIST);
  	if(!mapFieldListConstructed || reload){
  		list.loadIfNotLoadedFromDB();
  		FocDesc desc = getDestinationFocDesc();
  		FocFieldEnum enumer = desc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
  		while(enumer != null && enumer.hasNext()){
  			FField fld = (FField) enumer.next();
  			if(fld != null){
  				if(fld instanceof FObjectField121){
  					FObjectField121 obj121Fld = (FObjectField121) fld;
  		  		FocDesc desc121 = obj121Fld.getFocDesc();
  		  		FocFieldEnum enumer121 = desc121.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
  		  		while(enumer121 != null && enumer121.hasNext()){
  		  			FField fld2 = (FField) enumer121.next();
  		  			if(fld2 != null){
  		  				String fieldName = fld.getName()+"."+fld2.getName(); 
  		  				MigFieldMap mapFld = (MigFieldMap) list.searchByPropertyStringValue(MigFieldMapDesc.FLD_DB_FIELD_NAME, fieldName);
  		  				if(mapFld == null){
  		  					mapFld = (MigFieldMap) list.newEmptyItem();
  		  				}
  		  				mapFld.setDBFldType(fld2.getFabType());
  		  				mapFld.setDBFldID(fld2.getID());
  		  				mapFld.setDBFldName(fieldName);
  		  				mapFld.setDBFldTitle(fld2.getTitle());
  		  				mapFld.setDBFldExplanation(fld2.getExplanation());
  		  			}  		  			
  		  		}
  				}else{
	  				MigFieldMap mapFld = (MigFieldMap) list.searchByPropertyStringValue(MigFieldMapDesc.FLD_DB_FIELD_NAME, fld.getName());
	  				if(mapFld == null){
	  					mapFld = (MigFieldMap) list.newEmptyItem();
	  				}
	  				mapFld.setDBFldType(fld.getFabType());
	  				mapFld.setDBFldID(fld.getID());
	  				mapFld.setDBFldName(fld.getName());
	  				mapFld.setDBFldTitle(fld.getTitle());
	  				mapFld.setDBFldExplanation(fld.getExplanation());
  				}
  			}
  		}
  		
  		mapFieldListConstructed = true;
  	}
  	return list;
  }

//  public FocList getMapFieldList(){
//  	FocList list = getPropertyList(MigrationSourceDesc.FLD_FIELD_LIST);
//  	if(!mapFieldListConstructed){
//  		list.loadIfNotLoadedFromDB();
//  		FocDesc desc = getDestinationFocDesc();
//  		FocFieldEnum enumer = desc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
//  		while(enumer != null && enumer.hasNext()){
//  			FField fld = (FField) enumer.next();
//  			if(fld != null){
//  				MigFieldMap mapFld = (MigFieldMap) list.searchByPropertyStringValue(MigFieldMapDesc.FLD_DEST_FIELD_NAME, fld.getName());
//  				if(mapFld == null){
//  					mapFld = (MigFieldMap) list.newEmptyItem();
//  				}
//  				mapFld.setDestinationFieldID(fld.getID());
//  				mapFld.setDestinationFieldName(fld.getName());
//  				mapFld.setDestinationFieldTitle(fld.getTitle());
//  				mapFld.setDestinationFieldExplanation(fld.getExplanation());
//  				fld.getName();
//  			}
//  		}
//  		
//  		mapFieldListConstructed = true;
//  	}
//  	return list;
//  }
  
  public void doImport(){
  	if(getSourceType() == MigrationSourceDesc.SOURCE_TYPE_COMMA_SEPARATED_FILE){
  		importFile();
  	}else{
  		importDB();
  	}
  }
  
  private void importDB(){
  	
  }
  
  private void importFile(){
  	//IFocXmlService
  	
  	/*
  	boolean     error       = false;
  	InputStream inputStream = null; 
 
  	String fullFileName = getDirectory().getDirectoryPath();
  	error = fullFileName.isEmpty();
  	if(error){
  		Globals.getDisplayManager().popupMessage("No Directory selected!");
  	}
  	if(!error){
  		error = getFileName().isEmpty();
  		if(error) Globals.getDisplayManager().popupMessage("No File selected!");
  	}

  	if(!error){
	  	if(fullFileName.charAt(fullFileName.length() - 1) != '/'){
	  		fullFileName += "/";
	  	}
	  	fullFileName += getFileName();
	  	
	  	inputStream = Globals.getInputStream(fullFileName);
	  	error = inputStream == null;
  	}
  	
  	if(!error){
	  	Globals.getDBManager().beginTransaction();
	  	try{
		  	MigrationFileReader reader = new MigrationFileReader(this, inputStream, ',');
		  	reader.readFile();
		  	reader.dispose();
		  	Globals.getDBManager().commitTransaction();
	  	}catch(Exception e){
	  		Globals.logException(e);	  		
	  		Globals.getDBManager().rollbackTransaction(null);
	  	}
  	}
  	*/
  }

	public ArrayList<String> getColumnTitleArray() {
		return columnTitleArray;
	}

	public void setColumnTitleArray(ArrayList<String> columnTitleArray) {
		this.columnTitleArray = columnTitleArray;
	}
}
