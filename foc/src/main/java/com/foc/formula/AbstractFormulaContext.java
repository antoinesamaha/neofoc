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

import com.foc.tree.TreeScanner;

public abstract class AbstractFormulaContext implements IFormulaContext{
	
	public abstract void plugListeners(String expression);
	public abstract void unplugListeners(String expression);
	
	private Formula formula = null;
	public AbstractFormulaContext(Formula formula){
		setFormula(formula);
	}
	
	public void dispose(){
		unplugListeners();
		this.formula = null;
	}
	
	public Object compute(){
		Object object = evaluateFormula();		
		commitValueToOutput(object);
		return object;
	}
	
	public Object evaluateFormula(){
		Object object = null;
		if(getFormula() != null){
			object = getFormula().compute(this);
		}
		return object;
	}
	
	public Formula getFormula(){
		return formula;
	}
	
	public void setFormula(Formula formula){
		this.formula = formula;
	}
	
	public boolean computeUponConstruction(){
		return this.formula != null ? this.formula.computeUponConstruction() : false;
	}
	
	public void plugListeners(){
		plugUnplugListeners(true);
	}
	
	public void unplugListeners(){
		plugUnplugListeners(false);
	}
	
	private void plugUnplugListeners(boolean plug){
		Formula formula = getFormula();
		if(formula != null){
			FFormulaTree formulaTree = formula.getFormulaTree();
			if(formulaTree != null){
				formulaTree.scan(new formulaTreeScaner(plug, this));
			}
		}
	}
	
	private static class formulaTreeScaner implements TreeScanner<FFormulaNode>{
		private boolean plug = false;
		private AbstractFormulaContext context = null;
		private formulaTreeScaner(boolean plug, AbstractFormulaContext context){
			this.plug = plug;
			this.context = context;
		}
		
		public void afterChildren(FFormulaNode node) {
			if(context != null){
				if(node != null && !node.isRoot()){
					String functionName = node.getFunctionName();
					if(functionName == null){
						String expression = node.getExpression();
						if(expression != null){
							if(plug){
								context.plugListeners(expression);
							}else{
								context.unplugListeners(expression);
							}
						}
					}
				}
			}
		}
		
		public boolean beforChildren(FFormulaNode node) {
			return true;
		}
	}
}



/*public abstract class AbstractFormulaContext implements FPropertyListener {

public abstract FocDesc getBaseFocDesc();

public static final int LISTENING_LEVEL_PROPERTY_LEVEL = 2;
public static final int LISTENING_LEVEL_FIELD_LEVEL    = 1;

public static final int LISTENING_MODE_SMART_LISTENER  = 4;
public static final int LISTENING_MODE_LAST_LEVEL      = 3;
public static final int LISTENING_MODE_FIRST_LEVEL     = 2;
public static final int LISTENING_MODE_NO_LISTENER     = 1;

private int listeningMode = 0;
private int listeningLevel = 0;
private int outputFieldID = FField.NO_FIELD_ID;

private Formula   formula       = null;                   
private FocObject baseFocObject = null;

public AbstractFormulaContext(int listeningMode, int listeningLevel, int outputFieldID, FocDesc baseFocDesc){
	setListeningMode(listeningMode);
	setListeningLevel(listeningLevel);
	setOutputFieldID(outputFieldID);
	setBaseFocDesc(baseFocDesc);
}

public AbstractFormulaContext(int listeningMode, int listeningLevel,int outputFieldID, FocObject baseFocObject){
	this(listeningMode, listeningLevel, outputFieldID, baseFocObject == null ? null : baseFocObject.getThisFocDesc());
	setBaseFocObject(baseFocObject);
}

public void dispose() {
	formula = null;
	baseFocObject = null; 
}	

public void contextNeeded(String value) {
	if(value != null){
		FFieldPath fieldPath = getFieldPathForStringValue(value);
		if(fieldPath != null){
			plugListenerAccordinglyToListeningAndLevelModes(fieldPath);
		}
	}
}

public Object evaluate(String value) {
	Object result = null;
	FFieldPath fieldPath = getFieldPathForStringValue(value);
	if(fieldPath != null){
		FProperty property = fieldPath.getPropertyFromObject(getBaseFocObject());
		if(property != null){
			result = property.getObject();
		}
	}else{
		//it is not a legal path (fieldName.fieldName.fieldName) so get the value from a hashMap or something else
	}
	return result;
}

public void propertyModified(FProperty property) {
	if(property != null){
		if(getListeningLevel() == AbstractFormulaContext.LISTENING_LEVEL_FIELD_LEVEL){
			FocObject baseFocObject = null;
			if(getListeningMode() == AbstractFormulaContext.LISTENING_MODE_FIRST_LEVEL){
				baseFocObject = property.getFocObject();					
			}else if (getListeningMode() == AbstractFormulaContext.LISTENING_MODE_LAST_LEVEL){
				
			}else if (getListeningMode() == AbstractFormulaContext.LISTENING_MODE_LAST_LEVEL){
				
			}
			setBaseFocObject(baseFocObject);
		}
		Formula formula = getFormula();
		if(formula != null){
			Object resultObject = formula.compute();
			FocObject baseFocObject = getBaseFocObject();
			if(baseFocObject != null){
				FProperty outputProperty = baseFocObject.getFocProperty(getOutputFieldID());
				if(outputProperty != null){
					outputProperty.setObject(resultObject);
				}
			}
		}
	}
}

private FFieldPath getFieldPathForStringValue(String value){
	FFieldPath fieldPath = null;
	if(value != null){
		FocDesc focDesc = getBaseFocDesc();
		if(focDesc != null){
			fieldPath = FAttributeLocationProperty.newFieldPath(false, value, focDesc);
		}
	}
	return fieldPath;
}


private void plugListenerAccordinglyToListeningAndLevelModes(FFieldPath fieldPath){
	if(fieldPath != null){
		int listeningMode = getListeningMode();
		switch(listeningMode){
			case AbstractFormulaContext.LISTENING_MODE_FIRST_LEVEL:
				int fieldId = fieldPath.get(0);
				plugListenerAccordinglyToListeningLevel(fieldId);
			break;
			
			case AbstractFormulaContext.LISTENING_MODE_LAST_LEVEL:
				fieldId = fieldPath.get(fieldPath.size() - 1);
				plugListenerAccordinglyToListeningLevel(fieldId);
			break;
			
			case AbstractFormulaContext.LISTENING_MODE_SMART_LISTENER:
			break;
		}
	}
}

private void plugListenerAccordinglyToListeningLevel(int fieldID){
	int listeningLevel = getListeningLevel();
	if(listeningLevel == AbstractFormulaContext.LISTENING_LEVEL_FIELD_LEVEL){
		FocDesc baseFocDesc = getBaseFocDesc();
		if(baseFocDesc != null){
			FField fField = baseFocDesc.getFieldByID(fieldID);
			if(fField != null){
				fField.addListener(this);
			}
		}
	}else if(listeningLevel == AbstractFormulaContext.LISTENING_LEVEL_PROPERTY_LEVEL){
		FocObject baseFocObject = getBaseFocObject();
		if(baseFocObject != null){
			FProperty property = baseFocObject.getFocProperty(fieldID);
			if(property != null){
				property.addListener(this);
			}
		}
	}
}

private int getListeningMode() {
	return listeningMode;
}

private void setListeningMode(int listeningMode) {
	this.listeningMode = listeningMode;
}

private int getListeningLevel() {
	return listeningLevel;
}

private void setListeningLevel(int listeningLevel) {
	this.listeningLevel = listeningLevel;
}

private int getOutputFieldID() {
	return outputFieldID;
}

private void setOutputFieldID(int outputFieldID) {
	this.outputFieldID = outputFieldID;
}

private Formula getFormula() {
	return formula;
}

public void setFormula(Formula formula) {
	this.formula = formula;
}

private void setBaseFocDesc(FocDesc baseFocDesc) {
	this.baseFocDesc = baseFocDesc;
}

private FocObject getBaseFocObject() {
	return baseFocObject;
}

private void setBaseFocObject(FocObject baseFocObject) {
	this.baseFocObject = baseFocObject;
}
}*/
