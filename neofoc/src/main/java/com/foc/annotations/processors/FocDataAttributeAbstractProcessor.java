package com.foc.annotations.processors;

import javax.lang.model.element.VariableElement;

public abstract class FocDataAttributeAbstractProcessor implements IFocDataAttributeProcessor {

    protected String getFieldName(VariableElement field) {
        return field.getSimpleName().toString();
    }

    protected String getDBFieldName(VariableElement field) {
        return toCamelCase(getFieldName(field));
    }

    protected String getGetterSetterMethodName(VariableElement field) {
        String fieldName = getFieldName(field);
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    protected String toCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) result.append('_');
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

}
