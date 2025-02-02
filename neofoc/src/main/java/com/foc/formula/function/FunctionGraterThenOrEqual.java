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

public class FunctionGraterThenOrEqual extends BooleanFunction {

	private static final String FUNCTION_NAME = "GE";
	private static final String OPERATOR_SYMBOL = ">=";

	/*public Object compute() {
		boolean res = false;
		IOperand operand1 = getOperandAt(0);
		IOperand operand2 = getOperandAt(1);
		if(operand1 != null && operand2 != null){
			String operandStr = String.valueOf(operand1.compute());
			Double operand1Double = Double.valueOf(operandStr);
			operandStr = String.valueOf(operand2.compute());
			Double operand2Double = Double.valueOf(operandStr);
			res = operand1Double.doubleValue() >= operand2Double.doubleValue();
		}
		return res;
	}*/
	
	public synchronized Object compute(FFormulaNode formulaNode){
		boolean result = false;
		if(formulaNode != null){
			FFormulaNode childNode1 = (FFormulaNode) formulaNode.getChildAt(0);
			FFormulaNode childNode2 = (FFormulaNode) formulaNode.getChildAt(1);
			if(childNode1 != null && childNode2 != null){
				double double1 = childNode1.getCalculatedValue_double();
				double double2 = childNode2.getCalculatedValue_double();
				result = double1 >= double2;
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
		return OPERATOR_SYMBOL;
	}
	
	public int getOperatorPriority(){
		return FunctionFactory.PRIORITY_RELATIONAL;
	}
}
