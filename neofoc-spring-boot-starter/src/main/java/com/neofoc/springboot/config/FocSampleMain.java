package com.neofoc.springboot.config;

import com.fab.FabModule;
import com.fab.parameterSheet.ParameterSheetFactory;
import com.foc.Application;
import com.foc.FocMainClass;
import com.foc.business.calendar.CalendarModule;
import com.foc.business.currency.CurrencyModule;
import com.foc.business.notifier.NotifierModule;
import com.foc.business.workflow.WorkflowModule;
import com.foc.db.migration.MigrationModule;
import com.foc.link.LinkModule;
import com.foc.pivot.PivotModule;

public class FocSampleMain extends FocMainClass {

	public FocSampleMain(String[] args) {
		super(args);
	}
	
	protected void declareModules(Application app) {
		MigrationModule.getInstance().declare();
		FabModule.getInstance().declare();
		WorkflowModule.getInstance().declare();
		NotifierModule.getInstance().declare();
		PivotModule.getInstance().declare();
		LinkModule.getInstance().declare();
		ParameterSheetFactory.setEmptyParamSetAsDefaultParamSet();
		CurrencyModule.includeCurrencyModule();
		CalendarModule.getInstance().declare();
	}
}
