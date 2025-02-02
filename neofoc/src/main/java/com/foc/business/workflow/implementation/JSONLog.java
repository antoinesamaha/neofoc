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
package com.foc.business.workflow.implementation;

import java.util.ArrayList;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.shared.json.B01JsonBuilder;

public class JSONLog {
	private FocObject focObject = null;
	private String    json      = null;
	
	public JSONLog(FocObject object) {
		focObject = object;
		
		if(focObject.workflow_IsLoggable()){
			B01JsonBuilder builder = new B01JsonBuilder();
			try {
				builder.setModifiedOnly(true);
				builder.setPrintObjectNamesNotRefs(true);
				builder.setScanSubList(true);
				builder.setPrintRootRef(false);
				focObject.toJson(builder);
				json = builder.toString();
				if(json.length() >= WFLogDesc.LEN_FLD_CHANGES) {
					json = json.substring(0, WFLogDesc.LEN_FLD_CHANGES-1);
				}
			} catch(Exception e) {
				Globals.logException(e);
			}
			builder.dispose();
		}
	}
	
	public void dispose() {
		focObject = null;
		json      = null;
	}
	
	public String getJson() {
		return json;
	}

	public FocObject getFocObject() {
		return focObject;
	}
	
	public boolean isEmptyLog() {
		return json == null || json.trim().equals("{}");
	}
	
	public class SQLBucket {
		private ArrayList<String> sqlArray = null;
		
		public SQLBucket() {
			
		}
		
		public void addSQL(String sql) {
			if(sql != null) {
				if(sqlArray == null) sqlArray = new ArrayList<>();
				sqlArray.add(sql);
			}
		}
	}
}
