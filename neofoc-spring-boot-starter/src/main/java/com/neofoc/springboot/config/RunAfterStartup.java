package com.neofoc.springboot.config;

import java.util.Iterator;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.foc.FocDescInstanceFocDescDeclaration;
import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.desc.FocDesc;
import com.foc.desc.FocDescMap;
import com.foc.desc.FocObject;
import com.foc.desc.FocObjectGeneral;
import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FStringField;
import com.foc.list.FocList;
import com.foc.util.ASCII;

@Component
public class RunAfterStartup {

	@Autowired
	@PersistenceContext(type = PersistenceContextType.TRANSACTION)
	EntityManager entityManager;

	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		initApplication();
		scanEntitiesAndCreateFocDesc();
		Globals.getApp().adaptDataModel(false, false);
		
		//insertProjectAndMilestones();
	}

	public void initApplication() {
//		ConfigInfo.loadFile();
		String[] focArgs = { "/IS_SERVER:1", "/nol:1" };
		FocSampleMain main = new FocSampleMain(focArgs);
		main.init2(focArgs);
		main.init3(focArgs);
	}

	public void scanEntitiesAndCreateFocDesc() {
		Set<EntityType<?>> entitySet = entityManager.getMetamodel().getEntities();
		
		Iterator<EntityType<?>> entityIter = entitySet.iterator();
		while(entityIter != null && entityIter.hasNext()) {
			EntityType type = entityIter.next();
			Globals.logString(" ENTITY TYPE " + type.getName());
			
			FocDesc focDesc = new FocDesc(FocObjectGeneral.class, FocDesc.DB_RESIDENT, type.getName(), false);
			int fieldID = 1;
			
			Set<Attribute> attSet = type.getAttributes();
			Iterator<Attribute> attIter = attSet.iterator();
			while(attIter != null && attIter.hasNext()) {
				Attribute att = attIter.next();
				Class cls = att.getJavaType();
				
				String fieldName = att.getName();
				fieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
				String title = fieldName = ASCII.convertJavaClassNameToATitleWithSpacesAndCapitals(fieldName);
				
				Globals.logString("    ATTRIBUTE TYPE: " + att.getName()+" class: "+cls.getName());

				if (att.getName().toLowerCase().equals("id")) {
					FField fld = focDesc.addReferenceField();
					fld.setName("Id");					
				} else if (cls == String.class) {
					FStringField fld = new FStringField(fieldName, title, fieldID++, false, 1000); 
					focDesc.addField(fld);
				} else if (cls == Integer.class) {
					FIntField fld = new FIntField(fieldName, title, fieldID++, false, 10); 
					focDesc.addField(fld);
				} else if (cls == Boolean.class) {
//					FIntField fld = new FIntField(att.getName(), null, fieldID++, false, 10); 
//					focDesc.addField(fld);			
				} else {
					FObjectField fld = new FObjectField(fieldName, title, fieldID++, false, focDesc, fieldName, null, 0, null, false);
					fld.setFocDescStorageName(fieldName, false, false);
					focDesc.addField(fld);
				}
				
			}			

			FocDescInstanceFocDescDeclaration declaration = new FocDescInstanceFocDescDeclaration(focDesc);
			Globals.getApp().declaredObjectList_DeclareDescription(declaration);
			FocDescMap.getInstance().put(focDesc.getName(), focDesc);
			
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
				  				((FObjectField)fld).getFocDescFromStorageNameIfNeeded(focDesc2);
				  			}
				  		}
			  		}
			  	}
			}
		}
	}

	public void insertProjectAndMilestones() {
		FocDesc projectDesc = Globals.getApp().getFocDescByName("Project");
		FocDesc milestoneDesc = Globals.getApp().getFocDescByName("Milestone");
		
		FocList projectList = projectDesc.getFocList();
		projectList.loadIfNotLoadedFromDB();
		FocObject project = projectList.newEmptyItem();
		project.setCreated(true);
		project.setPropertyString("Name", "MyProject");
		projectList.add(project);
		
		FocList milestoneList = milestoneDesc.getFocList();
		milestoneList.loadIfNotLoadedFromDB();
		FocObject milestone = milestoneList.newEmptyItem();
		milestone.setPropertyString("Name", "Milestone_1");
		milestone.setPropertyObject("Project", project);
		milestone.setCreated(true);
		milestoneList.add(milestone);
		
		project.validate(false);
		milestone.validate(false);
		
		projectList.validate(false);
		milestoneList.validate(false);
	}
	
}