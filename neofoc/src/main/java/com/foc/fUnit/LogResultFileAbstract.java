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
package com.foc.fUnit;

import java.io.FileWriter;
import java.io.PrintWriter;

import junit.framework.TestResult;

public abstract class LogResultFileAbstract implements LogInterface {

	protected FileWriter fileWriter = null;
	protected PrintWriter out = null;
	protected int step = 0;
	protected int indentation = 1;
	protected TestResult testResult = null;

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}
	
	protected String getIndentationSpaces(int nbrOfSpaces) {
		StringBuffer spaces = new StringBuffer("");
		for (int i = 0; i < nbrOfSpaces; i++){
			spaces.append("    ");
		}
		return spaces.toString();
	}

	protected String getLineBeginning() {
		return "-- ";
	}

	protected String getLineBeginningWithIndentation() {
		return getLineBeginning() + getIndentationSpaces(indentation);
	}

	public void logSuiteName(String suiteName) {
		out.println(getLineBeginning() + "Suite: " + suiteName);
	}

	public void logBeginTest(String testName) {
		out.println("--------------------------------------------------");
		out.println(getLineBeginning() + "Test: " + testName + " started");
	}

	public void logEndTest(String testName) {
		out.println("-- Test: " + testName + " ended");
		out.println("---------------------------------------------------");
		out.println("");
		out.println("");
	}

	public void logBeginStep(String message) {
		out.println(getLineBeginningWithIndentation() + "Step " + (++step) + " : " + message);
		indentation++;
	}

	public void logEndStep() {
		indentation--;
		out.println(getLineBeginningWithIndentation() + "Step Done");
	}

	public void logBeginGuiAction(String comp, String compName, String action) {
		out.println(getLineBeginningWithIndentation() + "Gui : " + comp + ": " + compName + "  Action: " + action);
		indentation++;
	}

	public void logEndGuiAction() {
		indentation--;
		out.println(getLineBeginningWithIndentation() + "Gui Done");
	}

	public void logBeginAssert(String message) {
		out.print(getLineBeginningWithIndentation() + "Asserting: " + message);
	}

	public void logEndAssert() {
		out.println("...Ok");
	}

	public void logStatus(boolean success){
		if(success){
			out.println("...Success");
		}else{
			out.println("...Fail");
		}
	}

	public void writeToFile(String line) {
		out.print(line);
	}

	public void writeToFileLn(String line) {
		out.println(line);
	}

	public FileWriter getFileWriter() {
		return fileWriter;
	}

	public PrintWriter getPrintWriter() {
		return out;
	}

	public void resetStepNumber() {
		step = 0;
	}

	public void logTestResult(int testCount) {
		writeToFileLn("---------------------------------------------------");
		writeToFileLn("---Test Result");
		writeToFileLn(getLineBeginning() + "Successfull: " + testResult.wasSuccessful());
		writeToFileLn(getLineBeginning() + "Errors: " + testResult.errorCount());
		writeToFileLn(getLineBeginning() + "Failures: " + testResult.failureCount());
		writeToFileLn(getLineBeginning() + "Test: " + testResult.runCount() + "/" + testCount);
		writeToFileLn("---------------------------------------------------");
	}

	public void dispose() {
		try {
			out.println("");
			out.println("-- End of suite --");
			out.flush();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
