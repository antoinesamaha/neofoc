/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.fab.codeWriter;

import java.util.Iterator;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.desc.FocDesc;
import com.foc.desc.FocModule;
import com.foc.util.ASCII;

public class CodeWriter_FocWebModule extends CodeWriter {

	private FocModule focModule = null;
	
	public CodeWriter_FocWebModule(FocModule focModule, CodeWriterSet set){
		super(set);
		this.focModule = focModule;
	}

	@Override
	public boolean hasInternalFile() {
		return true;
	}

	@Override
	public boolean hasExternalFile() {
		return true;
	}

	@Override
	public String getFileSuffix() {
		return "";
	}

	@Override
	public boolean isServerSide() {
		return true;
	}
	
	public String getProjectFullPath(){
		String path = ConfigInfo.getCodeProjectPath();
		
		if(path.charAt(path.length()-1) != '/'){
			path += "/";
		}
		
		if(isFoc()){
			path += "focWebServer/src/";
		}else{
			path += "everproWebServer/src/";
		}
		return path;
	}

	public String getModuleSimplePackageName(){
		String lastPackage = focModule.getClass().getSimpleName().replace("Module", "");
		String firstLetter = String.valueOf(lastPackage.charAt(0));
		lastPackage = firstLetter.toLowerCase() + lastPackage.substring(1); 

		return lastPackage;
	}
	
	public String getModuleTitle(){
		String title = focModule.getClass().getSimpleName().replace("Module", "");
		return ASCII.convertJavaClassNameToATitleWithSpacesAndCapitals(title);
	}
	
	public boolean isFoc(){
		String packageName = focModule.getClass().getPackage().getName();
		return packageName.startsWith("b01.foc");
	}	
	
	public String getPackageName(boolean autoGen){
		String packageName = "";
		
		if(isFoc()){
			packageName = "b01.foc.web.modules";
		}else{
			packageName = "b01.everpro.web.modules";
		}
		
		String lastPackage = getModuleSimplePackageName();
		
		packageName += "."+ lastPackage;
		
		if(autoGen){
			packageName = packageName + "." + PACKAGE_NAME_AUTO_GEN;
		}
		return packageName;
	}

	public String getClassName(){
		return getClassName(false);
	}
	
	public String getClassName(boolean autoGen){
		String prefix = focModule.getName();

		String internalClassName = prefix.replace("Module", "WebModule"); 
			
		if(autoGen){
			internalClassName = CLASS_NAME_PREFIX_AUTO_GEN+internalClassName;
		}
		
		return internalClassName;
	}
	
	public boolean isFocDescIncluded(FocDesc focDesc){
		return focDesc != null && focDesc.getModule() == focModule && !focDesc.getStorageName().endsWith("WFLog_Desc");
	}
	
	@Override
	public void generateCode() {
		initFiles();
		
		CodeWriter_OneFile intWriter = getInternalFileWriter(); 
		CodeWriter_OneFile extWriter = getExternalFileWriter();
		
		//  Internal
		//  --------
		
		intWriter.addImport("b01.foc.list.FocList");
		intWriter.addImport("b01.foc.menuStructure.FocMenuItem");
		intWriter.addImport("b01.foc.menuStructure.IFocMenuItemAction");
		intWriter.addImport("b01.foc.vaadin.FocWebModule");
		intWriter.addImport("b01.foc.vaadin.FocWebVaadinWindow");
		intWriter.addImport("b01.foc.vaadin.ICentralPanel");
		intWriter.addImport("b01.foc.web.gui.INavigationWindow");
		intWriter.addImport("b01.foc.web.server.xmlViewDictionary.XMLViewDictionary");
		intWriter.addImport("b01.foc.web.server.xmlViewDictionary.XMLViewKey");

		intWriter.printCore("public class "+intWriter.getClassName()+" extends FocWebModule {\n\n");
		
		intWriter.printCore("  public void declareXMLViewsInDictionary() {\n");
		
  	Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
  	while(iter != null && iter.hasNext()){
  		IFocDescDeclaration iFocDescDeclaration = iter.next();
  		if(iFocDescDeclaration != null){
  			FocDesc focDesc = iFocDescDeclaration.getFocDescription();
  			if(isFocDescIncluded(focDesc)){
  				String stockString         = getModuleSimplePackageName();
  				String xmlFileRelativePath = "/xml/b01/"+(isFoc()?"foc":"everpro")+"/"+stockString+"/";  
  				
  				String storageNameExpression = focDesc.getClass().getSimpleName()+".getInstance().getStorageName()";
  				String objectClassName       = focDesc.getClass().getSimpleName().replace("Desc", "");
  				
  				if(objectClassName.compareTo("Foc") == 0){
  					Globals.logString(" FOC DESC WITH PROBLEM : "+focDesc.getStorageName());
  					storageNameExpression = "\""+focDesc.getStorageName()+"\"";
  					
  					objectClassName = focDesc.getStorageName();
  					objectClassName = objectClassName.toLowerCase();
  					objectClassName = String.valueOf(objectClassName.charAt(0)).toUpperCase() + objectClassName.substring(1);
  					int idx = 1;
  					while(idx < objectClassName.length()){
  						char c = objectClassName.charAt(idx);
  						if(c == '_'){
  							objectClassName = objectClassName.substring(0, idx) + String.valueOf(objectClassName.charAt(idx+1)).toUpperCase() + objectClassName.substring(idx+2);
  						}
  						idx++;
  					}
  				}
  				
  				intWriter.addImport(focDesc.getClass().getName());
  				
  				intWriter.printCore("    XMLViewDictionary.getInstance().put(\n");
  				intWriter.printCore("      "+storageNameExpression+",\n");
  				intWriter.printCore("      XMLViewKey.TYPE_TABLE,\n"); 
  				intWriter.printCore("      XMLViewKey.CONTEXT_DEFAULT,\n"); 
  				intWriter.printCore("      XMLViewKey.VIEW_DEFAULT,\n"); 
  				intWriter.printCore("      \""+xmlFileRelativePath+objectClassName+"_Table.xml\", 0, null);\n\n");

  				intWriter.printCore("    XMLViewDictionary.getInstance().put(\n");
  				intWriter.printCore("      "+storageNameExpression+",\n");
  				intWriter.printCore("      XMLViewKey.TYPE_FORM,\n"); 
  				intWriter.printCore("      XMLViewKey.CONTEXT_DEFAULT,\n"); 
  				intWriter.printCore("      XMLViewKey.VIEW_DEFAULT,\n"); 
  				intWriter.printCore("      \""+xmlFileRelativePath+objectClassName+"_Form.xml\", 0, null);\n\n");
  			}
  		}
  	}
        
		intWriter.printCore("  }\n\n");

		intWriter.printCore("  public void menu_FillMenuTree(FocMenuItem fatherMenuItem) {\n");
		intWriter.printCore("    FocMenuItem mainMenu = fatherMenuItem.pushMenu(\""+getModuleTitle()+"\", \""+getModuleTitle()+"\");\n");
		intWriter.printCore("    FocMenuItem menuItem = null;"); 
			
		iter = Globals.getApp().getFocDescDeclarationIterator();
  	while(iter != null && iter.hasNext()){
  		IFocDescDeclaration iFocDescDeclaration = iter.next();
  		if(iFocDescDeclaration != null){
  			FocDesc focDesc = iFocDescDeclaration.getFocDescription();
  			if(isFocDescIncluded(focDesc)){
  				
  				String menuTitle = focDesc.getClass().getSimpleName();
  				menuTitle = menuTitle.replace("Desc", "");
  				menuTitle = ASCII.convertJavaClassNameToATitleWithSpacesAndCapitals(menuTitle);
  				
  				intWriter.printCore("    menuItem = mainMenu.pushMenu(\""+menuTitle+"\", \"_"+menuTitle+"\");\n");
  				intWriter.printCore("    menuItem.setMenuAction(new IFocMenuItemAction() {\n");
  				intWriter.printCore("      public void actionPerformed(Object navigationWindow, FocMenuItem menuItem) {\n");
  				intWriter.printCore("      	 INavigationWindow mainWindow = (INavigationWindow) navigationWindow;\n");
  				intWriter.printCore("      	 //FocList focList = "+focDesc.getClass().getSimpleName()+".getList(FocList.LOAD_IF_NEEDED);\n");
  				intWriter.printCore("      	 //mainWindow.changeCentralPanelContent_ToTableForFocList(focList);\n");
  				intWriter.printCore("      }\n");
  				intWriter.printCore("    });\n\n");
  			}
  		}
  	}

		/*
  	FocMenuItem finance = fatherMenuItem.pushMenu(MENU_WORK_ORDER, MENU_WORK_ORDER);
  	
  	FocMenuItem realProperty = finance.pushMenu(MENU_WORK_ORDER, MENU_WORK_ORDER);
  	realProperty.setMenuAction(new IFocMenuItemAction() {
			public void actionPerformed(Object navigationWindow, FocMenuItem menuItem) {
				INavigationWindow mainWindow = (INavigationWindow) navigationWindow;
				FocList workOrderList = WorkOrderDesc.newWorkOrderList();
		    mainWindow.changeCentralPanelContent_ToTableForFocList(workOrderList);
			}
		});
		*/
		
		
		
		intWriter.printCore("  }\n\n");

		/*
		intWriter.printCore("  public "+intWriter.getClassName()+"(FocConstructor constr){\n");
		
		intWriter.printCore("    super(constr);\n");
		intWriter.printCore("    newFocProperties();\n");
		intWriter.printCore("  }\n\n");

		FocList fieldList = getTblDef().getFieldDefinitionList();
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			FieldFactory.getInstance().addGetterSetterInFocObject(this, fieldDefinition);
		}
		
	  if(getTblDef().isSingleInstance()){
	  	intWriter.addImport("b01.foc.list.FocList");
	  	intWriter.addImport(extWriter.getPackageName()+"."+extWriter.getClassName());
	  	intWriter.addImport(extWriter.getPackageName()+"."+extWriter.getClassName()+CodeWriter.CLASS_NAME_SUFFIX_SERVICE_INSTANCE);

	  	intWriter.printCore("  private static "+extWriter.getClassName()+" instance = null;\n");
			intWriter.printCore("  public static "+extWriter.getClassName()+" getInstance(){\n");
			intWriter.printCore("    if(instance == null){\n");
			intWriter.printCore("    	 FocList list = "+extWriter.getClassName()+CodeWriter.CLASS_NAME_SUFFIX_SERVICE_INSTANCE+".getInstance().getFocList(true);\n");
			intWriter.printCore("    	 if(list.size() == 0){\n");
			intWriter.printCore("    	   instance = ("+extWriter.getClassName()+") list.newEmptyItem();\n");
			intWriter.printCore("    	   list.add(instance);\n");
			intWriter.printCore("    	   instance.validate(true);\n");
			intWriter.printCore("    	 }else{\n");
			intWriter.printCore("    	   instance = ("+extWriter.getClassName()+") list.getFocObject(0);\n");
			intWriter.printCore("    	 }\n");
			intWriter.printCore("    }\n");
			intWriter.printCore("    return instance;\n");
			intWriter.printCore("  }\n");
	  }
		*/
		
		intWriter.printCore("}\n");
		intWriter.compile();
		
		//  External
		//  --------
		
		extWriter.addImport("b01.foc.desc.FocConstructor");
		extWriter.addImport(intWriter.getPackageName()+"."+intWriter.getClassName());
		
		extWriter.printCore("public class "+extWriter.getClassName()+" extends "+intWriter.getClassName()+" {\n");
		
		/*
		extWriter.printCore("  public "+extWriter.getClassName()+"(FocConstructor constr){\n");
		extWriter.printCore("    super(constr);\n");
		extWriter.printCore("  }\n");
		*/
				
		extWriter.printCore("}");
		extWriter.compile();
		
		closeFiles();
	}
}
