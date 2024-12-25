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
package b01.officeLink.excel.synchronize;

import java.util.ArrayList;
import java.util.Iterator;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.progressBar.FGProgressDialog;
import com.foc.gui.progressBar.IProgressClass;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.FocListOrder;

import b01.officeLink.excel.synchronize.ExcelSyncFileReader.ColumnData;

public class ExcelSync extends FocObject implements IProgressClass{

	private ExcelSyncFileReader excelFile           = null;
	private FGProgressDialog    dialog              = null;
	private int                 totalLines          = 0;
	private boolean             requestInterruption = false;
	private boolean             interrupted         = false;
	private boolean             successful          = false;
	
  public ExcelSync(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public void dispose(){
  	super.dispose();
  	dispose_ExcelFile();
  	dispose_ProgressDialog();
  }
  
	private void dispose_ExcelFile(){
		if(excelFile != null){
			excelFile.dispose();
			excelFile = null;
		}
	}

	private void dispose_ProgressDialog(){
		if(dialog != null){
			dialog.dispose();
			dialog = null;
		}
	}

	public FGProgressDialog getProgressDialog(){
		if(dialog == null){
			dialog = new FGProgressDialog(this, totalLines-1);
		}
		return dialog;
	}

  public FocDesc getStorageDesc(){
  	return getPropertyDesc(ExcelSyncDesc.FLD_DESC);
  }

  public void setStorageDesc(FocDesc desc){
  	setPropertyDesc(ExcelSyncDesc.FLD_DESC, desc);
  }

  public String getFilePath(){
  	return getPropertyString(ExcelSyncDesc.FLD_FILE);
  }
  
  public FocList getMapList(){
  	FocList list = getPropertyList(ExcelSyncDesc.FLD_FIELDS_MAP_LIST);
  	if(list.getListOrder() != getMapOrderByPosition()){
  		list.setListOrder(getMapOrderByPosition());
  	}
  	return list;
  }
  
  //------------------------------------------------------------------------
  //------------------------------------------------------------------------
  // EXCEL READER
  //------------------------------------------------------------------------
  //------------------------------------------------------------------------
  
  public ExcelSyncFileReader getExcelFile(boolean create){
  	if(excelFile == null && create){
  		String fullPath = getFilePath();
  		if(fullPath != null && !fullPath.isEmpty()){
  			excelFile = new ExcelSyncFileReader(fullPath);
  		}
  	}
  	return excelFile;
  }

  public void resetExcelFile(){
  	dispose_ExcelFile();
  	ExcelSyncFileReader excelFile = getExcelFile(true);
  	
  	if(excelFile != null){
  		excelFile.resetHeader();
  		adjustMapListAccordingToFile();
  	}
  }

	public void adjustMapListAccordingToFile(){
		ExcelSyncFileReader reader = getExcelFile(true);
		boolean            error  = reader == null;
		
	  if(!error){
	  	FocList mapList = getMapList();
	  	if(mapList != null){
	  		mapList.reloadFromDB();
	  		Iterator iter = mapList.newSubjectIterator();
	  		while(iter != null && iter.hasNext()){
	  			ExcelColumn map = (ExcelColumn) iter.next();
	  			int pos = reader.getHeaderPosition(map.getFileHeader());
	  			if(pos < 0){
	  				if(!map.isCreated()) map.setDeleted(true);
	  				mapList.remove(map);
	  			}else{
	  				map.setPosition(pos+1);
	  			}
	  		}
	
	  		Iterator<ColumnData> hIter = reader.getHeaderIterator();
	  		while(hIter != null && hIter.hasNext()){
	  			ColumnData columnData = hIter.next();
	  			if(columnData != null){
	  				int pos = columnData.getIndex();
	  				ExcelColumn map = (ExcelColumn) mapList.searchByPropertyStringValue(ExcelColumnDesc.FLD_AUTOCAD_COL, columnData.getHeader(), true);    				 
	  				if(map == null){
	    				map = (ExcelColumn) mapList.newEmptyItem();
	    				map.setFileHeader(columnData.getHeader());
	  				}
	  				columnData.setFieldConfig(map);
	  				map.setPosition(pos);
	  			}
	  		}
	  		mapList.sort();
	  	}
	  }
	}

	public void importFile(final FocList targetlist){
		totalLines = importFile(targetlist, true, 0);
		Thread thread = new Thread(new Runnable(){
			public void run(){
				importFile(targetlist, false, totalLines);
			}
		});
		thread.start();
		getProgressDialog().popupDialog_NoThread();
		dispose_ProgressDialog();
	}
	
	public int importFile(FocList targetlist, boolean countingOnly, int nbrOfLines2){
		int nbrOfLines = nbrOfLines2;
		ExcelSyncFileReader fileReader = getExcelFile(true);
		adjustMapListAccordingToFile();
		
		int maxLines  = 5000;
		int lineCount = 0;
		
		fileReader.resetLine();
		fileReader.nextLine();
		
		ArrayList<FocObject> nodeSequence = new ArrayList<FocObject>();
		
		while(fileReader.getCurrentLineNumber() < maxLines && !requestInterruption){
			if(fileReader.isCurrentLineValid()){
				lineCount++;
				if(!countingOnly){
					long           ref       = fileReader.getReference();
					int            currLevel = fileReader.getLevel();
					FocListElement elem      = ref > 0 ? targetlist.searchElementByReference(ref) : null;
					FocObject      node      = elem != null ? elem.getFocObject() : null;
					
					if(node == null){
						FocObject parent = null;
						if(currLevel-2 < nodeSequence.size() && currLevel-2 >= 0){
							parent = nodeSequence.get(currLevel-2);
						//}else{
						//  Globals.getDisplayManager().popupMessage("Level Error at Excel line : "+fileReader.getCurrentLineNumber());
						}
						String name = fileReader.getName();
						node = targetlist.newEmptyItem();
						node.setFatherObject(parent);
					}
					
					if(node != null){
						while(nodeSequence.size() > currLevel){
							nodeSequence.remove(nodeSequence.size()-1);
						}
						if(currLevel-1 < nodeSequence.size()){
							nodeSequence.set(currLevel-1, node);
						}else{
							nodeSequence.add(node);
						}
						
						fileReader.fillObject(node);
						node.validate(true);
					}
				}
			}
			
			nbrOfLines = fileReader.nextLine();
			
			if(!countingOnly){
				getProgressDialog().setValue(nbrOfLines);
			}
		}
		
		if(!countingOnly){
			//fileReader.writeBkdnRefToExcel();
			//writeOutputFieldsToExcel(fileReader, tree);
			fileReader.dispose_Stream();
			fileReader.exportToExcel();
			
			if(requestInterruption){
				interrupted = true;
				successful  = false;
			}else{
				successful  = true;
			}
		}
		return lineCount;
	}
	
	/*
	public void writeOutputFieldsToExcel(final ExcelSyncFileReader fileReader, BkdnTree tree){
		if(tree != null){
			tree.scan(new TreeScanner<BkdnNode>() {
				@Override
				public void afterChildren(BkdnNode node) {
				}

				@Override
				public boolean beforChildren(BkdnNode node) {
					int lineNbr = fileReader.bkdnLineMap_getLineNbrForBkdnNode(node);
					if(lineNbr > 0){
						Bkdn bkdn = node.getObject();
						if(bkdn != null){
							fileReader.fillExcelFromBkdn(bkdn, lineNbr);
						}
					}
					return true;
				}
			});
		}
	}
	*/
	
  public static FocListOrder mapOrderByPosition = null;
  public static FocListOrder getMapOrderByPosition(){
  	if(mapOrderByPosition == null){
  		mapOrderByPosition = new FocListOrder(ExcelColumnDesc.FLD_POSITION);
  	}
  	return mapOrderByPosition;
  }

	@Override
	public String getInterruptedMessage() {
		return "Import Interrupted !";
	}

	@Override
	public String getRuntimeMessage() {
		return "Importing from excel...";
	}

	@Override
	public String getSuccessMessage() {
		return "Import completed successfully.";
	}

	@Override
	public boolean isInterrupted() {
		return interrupted;
	}

	@Override
	public boolean isSuccessful() {
		return successful;
	}

	@Override
	public void setRequestInterruption() {
		requestInterruption = true;
	}
}
