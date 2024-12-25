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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;

public class PerfFunction {

	private String                        title             = null;
	private PerfFunction                  father            = null;
	private HashMap<String, PerfFunction> map_PerfFunctions = null;
	//private ArrayList<PerfCall>           arr_TimeIntervals = null;
	private long                          time           = 0;
	private long                          callNumber     = 0;
	private long                          timeChrono     = 0;
	
	private long                          dbExecTime     = 0;
	private long                          dbReadTime     = 0;

	private long                          protectionTime = 0;

	private long                          dbExecTimeChrono     = 0;
	private long                          dbReadTimeChrono     = 0;
	private long                          protectionTimeChrono = 0;
	private int                           dbExecStartCounter     = 0;
	private int                           dbReadStartCounter     = 0;
	private int                           protectionStartCounter = 0;

	private double                        percentOfFather  = 0;
	private double                        percentDBExec    = 0;
	private double                        percentDBRead    = 0;
	
	private long                          lastRequestExec  = 0;
	private ArrayList<PerfDBRequest>      requestArrayList = null;
	
	public PerfFunction(PerfFunction father, String title){
		this.father = father;
		this.title  = title;
		//arr_TimeIntervals = new ArrayList<PerfCall>();
		map_PerfFunctions = new HashMap<String, PerfFunction>();
	}
		
	public void dispose(){
		/*
		if(arr_TimeIntervals != null){
			for(int i=0; i<arr_TimeIntervals.size(); i++){
				PerfCall inter = arr_TimeIntervals.get(i);
				inter.dispose();
			}
			arr_TimeIntervals = null;
		}
		*/
		
		if(map_PerfFunctions != null){
			Iterator<PerfFunction> iter = map_PerfFunctions.values().iterator();
			while(iter != null && iter.hasNext()){
				PerfFunction perfMeasure = iter.next();
				perfMeasure.dispose();
			}
			map_PerfFunctions.clear();
			map_PerfFunctions = null;
		}
	}
	
	public void printXML(PrintStream perfFile){
		perfFile.println("<"+title+" Total=\""+time+"\" DBExec=\""+dbExecTime+"\" DBRead=\""+dbReadTime+"\" Protection=\""+protectionTime+"\">");
		if(requestArrayList != null){
			for(int i=0; i<requestArrayList.size(); i++){
				PerfDBRequest perfReq = requestArrayList.get(i);
				if(perfReq != null){
					perfFile.println("<DBRequest time=\""+perfReq.getTime()+"\">"+perfReq.getRequest()+"</DBRequest>");
				}
			}
		}
		if(map_PerfFunctions != null){
			Iterator<PerfFunction> iter = map_PerfFunctions.values().iterator();
			while(iter != null && iter.hasNext()){
				PerfFunction perfMeasure = iter.next();
				perfMeasure.printXML(perfFile);
			}
		}
		perfFile.println("</"+title+">");
	}

	public void printCSV(PrintStream perfFile){
		boolean print = map_PerfFunctions == null;
		if(!print){
			print = map_PerfFunctions.values().size() == 0;
		}
		if(print){
			perfFile.println(title+","+time+","+dbExecTime+","+dbReadTime+","+protectionTime);
		}else{
			if(map_PerfFunctions != null){
				Iterator<PerfFunction> iter = map_PerfFunctions.values().iterator();
				while(iter != null && iter.hasNext()){
					PerfFunction perfMeasure = iter.next();
					perfMeasure.printCSV(perfFile);
				}
			}
		}
	}

	public void computeTotals(){
		if(map_PerfFunctions != null){
			Iterator<PerfFunction> iter = map_PerfFunctions.values().iterator();
			while(iter != null && iter.hasNext()){
				PerfFunction perfMeasure = iter.next();
				perfMeasure.computeTotals();
				time       += perfMeasure.getTime();
				dbReadTime += perfMeasure.getDbReadTime();
				dbExecTime += perfMeasure.getDbExecTime();
			}
		}
	}

	public void computePercentages(){
		if(map_PerfFunctions != null){
			Iterator<PerfFunction> iter = map_PerfFunctions.values().iterator();
			while(iter != null && iter.hasNext()){
				PerfFunction perfMeasure = iter.next();
				perfMeasure.computeTotals();
				if(time != 0){
					if(father != null && father.getTime() != 0){
						percentOfFather = 100 * time / father.getTime();
					}
					percentDBExec = 100 * dbExecTime / time;
					percentDBRead = 100 * dbReadTime / time;
				}
			}
		}
	}

	public String getTitle(){
		return title;
	}
	
	public PerfFunction getFather(){
		return father;
	}
	
	private HashMap<String, PerfFunction> getChildMap(){
		if(map_PerfFunctions == null){
			map_PerfFunctions = new HashMap<String, PerfFunction>();
		}
		return map_PerfFunctions;
	}

	public PerfFunction pushFunction(String functionTitle){
		PerfFunction function = getChildMap().get(functionTitle);
		if(function == null){
			function = new PerfFunction(this, functionTitle);
			getChildMap().put(functionTitle, function);
		}
		return function;
	}
	
	public void start(){
		callNumber++;
		timeChrono = System.currentTimeMillis();
	}
	
	public void end(){
		long end = System.currentTimeMillis();
		if(timeChrono == 0){
			Globals.logString("PERF MEASURE ERROR - function time end before start!!!");
		}
		time += (end - timeChrono);
		timeChrono = 0;
	}
		
	public void startDBExec(){
		if(dbExecStartCounter == 0){
			dbExecTimeChrono = System.currentTimeMillis();
		}
		dbExecStartCounter++;
	}

	public void endDBExec(){
		dbExecStartCounter--;
		if(dbExecStartCounter == 0){
			long end = System.currentTimeMillis();
			lastRequestExec = end - dbExecTimeChrono; 
			dbExecTime += lastRequestExec;
		}
	}

	public void endDBExecForRequest(String request){
		endDBExec();
		if(request != null){
			addRequest(request, lastRequestExec);
		}
	}

	private void addRequest(String request, long execTime){
		if(requestArrayList == null){
			requestArrayList = new ArrayList<PerfDBRequest>(); 
		}
		PerfDBRequest prefReq = new PerfDBRequest(request, execTime);
		requestArrayList.add(prefReq);
	}
	
	public void startDBRead(){
		if(dbReadStartCounter == 0){
			dbReadTimeChrono = System.currentTimeMillis();
		}
		dbReadStartCounter++;
	}

	public void endDBRead(){
		dbReadStartCounter--;
		if(dbReadStartCounter == 0){
			long end = System.currentTimeMillis();
			dbReadTime += end - dbReadTimeChrono;
		}
	}
	
	public void startProtection(){
		if(protectionStartCounter == 0){
			protectionTimeChrono = System.currentTimeMillis();
		}
		protectionStartCounter++;
	}

	public void endProtection(){
		protectionStartCounter--;		
		if(protectionStartCounter == 0){
			long end = System.currentTimeMillis();
			protectionTime += end - protectionTimeChrono;
		}
	}	

	public long getProtectionTime() {
		return protectionTime;
	}

	public void setProtectionTime(long protectionTime) {
		this.protectionTime = protectionTime;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getDbExecTime() {
		return dbExecTime;
	}

	public void setDbExecTime(long dbExecTime) {
		this.dbExecTime = dbExecTime;
	}

	public long getDbReadTime() {
		return dbReadTime;
	}

	public void setDbReadTime(long dbReadTime) {
		this.dbReadTime = dbReadTime;
	}

	public double getPercentOfFather() {
		return percentOfFather;
	}

	public void setPercentOfFather(double percentOfFather) {
		this.percentOfFather = percentOfFather;
	}

	public double getPercentDBRequest() {
		return percentDBExec;
	}

	public void setPercentDBRequest(double percentDBRequest) {
		this.percentDBExec = percentDBRequest;
	}

	public double getPercentDBRead() {
		return percentDBRead;
	}

	public void setPercentDBRead(double percentDBRead) {
		this.percentDBRead = percentDBRead;
	}
}
