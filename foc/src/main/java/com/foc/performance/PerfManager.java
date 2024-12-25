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
package com.foc.performance;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.ConfigInfo;
import com.foc.Globals;

public class PerfManager {

	private HashMap<String, PerfThread> map_Thread = null;
	
	public PerfManager(){
	}
	
	public void dispose(){
		if(map_Thread != null){
			Iterator<PerfThread> iter = map_Thread.values().iterator();
			while(iter != null && iter.hasNext()){
				PerfThread perfMeasure = iter.next();
				perfMeasure.dispose();
			}
			map_Thread.clear();
			map_Thread = null;
		}
	}
	
	private HashMap<String, PerfThread> getThreadMap(){
		if(map_Thread == null){
			map_Thread = new HashMap<String, PerfThread>();
		}
		return map_Thread;
	}
	
	public boolean isActive() {
		return ConfigInfo.isPerformanceActive();
	}

	public void printAll(){
		PrintStream perfFileXML = null;
		PrintStream perfFileCSV = null;
    try{
    	perfFileXML = new PrintStream(Globals.logFile_GetFileName("performance", "xml"));
    	perfFileCSV = new PrintStream(Globals.logFile_GetFileName("performance", "csv"));
    }catch(Exception e){
    	Globals.logException(e);
    }
		
		if(map_Thread != null){
			Iterator iter = map_Thread.values().iterator();
			while(iter != null && iter.hasNext()){
				PerfThread thread = (PerfThread) iter.next();
				if(thread != null){
					thread.compute();
					thread.printXML(perfFileXML);
					thread.printCSV(perfFileCSV);
				}
			}
		}
		
    if(perfFileXML != null){
      perfFileXML = null;  
    }
    if(perfFileCSV != null){
      perfFileCSV = null;  
    }
	}
	
	//---------------------------------------------------------------
	// STATIC
	//---------------------------------------------------------------
	
	private static PerfManager perfManager = null;
	public static PerfManager getInstance(){
		if(perfManager == null){
			perfManager = new PerfManager();
		}
		return perfManager;
	}
	
	public static void start(String threadTitle, String functionTitle){
		PerfManager manager = getInstance();
		if(manager.isActive()){
			PerfThread thread = manager.getThreadMap().get(threadTitle);
			if(thread == null){
				thread = new PerfThread();
				manager.getThreadMap().put(threadTitle, thread);
			}
			thread.start(functionTitle);
		}
	}
	
	public static void end(String threadTitle){
		PerfManager manager = getInstance();
		if(manager.isActive()){
			PerfThread thread = getInstance().getThreadMap().get(threadTitle);
			thread.end();
		}
	}
	
	public static void startDBExec(String threadTitle){
		PerfManager manager = getInstance();
		if(manager.isActive()){
			PerfThread thread = manager.getThreadMap().get(threadTitle);
			if(thread != null){
				thread.startDBExec();
			}
		}
	}
	
	public static void endDBExec(String threadTitle){
		endDBExecForRequest(threadTitle, null);
	}

	public static void endDBExecForRequest(String threadTitle, String request){
		PerfManager manager = getInstance();
		if(manager.isActive()){
			PerfThread thread = getInstance().getThreadMap().get(threadTitle);
			thread.endDBExecForRequest(request);
		}
	}

	public static void startDBRead(String threadTitle){
		PerfManager manager = getInstance();
		if(manager.isActive()){
			PerfThread thread = manager.getThreadMap().get(threadTitle);
			if(thread != null){
				thread.startDBRead();
			}
		}
	}
	
	public static void endDBRead(String threadTitle){
		PerfManager manager = getInstance();
		if(manager.isActive()){
			PerfThread thread = getInstance().getThreadMap().get(threadTitle);
			thread.endDBRead();
		}
	}
	
	public static void startProtection(String threadTitle){
		PerfManager manager = getInstance();
		if(manager.isActive()){
			PerfThread thread = manager.getThreadMap().get(threadTitle);
			if(thread != null){
				thread.startProtection();
			}
		}
	}
	
	public static void endProtection(String threadTitle){
		PerfManager manager = getInstance();
		if(manager.isActive()){
			PerfThread thread = getInstance().getThreadMap().get(threadTitle);
			if(thread != null){
				thread.endProtection();
			}
		}
	}
	
	public static void start(String functionTitle){
		start("MAIN", functionTitle);
	}
	
	public static void end(){
		end("MAIN");
	}

	public static void startDBExec(){
		startDBExec("MAIN");
	}
	
	public static void endDBExec(){
		endDBExec("MAIN");
	}

	public static void endDBExecForRequest(String request){
		endDBExecForRequest("MAIN", request);
	}

	public static void startDBRead(){
		startDBRead("MAIN");
	}
	
	public static void endDBRead(){
		endDBRead("MAIN");
	}

	public static void startProtection(){
		startProtection("MAIN");
	}
	
	public static void endProtection(){
		endProtection("MAIN");
	}

	public static void print(){
		PerfManager manager = getInstance();
		if(manager.isActive()){
			manager.printAll();
		}
	}
}
