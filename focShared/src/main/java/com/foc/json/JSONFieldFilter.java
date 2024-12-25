package com.foc.json;

import java.util.HashMap;

public class JSONFieldFilter {
	
	HashMap<String, String> excludedFieldsMap = null;
	
	public JSONFieldFilter() {
		excludedFieldsMap = null;
	}
	
	private String buildKey(String tableName, String fieldName) {
		String merge = tableName != null ? tableName : "";
		merge += "|"+fieldName;
		return merge;
	}
	
	public void putField(String tableName, String fieldName) {
		if(excludedFieldsMap == null) {
			excludedFieldsMap = new HashMap<String, String>();
		}
		String merge = buildKey(tableName, fieldName);
		excludedFieldsMap.put(merge, merge);
	}
	
	public boolean includeField(String tableName, String fieldName) {
		String merge = buildKey(tableName, fieldName);
		
		return excludedFieldsMap != null ? excludedFieldsMap.get(merge) == null : true;
	}

}
