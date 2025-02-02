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
package com.foc.db.sync;

import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.db.sync.model.SyncTable;
import com.foc.desc.FocDesc;

public class SyncManager {

	private HashMap<String, SyncTable> syncTableMap = null;
	
	public SyncManager(){
	}

	public void dispose(){
		if(syncTableMap != null){
			Iterator iter = syncTableMap.values().iterator();
			while(iter != null && iter.hasNext()){
				SyncTable st = (SyncTable) iter.next();	
				st.dispose();
			}
			syncTableMap.clear();
			syncTableMap = null;
		}
	}

	public HashMap<String, SyncTable> getSyncTableArray(boolean create){
		if(syncTableMap == null && create){
			syncTableMap = new HashMap<String, SyncTable>();

			Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
	    while(iter != null && iter.hasNext()){
	    	IFocDescDeclaration focDescDeclaration = iter.next();
	    	if(focDescDeclaration != null){
		    	FocDesc focDesc =  focDescDeclaration.getFocDescription();
		    	if(focDesc != null && focDesc.isDbResident()){
		    		SyncTable syncTable = new SyncTable(focDesc);
		    		syncTableMap.put(focDesc.getStorageName(), syncTable);
		    	}
	    	}
	    }
		}
		return syncTableMap;
	}
	
	public void fillTableListFromDatabase(){
		HashMap<String, SyncTable> syncTableMap = getSyncTableArray(true);
		
		Iterator<SyncTable> iter = syncTableMap.values().iterator();
		while(iter != null && iter.hasNext()){
			SyncTable syncTable = iter.next();
			syncTable.fillFromDatabase();
		}
	}
	
	public boolean writeToFile(){
		boolean error = false;
		
		return error;
	}
}
