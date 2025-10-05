package com.foc.annotations.processors;

import com.foc.annotations.model.FocData;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Annotation processor for @FocData annotation.
 * This processor generates getters and setters for classes with @FocData annotation
 * based on the corresponding entity class (same name without 'Foc' prefix).
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.foc.annotations.model.FocData")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FocDataProcessor extends AbstractProcessor {

    private enum AccessorType {
        GETTER, SETTER, TYPE
    }

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    private Map<String, TypeElement> allProcessedTypes = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        System.out.println("FocDataProcessor initialized 123 ##################################################");
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("FocDataProcessor process ##################################################");

        // Scan all Classes only once
        if (allProcessedTypes == null) {
            allProcessedTypes = new HashMap<>();
            // Collect all types first
            for (Element element : roundEnv.getRootElements()) {
                if (element.getKind() == ElementKind.CLASS) {
                    allProcessedTypes.put(String.valueOf(element.getSimpleName()), (TypeElement)element);
                }
            }
        }

        // Process each element annotated with @FocData
        for (Element element : roundEnv.getElementsAnnotatedWith(FocData.class)) {
            try {
                if (element.getKind() != ElementKind.CLASS) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            "@FocData can only be applied to classes", element);
                    continue;
                }

                TypeElement entityElement = (TypeElement) element;
                // Generate a class with the suffix _FocObject in the same package as the entity
                generateFocObject(entityElement);
            } catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Error processing @FocData: " + e.getMessage(), element);
            }
        }
        return true;
    }

    /**
     * Generates a class with the suffix _FocObject in the same package as the entity,
     * containing getters and setters for all fields.
     */
    private void generateFocObject(TypeElement entityClass) throws IOException {
        System.out.println("FocDataProcessor generateFocObject ##################################################");
        String className = entityClass.getSimpleName().toString();
        String packageName = elementUtils.getPackageOf(entityClass).getQualifiedName().toString();
        String generatedClassName = className + "_FocObject";

        JavaFileObject builderFile = filer.createSourceFile(packageName + "." + generatedClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            // Write package and imports
            out.println("package " + packageName + ";");
            out.println();
            out.println("// Generated code - do not modify!");
            out.println("// Generated for " + className);
            out.println();
            out.println("import com.foc.desc.FocConstructor;");
            out.println("import com.foc.desc.FocObjectGeneral;");
            out.println();

            // Begin class
            out.println("public class " + generatedClassName + " extends FocObjectGeneral {");

            out.println();
            out.println("    public " + generatedClassName + "(FocConstructor constr) {");
            out.println("        super(constr);");
            out.println("    }");
            out.println();

            // Generate fields, getters, and setters for each field in the entity class
            for (Element enclosedElement : entityClass.getEnclosedElements()) {
                if (enclosedElement.getKind() == ElementKind.FIELD) {
                    VariableElement field = (VariableElement) enclosedElement;
                    String fieldName = field.getSimpleName().toString();
                    String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    String camelCaseFieldName = toCamelCaseWithUnderscore(fieldName);
                    TypeMirror fieldType = field.asType();
                    String typeName = fieldType.toString();

                    String typeForDeclaration = getterSetterMethods(typeName, fieldName, camelCaseFieldName, AccessorType.TYPE);

                    // Getter
                    String getterContent = getterSetterMethods(typeName, fieldName, camelCaseFieldName, AccessorType.GETTER);
                    if (getterContent != null && !getterContent.isEmpty()) {
                        out.println("    public " + typeForDeclaration + " get" + capitalizedFieldName + "() {");
                        out.println("        " + getterContent);
                        out.println("    }");
                        out.println();
                    }

                    // Setter
                    String setterContent = getterSetterMethods(typeName, fieldName, camelCaseFieldName, AccessorType.SETTER);
                    if (setterContent != null && !setterContent.isEmpty()) {
                        out.println("    public void set" + capitalizedFieldName + "(" + typeForDeclaration + " value) {");
                        out.println("        " + setterContent);
                        out.println("    }");
                        out.println();
                    }
                }
            }
            out.println("}");
        }
    }

    public String getterSetterMethods(String typeName, String fieldName, String fieldNameCamelCase, AccessorType accessorType) {
        System.out.println("FocDataProcessor getterSetterMethods ##################################################");
        if (typeName.equals("int") || typeName.equals("java.lang.Integer")) {
            if (accessorType == AccessorType.SETTER) {
                return "setPropertyInteger(\"" + fieldNameCamelCase + "\", value);";
            } else if (accessorType == AccessorType.GETTER) {
                return "return getPropertyInteger(\"" + fieldNameCamelCase + "\");";
            } else {
                return "int";
            }
        } else if (typeName.equals("long") || typeName.equals("java.lang.Long")) {
            if (accessorType == AccessorType.SETTER) {
                return "setPropertyLong(\"" + fieldNameCamelCase + "\", value);";
            } else if (accessorType == AccessorType.GETTER) {
                return "return getPropertyLong(\"" + fieldNameCamelCase + "\");";
            } else {
                return "long";
            }
        } else if (typeName.equals("double") || typeName.equals("java.lang.Double")) {
            if (accessorType == AccessorType.SETTER) {
                return "setPropertyDouble(\"" + fieldNameCamelCase + "\", value);";
            } else if (accessorType == AccessorType.GETTER) {
                return "return getPropertyDouble(\"" + fieldNameCamelCase + "\");";
            } else {
                return "double";
            }
        } else if (typeName.equals("float") || typeName.equals("java.lang.Float")) {
            if (accessorType == AccessorType.SETTER) {
                return "setPropertyFloat(\"" + fieldNameCamelCase + "\", value);";
            } else if (accessorType == AccessorType.GETTER) {
                return "return getPropertyFloat(\"" + fieldNameCamelCase + "\");";
            } else {
                return "float";
            }
        } else if (typeName.equals("boolean") || typeName.equals("java.lang.Boolean")) {
            if (accessorType == AccessorType.SETTER) {
                return "setPropertyBoolean(\"" + fieldNameCamelCase + "\", value);";
            } else if (accessorType == AccessorType.GETTER) {
                return "return getPropertyBoolean(\"" + fieldNameCamelCase + "\");";
            } else {
                return "boolean";
            }
        } else if (typeName.equals("java.lang.String")) {
            if (accessorType == AccessorType.SETTER) {
                return "setPropertyString(\"" + fieldNameCamelCase + "\", value);";
            } else if (accessorType == AccessorType.GETTER) {
                return "return getPropertyString(\"" + fieldNameCamelCase + "\");";
            } else {
                return "String";
            }
        } else if (typeName.equals("java.time.LocalDateTime")) {
            if (accessorType == AccessorType.SETTER) {

                String str = "java.sql.Date date = value != null ? java.sql.Date.valueOf(value.toLocalDate()) : null;\n";
                str += "        setPropertyDate(\"" + fieldNameCamelCase + "\", date);";

                return str;

            } else if (accessorType == AccessorType.GETTER) {
                String str = "java.sql.Date date = getPropertyDate(\"" + fieldNameCamelCase + "\");\n";
                str += "        return date != null ? date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null;";
                return str;

            } else {
                return typeName;

            }
        } else {
            System.out.println("FocDataProcessor In Else for typeName = " + typeName);

            // Check if the typeName corresponds to a JPA entity
            try {
                // Get the simple name of the type (without package)
                String simpleTypeName = typeName;
                if (typeName.contains(".")) {
                    simpleTypeName = typeName.substring(typeName.lastIndexOf('.') + 1);
                }

                // Look for a class with the prefix "Foc" + typeName
                String focClassName = "Foc" + simpleTypeName;
                TypeElement focTypeElement = null;

                System.out.println("===== DEBUG FocDataProcessor =====");
                System.out.println("Looking for class: " + focClassName);
                System.out.println("Original type: " + typeName);

                // First, check in collected types
                focTypeElement = allProcessedTypes.get(focClassName);
                System.out.println("Found in allProcessedTypes: " + (focTypeElement != null));

                // If not found in collected types, search through all module elements
                if (focTypeElement == null) {
                    System.out.println("Searching for " + focClassName + " in all available classes");

                    // Try to find it by iterating through all known types
                    for (Map.Entry<String, TypeElement> entry : allProcessedTypes.entrySet()) {
                        if (entry.getKey().equals(focClassName)) {
                            focTypeElement = entry.getValue();
                            System.out.println("Found " + focClassName + " in processed types: " + focTypeElement.getQualifiedName());
                            break;
                        }
                    }
                }

                System.out.println("FocDataProcessor complete check for typeName = " + typeName +
                                  ", found match: " + (focTypeElement != null));

                if (focTypeElement != null) {
                    String focTypeName = focTypeElement.getQualifiedName().toString();
                    System.out.println("Using Foc type: " + focTypeName);

                    // We found a corresponding Foc class
                    if (accessorType == AccessorType.SETTER) {
                        return "setPropertyObject(\"" + fieldNameCamelCase + "\", value);";
                    } else if (accessorType == AccessorType.GETTER) {
                        return "return (" + focTypeName + ") getPropertyObject(\"" + fieldNameCamelCase + "\");";
                    } else {
                        return focTypeName;
                    }
                } else {
                    System.out.println("WARNING: Could not find Foc class for " + typeName);
                }
            } catch (Exception e) {
                System.err.println("Failed to check for JPA entity: " + e.getMessage());
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.NOTE,
                        "Failed to check for JPA entity: " + e.getMessage());
            }
        }

        return "";
    }

    private String toCamelCaseWithUnderscore(String input) {
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
