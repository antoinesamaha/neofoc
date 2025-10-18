package com.foc.annotations.processors.attributes;

import com.foc.annotations.processors.FocDataAttributeAbstractProcessor;
import com.foc.annotations.processors.FocDataProcessor;

import javax.lang.model.element.VariableElement;

public class FocDataAttributeIntegerProcessor extends FocDataAttributeAbstractProcessor {

    @Override
    public String getGetterSetter(FocDataProcessor focDataProcessor, VariableElement field) {
        StringBuilder out = new StringBuilder();
        String capitalizedFieldName = getGetterSetterMethodName(field);
        String fieldName = getDBFieldName(field);

        out.append("    public int get").append(capitalizedFieldName).append("() {\n");
        out.append("        return getPropertyInteger(\"").append(fieldName).append("\");\n");
        out.append("    }\n");
        out.append("\n");

        out.append("    public void set").append(capitalizedFieldName).append("(int value) {\n");
        out.append("        setPropertyInteger(\"").append(fieldName).append("\", value);\n");
        out.append("    }");
        out.append("\n");

        return out.toString();
    }
}
