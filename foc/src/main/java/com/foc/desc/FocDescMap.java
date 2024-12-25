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
package com.foc.desc;

import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.shared.dataStore.AbstractDataStore;

@SuppressWarnings("serial")
public class FocDescMap extends HashMap<String, FocDesc> {

	private HashMap<String, FocDesc> lowerCaseMap;// Needed for API rest calls

	public FocDescMap() {
		lowerCaseMap = new HashMap<String, FocDesc>();
	}

	public void dispose() {
		if (lowerCaseMap != null) {
			lowerCaseMap = null;
		}

		Iterator<FocDesc> iter = values().iterator();
		while (iter != null && iter.hasNext()) {
			FocDesc focDesc = iter.next();
			if (focDesc != null) {
				focDesc.dispose();
			}
		}
		clear();
	}

	public static FocDescMap getInstance() {
		return (Globals.getApp() != null) ? Globals.getApp().getFocDescMap() : null;
	}

	@Override
	public FocDesc put(String arg0, FocDesc arg1) {
		lowerCaseMap.put(arg0.toLowerCase(), arg1);
		return super.put(arg0, arg1);
	}

	public FocDesc put(String tableName, int type, FocDesc arg1) {
		lowerCaseMap.put(tableName.toLowerCase(), arg1);
		return super.put(buildKey(tableName, type), arg1);
	}

	public FocDesc get(String arg0) {
		FocDesc focDesc = super.get(arg0);
		if (focDesc == null) {
			focDesc = lowerCaseMap.get(arg0.toLowerCase());
		}
		return focDesc;
	}

	public FocDesc get(String tableName, int type) {
		FocDesc focDesc = super.get(buildKey(tableName, type));
		if (focDesc == null) {
			focDesc = lowerCaseMap.get(tableName.toLowerCase());
		}		
		return focDesc;
	}

	private String buildKey(String tableName, int type) {
		return type != AbstractDataStore.TRANSACTION_TYPE_NONE ? tableName + "|" + type : tableName;
	}
}
