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

import java.util.HashMap;

import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;

public class FocSimpleFormulaContext extends FocAbstractFormulaContext {
	private FocObject originFocObject = null;
	
	public FocSimpleFormulaContext(Formula formula, FFieldPath outputFieldPath) {
		super(formula, outputFieldPath);
	}
	
	public FocSimpleFormulaContext(Formula formula) {
		this(formula, null);
	}
	
	public void dispose(){
		originFocObject = null;
	}

	public Object compute(FocObject originFocObject){
		this.originFocObject = originFocObject;
		return compute();
	}
	
	@Override
	public void plugListeners(String expression) {
	}

	@Override
	public void unplugListeners(String expression) {
	}

	public String getNewExpression(String oldExpression, HashMap<String, String> oldValuesNewValuedMap) {
		return null;
	}

	@Override
	public FocObject getOriginFocObject() {
		return originFocObject;
	}
	
	public boolean computeBooleanValue(FocObject focObj){
		boolean visible = false;
    Object valueObj = compute(focObj);
    if(valueObj instanceof Boolean){
      visible = ((Boolean)valueObj).booleanValue();
    }else if(valueObj instanceof String){
      visible = ((String)valueObj).toUpperCase().equals("TRUE");                    
    }
    return visible;
	}
}
