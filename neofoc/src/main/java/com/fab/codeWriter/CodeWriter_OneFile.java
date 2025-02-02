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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;

public class CodeWriter_OneFile {

	private CodeWriter  						codeWriter    = null;
	private File        						file          = null;
	private String      						className     = null;
	private String      						packageName   = null;
	private PrintStream 						fileStream    = null;
	private HashMap<String, String> importsMap    = null;
	private StringBuffer            coreBuffer    = null;
	private String      						fileExtention = "java";
	
	public CodeWriter_OneFile(CodeWriter codeWriter, String packageName, String className){
		this.codeWriter  = codeWriter ;
		this.packageName = packageName;
		this.className   = className  ;
		
		coreBuffer = new StringBuffer();
		importsMap = new HashMap<String, String>();
	}
	
	public void dispose(){
		file          = null;
		codeWriter    = null;
		coreBuffer    = null;
		fileExtention = null;
		
		if(fileStream != null){
			fileStream.close();
			fileStream = null;
		}
		
		if(importsMap != null){
			importsMap.clear();
			importsMap = null;
		}
	}

	public boolean initFile(boolean deleteAndRecreate){
		boolean error = false;
		
		String fullFileName = packageName == null ? "" : packageName;
		fullFileName = fullFileName.replace('.', '/');
		fullFileName = codeWriter.getProjectFullPath() + fullFileName;
		File tempFile = new File(fullFileName); 
		tempFile.mkdirs();

//		fullFileName += "/" + className + ".java";
		String fileExtention = getFileExtention();
		if(fileExtention == null || fileExtention.isEmpty()){
			fileExtention = "java";
		}
		fullFileName += "/" + className + "." + fileExtention;
		
		file = new File(fullFileName);
		
		if(file != null){
			if(deleteAndRecreate){
				file.delete();
				try{
					error      = !file.createNewFile();
					fileStream = new PrintStream(file);
				}catch (IOException e){
					error = true;
					Globals.logException(e);
				}
			}else{
				if(file != null && !file.exists()){
					try{
						error      = !file.createNewFile();
						fileStream = new PrintStream(file); 
					}catch (IOException e){
						error = true;
						Globals.logException(e);
					}
				}
			}
		}

		return error; 
	}
	
	public CodeWriter getCodeWriter() {
		return codeWriter;
	}

	public void setCodeWriter(CodeWriter codeWriter) {
		this.codeWriter = codeWriter;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public PrintStream getFileStream() {
		return fileStream;
	}

	public void setFileStream(PrintStream fileStream) {
		this.fileStream = fileStream;
	}
	
	public void addImport(String imp){
		importsMap.put(imp, imp);
	}
		
	public void printCore(String str){
		coreBuffer.append(str);
	}
	
	public void compile(){
		if(getFileStream() != null){
			if(getPackageName() != null && getFileExtention() != null && getFileExtention().equals("java")){
				getFileStream().print("package "+getPackageName()+";\n\n");
			
				Iterator<String> iter = importsMap.values().iterator();
				while(iter != null && iter.hasNext()){
					getFileStream().print("import " + iter.next() + ";\n");
				}
				getFileStream().print("\n");
			}
			
			getFileStream().print(coreBuffer);
		}
	}

	public String getFileExtention() {
		return fileExtention;
	}

	public void setFileExtention(String fileExtention) {
		this.fileExtention = fileExtention;
	}
}
