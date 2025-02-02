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

public class FunctionDivide extends Function {
	private static final String FUNCTION_NAME = "DIV";
	private static final String OPERATOR_SYMBOL = "/";

	/*public Object compute() {
		double value = 0;
		IOperand operand0 = getOperandAt(0);
		String value0Str = String.valueOf(operand0.compute());
		value = Double.valueOf(value0Str);
		
		int operandCount = getOperandCount();
		for(int i = 1; i < operandCount; i++){
			IOperand operand = getOperandAt(i);
			String operandValueStr = String.valueOf(operand.compute());
			double operandValue = Double.valueOf(operandValueStr);
			if(operandValue != 0){
				value /= operandValue;
			}
		}
		return value;
	}*/
	
	public synchronized Object compute(FFormulaNode formulaNode){
		double result = 0;
		if(formulaNode != null){
			FFormulaNode childNode = (FFormulaNode) formulaNode.getChildAt(0);
			if(childNode != null){
				double childNodeCalculatedValue = childNode.getCalculatedValue_double();
				result = childNodeCalculatedValue;
			}
			for(int i = 1; i < formulaNode.getChildCount(); i++){
				childNode = (FFormulaNode) formulaNode.getChildAt(i);
				if(childNode != null){
					double childNodeCalculatedValue = childNode.getCalculatedValue_double();
					if(childNodeCalculatedValue == 0){
						childNodeCalculatedValue = 1;
					}
					result = result / childNodeCalculatedValue;
				}
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
		return FunctionFactory.PRIORITY_MULTIPLICATIVE;
	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}

}
