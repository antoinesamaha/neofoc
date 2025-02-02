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

import java.util.ArrayList;

import com.foc.formula.Formula;

public abstract class Function implements IOperand{
	
	public abstract boolean needsManualNotificationToCompute();
	
	private ArrayList<IOperand>          operandList   = null;
	private Formula                      formula       = null;
	
	public void dispose(){
		if(this.operandList != null){
			this.operandList.clear();
			this.operandList = null;
		}
		
	}
	
	public void setFormula(Formula formula){
		this.formula = formula;
	}
	
	protected Formula getFormula(){
		return this.formula;
	}
	
	private ArrayList<IOperand> getOperandList(){
		if(this.operandList == null){
			this.operandList = new ArrayList<IOperand>();
		}
		return this.operandList;
	}
	
	protected int getOperandCount(){
		return getOperandList().size();
	}
	
	protected IOperand getOperandAt(int i){
		return getOperandList().get(i);
	}
	
	public void addOperand(IOperand operand){
		getOperandList().add(operand);
	}
}
