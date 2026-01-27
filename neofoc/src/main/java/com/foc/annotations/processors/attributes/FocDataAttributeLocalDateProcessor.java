package com.foc.annotations.processors.attributes;

import com.foc.annotations.processors.FocDataAttributeAbstractProcessor;
import com.foc.annotations.processors.FocDataProcessor;
import com.foc.annotations.processors.IFocDataAttributeProcessor;

import javax.lang.model.element.VariableElement;

public class FocDataAttributeLocalDateProcessor extends FocDataAttributeAbstractProcessor {

    @Override
    public String getGetterSetter(FocDataProcessor focDataProcessor, VariableElement field) {
        StringBuilder out = new StringBuilder();
        String capitalizedFieldName = getGetterSetterMethodName(field);
        String fieldName = getDBFieldName(field);

        out.append("    public java.time.LocalDate get").append(capitalizedFieldName).append("() {\n");
        out.append("        java.sql.Date date = getPropertyDate(\"").append(fieldName).append("\");\n");
        out.append("        return date != null ? date.toLocalDate() : null;\n");
        out.append("    }\n");
        out.append("\n");
        out.append("    public void set").append(capitalizedFieldName).append("(java.time.LocalDate value) {\n");
        out.append("        java.sql.Date date = value != null ? java.sql.Date.valueOf(value) : null;\n");
        out.append("        setPropertyDate(\"").append(fieldName).append("\", date);\n");
        out.append("    }\n");
        out.append("\n");

        return out.toString();
    }

}
