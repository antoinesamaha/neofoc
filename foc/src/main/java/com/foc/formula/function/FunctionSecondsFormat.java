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

public class FunctionSecondsFormat extends Function {
	private static final String FUNCTION_NAME = "FORMAT_SECONDS";
	private static final String OPERATOR_SYMBOL = "";
	
	public synchronized Object compute(FFormulaNode formulaNode){
		String result = "";
		if(formulaNode != null){
			FFormulaNode childNodeHours  = (FFormulaNode) formulaNode.getChildAt(0);
			if(childNodeHours != null){
				double secondsDouble = childNodeHours.getCalculatedValue_double();

				int minutes = (int) (secondsDouble / 60);
				int seconds = (int) (secondsDouble - minutes*60); 
				int hours   = (int) (minutes / 60);
				minutes = minutes - hours * 60; 
				
				result = hours+":"+minutes+":"+seconds;
			}
		}
		return result;
	}
	
	/*
	public Object compute(FFormulaNode formulaNode){
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
	*/
	
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
