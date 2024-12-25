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

public class FunctionMultiply extends Function {
	private static final String FUNCTION_NAME = "MULT";
	private static final String OPERATOR_SYMBOL = "*";

	/*public Object compute() {
		double value = 1;
		int operandCount = getOperandCount();
		for(int i = 0; i < operandCount; i++){
			IOperand operand = getOperandAt(i);
			double operandValue = 0;
			Object operandValueObj = operand.compute();
			if(operandValueObj != null){
				String operandValueStr = String.valueOf(operandValueObj);
				if(operandValueStr != null && operandValueStr.length() != 0){
					try{
						operandValue = Double.valueOf(operandValueStr);
					}catch(Exception e){
						Globals.logString("!! Exception !! parsing Operand string "+ operandValueStr + " : " + e.getMessage());
					}
				}
			}
			value *= operandValue;
		}
		return value;
	}*/
	
	public synchronized Object compute(FFormulaNode formulaNode){
		double result = 1;
		if(formulaNode != null){
			for(int i = 0; i < formulaNode.getChildCount(); i++){
				FFormulaNode childNode = (FFormulaNode) formulaNode.getChildAt(i);
				if(childNode != null){
					double childNodeCalculatedValue = childNode.getCalculatedValue_double();
					result *= childNodeCalculatedValue;
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
