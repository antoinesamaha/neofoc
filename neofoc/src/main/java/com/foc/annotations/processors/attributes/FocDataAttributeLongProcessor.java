package com.foc.annotations.processors.attributes;

import com.foc.annotations.processors.FocDataAttributeAbstractProcessor;
import com.foc.annotations.processors.FocDataProcessor;
import com.foc.annotations.processors.IFocDataAttributeProcessor;

import javax.lang.model.element.VariableElement;

public class FocDataAttributeLongProcessor extends FocDataAttributeAbstractProcessor {

    @Override
    public String getGetterSetter(FocDataProcessor focDataProcessor, VariableElement field) {
        StringBuilder out = new StringBuilder();
        String capitalizedFieldName = getGetterSetterMethodName(field);
        String fieldName = getDBFieldName(field);

        out.append("    public long get").append(capitalizedFieldName).append("() {\n");
        out.append("        return getPropertyLong(\"").append(fieldName).append("\");\n");
        out.append("    }\n");
        out.append("\n");

        out.append("    public void set").append(capitalizedFieldName).append("(long value) {\n");
        out.append("        setPropertyLong(\"").append(fieldName).append("\", value);\n");
        out.append("    }");
        out.append("\n");

        return out.toString();
    }

}
