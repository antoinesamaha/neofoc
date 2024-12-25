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
package com.foc.formula;

import com.foc.db.DBIndex;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;

public class CompositeKeyPropertyFormulaDesc extends PropertyFormulaDesc {
  
  public static final int FLD_VIEW_NAME         = 200;
  public static final int FLD_FILTER_CRITERIA   = 201;
  public static final int FLD_OBJECT_REFS       = 202;
  
  public CompositeKeyPropertyFormulaDesc(){
    super();
    setFocObjectClass(CompositeKeyPropertyFormula.class);
    setStorageName("FORMULA_PROP_COMP_KEY");
    
    FField focField = new FStringField("VIEWNAME", "View Name", FLD_VIEW_NAME, false, 30);
    addField(focField);
    
    focField = new FStringField("FILTERCRITERIA", "FilterCriteria", FLD_FILTER_CRITERIA, false, 30);
    addField(focField);
    
    focField = new FStringField("OBJECTREFS", "Object Refs", FLD_OBJECT_REFS, false, 30);
    addField(focField);
    
    DBIndex index = new DBIndex("FILTER_CRITERIA", this, false);
    index.addField(FLD_FILTER_CRITERIA);
    indexAdd(index);
  }
  
  private static FocDesc focDesc = null;
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new CompositeKeyPropertyFormulaDesc();
    }
    return focDesc;
  }
}
