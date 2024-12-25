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
package com.foc.desc.popupMenu;

import java.io.File;

import com.foc.Globals;
import com.foc.gui.FGFileChooser;
import com.foc.property.FString;

public abstract class AbstractImportExportFile {

	private File file = null;
	
	public AbstractImportExportFile(){
		
	}
	
	public void dispose(){
		if(file != null){
			file = null;
		}
	}
	
	public void chooseFile(){
		file = null;
		FString       stringProp  = new FString(null, 1, "C:/");
		FGFileChooser fileChooser = new FGFileChooser(stringProp, Globals.getDisplayManager().getMainFrame(), "Choose a file to export to", FGFileChooser.MODE_FILES_AND_DIRECTORIES, null, null);
		fileChooser.popupFileChooser();
		fileChooser.dispose();
		
		String fullPath = stringProp.getString();
		if(fullPath != null && !fullPath.isEmpty()){
			file = new File(fullPath);
			if(file.isDirectory()) file = null;
		}
	}

}
