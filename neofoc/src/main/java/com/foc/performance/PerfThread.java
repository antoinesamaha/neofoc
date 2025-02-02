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

public class PerfThread {
	
	private PerfFunction rootFunction    = null;
	private PerfFunction currentFunction = null;
	
	public PerfThread(){
		rootFunction = new PerfFunction(null, "ROOT");
		rootFunction.start();
		currentFunction = rootFunction;
	}
	
	public void dispose(){
		if(rootFunction != null){
			rootFunction.dispose();
			rootFunction = null;
		}
		currentFunction = null;
	}
	
	public void start(String functionTitle){
		if(currentFunction != null){
			currentFunction = currentFunction.pushFunction(functionTitle);
			currentFunction.start();
		}
	}
	
	public void end(){
		if(currentFunction != null){
			currentFunction.end();
			currentFunction = currentFunction.getFather();
		}
	}
	
	public void startDBExec(){
		if(currentFunction != null){
			currentFunction.startDBExec();
		}
	}
	
	public void endDBExec(){
		if(currentFunction != null){
			currentFunction.endDBExec();
		}
	}

	public void endDBExecForRequest(String request){
		if(currentFunction != null){
			currentFunction.endDBExecForRequest(request);
		}
	}

	public void startDBRead(){
		if(currentFunction != null){
			currentFunction.startDBRead();
		}
	}
	
	public void endDBRead(){
		if(currentFunction != null){
			currentFunction.endDBRead();
		}
	}
	
	public void startProtection(){
		if(currentFunction != null){
			currentFunction.startProtection();
		}
	}
	
	public void endProtection(){
		if(currentFunction != null){
			currentFunction.endProtection();
		}
	}
	
	public void compute(){
		rootFunction.computeTotals();
		rootFunction.computePercentages();
	}
	
	public void printXML(PrintStream perfFile){
		if(rootFunction != null){
			rootFunction.printXML(perfFile);
		}
	}
	
	public void printCSV(PrintStream perfFile){
		if(rootFunction != null){
			perfFile.println("TITLE,TOTAL,DBExec,DBRead,NET");
			rootFunction.printCSV(perfFile);
		}
	}
}
