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
//IMPLEMENTED

/*
 * Created on Sep 9, 2005
 */
package com.foc.list.filter;

import java.awt.Component;
import java.awt.GridBagConstraints;

import com.foc.ConfigInfo;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.gui.FPanel;
import com.foc.property.FMultipleChoice;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.property.FString;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class ObjectCondition extends FilterCondition {
	static public final int FLD_CONDITION_OPERATION = 1;
	static public final int FLD_CONDITION_OBJECT = 2;
	static public final int FLD_CONDITION_OBJECT_LIST_STRING = 3;

	static public final int OPERATION_NONE = 0;
	static public final int OPERATION_EQUALS = 3;
	static public final int OPERATION_EMPTY = 4;
	static public final int OPERATION_NOT_EMPTY = 5;
	static public final int OPERATION_IN = 6;
	static public final int OPERATION_DIFFERENT_THEN = 7;

	private boolean withInList = false;
	private String captionProperty = null;

	public ObjectCondition(FFieldPath objectFieldPath, String fieldPrefix) {
		super(objectFieldPath, fieldPrefix);
	}

	public ObjectCondition(int fieldID) {
		super(fieldID);
	}

	public void setWithInList(boolean with) {
		withInList = with;
	}

	public boolean isWithInList() {
		return withInList;
	}

	public int getOperation(FocListFilter filter) {
		FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
		return prop.getInteger();
	}

	public void setOperation(FocListFilter filter, int operation) {
		filter.setPropertyMultiChoice(getFirstFieldID() + FLD_CONDITION_OPERATION, operation);
	}
	
	public void setOperation(FocListFilter filter, String operation) {
		filter.setPropertyMultiChoice(getFirstFieldID() + FLD_CONDITION_OPERATION, operation);
	}

	public FocObject getObject(FocListFilter filter) {
		FObject prop = (FObject) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OBJECT);
		return prop.getObject_CreateIfNeeded();
	}

	public void setObject(FocListFilter filter, FocObject object) {
		setOperation(filter, OPERATION_EQUALS);

		FObject oprop = (FObject) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OBJECT);
		oprop.setObject(object);
	}
	
	public void setObject(FocListFilter filter, long objectRef) {
		setOperation(filter, OPERATION_EQUALS);

		FObject oprop = (FObject) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OBJECT);
		oprop.setLocalReferenceInt(objectRef);
	}

	public String getInList(FocListFilter filter) {
		FString prop = (FString) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OBJECT_LIST_STRING);
		return prop.getString();
	}

	public void setInList(FocListFilter filter, String inList) {
		FString prop = (FString) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OBJECT_LIST_STRING);
		prop.setString(inList);
	}

	public void setToValue(FocListFilter filter, int operation, FocObject focObject) {
		setToValue(filter, operation, focObject, false);
	}

	public void forceToValue(FocListFilter filter, int operation, FocObject focObject) {
		setToValue(filter, operation, focObject, true);
	}

	private void setToValue(FocListFilter filter, int operation, FocObject focObject, boolean lockConditionAlso) {
		FProperty operationProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
		operationProp.setInteger(operation);
		if (lockConditionAlso) {
			operationProp.setValueLocked(true);
		}
		FObject valueProp = (FObject) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OBJECT);
		valueProp.setObject(focObject);
		if (lockConditionAlso) {
			valueProp.setValueLocked(true);
		}
	}

	// oooooooooooooooooooooooooooooooooo
	// oooooooooooooooooooooooooooooooooo
	// IMPLEMENTED
	// oooooooooooooooooooooooooooooooooo
	// oooooooooooooooooooooooooooooooooo

	public void fillProperties(FocObject focFatherObject) {
		new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_CONDITION_OPERATION, OPERATION_NONE);
		new FObject(focFatherObject, getFirstFieldID() + FLD_CONDITION_OBJECT, null);
		if (isWithInList()) {
			new FString(focFatherObject, getFirstFieldID() + FLD_CONDITION_OBJECT_LIST_STRING, "");
		}
	}

	@Override
	public void copyCondition(FocObject tarObject, FocObject srcObject, FilterCondition srcCondition) {
		copyProperty(tarObject, getFirstFieldID() + FLD_CONDITION_OPERATION, srcObject, srcCondition.getFirstFieldID() + FLD_CONDITION_OPERATION);
		copyProperty(tarObject, getFirstFieldID() + FLD_CONDITION_OBJECT, srcObject, srcCondition.getFirstFieldID() + FLD_CONDITION_OBJECT);
		if (isWithInList()) {
			copyProperty(tarObject, getFirstFieldID() + FLD_CONDITION_OBJECT_LIST_STRING, srcObject, srcCondition.getFirstFieldID() + FLD_CONDITION_OBJECT_LIST_STRING);
		}
	}

	public boolean includeObject(FocListFilter filter, FocObject object) {
		boolean include = true;
		int operation = getOperation(filter);
		if (operation != OPERATION_NONE) {
			FocObject condObject = getObject(filter);

			FObject objProp = (FObject) getFieldPath().getPropertyFromObject(object);
			FocObject itemObject = objProp != null ? objProp.getObject_CreateIfNeeded() : null;

			switch (operation) {
			case OPERATION_EQUALS:
				include = false;
				if (condObject == null && itemObject == null) {
					include = true;
				} else if (condObject != null && itemObject != null) {
					include = itemObject.getReference().getInteger() == condObject.getReference().getInteger();
				}
				break;
			case OPERATION_DIFFERENT_THEN:
				include = false;
				if (condObject != null && itemObject == null) {
					include = true;
				}else if (condObject == null && itemObject != null) {
					include = true;
				} else if (condObject != null && itemObject != null) {
					include = itemObject.getReference().getInteger() != condObject.getReference().getInteger();
				}
				break;
			case OPERATION_EMPTY:
				include = itemObject == null;
				break;
			case OPERATION_NOT_EMPTY:
				include = itemObject != null;
				break;
			case OPERATION_IN:
				String refList = getInList(filter);
				include = false;
				if (refList != null && !refList.isEmpty()) {
					refList.replaceAll(" ", "");
					refList = "," + refList + ",";
					include = refList.contains("," + object.getReference().getInteger() + ",");
				}
				break;
			}
		}
		return include;
	}

	@Override
	public void forceFocObjectToConditionValueIfNeeded(FocListFilter filter, FocObject focObject) {
		if (getOperation(filter) == OPERATION_EQUALS) {
			FObject objProp = (FObject) getFieldPath().getPropertyFromObject(focObject);
			if (objProp != null) {
				objProp.setObject(getObject(filter));
			}
		}
	}

	public StringBuffer buildSQLWhere(FocListFilter filter, String fieldName) {
		StringBuffer buffer = null;

		String fieldPrefix = "";
		int dot = fieldName.indexOf('.');
		if (dot > 0) {
			fieldPrefix = fieldName.substring(0, dot + 1);
		}

		int operation = getOperation(filter);
		if (operation != OPERATION_NONE) {
			buffer = new StringBuffer();
			FocObject condObject = getObject(filter);

			FObjectField objField = (FObjectField) getFieldPath().getFieldFromDesc(filter.getThisFilterDesc().getSubjectFocDesc());

			// Here we add the "T." only if it is not added already. Because in the
			// Join Request case, it is added by the joins
			String fullFieldName = "";
			if (!Utils.isStringEmpty(objField.getForcedDBName())) {
				fullFieldName = objField.getForcedDBName();
				if (!fullFieldName.contains(".")) {
					fullFieldName = fieldPrefix + fullFieldName;
				}
			} else {
				fullFieldName = objField.getKeyPrefix();
				if (!fullFieldName.contains(".")) {
					fullFieldName = fieldPrefix + objField.getKeyPrefix();
				}
				fullFieldName = fullFieldName + objField.getFocDesc().getRefFieldName();
			}

			fullFieldName = DBManager.provider_ConvertFieldName(getProvider(), fullFieldName);

			switch (operation) {
			case OPERATION_EQUALS: {
				int refValue = (condObject == null) ? 0 : condObject.getReference().getInteger();
				buffer.append(fullFieldName + "=" + refValue);
			}
				break;
			case OPERATION_DIFFERENT_THEN: {
				int refValue = (condObject == null) ? 0 : condObject.getReference().getInteger();
				buffer.append(fullFieldName + "!=" + refValue);
			}
				break;
			case OPERATION_EMPTY:
				buffer.append(fullFieldName + " = 0 OR " + fullFieldName + " IS NULL ");
				break;
			case OPERATION_NOT_EMPTY:
				buffer.append(fullFieldName + " <> 0 AND " + fullFieldName + " IS NOT NULL ");
				break;
			case OPERATION_IN:
				String refList = getInList(filter);
				if (refList != null && !refList.isEmpty()) {
					buffer.append(fullFieldName + " in (" + refList + ")");
				}
				break;
			}
		}
		return buffer;
	}

	public int fillDesc(FocDesc focDesc, int firstID) {
		setFirstFieldID(firstID);

		if (focDesc != null) {
			FPropertyListener colorListener = new ColorPropertyListener(this, firstID + FLD_CONDITION_OPERATION);
			FMultipleChoiceField multipleChoice = new FMultipleChoiceField(getFieldPrefix() + "_OP", "Operation", firstID + FLD_CONDITION_OPERATION, false, 1);
			if (ConfigInfo.isArabic()) {
				multipleChoice.addChoice(OPERATION_NONE, "لا شرط");
				multipleChoice.addChoice(OPERATION_EQUALS, "=");
				multipleChoice.addChoice(OPERATION_DIFFERENT_THEN, "لايساوي");
				multipleChoice.addChoice(OPERATION_EMPTY, "فارغ");
				multipleChoice.addChoice(OPERATION_NOT_EMPTY, "غير فارغ");
			} else {
				multipleChoice.addChoice(OPERATION_NONE, "None");
				multipleChoice.addChoice(OPERATION_EQUALS, "=");
				multipleChoice.addChoice(OPERATION_DIFFERENT_THEN, "<>");
				multipleChoice.addChoice(OPERATION_EMPTY, "Empty");
				multipleChoice.addChoice(OPERATION_NOT_EMPTY, "Not Empty");
			}
			multipleChoice.setSortItems(false);
			focDesc.addField(multipleChoice);
			multipleChoice.addListener(colorListener);

			FObjectField field = (FObjectField) getFilterDesc().getSubjectFocDesc().getFieldByPath(getFieldPath());
			FObjectField objField = new FObjectField(getFieldPrefix() + "_OBJREF", "Object field", firstID + FLD_CONDITION_OBJECT,
					field.getFocDesc() /* ,field.getKeyPrefix() */);
			objField.copyInteralProperties(field);
			focDesc.addField(objField);
			objField.addListener(colorListener);

			if (isWithInList()) {
				FBlobStringField cFld = new FBlobStringField(getFieldPrefix() + "_IN_REF_LIST", "In", firstID + FLD_CONDITION_OBJECT_LIST_STRING, false, 1, 50);
				cFld.setIncludeInDBRequests(true);
				focDesc.addField(cFld);
			}
		}

		return firstID + FLD_CONDITION_OBJECT_LIST_STRING + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel,
	 * int, int)
	 */
	public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
		GuiSpace space = new GuiSpace();

		FProperty operationProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
		FObject objectProp = (FObject) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OBJECT);

		Component comp = operationProp.getGuiComponent();
		panel.add(getFieldLabel(), comp, x, y);

		/*
		 * FGObjectComboBox comboBox =
		 * (FGObjectComboBox)objectProp.getGuiComponent_ComboBox();
		 * comboBox.refreshList(objectProp);
		 * objectProp.getPropertySourceList().reloadFromDB(); panel.add(comboBox,
		 * x+2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		 */
		comp = objectProp.getGuiComponent();
		panel.add(comp, x + 2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		space.setLocation(2, 2);
		return space;
	}

	public boolean isValueLocked(FocListFilter filter) {
		boolean locked = false;
		FProperty operationProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
		if (operationProp != null) {
			locked = operationProp.isValueLocked();
		}
		return locked;
	}

	public void resetToDefaultValue(FocListFilter filter) {
		setToValue(filter, OPERATION_NONE, null);
	}

	public String getCaptionProperty() {
		return captionProperty;
	}

	public void setCaptionProperty(String captionProperty) {
		this.captionProperty = captionProperty;
	}

	@Override
	public String buildDescriptionText(FocListFilter filter) {
		String description = null;
		String empty = " Is Empty";
		String notEmpty = " Not Empty";
		if (ConfigInfo.isArabic()) {
			empty = " فارغ";
			notEmpty = " غير فارغ";
		}		
		int operation = getOperation(filter);
		if (operation != OPERATION_NONE) {
			String fieldName = getFieldLabel();
			FocObject condObject = getObject(filter);
			switch (operation) {
			case OPERATION_EMPTY:
				description = fieldName + empty;
				break;
			case OPERATION_EQUALS:
			case OPERATION_DIFFERENT_THEN:
				String propertyName = getCaptionProperty();
				if (condObject != null) {
					FProperty prop = propertyName != null ? condObject.getFocPropertyByName(propertyName) : null;
					if (prop != null) {
						String val = prop.getString();
						if(operation == OPERATION_EQUALS) description = fieldName + " = " + val;
						else description = fieldName + " <> " + val;
					}
				}
				break;
			case OPERATION_NOT_EMPTY:
				description = fieldName + notEmpty;
				break;
			}
		}

		return description;
	}
	
	@Override
	public void parseString(FocListFilter filter, String str) {
		super.parseString(filter, str);
		
		String[] parts = str.split("::");
		if(parts.length > 0) {
			String operation = parts[0];
      if(ConfigInfo.isArabic()){//The String always contains English if the application is arabic we need to convert because Multiplechoice contains arabic
      	if(operation.equals("None")) operation = "لا شرط";
      	else if(operation.equals("=")) operation = "=";
      	else if(operation.equals("<>")) operation = "لايساوي";
      	else if(operation.equals("Empty")) operation = "فارغ";
      	else if(operation.equals("Not Empty")) operation = "غير فارغ";
      }
			setOperation(filter, operation);
			
			if(parts.length > 1) {
				String text = parts[1];
				long objectRef = Utils.parseLong(text, 0);
				if(objectRef != 0) setObject(filter, objectRef);
			}
		}
	}
}
