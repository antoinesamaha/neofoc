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
package com.foc.pivot;

import com.foc.admin.FocVersion;
import com.foc.desc.FocModule;

public class PivotModule extends FocModule {

  public PivotModule() {
    FocVersion.addVersion("PIVOT", "pivot 1.0", 1000);
  }

  @Override
  public void declareFocObjectsOnce() {
    declareFocDescClass(FPivotValueDesc.class);
    declareFocDescClass(FPivotBreakdownDesc.class);
    declareFocDescClass(FPivotViewDesc.class);
  }
  
  
  private static PivotModule pivotModule = null;
  public static PivotModule getInstance(){
    if(pivotModule == null){
      pivotModule = new PivotModule();
    }
    return pivotModule;
  }
}
