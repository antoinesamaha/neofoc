package com.foc.annotations.processors.attributes;

import com.foc.annotations.processors.IFocDataAttributeProcessor;
import java.util.HashMap;
import java.util.Map;

public class FocDataAttributeProcessorFactory {
    
    private static final Map<String, IFocDataAttributeProcessor> processorMap = new HashMap<>();
    
    static {
        // Initialize the map with all available processors
        processorMap.put("int", new FocDataAttributeIntegerProcessor());
        processorMap.put("java.lang.Integer", new FocDataAttributeIntegerProcessor());
        processorMap.put("long", new FocDataAttributeLongProcessor());
        processorMap.put("java.lang.Long", new FocDataAttributeLongProcessor());
        processorMap.put("double", new FocDataAttributeDoubleProcessor());
        processorMap.put("java.lang.Double", new FocDataAttributeDoubleProcessor());
        processorMap.put("float", new FocDataAttributeFloatProcessor());
        processorMap.put("java.lang.Float", new FocDataAttributeFloatProcessor());
        processorMap.put("boolean", new FocDataAttributeBooleanProcessor());
        processorMap.put("java.lang.Boolean", new FocDataAttributeBooleanProcessor());
        processorMap.put("LocalDateTime", new FocDataAttributeLocalDateTimeProcessor());
        processorMap.put("java.time.LocalDateTime", new FocDataAttributeLocalDateTimeProcessor());
        processorMap.put("LocalDate", new FocDataAttributeLocalDateProcessor());
        processorMap.put("java.time.LocalDate", new FocDataAttributeLocalDateProcessor());
        processorMap.put("String", new FocDataAttributeStringProcessor());
        processorMap.put("java.lang.String", new FocDataAttributeStringProcessor());
        // Entity processor for complex types
        processorMap.put("Entity", new FocDataAttributeEntityProcessor());
    }
    
    /**
     * Get the appropriate processor for the given type
     * @param typeName the name of the type (e.g., "int", "String", "LocalDateTime")
     * @return the corresponding processor, or the EntityProcessor for unknown types
     */
    public static IFocDataAttributeProcessor getProcessor(String typeName) {
        IFocDataAttributeProcessor processor = processorMap.get(typeName);
        if (processor != null) {
            return processor;
        }
        
        // For unknown types, use the Entity processor as default
        return processorMap.get("Entity");
    }
    
    /**
     * Check if a specific processor exists for the given type
     * @param typeName the name of the type
     * @return true if a specific processor exists, false otherwise
     */
    public static boolean hasProcessor(String typeName) {
        return processorMap.containsKey(typeName);
    }
    
    /**
     * Get all supported type names
     * @return a set of all supported type names
     */
    public static java.util.Set<String> getSupportedTypes() {
        return processorMap.keySet();
    }
    
    /**
     * Register a new processor for a specific type
     * @param typeName the type name to register
     * @param processor the processor instance
     */
    public static void registerProcessor(String typeName, IFocDataAttributeProcessor processor) {
        processorMap.put(typeName, processor);
    }
}
