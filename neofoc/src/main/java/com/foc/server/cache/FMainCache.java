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
package com.foc.server.cache;

import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.desc.FocDesc;

public class FMainCache {

	private HashMap<String, FTableCache> tableToFiltersMap = null;
	
	public FMainCache(){
		tableToFiltersMap = new HashMap<String, FTableCache>();
	}
	
	public void dispose(){
		if(tableToFiltersMap != null){
			Iterator<FTableCache> iter = tableToFiltersMap.values().iterator();
			while(iter != null && iter.hasNext()){
				FTableCache tableCache = iter.next();
				if(tableCache != null){
					tableCache.dispose();
					tableCache = null;
				}
			}
			tableToFiltersMap.clear();
			tableToFiltersMap = null;
		}
	}
	
	public FTableCache pushTableCache(String storageName){
		FTableCache tableCache = tableToFiltersMap.get(storageName);
		if(tableCache == null){
			FocDesc focDesc = Globals.getApp().getFocDescByName(storageName);
			tableCache = new FTableCache(focDesc);
			tableToFiltersMap.put(storageName, tableCache);
		}
		return tableCache;
	}
	
	public void getList(StringBuffer stringBuffer, String storageName, String filter){
		FTableCache tableCache = pushTableCache(storageName);
		if(tableCache != null){
			FListCache listCache = tableCache.pushListCache(filter);
			if(listCache != null){
				
			}
		}
	}
}
