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

public class FunctionChoose extends Function {
	private static final String FUNCTION_NAME = "CHOOSE";
	private static final String OPERATOR_SYMBOL = "";
	
	/*public Object compute() {
		Object res = null;
		IOperand operand1 = getOperandAt(0);
		String operand1Str = String.valueOf(operand1.compute());
		boolean valueOfOperand1 = Boolean.valueOf(operand1Str);
		IOperand operandToApply = null;
		if(valueOfOperand1){
			operandToApply = getOperandAt(1);
		}else{
			operandToApply = getOperandAt(2);
		}
		if(operandToApply != null){
			res = operandToApply.compute();
		}
		return res;
	}*/
	
	public synchronized Object compute(FFormulaNode formulaNode){
		Object result = false;
		if(formulaNode != null){
			FFormulaNode childNode1 = (FFormulaNode) formulaNode.getChildAt(0);
			
			if(childNode1 != null){
				boolean childNode1Boolean = childNode1.getCalculatedValue_boolean();
				if(childNode1Boolean){
					FFormulaNode childNode2 = (FFormulaNode) formulaNode.getChildAt(1);
					if(childNode2 != null){
						result = childNode2.getCalculatedValue();
					}
				}else{
					FFormulaNode childNode3 = (FFormulaNode) formulaNode.getChildAt(2);
					if(childNode3 != null){
						result = childNode3.getCalculatedValue();
					}
				}
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
		return FunctionFactory.PRIORITY_CONDITIONAL;
	}
}
