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
package com.foc.list;

import java.util.*;

import com.foc.desc.FocObject;
import com.foc.desc.field.*;
import com.foc.property.*;

public class FocListOrder implements Comparator<FocListElement> {
  private FListOrderFieldList fieldList = null ;
  private boolean             reverted  = false; 

  public FocListOrder() {
    this.fieldList = new FListOrderFieldList();
  }

  public FocListOrder(int fld1) {
    this.fieldList = new FListOrderFieldList();
    addField(FFieldPath.newFieldPath(fld1));
  }

  public FocListOrder(int fld1, int fld2) {
    this.fieldList = new FListOrderFieldList();
    addField(FFieldPath.newFieldPath(fld1));
    addField(FFieldPath.newFieldPath(fld2));
  }
  
  public FocListOrder(int fld1, int fld2, boolean reverted) {
    this.reverted = reverted;
  	this.fieldList = new FListOrderFieldList();
    addField(FFieldPath.newFieldPath(fld1));
    addField(FFieldPath.newFieldPath(fld2));
  }

  public void dispose(){
    if(fieldList != null){
      fieldList.dispose();
      fieldList = null;
    }
  }
  
  public int getFieldsNumber() {
    return fieldList != null ? fieldList.size() : 0;
  }

  public FFieldPath getFieldAt(int i) {
    return fieldList != null ? fieldList.getFieldPathAt(i) : null;
  }

  public boolean isAscendingAt(int i) {
  	boolean ascending = fieldList != null ? fieldList.isFieldAscending(i) : true;
  	if(isReverted()) ascending = !ascending;
    return ascending;
  }

  public boolean isReverted(){
  	return reverted;
  }
  
  public void addField(FFieldPath path, boolean ascending) {
    if (fieldList != null) {
      fieldList.add(path, ascending);
    }
  }

  public void addField(FFieldPath path) {
    addField(path, true);
  }

  public int compareFocObject(FocObject focObj, FocObject otherFocObj) {
    int compRes = 0;
    boolean error = focObj == null || otherFocObj == null;

    if (!error) {
      FProperty prop = null;
      FProperty otherProp = null;

      for (int iFld = 0; iFld < getFieldsNumber() && compRes == 0; iFld++) {
        FFieldPath fPath = this.getFieldAt(iFld);

        if (fPath != null) {
          prop = fPath.getPropertyFromObject(focObj);
          otherProp = fPath.getPropertyFromObject(otherFocObj);
          if (prop != null && otherProp != null) {
            compRes = prop.compareTo(otherProp);
            if(!this.isAscendingAt(iFld)){
              compRes = -compRes;  
            }            
          }else{
          	if(prop == null && otherProp == null){
          		compRes = 0;
          	}else if(prop == null){
          		compRes = 1;
          	}else{
          		compRes = -1;
          	}
          }
        }
      }
    }else{
    	if(focObj == null && otherFocObj == null){
    		compRes = 0;
    	}else if(focObj == null){
    		compRes = 1;
    	}else{
    		compRes = -1;
    	}
    }

    return compRes;
  }

  public int compare(FocListElement element, FocListElement otherElement) {
    int compRes = 0;
    FocObject otherFocObj = null;
    FocObject focObj = null;
    boolean error = otherElement == null || element == null;

    if (!error) {
      otherFocObj = otherElement.getFocObject();
      focObj = element.getFocObject();
      error = otherFocObj == null || focObj == null;
    }

    if (!error) {
      compRes = compareFocObject(focObj, otherFocObj);
    }

    return compRes;
  }

	public void setReverted(boolean reverted) {
		this.reverted = reverted;
	}
}
