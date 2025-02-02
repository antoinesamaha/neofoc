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
package com.foc.formula.function;

import com.foc.formula.FFormulaNode;
import com.foc.formula.FunctionFactory;

public class FunctionLength extends Function {
	private static final String FUNCTION_NAME   = "LEN";
	private static final String OPERATOR_SYMBOL = "";

	public synchronized Object compute(FFormulaNode formulaNode){
		int result = 0;
		if(formulaNode != null && formulaNode.getChildCount() > 0){
			FFormulaNode childNode = (FFormulaNode) formulaNode.getChildAt(0);
			if(childNode != null){
				String childNodeCalculatedValue = childNode.getCalculatedValue_String();
				result = childNodeCalculatedValue.length();
			}
		}
		return result;
	}
	
	public String getName(){
		return FUNCTION_NAME;
	}
	
	public String getOperatorSymbol(){
		return OPERATOR_SYMBOL;
	}
	
	public int getOperatorPriority(){
		return FunctionFactory.PRIORITY_ADDITIVE;
	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}
}
