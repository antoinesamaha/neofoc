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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.FGButton;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FPopupMenu;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

import b01.officeLink.excel.ExcelRefillerInterface;
import b01.officeLink.excel.FocExcelDocument;
  
@SuppressWarnings("serial")
public class OfficeLinkGuiBrowsePanel extends FListPanel{

  private FocList       officeLinkList  = null;
  private FocObject     object          = null;
  private String        configDocName   = null;
  private ExcelRefillerInterface excelRefiller  = null;
  //private String newDocName = null;
  
  public OfficeLinkGuiBrowsePanel(FocList list, int viewID, boolean modified, String descStorageName, FocObject obj, String docName, ExcelRefillerInterface excelRefiller){
    FocDesc desc = OfficeLinkDesc.getInstance();
    setFrameTitle("Export Settings");
    //FPanel buttonsPanel = new FPanel();
    FPanel   totalsPanel  = getTotalsPanel();
    FGButton exportButton = new FGButton("Export");
    if(desc != null){
      if(list == null){
        list = OfficeLinkDesc.getList(/*FocList.FORCE_RELOAD,*/ descStorageName);
      }
      if(list != null){
        list.setDirectImpactOnDatabase(false);
        list.setDirectlyEditable(false);
        
        setFocList(list);
        this.excelRefiller = excelRefiller;
        officeLinkList     = list;
        object             = obj;
        configDocName      = docName;

        FTableView tableView = getTableView();       
        tableView.addColumn(desc, OfficeLinkDesc.FLD_DESCRIPTION, 50, true);
        tableView.addColumn(desc, OfficeLinkDesc.FLD_FILE_INPUT_PATH, false);
        tableView.addColumn(desc, OfficeLinkDesc.FLD_FILE_OUTPUT_PATH, false);
        
        //tableView.addColumn(desc, OfficeLinkDesc.FLD_DESC_STORAGE_NAME, false);
      
        /*FGButton addButton = new FGButton("+");
        addButton.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e) {
            OfficeLink officeLink = null;
            officeLink = (OfficeLink) officeLinkList.newEmptyItem();
            String filePath = officeLink.getSelectedPath(JFileChooser.FILES_ONLY);
            if (filePath != null){
              officeLink.setPropertyString(OfficeLinkDesc.FLD_DESC_STORAGE_NAME, storageName);  
              officeLink.setPropertyString(OfficeLinkDesc.FLD_FILE_FULL_PATH, filePath);
              officeLinkList.add(officeLink);
            }else{
              officeLinkList.cancel();
            }
          }
          
        });

        FGButton removeButton = new FGButton("-");
        removeButton.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e) {
            FocObject obj = officeLinkList.getSelectedObject();
            officeLinkList.remove(obj);
            obj.setDeleted(true);
            getDeleteAction().focActionPerformed(null);
          }
        });
        */    
        
        exportButton.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e) {
            try{
              FocObject fObject = officeLinkList.getSelectedObject();
              String sourceFile = fObject.getPropertyString(OfficeLinkDesc.FLD_FILE_INPUT_PATH);
              String outputFilePath = fObject.getPropertyString(OfficeLinkDesc.FLD_FILE_OUTPUT_PATH);
              /*
              String outputFilePath = null;
              String newDocName = fObject.getPropertyString(OfficeLinkDesc.FLD_DESCRIPTION)+ configDocName;
              //defaultOutputPath = defaultOutputPath + "/"+ newDocName+ getDocPrefix(sourceFile);
              if(defaultOutputPath != "" && defaultOutputPath != null){
                outputFilePath = ((OfficeLink) fObject).getSelectedPath(defaultOutputPath, newDocName, getDocPrefix(sourceFile) ,JFileChooser.FILES_ONLY);
              }else{
                outputFilePath = ((OfficeLink) fObject).getSelectedPath("", newDocName, getDocPrefix(sourceFile), JFileChooser.FILES_ONLY);
              }
              */
              
              /*String last = analysePath(destinationFilePath);
              StringBuffer buffer = new StringBuffer();
              boolean isFile = false;
              for(int j=0; j< last.length(); j++){
                buffer.append(last.charAt(j));
                if (last.charAt(j)=='.'){
                  isFile = true;
                  break;
                }
              }
              
              if (isFile || (newDocName != null && newDocName!= "")){
                newDocName = buffer.toString();
              }else{
                newDocName = fObject.getPropertyString(OfficeLinkDesc.FLD_DESCRIPTION)+ " "+ configDocName;
              }*/
              
              if(outputFilePath != null){
                if(sourceFile.endsWith(OfficeLink.PREFIX_WORD_XP) || sourceFile.endsWith(OfficeLink.PREFIX_WORD_07)){
                  FocWordDocument doc;
                  doc = new FocWordDocument(Globals.getInputStream(sourceFile));
                  doc.export(object);
                  OutputStream outputStream = new FileOutputStream(outputFilePath+getDocPrefix(sourceFile));
                  doc.write(outputStream);
                  outputStream.flush();
                  outputStream.close();
                }else if (sourceFile.endsWith(OfficeLink.PREFIX_EXCEL_XP) || sourceFile.endsWith(OfficeLink.PREFIX_EXCEL_07)){
                  FocExcelDocument doc = new FocExcelDocument(Globals.getInputStream(sourceFile), getExcelRefiller());
                  doc.export(object);
                  FileOutputStream outputStream = new FileOutputStream(outputFilePath+/*"/"+newDocName+*/getDocPrefix(sourceFile));
                  doc.write(outputStream);
                  outputStream.flush();
                  outputStream.close();
                }
                File file = new File(outputFilePath+getDocPrefix(sourceFile));
                if (file.exists()){
                  Globals.getDisplayManager().popupMessage("File Successfully Created");
                }else{
                  Globals.getDisplayManager().popupMessage("An Error Occured While Creating File");
                }
              }             
            } catch (FileNotFoundException e1) {
              e1.printStackTrace();
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          }
        });
        construct(); 
        FPopupMenu popupMenu = getTable().getPopupMenu();
        JMenuItem item = new JMenuItem("Set Destination");
         item.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e) {
            String newDocName = null;
            String description = null;
            OfficeLink officeLink = (OfficeLink) officeLinkList.getSelectedObject();
            if (officeLink != null){
              description = officeLink.getPropertyString(OfficeLinkDesc.FLD_DESCRIPTION);  
              newDocName = description+configDocName;  
              String filePath = officeLink.getSelectedPath(OfficeLink.defaultFilePath, newDocName, OfficeLink.defaultFileType, JFileChooser.DIRECTORIES_ONLY);
              if (filePath != null){
                officeLink.setPropertyString(OfficeLinkDesc.FLD_FILE_OUTPUT_PATH, filePath);  
              }
            }else{
              Globals.getDisplayManager().popupMessage("No row is selected");
            }
          }
        });
        popupMenu.add(item);

        requestFocusOnCurrentItem();
        FValidationPanel savePanel = showValidationPanel(true);
        savePanel.addSubject(list);
      }
      if(object != null && modified && (descStorageName != null && descStorageName != "")){
        totalsPanel.add(exportButton, 0, 0);
        showEditButton(true);
      }else{
        showAddButton(false);
        showRemoveButton(false);
        showEditButton(false);
      }
    }
  }
  
  public OfficeLinkGuiBrowsePanel(FocList list, int viewID, boolean modified){
    this(list, viewID, modified, "", null, "", null);
  }

  public OfficeLinkGuiBrowsePanel(FocList list, int viewID){
    this(list, viewID, false);
  }

  /*
  @Override
  public FocObject newEmptyItem() {
    OfficeLink officeLink = null;
    officeLink = (OfficeLink) officeLinkList.newEmptyItem();
    String newDocName = officeLink.getPropertyString(OfficeLinkDesc.FLD_DESCRIPTION)+" "+configDocName;
    String filePath = officeLink.getSelectedPath(OfficeLink.defaultFilePath, newDocName, OfficeLink.defaultFileType, JFileChooser.FILES_ONLY);
    if (filePath != null){
      if (filePath.endsWith(OfficeLink.PREFIX_EXCEL_07)|| filePath.endsWith(OfficeLink.PREFIX_EXCEL_XP)||filePath.endsWith(OfficeLink.PREFIX_WORD_07)||filePath.endsWith(OfficeLink.PREFIX_WORD_XP)){
        officeLink.setPropertyString(OfficeLinkDesc.FLD_DESC_STORAGE_NAME, storageName);  
        officeLink.setPropertyString(OfficeLinkDesc.FLD_FILE_INPUT_PATH, filePath);
        //officeLink.setPropertyString(OfficeLinkDesc.FLD_FILE_OUTPUT_PATH, filePath);
        //officeLink.setPropertyString(OfficeLinkDesc.FLD_DESCRIPTION, filePath);
        officeLinkList.add(officeLink);  
      }else{
        Globals.getDisplayManager().popupMessage("File Must be Word or Excel document");
        officeLinkList.cancel();
      }
    }else{
      officeLinkList.cancel();
    }
    return officeLink;
  }
  */
  
  private String getDocPrefix(String sourcePath){
    String prefix = null;
    if (sourcePath.endsWith(OfficeLink.PREFIX_EXCEL_07)){
      prefix = OfficeLink.PREFIX_EXCEL_07;
    }else if (sourcePath.endsWith(OfficeLink.PREFIX_EXCEL_XP)){
      prefix = OfficeLink.PREFIX_EXCEL_XP;
    }else if (sourcePath.endsWith(OfficeLink.PREFIX_WORD_07)){
      prefix = OfficeLink.PREFIX_WORD_07;
    }else if (sourcePath.endsWith(OfficeLink.PREFIX_WORD_XP)){
      prefix = OfficeLink.PREFIX_WORD_XP;
    }
    prefix = "."+prefix;
    return prefix;
  }
    
  public void dispose(){
    officeLinkList = null;
    object = null;
    configDocName = null;
  }

	public ExcelRefillerInterface getExcelRefiller() {
		return excelRefiller;
	}
}
