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

import java.util.Date;

import com.foc.Globals;
import com.foc.business.calendar.FCalendar;
import com.foc.formula.FFormulaNode;
import com.foc.formula.FunctionFactory;
import com.foc.property.FDate;

public class FunctionSubstract extends Function {
	private static final String FUNCTION_NAME = "SUBSTR";
	private static final String OPERATOR_SYMBOL = "-";

	/*public Object compute() {
		double value = 0;
		IOperand operand0 = getOperandAt(0);
		//String value0Str = (String) operand0.compute();
		String value0Str = String.valueOf(operand0.compute());
		value = Double.valueOf(value0Str);
		
		int operandCount = getOperandCount();
		for(int i = 1; i < operandCount; i++){
			double operandValue = 0; 
			IOperand operand = getOperandAt(i);
			Object operandValueObj = operand.compute();
			if(operandValueObj != null){
				String operandValueStr = String.valueOf(operand.compute());
				operandValue = Double.valueOf(operandValueStr);
			}
			value -= operandValue;
		}
		return value;
	}*/
	
	private double compute_NormalSubstract(FFormulaNode formulaNode){
		double result = 0;
		if(formulaNode != null){
			FFormulaNode childNode = (FFormulaNode) formulaNode.getChildAt(0);
			FFormulaNode childNode2 = (FFormulaNode) formulaNode.getChildAt(1);
			if(childNode != null && childNode2 != null){
				if(childNode.getCalculatedValue() instanceof Date && childNode2.getCalculatedValue() instanceof Date){
					if(formulaNode.getChildCount()>1){
 					  long diffInMilliseconds = FCalendar.compareDatesRegardlessOfTime(((Date)childNode.getCalculatedValue()), ((Date)childNode2.getCalculatedValue()));
	          result = diffInMilliseconds/Globals.DAY_TIME;
					}
				}else{
					double childNodeCalculatedValue = childNode.getCalculatedValue_double();
					result = childNodeCalculatedValue;
					for(int i = 1; i < formulaNode.getChildCount(); i++){
						childNode = (FFormulaNode) formulaNode.getChildAt(i);
						if(childNode != null){
							childNodeCalculatedValue = childNode.getCalculatedValue_double();
							result -= childNodeCalculatedValue;
						}
					}
			  }
		  }
	  }
		return result;
  }
	
	private double compute_UnaryMinus(FFormulaNode formulaNode){
		double result = 0;
		if(formulaNode != null){
			FFormulaNode childNode = (FFormulaNode) formulaNode.getChildAt(0);
			if(childNode != null){
				double childNodeCalculatedValue = childNode.getCalculatedValue_double();
				result = -childNodeCalculatedValue;
			}
		}
		return result;
	}
	
	public synchronized Object compute(FFormulaNode formulaNode){
		double result = 0;
		if(formulaNode != null){
			if(formulaNode.getChildCount() == 1){
				result = compute_UnaryMinus(formulaNode);
			}else{
				result = compute_NormalSubstract(formulaNode);
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
