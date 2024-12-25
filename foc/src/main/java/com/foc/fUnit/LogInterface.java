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

import junit.framework.TestResult;

public interface LogInterface {

  public void logSuiteName(String suiteName);
  
  public void logBeginTest(String testName);
	public void logEndTest(String testName);
	
  public void logBeginStep(String message);
  public void logEndStep();
  
  public void logBeginGuiAction(String comp, String compName, String action);
  public void logEndGuiAction();
  
  public void logBeginAssert(String message);
  public void logEndAssert();
  public void logStatus(boolean success);

  public void setTestResult(TestResult testResult);
  public void logTestResult(int testCount);
  
  public void dispose();
}
