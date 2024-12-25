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

import com.fab.model.table.TableDefinition;

public abstract class CodeWriter implements CodeWriterConstants {
	
	//public abstract String  rebuildFileFullName();
	public abstract boolean hasInternalFile();
	public abstract boolean hasExternalFile();
	public abstract String  getFileSuffix();
	public abstract void    generateCode();
	//public abstract String  getIntermediatePackage();
	public abstract boolean isServerSide();
	
//	private String internalFileFullName = null; 
//	private String externalFileFullName = null;

	private CodeWriter_OneFile internalCodeWriter = null;
	private CodeWriter_OneFile externalCodeWriter = null;
	
	//private TableDefinition tableDefinition     = null;
	private CodeWriterSet   codeWriterSet       = null;
		
	private boolean         externalFileIsNew   = false;
	
	private String fileExtention = "java";
	
	public CodeWriter(CodeWriterSet codeWriterSet){
		this.codeWriterSet = codeWriterSet;
	}
	
	public void dispose(){
		codeWriterSet = null;
		closeFiles();
	}

	public boolean isUseAutoGenDirectory(){
		return false;
	}
	
	public void closeFiles(){
		if(internalCodeWriter != null){
			if(isUseAutoGenDirectory()) internalCodeWriter.dispose();
			internalCodeWriter = null;
		}
		if(externalCodeWriter != null){
			externalCodeWriter.dispose();
			externalCodeWriter = null;
		}
	}

	public boolean isExternalFileIsNew() {
		return externalFileIsNew;
	}
	
	public void setExternalFileIsNew(boolean externalFileIsNew) {
		this.externalFileIsNew = externalFileIsNew;
	}
	
	public String getFileExtention() {
		return fileExtention;
	}

	public void setFileExtention(String fileExtention) {
		this.fileExtention = fileExtention;
	}

	/*
	public File getInternalFile() {
		return internalCodeWriter != null ? internalCodeWriter.getFile() : null;
	}

	public File getExternalFile() {
		return externalCodeWriter != null ? externalCodeWriter.getFile() : null;
	}
	*/

	public CodeWriterSet getCodeWriterSet() {
		return codeWriterSet;
	}

	public TableDefinition getTblDef() {
		return getCodeWriterSet() != null ? getCodeWriterSet().getTableDefinition() : null;
	}
	
	public String getPackageName(boolean autoGen){
		String packageName = getTblDef().getPackageName(isServerSide());
		if(autoGen){
			packageName = packageName + "." + PACKAGE_NAME_AUTO_GEN;
		}
		return packageName;
	}

	public String getClassName(){
		return getClassName(false);
	}
	
	public String getClassName(boolean autoGen){
		String prefix = getTblDef().getCW_ClassName_FocObject();

		String internalClassName = prefix+getFileSuffix(); 
			
		if(autoGen){
			internalClassName = CLASS_NAME_PREFIX_AUTO_GEN+internalClassName;
		}
		
		return internalClassName;
	}
	
	public String getClassName_Full(boolean autoGen){
		return getPackageName(autoGen)+"."+getClassName(autoGen);
	}
		
	private boolean newFileObjects(){
		if(hasExternalFile()){
			externalCodeWriter = new CodeWriter_OneFile(this, getPackageName(false), getClassName(false));
		}
		if(hasInternalFile()){
			if(isUseAutoGenDirectory()){
				internalCodeWriter = new CodeWriter_OneFile(this, getPackageName(true), getClassName(true));
			}else{
				internalCodeWriter = externalCodeWriter;
			}
		}
		externalCodeWriter.setFileExtention(getFileExtention());
		return false;
	}

	protected boolean initFiles(){
		boolean error = newFileObjects();
		
		//Start by deleting then recreating the internal file
		if(!error && internalCodeWriter != null && isUseAutoGenDirectory()){
			internalCodeWriter.initFile(true);
		}
		
		//If The External file does not exist create it
		if(!error){
			externalCodeWriter.initFile(false);
		}		
		
		return error;
	}

	public CodeWriter_OneFile getInternalFileWriter() {
		return internalCodeWriter;
	}
	
	public void setInternalFileWriter(CodeWriter_OneFile internalFileWriter) {
		this.internalCodeWriter = internalFileWriter;
	}

	public CodeWriter_OneFile getExternalFileWriter() {
		return externalCodeWriter;
	}
	
	public void setExternalFileWriter(CodeWriter_OneFile externalFileWriter) {
		this.externalCodeWriter = externalFileWriter;
	}
	
	//----------------------------------------
	// Delegate Methods
	//----------------------------------------
	
	public String getClassName_FocObject(){
		return getTblDef().getCW_ClassName_FocObject();
	}

	public String getClassName_FocDesc(){
		return getTblDef().getCW_ClassName_FocDesc();
	}

	public String getClassName_GwfObject(){
		return getTblDef().getCW_ClassName_GwfObject();
	}

	public String getClassName_ServiceInstance(){
		return getTblDef().getCW_ClassName_ServiceInstance();
	}

	public String getClassName_ServiceImplementation(){
		return getTblDef().getCW_ClassName_ServiceImplementation();
	}

	public String getClassName_WebService(){
		return getTblDef().getCW_ClassName_WebService();
	}

	public String getClassName_GwfList(){
		return getTblDef().getCW_ClassName_GwfList();
	}

	public String getClassName_WebServiceAsync(){
		return getTblDef().getCW_ClassName_WebServiceAsync();
	}

	public String getProjectFullPath(){
		return getTblDef().getCW_ProjectFullPath(isServerSide());
	}

	public String getPackageName_ClientRoot(){
		return getTblDef().getCW_PackageName_ClientRoot();
	}

	public String getPackageName_Client(){
		return getTblDef().getCW_PackageName_Client();
	}
	
	public String getPackageName_ServerRoot(){
		return getTblDef().getCW_PackageName_ServerRoot();
	}
	
	public String getPackageName_Server(){
		return getTblDef().getCW_PackageName_Server();
	}
	
	public String getServiceName(){
		return getTblDef().getCW_ServiceName();
	}
}
