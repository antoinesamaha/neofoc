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

/*public class FunctionPath extends Function {
	private static final String FUNCTION_NAME   = "PATH";
	private static final String OPERATOR_SYMBOL = null;
	
	private FFieldPath fieldPath = null;
	
	public void dispose(){
		super.dispose();
		fieldPath = null;
		Formula formula = getFormula();
		if(this.fieldPath != null && formula != null){
			FProperty property = this.fieldPath.getPropertyFromObject(getFormula().getCurrentFocObject());
			if(property != null){
				property.removeListener(formula);
			}
		}
	}
	
	public void addOperand(IOperand operand){
		if(operand != null){
			super.addOperand(operand);
			createAndSetFieldPath();
		}
	}
	
	protected void addOperand_SUPPER(IOperand operand){
		super.addOperand(operand);
	}
	
	protected int getArgumentPositionForStringPath(){
		return 0;
	}
	
	private void createAndSetFieldPath(){
		IOperand operand = getOperandAt(getArgumentPositionForStringPath());
		FocDesc focDesc = getFocDescToStartFieldPathFrom();
		if(operand != null && focDesc != null){
			String strPath = String.valueOf(operand.compute());
			FFieldPath fieldPath = FAttributeLocationProperty.newFieldPath(false, strPath, focDesc);
			this.fieldPath = fieldPath;
			plugFormulaAsListener(fieldPath);
		}
	}
	
	public Object compute(){
		FProperty fProperty = null;
		FocObject focObject = getFocObjectToStartFieldPathFrom();
		FFieldPath fieldPaht = getFieldPath();
		if(focObject != null && fieldPaht != null){
			fProperty = fieldPaht.getPropertyFromObject(focObject);
		}
		return fProperty != null ? fProperty.getObject() : null;
	}
	
	protected void plugFormulaAsListener(FFieldPath fieldPath){
		if(getFormula() != null){
			if(this.fieldPath != null){
				int fieldId = this.fieldPath.get(0);
				FocDesc focDesc = getFocDescToStartFieldPathFrom();
				
        FField field  = this.fieldPath.getFieldFromDesc(focDesc);
        if( field != null ){
          field.addListener(getFormula());  
        }
        
        
        if(focDesc != null){
					FField field = focDesc.getFieldByID(fieldId);
					if(field != null){
						field.addListener(getFormula());
					}
				}
			}
		}
	}
	
	private FFieldPath getFieldPath(){
		return this.fieldPath;
	}
	
	protected FocObject getFocObjectToStartFieldPathFrom(){
		Formula formula = getFormula();
		return formula != null ? formula.getCurrentFocObject() : null;
	}
	
	protected FocDesc getFocDescToStartFieldPathFrom(){
		Formula formula = getFormula();
		return formula != null ? formula.getFocDesc() : null;
	}
	
	public static String getFunctionName(){
		return FUNCTION_NAME;
	}
	
	public static String getOperatorSymbol(){
		return OPERATOR_SYMBOL;
	}
	
	public static int getOperatorPriority(){
		return FunctionFactory.PRIORITY_NOT_APPLICABLE;
	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}
}*/

public class FunctionPath extends Function {
	private static final String FUNCTION_NAME   = "PATH";
	private static final String OPERATOR_SYMBOL = null;
	
	public void dispose(){
		super.dispose();		
	}
	
	protected void addOperand_SUPPER(IOperand operand){
		super.addOperand(operand);
	}
	
	protected int getArgumentPositionForStringPath(){
		return 0;
	}
	
		
	/*public Object compute(){
		Object result = null;
		Formula formula = getFormula();
		if(formula != null){
			AbstractFormulaContext context = formula.getContext();
			if(context != null){
				String operandString = (String) getOperandAt(0).compute();
				result = context.evaluate(operandString);
			}
		}
		return result;
	}*/
	
	public synchronized Object compute(FFormulaNode formulaNode) {
		Object result = null;
		if(formulaNode != null){
			FFormulaNode childNodeLevel0 = (FFormulaNode) formulaNode.getChildAt(0);
			if(childNodeLevel0 != null){
				result = childNodeLevel0.getCalculatedValue();
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
		return FunctionFactory.PRIORITY_NOT_APPLICABLE;
	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}
}

