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
package b01.officeLink.excel;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.foc.Globals;

public class FocExcelSheet {

  private Sheet sheet       = null;

  public FocExcelSheet(Sheet sheet) {
    this.sheet = sheet;
  }

  public void dispose() {
    sheet = null;
  }

	public String getName() {
		return sheet != null ? sheet.getSheetName() : "";
	}

  public void set(int coord0, int coord1, Object obj){
		if(obj instanceof Double){
			set(coord0, coord1, (Double) obj);
		}else if(obj instanceof String){
			set(coord0, coord1, (String) obj);
		}
  }
  
  public void set(int coord0, int coord1, Double dVal) {
    Row row = sheet.getRow(coord0);
    Cell cell = null;

    if (row != null) {
      cell = row.getCell(coord1);
      if (cell == null)
        cell = row.createCell((short) coord1);
    }

    if (cell != null) {
      // cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
      cell.setCellValue(dVal);
    }
  }

  public void set(int coord0, int coord1, String sVal) {
    Row row = sheet.getRow(coord0);
    Cell cell = row != null ? row.getCell(coord1) : null;

    if (cell != null && sVal != null) {
      cell.setCellType(Cell.CELL_TYPE_STRING);
      if (sheet.getWorkbook() != null) {
        RichTextString textString = sheet.getWorkbook().getCreationHelper().createRichTextString(sVal);
        cell.setCellValue(textString);
      }
    }
  }

  public double getCellNum(int coord0, int coord1) {
    Row row = sheet.getRow(coord0);
    Cell cell = row != null ? row.getCell(coord1) : null;
    double dVal = 0;

    if (cell != null && (cell.getCellType() == Cell.CELL_TYPE_NUMERIC || cell.getCellType() == Cell.CELL_TYPE_FORMULA)) {
      dVal = cell.getNumericCellValue();
    }

    return dVal;
  }

  public Date getCellDate(int line, int col) {
    try {
	  	Row row = sheet.getRow(line);
	    Cell cell = row != null ? row.getCell(col) : null;
	    return cell != null ? cell.getDateCellValue() : null;
    } catch (Exception e) {
			Globals.logString("Could not read Date Value at Line=" + line + " col=" + col + " Sheet: " + getName());
			Globals.logException(e);
			return null;
    }
  }

	public int getCellType(int coord0, int coord1) {
		int type = 0;
		try {
			Row row = sheet.getRow(coord0);
			Cell cell = row != null ? row.getCell(coord1) : null;
			if (cell != null) {
				type = cell.getCellType();
			}
		} catch (Exception e) {
			Globals.logException(e);
			Globals.logString("Could Not get Value for cell [" + coord0 + "," + coord1 + "]");
		}
		return type;
	}

  public String getCellString(int coord0, int coord1) {
    String str = null;
    try{
	    Row row = sheet.getRow(coord0);
	    Cell cell = row != null ? row.getCell(coord1) : null;
	
	    if (cell != null) {
	      int type = cell.getCellType();
	
	      if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
	      	try{
		        double dVal = cell.getNumericCellValue();
		        if (Double.isNaN(dVal)) {
		          type = Cell.CELL_TYPE_STRING;
		        } else {
		          type = Cell.CELL_TYPE_NUMERIC;
		        }
	      	}catch(Exception e){
	      		type = Cell.CELL_TYPE_STRING;
	      	}
	      }
		      if (type == Cell.CELL_TYPE_STRING) {
	        str = cell.getRichStringCellValue().getString();
	      } else if (type == Cell.CELL_TYPE_NUMERIC) {
//	        str = String.valueOf((int) cell.getNumericCellValue());
	      	str = cell.getNumericCellValue()+"";
	      }
	    }
    }catch(Exception e){
    	Globals.logException(e);
    	Globals.logString("Could Not get Value for cell ["+coord0+","+coord1+"]");
    }
    return str;
  }

  public String getCellString(int coord[]) {
    return getCellString(coord[0], coord[1]);
  }
}
