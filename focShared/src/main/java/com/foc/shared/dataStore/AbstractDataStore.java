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
package com.foc.shared.dataStore;

import java.util.HashMap;
import java.util.Iterator;

public class AbstractDataStore implements IDataStoreConst {
	
	private HashMap<String, IFocData> map = null;
	
	public static int TRANSACTION_TYPE_NONE = -9999;
	
	public AbstractDataStore(){
		map = new HashMap<String, IFocData>();
	}
	
	public void dispose(){
		if(map != null){
			Iterator<IFocData> iter = map.values().iterator();
			while(iter.hasNext()){
				IFocData dataInStore = iter.next();
				dataInStore.dispose();
			}
			map.clear();
			map = null;
		}
	}
	
	public Object getData(String key){
		Object obj = map != null ? map.get(key) : null;
		return obj;  
	}
	
	public void putData(String key, IFocData data){
		if(map != null){
			map.put(key, data); 
		}
	}
	
	public void putList(String tableName, IFocData data){
		putList(tableName, data, TRANSACTION_TYPE_NONE);
	}
	
	public void putList(String tableName, IFocData data, int transactionType){
		String key = buildKey(tableName, transactionType, null);
		putData(key , data);
	}
	
	public Object getList(String tableName){
		return getList(tableName, null);
	}
	
	public Object getList(String tableName, String context){
		String key = buildKey(tableName, TRANSACTION_TYPE_NONE, context);
		return getData(key);
	}
	
	public Object getList(String tableName, int transactionType){
		Object obj = null;
		if(tableName != null){
			String key = buildKey(tableName, transactionType, null);
			if(key != null){
				obj = getData(key);
			}
		}
		return obj; 
	}
	
	public String buildKey(String tableName, int transactionType, String context){
		StringBuffer key = new StringBuffer("TABLE:"+tableName);
		if(transactionType != TRANSACTION_TYPE_NONE){
			key.append("|TYPE:"+transactionType);
		}
		if(context != null){
			key.append("|"+context);
		}
		return key.toString();
	}
	
//	public String buildKey(String tableName, int dataType, String context){
//		StringBuffer buff = new StringBuffer(tableName);
//		buff.append("|");
//		buff.append("|");
//		String key = context != null && !context.isEmpty() ? "TABLE:"+tableName+"|"+context : "TABLE:"+tableName;
//	}
}
