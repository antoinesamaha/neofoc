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
package com.fab.model.table;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import com.fab.DBFocDescDeclaration;
import com.fab.DB_WFLog_FocDescDeclaration;
import com.fab.codeWriter.CodeWriter;
import com.fab.codeWriter.CodeWriterConstants;
import com.fab.codeWriter.CodeWriterSet;
import com.fab.gui.browse.GuiBrowse;
import com.fab.gui.details.GuiDetails;
import com.fab.gui.details.GuiDetailsDesc;
import com.fab.gui.html.TableHtmlDesc;
import com.fab.model.project.FabProject;
import com.fab.model.project.FabProjectDesc;
import com.fab.model.project.FabWorkspace;
import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FEMailField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FNumField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FDescPropertyStringBased;
import com.foc.util.ASCII;
import com.foc.util.FileFinder;

@SuppressWarnings("serial")
public class TableDefinition extends FocObject implements CodeWriterConstants {
	private boolean             firstCallToGetFieldDefintion = true;
	private IFocDescDeclaration dbFocDescDeclaration         = null;
	private IFocDescDeclaration db_WFLog_FocDescDeclaration  = null;
	//private String              dbName                       = null;
	
	private String refFieldName = FField.REF_FIELD_NAME; 

	public TableDefinition(FocConstructor constr){
		super(constr);
		newFocProperties();
		setPropertyBoolean(TableDefinitionDesc.FLD_DB_RESIDENT, true );
		setPropertyBoolean(TableDefinitionDesc.FLD_KEY_UNIQUE , false);
		setPropertyBoolean(TableDefinitionDesc.FLD_WITH_REF   , true );
	}
	
	public String getName(){
		return getPropertyString(TableDefinitionDesc.FLD_NAME);
	}
	
	public void setName(String name){
		setPropertyString(TableDefinitionDesc.FLD_NAME, name);
	}

	public String getTitle(){
		return getPropertyString(TableDefinitionDesc.FLD_TITLE);
	}
	
	public void setTitle(String name){
		setPropertyString(TableDefinitionDesc.FLD_TITLE, name);
	}

	public boolean hasWorkflow(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_HAS_WORKFLOW);
	}

	public boolean isNotDirectlyEditable(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_NOT_DIRECTLY_EDITABLE);
	}

	public boolean isShowInMenu(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_SHOW_IN_MENU);
	}

	public boolean isShowInMenu_Descendent(){
		boolean isShowMenu = false;
		TableDefinition def = this;
		while(def != null && !isShowMenu){
			isShowMenu = def.isShowInMenu();
			def = (TableDefinition) def.getFatherObject();
		}
		return isShowMenu;
	}

	@Override
  public FocObject getFatherObject(){
		return super.getFatherObject();
  }
  	
	public String getClassName(){
		return getPropertyString(TableDefinitionDesc.FLD_CLASS_NAME);
	}
	
	public void setClassName(String name){
		setPropertyString(TableDefinitionDesc.FLD_CLASS_NAME, name);
	}
	
	public void adjustClassNameFromTableName(){
		String dbName = getName();
		String classSimpleName_FocObject = ASCII.convertJavaNaming_ToVariableGetterSetterNaming(dbName);
		setClassName(classSimpleName_FocObject);
	}
	
	public void adjustClassNameFromTableName_IfEmpty(){
		if(getClassName().isEmpty()){
			adjustClassNameFromTableName();
		}
	}
	
//
//	public String getDBName(){
//		return dbName == null ? getName() : dbName;
//	}
//	
//	public void setDBName(String name){
//		dbName = name;
//	}

	public FocDesc getExistingTableDesc(){
		return getPropertyDesc(TableDefinitionDesc.FLD_EXISTING_TABLE);
	}
	
	public boolean isAlreadyExisting(){
		FDescPropertyStringBased prop = (FDescPropertyStringBased) getFocProperty(TableDefinitionDesc.FLD_EXISTING_TABLE);
		String str = prop != null ? prop.getString() : null;
		return str != null && !str.trim().isEmpty();
	}
	
	public boolean isDBResident(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_DB_RESIDENT);
	}
	
	public void setDBResident(boolean dbResident){
		setPropertyBoolean(TableDefinitionDesc.FLD_DB_RESIDENT, dbResident);
	}

	public boolean isWebStructure(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_WEB_STRUCTURE);
	}
	
	public void setWebStructure(boolean dbResident){
		setPropertyBoolean(TableDefinitionDesc.FLD_WEB_STRUCTURE, dbResident);
	}

	public boolean isWithReference(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_WITH_REF);
	}
	
	public void setWithReference(boolean withReference){
		setPropertyBoolean(TableDefinitionDesc.FLD_WITH_REF, withReference);
	}
	
	public boolean isKeyUnique(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_KEY_UNIQUE);
	}
	
	public void setKeyUnique(boolean keyUnique){
		setPropertyBoolean(TableDefinitionDesc.FLD_KEY_UNIQUE, keyUnique);
	}
	
	public void setSingleInstance(boolean singleInstance){
		setPropertyBoolean(TableDefinitionDesc.FLD_SINGLE_INSTANCE, singleInstance);
	}
	
	public boolean isSingleInstance(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_SINGLE_INSTANCE);
	}

	public boolean isAddLogFields(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_ADD_LOG_FIELDS);
	}
	
	public void setAddLogFields(boolean addLogFields){
		setPropertyBoolean(TableDefinitionDesc.FLD_ADD_LOG_FIELDS, addLogFields);
	}
	
	public FabProject getProject(){
		return (FabProject) getPropertyObject(TableDefinitionDesc.FLD_PROJECT);
	}

	public void setProject(FabProject fabProject){
		setPropertyObject(TableDefinitionDesc.FLD_PROJECT, fabProject);
	}
	
	public String getPackageName_ServerSide(){
		return getPropertyString(TableDefinitionDesc.FLD_SERVER_SIDE_PACKAGE);
	}

	public void setPackageName_ServerSide(String str){
		setPropertyString(TableDefinitionDesc.FLD_SERVER_SIDE_PACKAGE, str);
	}
	
	public FabProject getProject_WebClient(){
		return (FabProject) getPropertyObject(TableDefinitionDesc.FLD_WEB_CLIENT_PROJECT);
	}

	public void setProject_WebClient(FabProject fabProject){
		setPropertyObject(TableDefinitionDesc.FLD_WEB_CLIENT_PROJECT, fabProject);
	}
	
	public String getPackageName_WebClient(){
		return getPropertyString(TableDefinitionDesc.FLD_WEB_CLIENT_PACKAGE);
	}

	public void setPackageName_WebClient(String str){
		setPropertyString(TableDefinitionDesc.FLD_WEB_CLIENT_PACKAGE, str);
	}
	
	public FocList getDictionaryGroupList(){
		FocList list = getPropertyList(TableDefinitionDesc.FLD_DICTIONARY_GROUP_LIST);
		if(list != null && list.getListOrder() == null){
			list.setListOrder(new FocListOrder(FabDictionaryGroupDesc.FLD_NAME));
		}
		return list;
	}
	
	public FocList getHtmlFormList(){
		FocList list = getPropertyList(TableDefinitionDesc.FLD_HTML_FORM_LIST);
		if(list != null && list.getListOrder() == null){
			list.setListOrder(new FocListOrder(TableHtmlDesc.FLD_TITLE));
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public void adjustIFocDescDeclaration(){
		IFocDescDeclaration alreadyDeclaredDBFocDescDeclaration = getDbFocDescDeclaration();
		FocDesc alreadyDeclaredFocDesc = alreadyDeclaredDBFocDescDeclaration != null ? alreadyDeclaredDBFocDescDeclaration.getFocDescription() : null;
		if(alreadyDeclaredFocDesc != null){
			FocList fieldDefinitionList = getFieldDefinitionList();
			
			alreadyDeclaredFocDesc.addFieldsFromTableDefinition(this, fieldDefinitionList);
			/*
			Iterator<FieldDefinition> iter = fieldDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext()){
				FieldDefinition fieldDefintion = iter.next();
				fieldDefintion.addToFocDesc(alreadyDeclaredFocDesc);
			}
			*/
		}
		
		/*
		IFocDescDeclaration alreadyDeclaredDBFocDescDeclaration = (IFocDescDeclaration) Globals.getApp().getIFocDescDeclarationByName(getName());
		if(alreadyDeclaredDBFocDescDeclaration == null){
			alreadyDeclaredDBFocDescDeclaration = DBFocDescDeclaration.getDbFocDescDeclaration(this);
			Globals.getApp().declaredObjectList_DeclareDescription(alreadyDeclaredDBFocDescDeclaration);
			alreadyDeclaredDBFocDescDeclaration.getFocDesctiption();
		}else{
			FocDesc alreadyDeclaredFocDesc = alreadyDeclaredDBFocDescDeclaration.getFocDesctiption();
			if(alreadyDeclaredFocDesc != null){
				FocList fieldDefinitionList = getFieldDefinitionList();
				Iterator<FieldDefinition> iter = fieldDefinitionList.focObjectIterator();
				while(iter != null && iter.hasNext()){
					FieldDefinition fieldDefintion = iter.next();
					fieldDefintion.addToFocDesc(alreadyDeclaredFocDesc);
				}
			}
		}
		*/
	}
	
	public FieldDefinition getFieldDefinitionById(int id){
		FieldDefinition fieldDefinition = null;
		FocList fieldDefinitionList = getFieldDefinitionList();
		if(fieldDefinitionList != null){
			Iterator<FieldDefinition> iter = fieldDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext() && fieldDefinition == null){
				FieldDefinition currFieldDefinition = iter.next();
				if(id == currFieldDefinition.getID()){
					fieldDefinition = currFieldDefinition; 
				}
			}
		}
		return fieldDefinition;
	}

	public int getMaxFieldDefinitionId(){
		int maxId = 0;
		FocList fieldDefinitionList = getFieldDefinitionList();
		if(fieldDefinitionList != null){
			Iterator<FieldDefinition> iter = fieldDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext()){
				FieldDefinition fieldDefinition = iter.next();
				int id = fieldDefinition.getID();
				if(id > maxId){
					maxId = id;
				}
			}
		}
		return maxId;
	}
	
	/*public FocList getFieldDefinitionList(boolean doNotTryLoading){
		if(fieldsList == null){
			FocLinkForeignKey link = new FocLinkForeignKey(FieldDefinitionDesc.getInstance(),FieldDefinitionDesc.FLD_TABLE,true);
			fieldsList = new FocList(this,link,null);
			fieldsList.setDirectlyEditable(true);
			fieldsList.setDirectImpactOnDatabase(false);
			FocListOrder order = new FocListOrder(FieldDefinitionDesc.FLD_ID);
			fieldsList.setListOrder(order);
			if(!doNotTryLoading){
				fieldsList.loadIfNotLoadedFromDB();
			}
		}
		return fieldsList;
	}*/
	
	public FocList getFieldDefinitionList(){
		FocList fieldDefintionList = getPropertyList(TableDefinitionDesc.FLD_FIELD_DEFINITION_LIST);
		if(firstCallToGetFieldDefintion){
			firstCallToGetFieldDefintion = false;
			FocListOrder order = new FocListOrder(FieldDefinitionDesc.FLD_DICTIONARY_GROUP, FieldDefinitionDesc.FLD_ID);
			fieldDefintionList.setListOrder(order);
			fieldDefintionList.setDirectlyEditable(false);
			fieldDefintionList.setDirectImpactOnDatabase(true);
		}
		return fieldDefintionList;
	}
	
	public FocList getBrowseViewDefinitionList(){
		FocList list = getPropertyList(TableDefinitionDesc.FLD_BROWSE_VIEW_LIST);
		return list;
	}
	
	public FocList getDetailsViewDefinitionList(){
		FocList list = getPropertyList(TableDefinitionDesc.FLD_DETAILS_VIEW_LIST);
		return list;
	}
	
	public FocList getFilterFieldDefinitionList(){
		return getPropertyList(TableDefinitionDesc.FLD_FILTER_FIELD_DEF_LIST);
	}
	
	@SuppressWarnings("unchecked")
	protected GuiDetails getDetailsViewDefinitionForViewId(int viewId){
		GuiDetails aDetailsViewDefinition = null;
		boolean found = false;
		if(viewId == FocObject.DEFAULT_VIEW_ID){
			aDetailsViewDefinition = getDefaultVeiwDefinition();
			found = true;
		}else if(viewId == FocObject.SUMMARY_VIEW_ID){
			aDetailsViewDefinition = getSummaryVeiwDefinition();
			found = true;
		}else{
			FocList detailsViewDefinitionList = getDetailsViewDefinitionList();
			if(detailsViewDefinitionList != null){
				aDetailsViewDefinition = (GuiDetails) detailsViewDefinitionList.searchByRealReferenceOnly(viewId);
				found = aDetailsViewDefinition != null;
			}
			/*Iterator<GuiDetails> iter = detailsViewDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext() && !found){
				aDetailsViewDefinition = iter.next();
				if(aDetailsViewDefinition != null && aDetailsViewDefinition.getReference().getInteger() == viewId){
					found = true;
				}
			}*/
		}
		return found ? aDetailsViewDefinition : null;
	}
	
	private GuiDetails getDefaultVeiwDefinition(){
		GuiDetails defaultDetailsViewDefinition = null;
		FocList detailsViewDefinitionList = getDetailsViewDefinitionList();
		if(detailsViewDefinitionList != null){
			defaultDetailsViewDefinition = (GuiDetails)detailsViewDefinitionList.searchByPropertyBooleanValue(GuiDetailsDesc.FLD_IS_DEFAULT_VIEW, true);
		}
		return defaultDetailsViewDefinition;
	}
	
	private GuiDetails getSummaryVeiwDefinition(){
		GuiDetails summaryDetailsViewDefinition = null;
		FocList detailsViewDefinitionList = getDetailsViewDefinitionList();
		if(detailsViewDefinitionList != null){
			summaryDetailsViewDefinition = (GuiDetails)detailsViewDefinitionList.searchByPropertyBooleanValue(GuiDetailsDesc.FLD_IS_SUMMARY_VIEW, true);
		}
		return summaryDetailsViewDefinition;
	}
	
	@SuppressWarnings("unchecked")
	public void addFieldsToFocDesc(FocDesc focDesc){
		focDesc.addFieldsFromTableDefinition();
//		FocLinkForeignKey link = new FocLinkForeignKey(((TableDefinitionDesc)getThisFocDesc()).getFieldDefinitionFocDesc(), FieldDefinitionDesc.FLD_TABLE, true);
//		FocList fieldsList = new FocList(this, link, null);
//		fieldsList.loadIfNotLoadedFromDB();
//		fieldsList.setFatherSubject(null);
//		
//		addFieldsToFocDesc(focDesc, fieldsList);
//		
//		fieldsList.dispose();
//		fieldsList = null;
	}

	public IFocDescDeclaration getDbFocDescDeclaration() {
		if(dbFocDescDeclaration == null){
			dbFocDescDeclaration = (IFocDescDeclaration) DBFocDescDeclaration.getDbFocDescDeclaration(this);
		}
		return dbFocDescDeclaration;
	}
	
	public void setDbFocDescDeclaration(DBFocDescDeclaration dbFocDescDeclaration) {
		this.dbFocDescDeclaration = dbFocDescDeclaration;
	}

	public IFocDescDeclaration getDb_WFLog_FocDescDeclaration() {
		if(db_WFLog_FocDescDeclaration == null && hasWorkflow()){
			db_WFLog_FocDescDeclaration = new DB_WFLog_FocDescDeclaration((DBFocDescDeclaration) getDbFocDescDeclaration());
		}
		return db_WFLog_FocDescDeclaration;
	}
	
	public void setDbFocDescDeclaration(DB_WFLog_FocDescDeclaration db_WFLog_FocDescDeclaration) {
		this.db_WFLog_FocDescDeclaration = db_WFLog_FocDescDeclaration;
	}
	
	//--------------------------------------------------------
	// Code Writer
	//--------------------------------------------------------
	
	public String getCW_ClassName_FocObject(){
		String clsName = getClassName();
		if(clsName == null || clsName.isEmpty()){
			adjustClassNameFromTableName();
			clsName = getClassName();
		}
		return clsName;
	}

	public void setCW_ClassName_FocObject(String str){
		setClassName(str);
	}

	public String getCW_ClassName_FocDesc(){
		return getCW_ClassName_FocObject()+CLASS_NAME_SUFFIX_FOC_DESC;
	}

	public String getCW_ClassName_GwfObject(){
		return getCW_ClassName_FocObject()+CLASS_NAME_SUFFIX_WEB_CLIENT;
	}

	public String getCW_ClassName_ServiceInstance(){
		return getCW_ClassName_FocObject()+CLASS_NAME_SUFFIX_SERVICE_INSTANCE;
	}

	public String getCW_ClassName_ServiceImplementation(){
		return getCW_ClassName_FocObject()+CLASS_NAME_SUFFIX_SERVICE_IMPLEMENTATION;
	}

	public String getCW_ClassName_WebService(){
		return getCW_ClassName_FocObject()+CLASS_NAME_SUFFIX_SERVICE;
	}

	public String getCW_ClassName_GwfList(){
		return getCW_ClassName_FocObject()+CLASS_NAME_SUFFIX_PROXY_LIST;
	}

	public String getCW_ClassName_WebServiceAsync(){
		return getCW_ClassName_FocObject()+CLASS_NAME_SUFFIX_SERVICE_ASYNC;
	}

	public String getCW_ProjectFullPath(boolean forServer){
		//String fullName = tableDefinition.getPro
		String projectFullPath = null;
		{
			FabProject   project         = forServer ? getProject() : getProject_WebClient();
			FabWorkspace workspace       = project.getWorkspace();
			
			String wsPath = workspace.getWorkspacePath();
			String pjPath = project.getProjectPath();
			
			String separator_WS_PJ = "";
			if(wsPath.charAt(wsPath.length()-1) != '/'){
				separator_WS_PJ = "/";
			}
	
			String separator_PJ_PK = "";
			if(pjPath.charAt(pjPath.length()-1) != '/'){
				separator_PJ_PK = "/";
			}
			
			//String packagePath = tableDefinition.getPackageName().replace('.', '/');
	
			projectFullPath = wsPath+separator_WS_PJ+pjPath+separator_PJ_PK;
		}
		return projectFullPath;
	}

	public String getCW_PackageName_ClientRoot(){
		return getProject().getCommonPackage()+"."+INTERMEDIATE_PACKAGE_CLIENT;
	}

	public String getCW_PackageName_Client(){
		String packageName = getPackageName_ServerSide();
		if(isWebStructure()){
			String tablePackageChunk = getPackageName_ServerSide().isEmpty() ? "": "."+getPackageName_ServerSide();
			packageName = getCW_PackageName_ClientRoot()+tablePackageChunk;
		}
		return packageName;
	}
	
	public String getCW_PackageName_ServerRoot(){
		return getProject().getCommonPackage()+"."+INTERMEDIATE_PACKAGE_SERVER;
	}
	
	public String getCW_PackageName_Server(){
		String packageName = getPackageName_ServerSide();
		if(isWebStructure()){
			String tablePackageChunk = getPackageName_ServerSide().isEmpty() ? "": "."+getPackageName_ServerSide();
			packageName = getCW_PackageName_ServerRoot()+tablePackageChunk;
		}
		return packageName;
	}
	
	public String getCW_ServiceName(){
		String str = getCW_ClassName_FocObject();
		return str.toLowerCase();
	}
	
	protected void fillTableDefinitionFromFocDesc(FocDesc focDesc){
		fillTableDefinitionFromFocDesc(focDesc, null);
	}
	
	protected void fillTableDefinitionFromFocDesc(FocDesc focDesc, Hashtable<String, String> foreignKeys){
		FocList projectList = FabProjectDesc.getList(FocList.LOAD_IF_NEEDED); 
		for(int i=0; i<projectList.size(); i++){
			FabProject project        = (FabProject) projectList.getFocObject(i);
			String     rootToSearchIn = project.getWorkspace().getWorkspacePath() + "/" +project.getProjectPath();
			
			String fileName = focDesc.getClass().getName();
			fileName = fileName.replace('.', '/');
			fileName += ".java";
			FileFinder fileFinder = new FileFinder(rootToSearchIn, fileName, FileFinder.MATCH_CONTAINS);
			fileFinder.find();
			if(fileFinder.size() == 1){
				File file = fileFinder.get(0);
				String packageStr = file.getPath();
				packageStr.substring(rootToSearchIn.length());
				packageStr.replace('/', '.');
				setPackageName_ServerSide(focDesc.getClass().getPackage().getName());
				String simpleName = focDesc.getClass().getSimpleName();
				int idxOfDesc = simpleName.indexOf(CodeWriterConstants.CLASS_NAME_SUFFIX_FOC_DESC);
				simpleName = simpleName.substring(0, idxOfDesc);
				setName(focDesc.getStorageName());
				setClassName(simpleName);
				setCW_ClassName_FocObject(simpleName);
				setProject(project);
				setFabOwner(TableDefinitionDesc.TABLE_OWNER_SOFTWARE_PROVIDER);
				setKeyUnique(focDesc.isKeyUnique());
			}
			fileFinder.dispose();
		}
		
		FocList fldDefList = getFieldDefinitionList();
		FocFieldEnum focFieldEnum = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
		while(focFieldEnum != null && focFieldEnum.hasNext()){
			FField focFld = focFieldEnum.nextField();
			
			if(focFld.getID() == FField.REF_FIELD_ID){
				setWithReference(true);
				setRefFieldName(focFld.getDBName());
			}
			
			if(!focFld.isObjectContainer() && focFld.getID() != FField.REF_FIELD_ID && !(focFld instanceof FBlobStringField)){
				String foreignTable = foreignKeys.get(focFld.getName());
				
				FieldDefinition fldDef = (FieldDefinition) fldDefList.newEmptyItem();
				fldDef.setID(focFld.getID());
				fldDef.setName(focFld.getName());
				fldDef.setTitle(focFld.getTitle());
				if(foreignTable != null){//Object Field in this case
					fldDef.setSQLType(FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD);
					fldDef.setFocDescName(foreignTable);
					String objFieldDBForcedName = focFld.getName();
					String objFieldName         = focFld.getName();
					if(objFieldName.endsWith("Id")) objFieldName = objFieldName.replace("Id", ""); 
					if(objFieldName.equals(objFieldDBForcedName)) objFieldName = objFieldName+"_OBJ";
					fldDef.setForcedDBName(objFieldDBForcedName);
					fldDef.setName(objFieldName);
				}else{
					fldDef.setSQLType(focFld.getFabType());
				}
				fldDef.setLength(focFld.getSize());
				fldDef.setDecimals(focFld.getDecimals());
				fldDefList.add(fldDef);
				
				if(foreignTable == null){
					if(focFld instanceof FStringField){
						fldDef.setSQLType(FieldDefinition.SQL_TYPE_ID_CHAR_FIELD);
					}else if(focFld instanceof FEMailField){
						fldDef.setSQLType(FieldDefinition.SQL_TYPE_ID_EMAIL_FIELD);
					}else if(focFld instanceof FNumField){
						fldDef.setSQLType(FieldDefinition.SQL_TYPE_ID_DOUBLE);
					}else if(focFld instanceof FIntField){
						fldDef.setSQLType(FieldDefinition.SQL_TYPE_ID_INT);
					}else if(focFld instanceof FBoolField){
						fldDef.setSQLType(FieldDefinition.SQL_TYPE_ID_BOOLEAN);
					}
				}
			}
		}
	}
	
	//Code Generation
	//---------------
	
	public void writeCode_ServerCode(){
		CodeWriterSet cws = new CodeWriterSet(this);
		
		CodeWriter codeWriter = null;
		
//		codeWriter = cws.getCodeWriter_Const();
//		codeWriter.generateCode();
//		codeWriter = null;
//
//		codeWriter = cws.getCodeWriter_FocDesc();
//		codeWriter.generateCode();
//		codeWriter = null;
//		
//		codeWriter = cws.getCodeWriter_FocObject();
//		codeWriter.generateCode();
//		codeWriter = null;
		
		codeWriter = cws.getCodeWriter_PojoFocObject();
		codeWriter.generateCode();
		codeWriter = null;
		
		codeWriter = cws.getCodeWriter_XMLTable();
		codeWriter.generateCode();
		codeWriter = null;
		
		codeWriter = cws.getCodeWriter_XMLForm();
		codeWriter.generateCode();
		codeWriter = null;
		
		cws.dispose();
		cws = null;
	}
	
	public void writeCode_ClientCode(){
		CodeWriterSet cws = new CodeWriterSet(this);
		
		CodeWriter codeWriter = null;
		
		codeWriter = cws.getCodeWriter_Proxy();
		codeWriter.generateCode();
		codeWriter = null;
		
		codeWriter = cws.getCodeWriter_ProxyDesc();
		codeWriter.generateCode();
		codeWriter = null;

		codeWriter = cws.getCodeWriter_ProxyList();
		codeWriter.generateCode();
		codeWriter = null;

		codeWriter = cws.getCodeWriter_GuiProxyPanel();
		codeWriter.generateCode();
		codeWriter = null;

		codeWriter = cws.getCodeWriter_GuiProxyListPanel();
		codeWriter.generateCode();
		codeWriter = null;

//		codeWriter = cws.getCodeWriter_GuiProxyListTable();
//		codeWriter.generateCode();
//		codeWriter = null;

		cws.dispose();
		cws = null;
		
		/*
		codeWriter = new CodeWriter_ListClt(this);
		codeWriter.generateCode();
		codeWriter.dispose();
		codeWriter = null;

		codeWriter = new CodeWriter_Service(this);
		codeWriter.generateCode();
		codeWriter.dispose();
		codeWriter = null;

		codeWriter = new CodeWriter_ServiceAsync(this);
		codeWriter.generateCode();
		codeWriter.dispose();
		codeWriter = null;
		
		codeWriter = new CodeWriter_ServiceInstance(this);
		codeWriter.generateCode();
		codeWriter.dispose();
		codeWriter = null;
		
		codeWriter = new CodeWriter_ServiceImpl(this);
		codeWriter.generateCode();
		codeWriter.dispose();
		codeWriter = null;
		*/
	}
	
	public String getPackageName(boolean server){
		String pak = null;
		if(server){
			pak = getPackageName_ServerSide();
		}else{
			pak = getPackageName_WebClient();
		}
		return pak;
	}
	
	//-----------------------------------------
	// STATIC
	//-----------------------------------------	
	
	private static ArrayList<FocList> tableDefinitionList_Array = new ArrayList<FocList>();
	
	public static FocList tableDefinitionList_Get(int i){
		return tableDefinitionList_Array != null ? tableDefinitionList_Array.get(i) : null;
	}
	
	public static int tableDefinitionList_Size(){
		return tableDefinitionList_Array != null ? tableDefinitionList_Array.size() : 0;
	}
	
	public static void tableDefinitionList_Add(FocList list){
		if(tableDefinitionList_Array == null){
			tableDefinitionList_Array = new ArrayList<FocList>();
		}
		tableDefinitionList_Array.add(list);
	}
	
	public static TableDefinition getTableDefinitionForFocDesc(String storageName){
		FocDesc focDesc = Globals.getApp().getFocDescByName(storageName);
		return TableDefinition.getTableDefinitionForFocDesc(focDesc);
	}
	
	public static TableDefinition getTableDefinitionForFocDesc(FocDesc focDesc){
		return getTableDefinitionForFocDesc(focDesc, null);
	}
	
	@SuppressWarnings("unchecked")
	public static TableDefinition getTableDefinitionForFocDesc(FocDesc focDesc, Hashtable foreignKeys){
		TableDefinition tableDefinition = null;
		boolean found = false;
		if(focDesc != null){
			for(int i=0; i<tableDefinitionList_Size(); i++){
				FocList tableDefinitionList = tableDefinitionList_Get(i);
				tableDefinitionList.loadIfNotLoadedFromDB();
				Iterator<TableDefinition> iter = tableDefinitionList.focObjectIterator();
				while(iter != null && iter.hasNext() && !found){
					tableDefinition = iter.next();
					if(tableDefinition != null){
						if(tableDefinition.getName().equals(focDesc.getStorageName())){
							found = true;
						}
					}
				}
			}
			
			if(!found){
				found = true;
				FocList tableDefinitionList = TableDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
				tableDefinition = (TableDefinition) tableDefinitionList.newEmptyItem();
				tableDefinition.fillTableDefinitionFromFocDesc(focDesc, foreignKeys);
				//tableDefinition.setDBResident(false);
				tableDefinitionList.add(tableDefinition);
			}
		}
		return found ? tableDefinition : null;
	}
	
	@SuppressWarnings("unchecked")
	public static GuiBrowse getBrowseViewDefinitionForFocDescAndViewId(FocDesc focDesc, int viewId){
		TableDefinition tableDefinition = getTableDefinitionForFocDesc(focDesc);
		GuiBrowse aBrowseViewDefinition = null;
		//boolean found = false;
		if(tableDefinition != null){
			FocList browseViewDefinitionList = tableDefinition.getBrowseViewDefinitionList();
			if(browseViewDefinitionList != null){
				aBrowseViewDefinition = (GuiBrowse) browseViewDefinitionList.searchByRealReferenceOnly(viewId);
			}
			/*Iterator<GuiBrowse> iter = browseViewDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext() && !found){
				aBrowseViewDefinition = iter.next();
				if(aBrowseViewDefinition != null && aBrowseViewDefinition.getReference().getInteger() == viewId){
					found = true;
				}
			}*/
		}
		return aBrowseViewDefinition ;
	}
	
	public static GuiDetails getDetailsViewDefinitionForFocDescAndViewId(FocDesc focDesc, int viewId){
		TableDefinition tableDefinition = getTableDefinitionForFocDesc(focDesc);
		GuiDetails detailsViewDefinition = null;
		if(tableDefinition != null){
			detailsViewDefinition = tableDefinition.getDetailsViewDefinitionForViewId(viewId);
		}
		return detailsViewDefinition;
	}

	public String getRefFieldName() {
		return refFieldName;
	}

	public void setRefFieldName(String refFieldName) {
		this.refFieldName = refFieldName;
	}
	
}
