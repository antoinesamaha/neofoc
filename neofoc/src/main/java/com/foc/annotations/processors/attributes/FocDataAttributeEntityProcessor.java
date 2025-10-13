package com.foc.annotations.processors.attributes;

import com.foc.annotations.processors.FocDataAttributeAbstractProcessor;
import com.foc.annotations.processors.FocDataProcessor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class FocDataAttributeEntityProcessor extends FocDataAttributeAbstractProcessor {

    @Override
    public String getGetterSetter(FocDataProcessor focDataProcessor, VariableElement field) {
        String capitalizedFieldName = getGetterSetterMethodName(field);
        String fieldName = getDBFieldName(field);

        TypeMirror fieldType = field.asType();
        String typeName = fieldType.toString();

        System.out.println(" ### Entity Processor typeName = " + typeName);

        try {
            // Get the simple name of the type (without package)
            String simpleTypeName = typeName;
            if (typeName.contains(".")) {
                simpleTypeName = typeName.substring(typeName.lastIndexOf('.') + 1);
            }

            // Look for a class with the prefix "Foc" + typeName
            String focClassName = "Foc" + simpleTypeName;

            System.out.println("===== DEBUG FocDataProcessor FocDataAttributeEntityProcessor =====");
            System.out.println("Looking for class: " + focClassName);
            System.out.println("Original type: " + typeName);

            // First, check in collected types
            TypeElement focTypeElement = focDataProcessor.getAllProcessedTypes().get(focClassName);
            System.out.println("Found in allProcessedTypes: " + (focTypeElement != null));

            System.out.println("FocDataProcessor complete check for typeName = " + typeName +
                    ", found match: " + (focTypeElement != null));

            if (focTypeElement != null) {
                String focTypeName = focTypeElement.getQualifiedName().toString();
                System.out.println("Using Foc type: " + focTypeName);

                StringBuilder out = new StringBuilder();
                out.append("    public ").append(focTypeName).append(" get").append(capitalizedFieldName).append("() {\n");
                out.append("        return (").append(focTypeName).append(") getPropertyObject(\"").append(fieldName).append("\");\n");
                out.append("    }\n");
                out.append("\n");

                out.append("    public void set").append(capitalizedFieldName).append("(").append(focTypeName).append(" value) {\n");
                out.append("        setPropertyObject(\"").append(fieldName).append("\", value);\n");
                out.append("    }");
                out.append("\n");

                return out.toString();

            } else {
                System.out.println("WARNING: Could not find Foc class for " + typeName);
            }
        } catch (Exception e) {
            System.err.println("Failed to check for JPA entity: " + e.getMessage());
            e.printStackTrace();
        }

        return "";
    }
}
