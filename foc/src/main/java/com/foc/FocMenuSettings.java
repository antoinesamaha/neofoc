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

public class FocMenuSettings {
  private boolean includeNavigation = true;
  private boolean includeAbout      = true;
  private boolean includeExit       = true;
  
  public FocMenuSettings(){
  }
  
  public boolean isIncludeNavigation() {
    return includeNavigation;
  }

  public void setIncludeNavigation(boolean includeNavigation) {
    this.includeNavigation = includeNavigation;
  }

  public boolean isIncludeAbout() {
    return includeAbout;
  }

  public void setIncludeAbout(boolean includeAbout) {
    this.includeAbout = includeAbout;
  }

  public boolean isIncludeExit() {
    return includeExit;
  }

  public void setIncludeExit(boolean includeExit) {
    this.includeExit = includeExit;
  }
}
