package com.foc.annotations.processors.attributes;

import com.foc.annotations.processors.FocDataAttributeAbstractProcessor;
import com.foc.annotations.processors.FocDataProcessor;
import com.foc.annotations.processors.IFocDataAttributeProcessor;

import javax.lang.model.element.VariableElement;

public class FocDataAttributeDoubleProcessor extends FocDataAttributeAbstractProcessor {

    @Override
    public String getGetterSetter(FocDataProcessor focDataProcessor, VariableElement field) {
        StringBuilder out = new StringBuilder();
        String capitalizedFieldName = getGetterSetterMethodName(field);
        String fieldName = getDBFieldName(field);

        out.append("    public double get").append(capitalizedFieldName).append("() {\n");
        out.append("        return getPropertyDouble(\"").append(fieldName).append("\");\n");
        out.append("    }\n");
        out.append("\n");

        out.append("    public void set").append(capitalizedFieldName).append("(double value) {\n");
        out.append("        setPropertyDouble(\"").append(fieldName).append("\", value);\n");
        out.append("    }");
        out.append("\n");

        return out.toString();
    }

}
