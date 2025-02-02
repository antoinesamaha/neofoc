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

import com.fab.model.table.FieldDefinition;
import com.foc.list.FocList;

public class CodeWriter_XMLForm extends CodeWriter {

	public CodeWriter_XMLForm(CodeWriterSet set){
		super(set);
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
		return XML_VIEW_FORM_SUFFIX;
	}
	
	@Override
	public boolean isServerSide() {
		return true;
	}
	
	@Override
	public String getFileExtention() {
		return "xml";
	}
	
	@Override
	public String getPackageName(boolean autoGen) {
		return super.getPackageName(autoGen)+".gui";
	}
	
	@Override	
	public void generateCode() {
		initFiles();
		
		CodeWriter_OneFile extWriter = getExternalFileWriter();
		if(extWriter != null && getTblDef() != null && getTblDef().getFieldDefinitionList() != null){
			extWriter.printCore("<VerticalLayout captionMargin=\"0\" margin=\"true\" width=\"100%\">\n");//Open Layout Tag
			extWriter.printCore("\t<ValidationSettings withApply=\"true\" withAttach=\"false\" withPrint=\"true\"/>\n\n");
			
			FocList tableFieldDefinitionList = getTblDef().getFieldDefinitionList();
			for(int i=0; i<tableFieldDefinitionList.size(); i++){
				FieldDefinition fieldDefinition = (FieldDefinition) tableFieldDefinitionList.getFocObject(i);
				if(fieldDefinition != null){
					extWriter.printCore("\t");
					extWriter.printCore("<GuiField");
					extWriter.printCore(" name=\"" + fieldDefinition.getName() + "\"");
					extWriter.printCore(" caption=\"" + fieldDefinition.getTitle() + "\"");
					extWriter.printCore(" width=\"-1px\"");
					extWriter.printCore(" height=\"-1px\"");
					extWriter.printCore(" />\n");
				}
			}
			
			extWriter.printCore("</VerticalLayout>");//Close Layout Tag
			
			extWriter.compile();
			closeFiles();
		}
	}
	
}
