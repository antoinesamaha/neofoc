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
// MAIN
// COMPARE
// CONCURRENT ACCESS
// ACCESS
// LISTENERS
// REFERENCE
// DATABASE
// LIST
// XML

/*
 * Created on Oct 14, 2004
 */
package com.foc.desc;

import com.foc.desc.field.*;
import com.foc.gui.*;

/**
 * @author 01Barmaja
 */
public class FocObjectGeneral extends FocObject{

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // MAIN
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public FocObjectGeneral(FocConstructor constr) {
    super(constr);
    
    focDesc = constr.getFocDesc();
    FocFieldEnum enumer = new FocFieldEnum(focDesc, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(enumer != null && enumer.hasNext()){
      FField field = (FField) enumer.next();
      if(field != null){
        field.newProperty(this, null);
      }
    }
  }
     
  public void dispose(){
    super.dispose();
  }

  public FPanel newDetailsPanel(int viewID){
    return null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static FocDesc getFocDesc() {
    return focDesc;
  }
  
  public static void setFocDesc(FocDesc focDesc2) {
    focDesc = focDesc2;
  }

}
