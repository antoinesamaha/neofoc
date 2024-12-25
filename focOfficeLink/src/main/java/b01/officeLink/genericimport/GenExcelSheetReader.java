package b01.officeLink.genericimport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;

import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.property.FDate;
import com.foc.util.Utils;

import b01.officeLink.excel.FocExcelSheet;

public class GenExcelSheetReader extends ExcelSheetReader {

	private ArrayList<GenExcelLine> itemArray ;
	
	private GenExcelReader reader = null;
	private String endOfSheetField = null;
	
	public GenExcelSheetReader(GenExcelReader reader, FocExcelSheet excelSheet, String endOfSheetField) {
		super(excelSheet);
		this.reader = reader; 
		this.endOfSheetField = endOfSheetField;
	}
	
	public void dispose() {
		if(itemArray != null) {
			for(int i=0; i<itemArray.size(); i++) {
				GenExcelLine line = itemArray.get(i);
				if (line != null) {
					line.dispose();
				}
			}
			itemArray.clear();
			itemArray = null;
		}
		reader = null;
	}

	public boolean isDateField(String fieldName) {
		return reader != null ? reader.isDateField(fieldName) : null;
	}
	
 	public String checkMandatoryHeaders(ArrayList<String> mandatoryHeaders) {
 		String error = null;
 		
 		if(mandatoryHeaders != null) {
			HashMap<String, Integer> columnHeaderMap = getColumnHeadersMap();
			if (columnHeaderMap != null) {
				for(int i=0; i<mandatoryHeaders.size(); i++) {
					String header = mandatoryHeaders.get(i);
					if (columnHeaderMap.get(header) == null) {
						if(error == null) error = "These mandatory headers are missing: ";
						error += header+" ";
					}
				}
			}
 		}
		
		return error;
	}
	
	@Override
 	public Boolean checkLine(int line) {
		Boolean valid = true;
		if (getColumnHeadersMap() != null) {
			String ld = readCell(line, endOfSheetField);
			if (!Utils.isStringEmpty(ld)) {
				valid = true;
			} else {
				valid = false;
			}
		}
		return valid;
	}

	@Override
	public void readLine(int line) {
		if (getColumnHeadersMap() != null && checkLine(line)) {
			
			Iterator iter = getColumnHeadersMap().keySet().iterator();

			GenExcelLine user = new GenExcelLine(reader);

			while (iter != null && iter.hasNext()) {
				String fieldName = (String) iter.next();
				int col = getColumnHeadersMap().get(fieldName);
				if (isNotEmpty(line, col)) {
					int type = getExcelSheet().getCellType(line, col);
					
					String value = reader.getValueFromCell(getExcelSheet(), fieldName, type, line, col);
					if (value == null) {
						if(isDateField(fieldName)) {
							Globals.logString(" Reading cell : line:"+line+" col:"+col);
							Date date = getExcelSheet().getCellDate(line, col);
							value = FDate.convertDateToDisplayString(date);
							//value = getExcelSheet().getCellDate(line, col);
						} else {
							value = getExcelSheet().getCellString(line, col).replace("null", "").trim();
							if(type == Cell.CELL_TYPE_NUMERIC) {
								double num = getExcelSheet().getCellNum(line, col);
								DecimalFormat df = new DecimalFormat("#");
								value = df.format(num); 
							}
						}
					}
					
					user.put(fieldName, value);
				}
			}
			
			if(itemArray == null) {
				itemArray = new ArrayList<GenExcelLine>();
			}
					
			if (itemArray != null) {
				String currentErrorMessage = user.isValid();
				if(currentErrorMessage != null) {
					String errorMessage = this.getErrorMessage();
					if (!errorMessage.contains(currentErrorMessage)) {
						setErrorMessage(errorMessage + " " + currentErrorMessage);
					}
				} else {
					itemArray.add(user);
				}
			}
		}
	}

	public ArrayList<GenExcelLine> getItemArray() {
		return itemArray;
	}

	public String executeWithError() {
		String error = null;
		fillHeaderMap();
		if (reader != null) {
			error = checkMandatoryHeaders(reader.getMandatoryHeaders());
			if (!Utils.isStringEmpty(error)) {
				Globals.showNotification("Error", error, IFocEnvironment.TYPE_ERROR_MESSAGE);
			}
		}
		if(error == null) readDataLines();
		return error;
	}

}
