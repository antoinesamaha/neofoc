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
import com.foc.util.FocMath;

public class FunctionRound extends BooleanFunction {
	private static final String FUNCTION_NAME   = "ROUND";
//	private static final String OPERATOR_SYMBOL = "="     ;
	
	
	//First node in formula represents the value to round.
	//Second node in formula represents the precision.
	public synchronized Object compute(FFormulaNode formulaNode){
		double result = 0.0;

		if(formulaNode != null && formulaNode.getChildCount() >= 2){
			FFormulaNode childNode1 = (FFormulaNode) formulaNode.getChildAt(0);
			FFormulaNode childNode2 = (FFormulaNode) formulaNode.getChildAt(1);

			if(childNode1 != null && childNode2 != null){
				double valueToRound = childNode1.getCalculatedValue_double();
				double precision    = childNode2.getCalculatedValue_double();

				result = FocMath.round(valueToRound, precision);
			}
		}
		return result;
	}
	
	public boolean needsManualNotificationToCompute() {
		return false;
	}
	
	public String getName(){
		return FUNCTION_NAME;
	}
	
	public String getOperatorSymbol(){
		return null;
	}
	
	public int getOperatorPriority(){
		return FunctionFactory.PRIORITY_EQUALITY;
	}
}
