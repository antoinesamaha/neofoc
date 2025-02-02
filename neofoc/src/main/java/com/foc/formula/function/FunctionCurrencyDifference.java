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

import java.sql.Date;

import com.foc.business.currency.Currencies;
import com.foc.business.currency.Currency;
import com.foc.formula.FFormulaNode;
import com.foc.formula.FunctionFactory;
import com.foc.util.FocMath;

public class FunctionCurrencyDifference extends Function{

	private static final String FUNCTION_NAME   = "FX_RATE";
	private static final String OPERATOR_SYMBOL = "";
	
	@Override
	public synchronized Object compute(FFormulaNode formulaNode) {
		double result = 0.0;

		if(formulaNode != null && formulaNode.getChildCount() >= 2){
			FFormulaNode childNode1 = (FFormulaNode) formulaNode.getChildAt(0);
			FFormulaNode childNode2 = (FFormulaNode) formulaNode.getChildAt(1);
			FFormulaNode childNode3 = null;
			
			if(formulaNode.getChildCount() > 2){
				childNode3 = (FFormulaNode) formulaNode.getChildAt(2);
			}

			if(childNode1 != null && childNode2 != null){
				Object childNode1Value = childNode1.getCalculatedValue();
				Object childNode2Value = childNode2.getCalculatedValue();
				Object childNode3Value = childNode3.getCalculatedValue();
				
				Currency curr1 = Currencies.getCurrencyByName(childNode1Value.toString());
				Currency curr2 = Currencies.getCurrencyByName(childNode2Value.toString());
				
				if(childNode3Value != null){
					result = Currencies.getRate((Date)childNode3Value, curr1, curr2);
				}else{
					result = Currencies.getRate(curr1, curr2);					
				}
			}
		}
		return result;

	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}

	@Override
	public String getName() {
		return FUNCTION_NAME;
	}

	@Override
	public String getOperatorSymbol() {
		return OPERATOR_SYMBOL;
	}

	@Override
	public int getOperatorPriority() {
		return FunctionFactory.PRIORITY_NOT_APPLICABLE;
	}

}
