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

import java.io.InputStream;
import java.util.HashMap;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.file.FocFileReader;
import com.foc.list.FocList;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.shared.dataStore.IFocData;

public class MigrationFileReader extends FocFileReader{
	private FocDesc                  focDesc            = null;
	private FocList                  fieldsMapList      = null;
	private HashMap<String, Integer> map_title_Position = null;
	private String                   values[]           = null;
	private FocObject                tempFocObject      = null;
	private FocList                  list               = null; 
	
  public MigrationFileReader(MigrationSource migrationSource, InputStream inputStream, char fieldDelimiter) {
    super(inputStream, fieldDelimiter);
    focDesc       = migrationSource.getDestinationFocDesc();
    fieldsMapList = migrationSource.getMapFieldList();
  }
  
  public void dispose(){
		super.dispose();
		focDesc       = null;
		fieldsMapList = null;
	}
  
  private FocObject getTempFocObject(){
  	if(tempFocObject == null){
  		tempFocObject = getFocList().newEmptyDisconnectedItem(); 
  	}
  	return tempFocObject;
  }

  private FocList getFocList(){
  	if(list == null){
  		list = focDesc.getFocList(FocList.LOAD_IF_NEEDED);//new FocList(new FocLinkSimple(focDesc), null);
  	}
  	return list;
  }  
  
  private int getTitlePositionInExcel(String colTitleInExcel){
  	int pos = -1;
		Integer posObject = getMapTitle2Position().get(colTitleInExcel);
		if(posObject != null){
			pos = posObject.intValue();
		}
  	return pos;
  }
  
  private boolean fillFocObject(FocObject focObject, String values[]){
  	boolean ok = true;
  	for(int i=0; i<fieldsMapList.size() && ok; i++){
  		MigFieldMap fieldMap = (MigFieldMap) fieldsMapList.getFocObject(i);
//  		if(fieldMap.isMandatory()) ok = false;
  		FProperty prop = focObject.getFocProperty(fieldMap.getDBFldID());
  		if(prop != null){
  			String colTitleInExcel = fieldMap.getColumnTitle();
  			int pos = getTitlePositionInExcel(colTitleInExcel);
				if(pos >= 0){
					String value = values[pos];
	  			if(prop.getFocField().isObjectContainer() && prop instanceof FObject){
	  				FObject objProp = (FObject) prop;
	  				if(prop.getFocField().getFocDesc() != null){
	  					FocList listOfTargetObject = objProp.getPropertySourceList();
	  					if(listOfTargetObject == null){
	  						listOfTargetObject = prop.getFocField().getFocDesc().getFocList(FocList.LOAD_IF_NEEDED);
	  					}
	  						
	  					if(listOfTargetObject != null){
			  				String captionPropertyPath = fieldMap.getDBForeignFieldName();
			  				
			  				FocObject targetFocObject = null;
			  				//Searching for the FocObject in the target FocList
			  				for(int j=0; j<listOfTargetObject.size() && targetFocObject == null; j++){
			  					FocObject focObj = listOfTargetObject.getFocObject(j);
			  					IFocData focData = focObj.iFocData_getDataByPath(captionPropertyPath);
			  					if(focData instanceof FProperty){
			  						if(((FProperty)focData).getString().compareTo(value) == 0){
			  							targetFocObject = focObj;
			  						}
			  					}
			  				}
			  				
			  				if(targetFocObject != null){
			  					objProp.setLocalReferenceInt_WithoutNotification(targetFocObject.getReference().getInteger());	
			  				}
//			  				int reference = Globals.getApp().getDataSource().focObject_GetReference_ForFilter(prop.getFocField().getFocDesc(), captionPropertyPath+"='"+value+"'");
	  					}
	  				}
	  			}else{
	  				prop.setString(value);
	  			}
				}
  		}
			if(fieldMap.isMandatory()){
				if(prop == null || prop.isEmpty()) ok = false;
			}
  	}
  	if(focObject.code_hasCode()){
  		if(focObject.code_getCode().isEmpty()){
  			focObject.code_resetCode();
  		}
  	}
  	return ok;
  }
  
  private boolean isWithKey(){
  	boolean withKey = false;
		for(int f=0; f<fieldsMapList.size() && !withKey; f++){
			MigFieldMap fMap = (MigFieldMap) fieldsMapList.getFocObject(f);
			withKey = fMap.isKeyField();
		}
		return withKey;
  }
  
  private String getKeyForObject(FocObject obj){
  	String key = null;
  	if(obj != null){
			for(int f=0; f<fieldsMapList.size(); f++){
				MigFieldMap fMap = (MigFieldMap) fieldsMapList.getFocObject(f);
				if(fMap.isKeyField()){
					FProperty prop = obj.getFocProperty(fMap.getDBFldID());
					if(prop != null){
						key="|"+prop.getString();
					}
				}
			}
  	}
  	return key;
  }
  
  public FocObject searchInList(FocObject focObject){
  	FocObject foundObject = null;
  	if(focObject != null && isWithKey()){
  		String keyToFind = getKeyForObject(focObject);
  		for(int i=0; i<getFocList().size() && foundObject == null; i++){
  			FocObject obj = getFocList().getFocObject(i);
  			String    key = getKeyForObject(obj);
  			if(keyToFind.equalsIgnoreCase(key)){
  				foundObject = obj;
  			}
  		}
  	}
  	return foundObject;
  }
  
  @Override
  public void readFile(){
  	super.readFile();
  	getFocList().validate(true);
  }
  
	@Override
	public void readLine(StringBuffer buffer) {
		if(isFirstLine()){
			scanTokens(buffer);			
		}else{
			values = new String[getMapTitle2Position().size()];
			scanTokens(buffer);
			
			FocObject obj = getTempFocObject();
			boolean ok = fillFocObject(obj, values);
			if(ok){
				FocObject objInList = searchInList(obj);
				if(objInList == null){
					objInList = list.newEmptyItem();	
				}
				fillFocObject(objInList, values);
				objInList.validate(true);
			}
		}
	}

	public HashMap<String, Integer> getMapTitle2Position(){
		if(map_title_Position == null){
			map_title_Position = new HashMap<String, Integer>();
		}
		return map_title_Position;
	}
	
	@Override
	public void readToken(String token, int pos) {
		Globals.logString("Token "+token+" at "+pos);
		if(isFirstLine()){
			HashMap<String, Integer> map = getMapTitle2Position();
			map.put(token, pos);
		}else{
			if(pos < values.length){
				values[pos] = token;
			}
		}
	}
}
