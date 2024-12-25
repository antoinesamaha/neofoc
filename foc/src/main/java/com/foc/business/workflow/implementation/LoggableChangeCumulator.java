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

import java.util.HashMap;
import java.util.Iterator;

import com.foc.FocThreadLocal;
import com.foc.desc.FocObject;

public class LoggableChangeCumulator {
	
	private HashMap<ILoggable, JSONLog> jsonMap = null;

	public LoggableChangeCumulator() {
		jsonMap = new HashMap<ILoggable, JSONLog>();
	}
	
	public void dispose() {
		if(jsonMap != null) {
			Iterator<JSONLog> iter = jsonMap.values().iterator();
			while(iter != null && iter.hasNext()) {
				JSONLog log = iter.next();
				log.dispose();
			}
			jsonMap.clear();
			jsonMap = null;
		}
	}
	
	public static LoggableChangeCumulator getInstanceForThread(){
		return (LoggableChangeCumulator) FocThreadLocal.getLoggableChangeCumulator();
	}
	
	public void push(ILoggable loggable) {
		JSONLog log = new JSONLog((FocObject) loggable);
		jsonMap.put((ILoggable) loggable, log);
	}

	public JSONLog get(ILoggable loggable) {
		JSONLog log = jsonMap.get(loggable);
		return log;
	}

	public String getJson(FocObject focObject) {
		JSONLog log = jsonMap.get((ILoggable) focObject);
		return log != null ? log.getJson() : null;
	}
	
	public void remove(ILoggable loggable) {
		JSONLog log = jsonMap.get(loggable);
		if(log != null) {
			jsonMap.remove(loggable);
			log.dispose();
		}
	}

	public void insertLogLine_IfNotInsertedYet(ILoggable loggable) {
		String jsonLog = null; 
		JSONLog log = get(loggable);
		if(log != null && !log.isEmptyLog()) {
			jsonLog = log.getJson();
			Loggable loggableHandler = loggable.iWorkflow_getWorkflow();
			if(loggableHandler != null) {
				loggableHandler.insertLogLine(WFLogDesc.EVENT_MODIFICATION, null, jsonLog);
			}
		}
		remove(loggable);
	}

}
