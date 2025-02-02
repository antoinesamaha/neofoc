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
package b01.officeLink;

import java.io.File;

import javax.swing.JFileChooser;

import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.jasper.ExtensionFileFilter;

public class OfficeLink extends FocObject{
  public static final String FORMULA_START_IDENTIFIER = "{";
  public static final String FORMULA_END_IDENTIFIER   = "}";
  
  public static final String PREFIX_EXCEL_XP  = "xls";
  public static final String PREFIX_EXCEL_07  = "xlsx";
  public static final String PREFIX_WORD_XP = "doc";
  public static final String PREFIX_WORD_07 = "docx";
  
  public static final String defaultFilePath = "C:\\";
  public static final String defaultFileType = "";
  
  public OfficeLink(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
    
  protected String getSelectedPath(String defaultPath, String defaultFileName, String fileType, int fileGender){
    String outputPath = null;
    JFileChooser fch = null;
    
    if (defaultPath != null && defaultPath != ""){
      defaultPath = defaultPath.replaceAll("\\\\","/");
    }else{
      defaultPath = defaultFilePath;
    }
    fch = new JFileChooser(defaultPath);
    fch.setSelectedFile(new File(defaultFileName));
    
    if (fileType != null && fileType != ""){
      ExtensionFileFilter filter = new ExtensionFileFilter(fileType.substring(1, fileType.length()));
      fch.addChoosableFileFilter(filter);
    }else{
      fch.setAcceptAllFileFilterUsed(false);
    }
    
    if (fileGender == JFileChooser.DIRECTORIES_ONLY){
      fch.setDialogTitle("Select Directory");
    }else if (fileGender == JFileChooser.FILES_ONLY){
      fch.setDialogTitle("Select File");
    }
    fch.setFileSelectionMode(fileGender);
    int result = fch.showDialog(null, "OK");
    
    if (result == JFileChooser.CANCEL_OPTION ){
      return null;  
    }else{
      try{
        outputPath = fch.getSelectedFile().toString();
        outputPath = outputPath.replaceAll("\\\\","/");
      }catch( Exception e ){
      	Globals.logException(e);
      }
      return outputPath.toLowerCase();
    }
  }
}
