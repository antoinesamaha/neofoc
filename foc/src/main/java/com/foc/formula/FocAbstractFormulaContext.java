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

import java.awt.Color;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.property.FDate;
import com.foc.property.FDouble;
import com.foc.property.FInt;
import com.foc.property.FList;
import com.foc.property.FMultipleChoice;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.property.FReference;
import com.foc.property.PropertyFocObjectLocator;

public abstract class FocAbstractFormulaContext extends AbstractFormulaContext {

	public abstract FocObject getOriginFocObject();

	private FFieldPath outputFieldPath = null;

	public FocAbstractFormulaContext(Formula formula, FFieldPath outputFieldPath) {
		super(formula);
		setOutputFieldPath(outputFieldPath);
	}

	public void dispose() {
		super.dispose();
		outputFieldPath = null;
	}

	private void setOutputFieldPath(FFieldPath outputFieldPath) {
		this.outputFieldPath = outputFieldPath;
	}

	public FFieldPath getOutputFieldPath() {
		return this.outputFieldPath;
	}

	public FocObject getOutputOriginFocObject() {
		return getOriginFocObject();
	}

	public boolean isFieldPathValid(FFieldPath fieldPath) {
		boolean valid = false;
		if (fieldPath != null) {
			valid = true;
			for (int i = 0; i < fieldPath.size() && valid; i++) {
				int at = fieldPath.get(i);
				valid = at != FField.NO_FIELD_ID;
			}
		}
		return valid;
	}

	/*
	 * public Object evaluateExpression(String expression){ Object object = null;
	 * if(expression != null){ FocObject originFocObject = getOriginFocObject();
	 * if(originFocObject != null){ FocDesc focDesc =
	 * originFocObject.getThisFocDesc(); FFieldPath fieldPath =
	 * FAttributeLocationProperty.newFieldPath(false, expression, focDesc);
	 * 
	 * if(fieldPath != null && isFieldPathValid(fieldPath)){ FProperty property =
	 * fieldPath.getPropertyFromObject(originFocObject); if(property != null){
	 * object = property.getObject(); } }else{ //it is not a legal path
	 * (fieldName.fieldName.fieldName) so get the value from a hashMap or
	 * something else } } } return object; }
	 */

	public Object evaluateExpression(String expression) throws FFormulaException {
		Object object = null;
		if (expression != null) {
			FocObject originFocObject = getOriginFocObject();
			if (originFocObject != null) {
				FocDesc focDesc = originFocObject.getThisFocDesc();
				// FProperty property =
				// FAttributeLocationProperty.newFieldPathReturnProperty(false,
				// expression, focDesc, getOriginFocObject());

				FProperty originProperty = outputFieldPath != null ? outputFieldPath.getPropertyFromObject(getOriginFocObject()) : null;
				PropertyFocObjectLocator propertyFocObjectLocator = new PropertyFocObjectLocator();
				propertyFocObjectLocator.parsePath(expression, focDesc, getOriginFocObject(), originProperty);
				FProperty property = propertyFocObjectLocator.getLocatedProperty();

				if (property != null) {
					FField fld = property.getFocField();
					FocObject obj = property.getFocObject();
					if (fld.isWithInheritance() && obj != null) {
						property = obj.getFirstCustomizedProperty(fld.getID());
					}
					if (property != null) {
						if (property instanceof FMultipleChoice) {
							object = property.getInteger();
						} else if (property instanceof FReference) {
							object = property.getInteger();
						} else if (property instanceof FList) {
							object = ((FList) property).getObject();
							if (object == null) object = ((FList) property).getList();
						} else if( property instanceof FDate){
							object = ((FDate) property).getDate();	
						}else {
							object = property.getObject();
						}
					}
				} else {
					// it is not a legal path (fieldName.fieldName.fieldName)
					throw new FFormulaException();
				}
			}
		}
		return object;
	}

	public void commitValueToOutput(Object value) {
		if (value != null) {
			FFieldPath outputFieldPath = getOutputFieldPath();
			if (outputFieldPath != null) {
				FocObject originFocObject = getOutputOriginFocObject();
				if (originFocObject != null) {
					FProperty property = outputFieldPath.getPropertyFromObject(originFocObject);
					if (property != null) {
						try {
							// property.setBackground(null);
							try {
								// boolean doSet = true;
								if (property instanceof FObject) {
									if (value == null || (value instanceof FocObject && property.getFocField().getFocDesc().getClass().isInstance(((FocObject) value).getThisFocDesc()))) {

									} else {
										value = null;
									}
								}
								property.setObject(value);
							} catch (Exception e) {
								if (value instanceof Boolean) {
									property.setObject(((Boolean) value).toString());
								} else if (value instanceof String) {
									// In this case the formula did not get to an object value to
									// we set to null
									if (property instanceof FObject) {
										property.setObject(null);
									} else if (property instanceof FDouble) {
										property.setObject(Double.valueOf(0));
									} else if (property instanceof FInt) {
										property.setObject(Integer.valueOf(0));
									}
								} else {
									throw e;
								}
							}
						} catch (Exception e) {
							if (getFormula() != null) {
								try {
									Globals.logString("Exception while evaluating :" + getFormula().getString() + " for field : " + property.getFocField().getName() + " value=" + (value != null ? value.toString() : "null"));
									Globals.logException(e);
								} catch (Exception r) {
									Globals.logException(r);
								}
							}
							// Globals.logExceptionWithoutPopup(e);
							property.setBackground(Color.RED);
						}
					}
				}
			}
		}
	}
}
