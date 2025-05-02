package com.neofoc.springboot.config;

import com.neofoc.springboot.service.ScanSpringBootEntitiesAndConvert2FocDesc;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.foc.Globals;

@Component
public class RunAfterStartup {

	private final ScanSpringBootEntitiesAndConvert2FocDesc scanSpringBootEntitiesAndConvert2FocDesc;

    public RunAfterStartup(ScanSpringBootEntitiesAndConvert2FocDesc scanSpringBootEntitiesAndConvert2FocDesc) {
        this.scanSpringBootEntitiesAndConvert2FocDesc = scanSpringBootEntitiesAndConvert2FocDesc;
    }

    @EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		initApplication();
		scanSpringBootEntitiesAndConvert2FocDesc.scanEntitiesAndCreateFocDesc();
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

	/*
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
    */
	
}