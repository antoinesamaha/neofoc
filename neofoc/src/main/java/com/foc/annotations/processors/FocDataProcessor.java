package com.foc.annotations.processors;

import com.foc.annotations.model.FocData;
import com.foc.annotations.processors.attributes.FocDataAttributeProcessorFactory;
import com.google.auto.service.AutoService;

// Annotation Processing API
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Filer;

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
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class FocDataProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    private Map<String, TypeElement> allProcessedTypes = null;

    public Map<String, TypeElement> getAllProcessedTypes() {
        return allProcessedTypes;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        System.out.println("FocDataProcessor initialized ##################################################");
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
            out.println("    public " + generatedClassName + "() {");
            out.println("        this(new FocConstructor(getFocDesc(), null));");
            out.println("    }");
            out.println();

            // Generate fields, getters, and setters for each field in the entity class
            for (Element enclosedElement : entityClass.getEnclosedElements()) {
                if (enclosedElement.getKind() == ElementKind.FIELD) {
                    VariableElement field = (VariableElement) enclosedElement;
                    String fieldName = field.getSimpleName().toString();
                    TypeMirror fieldType = field.asType();
                    String typeName = fieldType.toString();

                    IFocDataAttributeProcessor attributeProcessor = FocDataAttributeProcessorFactory.getProcessor(typeName);
                    if (attributeProcessor == null) {
                        attributeProcessor = FocDataAttributeProcessorFactory.getProcessor("Entity");
                    }
                    if (attributeProcessor != null) {
                        String getterSetterSection = attributeProcessor.getGetterSetter(this, field);
                        out.println(getterSetterSection);
                    }
                }
            }

            out.println();
            out.println("    public static com.foc.desc.FocDesc getFocDesc() {");
            out.println("        return com.foc.Globals.getApp().getFocDescByName(\""+getTableName(entityClass)+"\");");
            out.println("    }");
            out.println();
            out.println("}");
        }

    }

    private String getTableName(TypeElement entityClass) {
        // Check for @Table annotation first (it has priority for table name)
        jakarta.persistence.Table tableAnnotation = entityClass.getAnnotation(jakarta.persistence.Table.class);
        if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
            return tableAnnotation.name();
        }

        // Check for @Entity annotation
        jakarta.persistence.Entity entityAnnotation = entityClass.getAnnotation(jakarta.persistence.Entity.class);
        if (entityAnnotation != null && !entityAnnotation.name().isEmpty()) {
            return entityAnnotation.name();
        }

        // Default to class name if no explicit name is provided
        return entityClass.getSimpleName().toString();
    }
}
