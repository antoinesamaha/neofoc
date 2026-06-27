package com.neofoc.springboot.service;

import com.foc.FocDescInstanceFocDescDeclaration;
import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.desc.FocDesc;
import com.foc.desc.FocDescMap;
import com.foc.desc.FocModule;
import com.foc.desc.FocObjectGeneral;
import com.foc.desc.field.*;
import com.foc.util.ASCII;
import com.foc.db.DBIndex;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.Type;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ScanSpringBootEntitiesAndConvert2FocDesc {

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private final EntityManager entityManager;

    private HashMap<String, One2ManyRelation> one2ManyRelationMap = new HashMap<>();

    public ScanSpringBootEntitiesAndConvert2FocDesc(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void scanEntitiesAndCreateFocDesc() {
        Set<EntityType<?>> entitySet = entityManager.getMetamodel().getEntities();

        Iterator<EntityType<?>> entityIter = entitySet.iterator();
        while(entityIter != null && entityIter.hasNext()) {
            EntityType type = entityIter.next();
            Class typeClass = type.getJavaType();

            String tableName = getTableName(type);
            boolean cacheable = isEntityCacheable(type);

            // Searching for a Module where this Entity would be under
            // We will see if the Entity package is under the Module package
            FocModule focModule = findFocModule(type);

            Globals.logString(" ENTITY TYPE " + type.getName());

            // Try to find a class named "Foc" + type.getName() in any package
            Class focClass = FocObjectGeneral.class;
            String focSimpleName = "Foc" + type.getName();
            try {
                Class foundFocClass = findClassBySimpleName(focSimpleName);
                if (foundFocClass != null) {
                    focClass = foundFocClass;
                    Globals.logString("Found Foc class: " + focClass.getName());
                }
            } catch (Exception e) {
                Globals.logException(e);
            }

            FocDesc focDesc = new FocDesc(focClass, FocDesc.DB_RESIDENT, tableName, false);
            focDesc.setModule(focModule);
            focDesc.setListInCache(cacheable);
            int fieldID = 1;

            String idPrimaryKeyAttrinuteName = null;
            if (type.hasSingleIdAttribute()) {
                Attribute<?, ?> idAttr = type.getId(type.getIdType().getJavaType());
                idPrimaryKeyAttrinuteName = idAttr.getName();
            }

            Set<Attribute> attSet = type.getAttributes();
            Iterator<Attribute> attIter = attSet.iterator();
            while(attIter != null && attIter.hasNext()) {
                Attribute att = attIter.next();
                Class attributeClass = att.getJavaType();

                String attributeName = att.getName();
                String fieldNameTemp = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);
                String fieldName = ASCII.convertJavaClassNameTo_SmallLettersWith_(fieldNameTemp);
                String title = ASCII.convertJavaClassNameToATitleWithSpacesAndCapitals(fieldNameTemp);

                Globals.logString("    ATTRIBUTE TYPE: " + att.getName()+" class: "+attributeClass.getName());

                // Read @Column annotation if present
                boolean isNullable = true; // default for JPA
                int columnLength = 1000; // default for JPA String columns
                try {
                    Field entityField = typeClass.getDeclaredField(attributeName);
                    if (entityField.isAnnotationPresent(Column.class)) {
                        Column columnAnnotation = entityField.getAnnotation(Column.class);
                        isNullable = columnAnnotation.nullable();
                        columnLength = columnAnnotation.length();
                    }
                } catch (NoSuchFieldException nsfe) {
                    // Field not found, ignore
                }

                if (att.getName().toLowerCase().equals(idPrimaryKeyAttrinuteName)) {
                    FField fld = focDesc.addReferenceField();
                    fld.setName(idPrimaryKeyAttrinuteName);
                } else if (attributeClass == String.class) {
                    // Use columnLength and isNullable from @Column
                    FStringField fld = new FStringField(fieldName, title, fieldID++, !isNullable, columnLength > 0 ? columnLength : 1000);
                    focDesc.addField(fld);
                } else if (attributeClass == Integer.class) {
                    FIntField fld = new FIntField(fieldName, title, fieldID++, !isNullable, 10);
                    focDesc.addField(fld);
                } else if (attributeClass == Double.class) {
                    FNumField fld = new FNumField(fieldName, title, fieldID++, !isNullable, 20, 5);
                    focDesc.addField(fld);
                } else if (attributeClass == LocalDateTime.class) {
                    FDateTimeField fld = new FDateTimeField(fieldName, title, fieldID++, !isNullable);
                    focDesc.addField(fld);
                } else if (attributeClass == LocalDate.class) {
                    FDateField fld = new FDateField(fieldName, title, fieldID++, !isNullable);
                    focDesc.addField(fld);
                } else if (attributeClass == Boolean.class) {
                    FBoolField fld = new FBoolField(fieldName, title, fieldID++, !isNullable);
                    focDesc.addField(fld);
                } else if (attributeClass == Map.class) {
                    FJsonField fld = new FJsonField(fieldName, title, fieldID++, !isNullable);
                    focDesc.addField(fld);
                } else if (attributeClass == Set.class) {
                    if (att instanceof PluralAttribute) {
                        try {
                            Field field = typeClass.getDeclaredField(attributeName);
                            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                            if (oneToMany != null) {
                                PluralAttribute<?, ?, ?> pluralAttribute = (PluralAttribute<?, ?, ?>) att;
                                Type<?> elementType = pluralAttribute.getElementType();
                                if (elementType instanceof EntityType) {
                                    EntityType elementEntityType = (EntityType) elementType;
                                    String elementTableName = getTableName(elementEntityType);

                                    String mappedBy = oneToMany.mappedBy();
                                    CascadeType[] cascadeTypes = oneToMany.cascade();

                                    One2ManyRelation relation = new One2ManyRelation();
                                    relation.setFocDesc(focDesc);
                                    relation.setTableName(elementTableName);
                                    relation.setCascadeTypes(cascadeTypes);
                                    relation.setMappedBy(mappedBy);

                                    String key = ASCII.convertJavaClassNameTo_SmallLettersWith_(mappedBy);
                                    one2ManyRelationMap.put(elementTableName+"|"+key, relation);
                                }
                            }
                        } catch (Exception e) {
                            Globals.logException(e);
                        }
                    }
                } else {
                    String simpleAttributeClassName = attributeClass.getSimpleName();
                    String tableNameForAttributeClass = ASCII.convertJavaClassNameTo_SmallLettersWith_(simpleAttributeClassName);

                    FObjectField fld = new FObjectField(fieldName, title, fieldID++, false, null, fieldName+"_", null, 0, null, false);
                    if (isNullable) {
                        fld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
                    } else {
                        fld.setNullValueMode(FObjectField.NULL_VALUE_NOT_ALLOWED);
                    }
                    fld.setFocDescStorageName(tableNameForAttributeClass, false, false);
                    focDesc.addField(fld);
                }
            }

            processTableConstraints(typeClass, focDesc);

            FocDescInstanceFocDescDeclaration declaration = new FocDescInstanceFocDescDeclaration(focDesc);
            declaration.setFocModule(focModule);
            Globals.getApp().declaredObjectList_DeclareDescription(declaration);
            FocDescMap.getInstance().put(focDesc.getName(), focDesc);
        }

        Iterator<IFocDescDeclaration> iter3 = Globals.getApp().getFocDescDeclarationIterator();
        while(iter3 != null && iter3.hasNext()){
            IFocDescDeclaration focDescDeclaration = iter3.next();
            if(focDescDeclaration != null){
                FocDesc focDesc2 = focDescDeclaration.getFocDescription();
                if(focDesc2 != null){
                    //Scanning the FObectField
                    for(int i=0; i<focDesc2.getFieldsSize(); i++){
                        FField fld = focDesc2.getFieldAt(i);
                        if(fld instanceof FObjectField){
                            FObjectField objectFld = (FObjectField) fld;
                            One2ManyRelation one2ManyRelation = one2ManyRelationMap.get(focDesc2.getName()+"|"+fld.getName());
                            if (one2ManyRelation != null) {
                                boolean cascade = Arrays.stream(one2ManyRelation.getCascadeTypes()).anyMatch(cascadeType -> cascadeType == CascadeType.ALL);
                                objectFld.setCascade(cascade);
                            }
                            objectFld.getFocDescFromStorageNameIfNeeded(focDesc2);
                        }
                    }
                }
            }
        }
    }

    private String getTableName(EntityType<?> entityType) {
        Class<?> entityClass = entityType.getJavaType();
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            return tableAnnotation.name();
        }
        // Default to the entity name if @Table is not present
        return entityType.getName();
    }

    private boolean isEntityCacheable(EntityType<?> entityType) {
        Class<?> entityClass = entityType.getJavaType();
        return entityClass.isAnnotationPresent(Cacheable.class);
    }

    private FocModule findFocModule(EntityType type) {
        // Searching for a Module where this Entity would be under
        // We will see if the Entity package is under the Module package
        FocModule focModule = null;
        String entityPackageName = type.getJavaType().getPackageName();
        Iterator<FocModule> iter = Globals.getApp().modules_Iterator();
        while (iter != null && iter.hasNext()) {
            FocModule currentFocModule = iter.next();
            if (entityPackageName.contains(currentFocModule.getClass().getPackageName())) {
                focModule = currentFocModule;
                break;
            }
        }
        return focModule;
    }

    private boolean getOneToManyAnnotation(EntityType<?> entityType) {
        Class<?> entityClass = entityType.getJavaType();
        return entityClass.isAnnotationPresent(Cacheable.class);
    }

    /**
     * Searches the classpath for a class with the given simple name (e.g. "FocMyEntity").
     * Returns the Class object if found, or null if not found.
     *
     * This implementation uses the Reflections library for classpath scanning.
     */
    private Class<?> findClassBySimpleName(String simpleName) {
        try {
            // Create a Reflections instance that scans all your application packages
            // You should replace "com.neofoc" with your base package name
            org.reflections.Reflections reflections = new org.reflections.Reflections("com.neofoc");

            // Get classes that extend FocObjectGeneral
            Set<Class<? extends FocObjectGeneral>> focGeneralClasses = reflections.getSubTypesOf(FocObjectGeneral.class);
            for (Class<?> clazz : focGeneralClasses) {
                if (clazz.getSimpleName().equals(simpleName)) {
                    return clazz;
                }
            }

            // Additionally check for classes that extend FocObject (assuming it exists)
            try {
                Class<?> focObjectClass = Class.forName("com.foc.desc.FocObject");
                @SuppressWarnings("unchecked")
                Set<Class<?>> focObjectClasses = reflections.getSubTypesOf(
                        (Class<Object>) focObjectClass);
                for (Class<?> clazz : focObjectClasses) {
                    if (clazz.getSimpleName().equals(simpleName)) {
                        return clazz;
                    }
                }
            } catch (ClassNotFoundException e) {
                // FocObject class doesn't exist, just continue
            }

        } catch (Exception e) {
            Globals.logException(e);
        }
        return null;
    }

    private void processTableConstraints(Class<?> typeClass, FocDesc focDesc) {
        for (Field javaField : typeClass.getDeclaredFields()) {
            Column col = javaField.getAnnotation(Column.class);
            if (col != null && col.unique()) {
                FField fld = focDesc.getFieldByName(javaFieldNameToFocFieldName(javaField.getName()));
                if (fld != null) {
                    DBIndex idx = new DBIndex("UQ_" + focDesc.getName() + "_" + javaField.getName(), focDesc, true);
                    idx.addField(fld.getID());
                    focDesc.indexAdd(idx);
                }
            }
        }

        Table table = typeClass.getAnnotation(Table.class);
        if (table == null) return;

        for (UniqueConstraint uc : table.uniqueConstraints()) {
            DBIndex idx = new DBIndex(uc.name(), focDesc, true);
            for (String col : uc.columnNames()) {
                FField fld = findFieldByColumnName(focDesc, col);
                if (fld != null) {
                    idx.addField(fld.getID());
                } else {
                    Globals.logString("WARNING: No Foc field for column '" + col + "' in " + typeClass.getSimpleName());
                }
            }
            if (idx.getFieldCount() > 0) focDesc.indexAdd(idx);
        }

        for (Index index : table.indexes()) {
            DBIndex idx = new DBIndex(index.name(), focDesc, index.unique());
            for (String col : index.columnList().split(",")) {
                FField fld = findFieldByColumnName(focDesc, col);
                if (fld != null) {
                    idx.addField(fld.getID());
                } else {
                    Globals.logString("WARNING: No Foc field for column '" + col.trim() + "' in " + typeClass.getSimpleName());
                }
            }
            if (idx.getFieldCount() > 0) focDesc.indexAdd(idx);
        }
    }

    private FField findFieldByColumnName(FocDesc focDesc, String columnName) {
        columnName = columnName.trim();
        FField fld = focDesc.getFieldByName(columnName);
        if (fld == null && columnName.endsWith("_id")) {
            fld = focDesc.getFieldByName(columnName.substring(0, columnName.length() - 3));
        }
        return fld;
    }

    private String javaFieldNameToFocFieldName(String javaFieldName) {
        String capitalized = Character.toUpperCase(javaFieldName.charAt(0)) + javaFieldName.substring(1);
        return ASCII.convertJavaClassNameTo_SmallLettersWith_(capitalized);
    }

    @Data
    private class One2ManyRelation {
        private FocDesc focDesc;
        private String tableName;
        private String mappedBy;
        private CascadeType[] cascadeTypes;
    }
}
