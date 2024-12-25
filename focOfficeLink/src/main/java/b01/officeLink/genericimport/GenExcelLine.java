package b01.officeLink.genericimport;

import java.util.HashMap;

public class GenExcelLine {
	
	private GenExcelReader reader = null;
	private HashMap<String, String> map = null;

	public GenExcelLine(GenExcelReader reader) {
		this.reader = reader;
		map = new HashMap<String, String>();
	}
	
	public void dispose() {
		reader = null;
	}
	
	public void put(String key, String value) {
		map.put(key, value);
	}

	public String get(String key) {
		return map.get(key);
	}
	
	public String isValid() {
		String error = reader.isLineValid(this);
//		if(Utils.isStringEmpty(get("phone"))){
//			error = "no phone number";
//		}
		return error;
	}
}