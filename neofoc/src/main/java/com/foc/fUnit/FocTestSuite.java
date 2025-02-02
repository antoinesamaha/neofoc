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

import junit.framework.*;

public class FocTestSuite extends TestSuite {

  private LogInterface logFile = null;
  
  public FocTestSuite(Class<? extends TestCase> cls) {
    super(cls);
  }

  public FocTestSuite() {
  }

  public void run(TestResult result) {
    start(result);
    super.run(result);
    end();
  }

  public LogInterface getLogFile() {
    return logFile;
  }

  public void initializeLogFile() {
    if (logFile == null && getName()!= null) {
      //logFile = new LogFile(getName());
      logFile = new LogXMLFile(getName());
    }
  }

  public void logTestResult(){
    logFile.logTestResult(testCount());
  }
  
  public void start(TestResult result) {
    initializeLogFile();
    logFile.setTestResult(result);
  }
  
  public void end() {
    logTestResult();
    disposeLogFileObject();
  }
  
  public void disposeLogFileObject() {
    if(getName()!= null){
      logFile.dispose();
//      try {
//        Runtime.getRuntime().exec("notepad "+getName()+".txt");
//      } catch (IOException e) {
//        e.printStackTrace();
//      }  
    }
  }
}
