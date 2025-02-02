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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.property.FProperty;
import com.foc.property.PropertyFocObjectLocator;

import b01.officeLink.OfficeLink;

public class FocExcelDocument{

	private ExcelRefillerInterface excelRefiller = null;
	private Workbook               workbook  = null;
	public FocExcelDocument(InputStream inputStream, ExcelRefillerInterface excelRefillerInterface) throws IOException {
		
	  try {
      workbook = WorkbookFactory.create(inputStream);
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    }
		this.excelRefiller = excelRefillerInterface;
	}
	
	public void dispose(){
		excelRefiller = null;
	}
	
	public void export(FocObject object) {
		if(excelRefiller != null){
			excelRefiller.export(this, object);
		}else{
			exportLocally(object);
		}
	}

	public void exportLocally(FocObject object) {
		try{
			Sheet sheet = workbook.getSheetAt(0);
			// Iterate over each row in the sheet
			Iterator rows = sheet.rowIterator();
			while (rows.hasNext()){
				Row row = (Row) rows.next();
				System.out.println("Row #" + row.getRowNum());
				// Iterate over each cell in the row and print out the cell's content
				Iterator cells = row.cellIterator();
				while (cells.hasNext()){
					Cell cell = (Cell) cells.next();
					System.out.println("Cell #" + cell.getColumnIndex());
					/* System.out.println(String.valueOf(cell.getRichStringCellValue())); */
					String str = null;
					try{
						str = String.valueOf(cell.getRichStringCellValue());
					}catch (Exception e){
						Globals.logExceptionWithoutPopup(e);
						str = "";
					}
					String result = analyseContent(str, object);
					if(result != null){
					  
					  if(getWorkbook() != null){
					    cell.setCellValue(getWorkbook().getCreationHelper().createRichTextString(result));
					  }
					}
					/*
					 * switch (cell.getCellType()) { case HSSFCell.CELL_TYPE_NUMERIC:
					 * System.out.println(cell.getNumericCellValue()); String result =
					 * analyseContent(String.valueOf(cell.getNumericCellValue()), object);
					 * if (result != null){ cell.setCellValue(Double.valueOf(result)); }
					 * break; case HSSFCell.CELL_TYPE_STRING:
					 * System.out.println(cell.getRichStringCellValue()); result =
					 * analyseContent(String.valueOf(cell.getRichStringCellValue()),
					 * object); if (result != null){ cell.setCellValue(new
					 * HSSFRichTextString(result)); } break; default:
					 * System.out.println("unsuported cell type"); break; }
					 */}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private String analyseContent(String cellValue, FocObject object) {
		int    startIndex    = 0;
		int    endIndex      = 0;
		String formulaResult = null;
		
		while(startIndex >= 0 && endIndex >= 0){
			startIndex = cellValue.indexOf(OfficeLink.FORMULA_START_IDENTIFIER, endIndex);
			if(startIndex >= 0){
				endIndex = cellValue.indexOf(OfficeLink.FORMULA_END_IDENTIFIER, startIndex);
				String formulaString = cellValue.substring(startIndex + OfficeLink.FORMULA_START_IDENTIFIER.length(), endIndex);
				/*
				 * Formula formula = new Formula(object.getThisFocDesc(),
				 * FField.NO_FIELD_ID, formulaString);
				 * formula.setCurrentFocObject(object); formulaResult =
				 * String.valueOf(formula.compute());
				 */
				/*
				 * Formula formula = new Formula(formulaString); FieldFormulaContext
				 * context = new FieldFormulaContext(formula, null,
				 * object.getThisFocDesc()); context.setCurrentFocObject(object);
				 * formulaResult = String.valueOf(context.evaluateFormula());
				 */

				PropertyFocObjectLocator propertyFocObjectLocator = new PropertyFocObjectLocator();
				propertyFocObjectLocator.parsePath(formulaString, object.getThisFocDesc(), object, null);
				FProperty property = propertyFocObjectLocator.getLocatedProperty();

				if(property != null && property.getObject() != null){
					formulaResult = property.getObject().toString();
				}
			}
		}
		return formulaResult;
	}

	public ExcelRefillerInterface getExcelRefiller() {
		return excelRefiller;
	}

	public void setExcelRefiller(ExcelRefillerInterface excelRefiller) {
		this.excelRefiller = excelRefiller;
	}

  public Workbook getWorkbook() {
    return workbook;
  }

  public void setWorkbook(Workbook workbook) {
    this.workbook = workbook;
  }
  
  public Sheet getSheetAt(int i){
    Sheet sheet = null;
    if(workbook != null && i>=0){
      sheet = workbook.getSheetAt(i);
    }
    return sheet;
  }
  
  public void write(OutputStream output){
    if(getWorkbook() != null){
      try {
        getWorkbook().write(output);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
