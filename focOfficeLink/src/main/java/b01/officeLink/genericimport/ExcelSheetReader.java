package b01.officeLink.genericimport;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.property.FDate;
import com.foc.property.FDouble;
import com.foc.property.FInt;
import com.foc.property.FMultipleChoice;
import com.foc.property.FProperty;
import com.foc.property.FTime;
import com.foc.util.Utils;

import b01.officeLink.excel.FocExcelSheet;

public class ExcelSheetReader {

	private HashMap<String, Integer> columnHeadersMap = null;
	private FocExcelSheet excelSheet = null;
	private FocList listOfData = null;
	private String errorMessage = "";
	private String fileName = "";
	private boolean lastSheet = false;

	public ExcelSheetReader(FocExcelSheet excelSheet) {
		this.excelSheet = excelSheet;
	}
	
	public ExcelSheetReader(FocExcelSheet excelSheet, FocList data) {
		this.excelSheet = excelSheet;
		this.setListOfData(data);
		execute();
	}
	
	public ExcelSheetReader(FocExcelSheet excelSheet, FocList data, String fileName, boolean lastSheet) {
		this.excelSheet = excelSheet;
		this.setListOfData(data);
		this.fileName = fileName;
		this.lastSheet = lastSheet;
		execute();
	}

	public ExcelSheetReader(FocExcelSheet excelSheet2, HashMap<String, String> mapper) {
		this.excelSheet = excelSheet2;
		this.setListOfData(null);
		execute();
	}
	
	public void execute() {
		fillHeaderMap();
		readDataLines();
	}

	public void dispose() {
		excelSheet = null;
		setListOfData(null);
	}

	public FocExcelSheet getExcelSheet() {
		return excelSheet;
	}

	public String getSheetName() {
		return getExcelSheet() != null ? getExcelSheet().getName() : "";
	}

	public void fillHeaderMap() {
		setColumnHeadersMap(new HashMap<String, Integer>());
		Boolean endOfFile = false;
		int i = 0;
		while (!endOfFile) {
			String value = excelSheet.getCellString(0, i);
			if (!Utils.isStringEmpty(value) && !value.startsWith("//")) {
				getColumnHeadersMap().put(value, i);
			} else {
				endOfFile = true;
			}
			i++;
		}
	}

	public void readDataLines() {
		int i = 1;
		while (checkLine(i)) {
			readLine(i);
			i++;
		}
	}

	public String readCell(int line, String column) {
		String value = null;
		if (getColumnHeadersMap() != null) {
			Object colObj = getColumnHeadersMap().get(column);
			if (colObj != null) {
				int col = (int) colObj;
				value = excelSheet.getCellString(line, col);
			} else {
				Globals.logString("Column: " + column);
			}
		}
		return value;
	}

	public String adjustStringValue(String value) {
		if (value != null) {
			if (value.endsWith(".0") && value.length() > 2) {
				value = value.substring(0, value.length() - 2);
			}
		}
		return value;
	}

	protected boolean isNotEmpty(int line, int col) {
		boolean notEmpty = true;
		try {
			notEmpty = !Utils.isStringEmpty(excelSheet.getCellString(line, col)) || excelSheet.getCellNum(line, col) != 0;
			if (!notEmpty) {
				Date date = (Date) excelSheet.getCellDate(line, col);
				if (date != null) {
					notEmpty = date.getTime() > Globals.DAY_TIME || date.getTime() < -Globals.DAY_TIME;
				}
			}
		} catch (Exception e) {
			notEmpty = false;
		}
		return notEmpty;
	}

	protected void putValueInObjectProperty(FocObject focObj, String fieldName, FProperty prop, int line, int col) {
		if (prop != null) {
			try {
				if (prop instanceof FDouble) {
					if (prop.getDouble() != 0) {
						prop.setDouble(prop.getDouble() + excelSheet.getCellNum(line, col));
					} else {
						prop.setDouble(excelSheet.getCellNum(line, col));
					}
				} else if (prop instanceof FMultipleChoice) {
					prop.setString(excelSheet.getCellString(line, col));
				} else if (prop instanceof FInt) {
					FInt intProp = (FInt) prop;
					double num = excelSheet.getCellNum(line, col);
					if (intProp.getInteger() != 0) {
						intProp.setInteger(intProp.getInteger() + (int) num);
					} else {
						intProp.setInteger((int) num);
					}
				} else if (prop instanceof FDate) {
					java.util.Date tempDate = excelSheet.getCellDate(line, col);
					if (tempDate != null) {
						((FDate) prop).setDate(new Date(tempDate.getTime()));
					}
				} else if (prop instanceof FTime) {
					java.util.Date tempDate = excelSheet.getCellDate(line, col);
					if (tempDate != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
						prop.setString(sdf.format(tempDate));
					}
				} else {
					String value = excelSheet.getCellString(line, col);
					if (value != null) {
						value = adjustStringValue(value);
						if (!Utils.isStringEmpty(prop.getString())) {
							prop.setString(prop.getString() + value);
						} else {
							prop.setString(value);
						}
						if (prop.getFocField().getSize() < value.length()) {
							Globals.showNotification("Field size " + prop.getFocField().getName() + " = " + prop.getFocField().getSize() + " smaller that Value length: " + value.length(), "Value=" + value, IFocEnvironment.TYPE_ERROR_MESSAGE);
						}
					}
				}
			} catch (Exception e) {
				Globals.logException(e);
			}
		} else {
			Globals.logString("COULD NOT FIND PROPERTY:" + fieldName + " Sheet:" + getSheetName());
		}
	}

	public Boolean checkLine(int line) {
		return true;
	}

	public void readLine(int line) {

	}

	@SuppressWarnings("rawtypes")
	public static String maplookupValue(HashMap<String, String> mapper, String name) {
		if (mapper != null) {
			Iterator iterator = mapper.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry pair = (Map.Entry) iterator.next();
				if (pair.getKey().equals(name)) {
					return (String) pair.getValue();
				}
			}
		}
		return name;
	}

	public static String CleanInput(String input, Boolean canBeNbr) {
		if (canBeNbr) {
			return input.replace("NULL", "").replace(".0", "").trim();
		} else {
			return input.replace("NULL", "").trim();
		}
	}

	public FocList getListOfData() {
		return listOfData;
	}

	public void setListOfData(FocList listOfData) {
		this.listOfData = listOfData;
	}

	public HashMap<String, Integer> getColumnHeadersMap() {
		return columnHeadersMap;
	}

	public void setColumnHeadersMap(HashMap<String, Integer> columnHeadersMap) {
		this.columnHeadersMap = columnHeadersMap;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isLastSheet() {
		return lastSheet;
	}

	public void setLastSheet(boolean lastSheet) {
		this.lastSheet = lastSheet;
	}

}
