package com.foc.annotations.processors.attributes;

import com.foc.annotations.processors.FocDataAttributeAbstractProcessor;
import com.foc.annotations.processors.FocDataProcessor;
import com.foc.annotations.processors.IFocDataAttributeProcessor;

import javax.lang.model.element.VariableElement;

public class FocDataAttributeLocalDateTimeProcessor extends FocDataAttributeAbstractProcessor {

    @Override
    public String getGetterSetter(FocDataProcessor focDataProcessor, VariableElement field) {
        StringBuilder out = new StringBuilder();
        String capitalizedFieldName = getGetterSetterMethodName(field);
        String fieldName = getDBFieldName(field);

        out.append("    public java.time.LocalDateTime get").append(capitalizedFieldName).append("() {\n");
        out.append("        java.sql.Date date = getPropertyDate(\"").append(fieldName).append("\");\n");
        out.append("        if (date != null) {\n");
        out.append("            return new java.sql.Timestamp(date.getTime()).toLocalDateTime();\n");
        out.append("        }\n");
        out.append("        return null;\n");
        out.append("    }\n");
        out.append("\n");
        out.append("    public void set").append(capitalizedFieldName).append("(java.time.LocalDateTime value) {\n");
        out.append("        if (value != null) {\n");
        out.append("            java.sql.Date date = new java.sql.Date(0);\n");
        out.append("            date.setTime(java.sql.Timestamp.valueOf(value).getTime());\n");
        out.append("            setPropertyDate(\"").append(fieldName).append("\", date);\n");
        out.append("        } else {\n");
        out.append("            setPropertyDate(\"").append(fieldName).append("\", null);\n");
        out.append("        }\n");
        out.append("    }\n");
        out.append("\n");

        return out.toString();
    }

}
