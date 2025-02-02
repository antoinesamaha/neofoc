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
package com.foc.formula.fucntion.old;

import com.foc.Globals;
import com.foc.formula.FunctionFactory;

public class FunctionSum extends Function {
	private static final String FUNCTION_NAME = "SUM";
	private static final String OPERATOR_SYMBOL = "+";

	public Object compute(){
		double value = 0;
		int operandCount = getOperandCount();
		for(int i = 0; i < operandCount; i++){
			IOperand operand = getOperandAt(i);
			String operandValueStr = String.valueOf(operand.compute());
			try{
				double operandValue = Double.valueOf(operandValueStr);
				value += operandValue;
			}catch(NumberFormatException e){
				Globals.logString("Exception while computing value of function : "+ FunctionSum.getFunctionName());
			}
		}
		return value;
	}
	
	public static String getFunctionName(){
		return FUNCTION_NAME;
	}
	
	public static String getOperatorSymbol(){
		return OPERATOR_SYMBOL;
	}
	
	public static int getOperatorPriority(){
		return FunctionFactory.PRIORITY_ADDITIVE;
	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}

}
