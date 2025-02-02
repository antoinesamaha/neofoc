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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.property.FDate;
import com.foc.property.FProperty;
import com.foc.util.FocMath;

import b01.officeLink.OfficeLink;
import b01.officeLink.excel.FocExcelSheet;

public class ExcelSyncFileReader {

	private File                         file         = null;
	private InputStream                  inputStream  = null;
	private Workbook                     workbook     = null;
	private FocExcelSheet                sheet        = null;
	private HashMap<Integer, ColumnData> columnMap    = null;
	//private HashMap<BkdnNode, Integer>   bkdn2LineMap = null;
	private boolean                      error        = false;
	private int                          currentLine  = 0;
	
	private ColumnData                   columnData_Level = null;
	private ColumnData                   columnData_REF   = null;
	private ColumnData                   columnData_NAME  = null;
  
  public ExcelSyncFileReader(String fullFileName){
		File file = new File(fullFileName);
		openWorkbook(file);
		openSheet();
  }
  
  public void dispose(){
		sheet    = null;
		workbook = null;

    dispose_Stream();
    dispose_ColumnArray();
    //bkdnLineMap_dispose();
  }
  
  private void dispose_ColumnArray(){
  	if(columnMap != null){
  		Iterator<ColumnData> iter = columnMap.values().iterator();
  		while(iter != null && iter.hasNext()){
  			ColumnData columnData = iter.next();
  			columnData.dispose();
  		}
  		columnMap.clear();
  		columnMap = null;
  	}
  }
  
	public void dispose_Stream(){
		if(inputStream != null){
			try{
				inputStream.close();
			}catch (IOException e){
				Globals.logException(e);
			}
			inputStream = null;
		}
	}
	
	/*
	private void bkdnLineMap_dispose(){
    if(bkdn2LineMap != null){
    	bkdn2LineMap.clear();
    	bkdn2LineMap = null;
    }
	}
	*/

	public File getFile(){
		return file;
	}
	
	/*
	private HashMap<BkdnNode, Integer> bkdnLineMap_getMap(){
		if(bkdn2LineMap == null){
			bkdn2LineMap = new HashMap<BkdnNode, Integer>();
		}
		return bkdn2LineMap;
	}

	public int bkdnLineMap_getLineNbrForBkdnNode(BkdnNode node){
		int lineNbr = -1;
		HashMap<BkdnNode, Integer> map = bkdnLineMap_getMap();
		if(map != null && node != null){
			Integer lineNbrInteger = map.get(node);
			if(lineNbrInteger != null) lineNbr = lineNbrInteger.intValue(); 
		}
		return lineNbr;
	}

	public void bkdnLineMap_put(BkdnNode node, int lineNbr){
		HashMap<BkdnNode, Integer> map = bkdnLineMap_getMap();
		if(map != null){
			map.put(node, lineNbr);
		}
	}
	*/
	
	private void openWorkbook(File file){
		this.file = file;
		error = !file.getAbsolutePath().endsWith(OfficeLink.PREFIX_EXCEL_XP) && !file.getAbsolutePath().endsWith(OfficeLink.PREFIX_EXCEL_07);
		if(error){
			Globals.logString("This is not an Excel extension file");
		}
		if(!error){
			error = !file.exists();
			Globals.logString("File Does not Exist!");
		}
		if(!error){
			inputStream  = Globals.getInputStream(file.getAbsolutePath());
			try{
				workbook = WorkbookFactory.create(inputStream);
			}catch (IOException e){
				Globals.logExceptionWithoutPopup(e);
				error = true;
			} catch (InvalidFormatException e) {
        e.printStackTrace();
      }
			error = error || workbook == null;
		}
	}			
	
	private boolean openSheet(){
		sheet = null;
		if(!error){
			Sheet xlSheet = workbook.getSheetAt(0);
			sheet = new FocExcelSheet(xlSheet);
		}
		return sheet != null;
	}
	
	public void resetHeader(){
		dispose_ColumnArray();
		columnMap = new HashMap<Integer, ColumnData>();
		if(sheet != null){
			for(int i=0; i<100; i++){
				String val = sheet.getCellString(0, i);
				if(val != null && !val.isEmpty()){
					ColumnData data = new ColumnData();
					data.setIndex(i);
					data.setHeader(val);
					columnMap.put(i, data);
				}
			}
		}
	}
	
	public int getHeaderPosition(String header){
		int pos = -1;
		Iterator<ColumnData> iter = columnMap.values().iterator();
		while(iter != null && iter.hasNext()){
			ColumnData data = iter.next();
			if(data.getHeader().equals(header)){
				pos = data.getIndex();
			}
		}
		return pos;
	}
	
	public Iterator<ColumnData> getHeaderIterator(){
		return columnMap.values().iterator();
	}

	public void resetLine(){
		currentLine = 0;
	}
	
	public int nextLine(){
		currentLine++;
		return currentLine;
	}
	
	public int getCurrentLineNumber(){
		return currentLine;
	}

	public ColumnData getColumnData_NAME(){
		if(columnData_NAME == null){
			Iterator<ColumnData> headerIter = getHeaderIterator();
			while(headerIter != null && headerIter.hasNext()){
				ColumnData colData = headerIter.next();
				if(colData.getFieldConfig().getC3Header().equals("NAME")){
					columnData_NAME = colData;
				}
			}
		}
		return columnData_NAME;
	}

	public ColumnData getColumnData_LEVEL(){
		if(columnData_Level == null){
			Iterator<ColumnData> headerIter = getHeaderIterator();
			while(headerIter != null && headerIter.hasNext()){
				ColumnData colData = headerIter.next();
				if(colData.getFieldConfig().getFieldMode() == ExcelColumnDesc.MODE_LEVEL){
					columnData_Level = colData;
				}
			}
		}
		return columnData_Level;
	}

	public ColumnData getColumnData_REF(){
		if(columnData_REF == null){
			Iterator<ColumnData> headerIter = getHeaderIterator();
			while(headerIter != null && headerIter.hasNext()){
				ColumnData colData = headerIter.next();
				if(colData.getFieldConfig().getFieldMode() == ExcelColumnDesc.MODE_REF){
					columnData_REF = colData;
				}
			}
		}
		return columnData_REF;
	}
	
	public long getReference(){
		long ref = 0;
		ColumnData data = getColumnData_REF();
		if(data != null){
			String value = sheet.getCellString(currentLine, data.getIndex());
			ref = FocMath.parseLong(value);
		}
		return ref;
	}

	public int getLevel(){
		int level = 0;
		ColumnData data = getColumnData_LEVEL();
		if(data != null){
			String value = sheet.getCellString(currentLine, data.getIndex());
			level = FocMath.parseInteger(value);
		}
		return level;
	}

	public String getName(){
		String value = null;
		ColumnData data = getColumnData_NAME();
		if(data != null){
			value = sheet.getCellString(currentLine, data.getIndex());
		}
		return value;
	}

	public void fillObject(FocObject bkdn){
		if(currentLine >= 0){

			Iterator<ColumnData> iter = columnMap.values().iterator();
			while(iter != null && iter.hasNext()){
				ColumnData data = iter.next();
				if(data != null && data.getIndex() >= 0){
					ExcelColumn fieldMap = data.getFieldConfig();
					String       value    = sheet.getCellString(currentLine, data.getIndex());
					value = value != null ? value.trim() : null;
					if(fieldMap != null && data.getIndex()>=0 && (fieldMap.getFieldMode() == ExcelColumnDesc.MODE_EXCEL_C3 || fieldMap.getFieldMode() == ExcelColumnDesc.MODE_BOTH)){
						String fieldName = fieldMap.getC3Header();
						if(fieldName != null && !fieldName.isEmpty()){
							FField fld = bkdn.getThisFocDesc().getFieldByName(fieldName);
							if(fld != null){
								FProperty prop = bkdn.getFocProperty(fld.getID());
								
								if(prop instanceof FDate){
									Date date = sheet.getCellDate(currentLine, data.getIndex());
									((FDate)prop).setDate(new java.sql.Date(date.getTime()));
								}else{
									/*
									if(fld.getID() == bkdn.getThisFocDesc().FLD_DESCRIPTION){
										int enterIndex = value.indexOf('\n');
										if(enterIndex < 0){//If there is no enter we split the string into chunks
											enterIndex = 0;
											int spaceIndex = value.indexOf(' ');
											while(spaceIndex >= 0 && spaceIndex+1 < value.length()){
												if(spaceIndex - enterIndex > 70){
													String newValue = value.substring(0, spaceIndex)+"\n"+value.substring(spaceIndex);
													value = newValue;
													enterIndex = spaceIndex;
												}
												spaceIndex = value.indexOf(' ', spaceIndex+1);
											}
										}
									}
									*/
									prop.setString(value);
								}
							}
						}
					}
				}
			}
			
			/*
			if(!nameFieldVisited){
				String descrip = bkdn.getDescriptionBlob();
				if(descrip != null && !descrip.isEmpty()){
					int indexOfEnter = descrip.indexOf('\n');
					if(indexOfEnter > 0){
						descrip = descrip.substring(0, indexOfEnter);
					}
					if(descrip.length() > BkdnDesc.LEN_NAME){
						descrip = descrip.substring(0, BkdnDesc.LEN_NAME);
					}
					bkdn.setName(descrip);
				}
			}
			*/
		}
	}
	
	/*
	public void fillExcelFromBkdn(Bkdn bkdn, int lineNbr){
		if(bkdn != null){
			Iterator<ColumnData> iter = getHeaderIterator();
			while(iter != null && iter.hasNext()){
				ColumnData data = iter.next();
				if(data != null && data.getIndex() >= 0){
					ExcelColumn importMap = data.getFieldConfig();
					if(importMap.getFieldMode() == ExcelColumnDesc.MODE_C3_EXCEL || importMap.getFieldMode() == ExcelColumnDesc.MODE_BOTH){ 
						String fieldName = importMap.getC3Header();
						if(fieldName != null && !fieldName.isEmpty()){
							FField fld = BkdnDesc.getInstance().getFieldByName(fieldName);
							if(fld != null){
								if(fld instanceof FNumField){
									sheet.set(lineNbr, data.getIndex(), bkdn.getPropertyDouble(importMap.getC3Header()));
								}else if(fld instanceof FIntField){
									sheet.set(lineNbr, data.getIndex(), (double)bkdn.getPropertyInteger(importMap.getC3Header()));
								}else{
									sheet.set(lineNbr, data.getIndex(), bkdn.getPropertyString(importMap.getC3Header()));
								}
							}
						}
					}
				}
			}
		}	
	}
	*/
	
	public boolean isCurrentLineValid(){
		boolean valid = currentLine >= 0;
		Iterator<ColumnData> iter = columnMap.values().iterator();
		while(iter != null && iter.hasNext() && valid){
			ColumnData data = iter.next();
			if(data != null){
				ExcelColumn fieldMap = data.getFieldConfig();
				if(fieldMap.isMandatory()){
					String value = sheet.getCellString(currentLine, data.getIndex());
					valid = value != null && !value.isEmpty();
					if(valid && data.getFieldConfig().getFieldMode() == ExcelColumnDesc.MODE_LEVEL){
						int v = FocMath.parseInteger(value);
						valid = v > 0;
					}
				}
			}
		}
		return valid;
	}
	
	/*
	public void writeBkdnRefToExcel(){
		if(bkdn2LineMap != null && sheet != null){
			ColumnData data = getColumnData_REF();
			if(data != null && data.getIndex() >= 0){
				Iterator<BkdnNode> iter = bkdn2LineMap.keySet().iterator();
				while(iter != null && iter.hasNext()){
					BkdnNode bkdnNode = iter.next();
					int      lineNbr  = bkdn2LineMap.get(bkdnNode);

					sheet.set(lineNbr, data.getIndex(), bkdnNode.getObject().getReference().getInteger());
				}
			}
		}
	}
	*/

	public void exportToExcel(){
		try{
//			String path = file.getAbsolutePath();
//			String sub1 = path.substring(0, path.length()-4);
//			sub1 += "_bis.xls";
			FileOutputStream outputStream = new FileOutputStream(file);
			workbook.write(outputStream);
			outputStream.flush();
			outputStream.close();
		}catch (Exception e){
			Globals.logException(e);
		}
	}
	
	public class ColumnData {
		private int          index       = -1;
		private String       header      = "";
		private ExcelColumn fieldConfig = null;
		
		public void dispose() {
		}
		
		public int getIndex() {
			return index;
		}
		
		public void setIndex(int index) {
			this.index = index;
		}
		
		public String getHeader() {
			return header;
		}

		public void setHeader(String header) {
			this.header = header;
		}

		public ExcelColumn getFieldConfig() {
			return fieldConfig;
		}

		public void setFieldConfig(ExcelColumn fieldConfig) {
			this.fieldConfig = fieldConfig;
		}
	}
}
