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
import jakarta.persistence.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.Type;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
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

            FocDesc focDesc = new FocDesc(FocObjectGeneral.class, FocDesc.DB_RESIDENT, tableName, false);
            focDesc.setModule(focModule);
            focDesc.setListInCache(cacheable);
            int fieldID = 1;

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

                if (att.getName().toLowerCase().equals("id")) {
                    FField fld = focDesc.addReferenceField();
                    fld.setName("id");
                } else if (attributeClass == String.class) {
                    FStringField fld = new FStringField(fieldName, title, fieldID++, false, 1000);
                    focDesc.addField(fld);
                } else if (attributeClass == Integer.class) {
                    FIntField fld = new FIntField(fieldName, title, fieldID++, false, 10);
                    focDesc.addField(fld);
                } else if (attributeClass == Boolean.class) {
                    FBoolField fld = new FBoolField(fieldName, title, fieldID++, false);
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

                                    one2ManyRelationMap.put(elementTableName+"|"+mappedBy, relation);
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
                    fld.setFocDescStorageName(tableNameForAttributeClass, false, false);
                    focDesc.addField(fld);
                }
            }

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

    @Data
    private class One2ManyRelation {
        private FocDesc focDesc;
        private String tableName;
        private String mappedBy;
        private CascadeType[] cascadeTypes;
    }
}
