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
package com.foc.db;

import javax.swing.JFileChooser;

import com.foc.ConfigInfo;
import com.foc.jasper.ExtensionFileFilter;

@SuppressWarnings("serial")
public class DBBackupFileChooser extends JFileChooser{

	public DBBackupFileChooser(){
		super(ConfigInfo.getLogDir());
		setDialogTitle("Select Backup File");
		setFileSelectionMode(JFileChooser.FILES_ONLY);
		setFileFilter(new ExtensionFileFilter(DBManager.BACKUP_FILE_EXTENSION, "FOC Backup File"));
	}

	public String choose(){
		String fileName = null;

	  int result = showDialog(null, "OK");
	  
	  if (result != JFileChooser.CANCEL_OPTION ){
	  	fileName = getSelectedFile().toString();
	  	fileName = fileName.replaceAll("\\\\","/");
      if(!fileName.endsWith("."+DBManager.BACKUP_FILE_EXTENSION)){
      	fileName += "."+DBManager.BACKUP_FILE_EXTENSION;	
      }
	  }

	  return fileName;
	}
}
