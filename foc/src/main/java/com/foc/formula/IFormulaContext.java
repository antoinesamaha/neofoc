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
package com.foc.formula;

import java.util.HashMap;

public interface IFormulaContext {
	public abstract Object compute();
	public abstract Object evaluateFormula();
	public abstract Object evaluateExpression(String expression) throws FFormulaException;
	public abstract void commitValueToOutput(Object value);
	public abstract String getNewExpression(String oldExpression, HashMap<String, String> oldValuesNewValuedMap);

	public abstract void plugListeners();
	public abstract void unplugListeners();
	public abstract void dispose();

}
