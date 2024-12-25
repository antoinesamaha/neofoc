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
package com.foc;

import java.io.Serializable;

import com.foc.business.workflow.implementation.LoggableChangeCumulator;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class FocThreadLocal implements Serializable{
	private static ThreadLocal<Object> threadWebServer      = new ThreadLocal<Object>();
	private static ThreadLocal<Object> threadWebApplication = new ThreadLocal<Object>();
	private static ThreadLocal<Object> threadLoggableSubSQLRequest = new ThreadLocal<Object>();
	
	public static void setWebServer(Object webServer){
		if(webServer == null){
			Globals.logString("THREAD SET SERVER ThreadID="+Thread.currentThread().getId()+" TO NULL");
		}
		
		UI currentUI = UI.getCurrent();
		if(currentUI != null){
			currentUI.setData(webServer);
		}
		
		threadWebServer.set(webServer);
		if(webServer != null){
			Globals.logString("THREAD SET SERVER ThreadID="+Thread.currentThread().getId()+" ");
		}		
	}

	public static Object getWebServer(){
		Object obj = null;
		
		UI currentUI = UI.getCurrent();
		if(currentUI != null){
			obj = currentUI.getData();
		}
		
		if(obj == null){
			obj = threadWebServer.get();
		}
//		if(obj == null){
//			Globals.logString("THREAD GETTING NULL SERVER !!! ThreadID="+Thread.currentThread().getId());
//		}
		return obj;
	}

	public static void unsetThreadLocal(){
		Globals.logString("THREAD SET SERVER ThreadID="+Thread.currentThread().getId()+" TO NULL (Remove)");
		threadWebServer.remove();
		threadWebApplication.remove();		
		threadLoggableSubSQLRequest.remove();
	}
	
	public static void setWebApplication(Object webApplication){
		UI.setCurrent((UI) webApplication);
		threadWebApplication.set(webApplication);
	}

	public static Object getWebApplication(){
		Object obj = UI.getCurrent(); 
		if(obj == null){
			Globals.logString("DEBUG_SESSION_NOT_VALID THIS IS NULL FocThreadLocal->UI.getCurrent()");
			obj = threadWebApplication.get();
			if(obj == null) Globals.logString("DEBUG_SESSION_NOT_VALID THIS IS NULL TOOO FocThreadLocal->threadWebApplication.get()");
		}
		return obj;
	}

	public static Object getLoggableChangeCumulator(){
		LoggableChangeCumulator logSqlReq = (LoggableChangeCumulator) threadLoggableSubSQLRequest.get();
		if(logSqlReq == null) {
			logSqlReq = new LoggableChangeCumulator();
			threadLoggableSubSQLRequest.set(logSqlReq);
		}
		return logSqlReq;
	}
	
}
