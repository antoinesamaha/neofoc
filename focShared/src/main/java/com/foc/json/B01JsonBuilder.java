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
package com.foc.json;

import java.util.ArrayList;
import java.util.HashMap;

public class B01JsonBuilder {
	private StringBuffer buffer = null;
	
	private ArrayList<Boolean> firstIndicator       = new ArrayList<Boolean>();
	private ArrayList<String>  masterObjectsPrinted = new ArrayList<String>();

	private JSONObjectFilter objectFilter          = null;
	private JSONFieldFilter  additionalFieldFilter = null;
	private HashMap<String, JSONObjectWriter> jsonObjectWriterMap = null;
	
	private boolean modifiedPropertiesOnly    = false;
	private boolean printObjectNamesNotRefs   = false;
	private boolean scanSubList               = false;
	private boolean printRootRef              = true ;
	private boolean printCRUD                 = false;
	private boolean hideWorkflowFields        = false;
	private boolean hideCreationUser          = false;	
	private boolean printForeignKeyFullObject = false;
	private boolean printOrderField           = false;
	private boolean printDepricatedField      = false;
	
	private int     listStart                 = -1;
	private int     listCount                 = -1;
	
	public B01JsonBuilder(){
		buffer = new StringBuffer();
	}
	
	public B01JsonBuilder(B01JsonBuilder src){
		this();
		setModifiedOnly(src.isModifiedOnly());
		setPrintCRUD(src.isPrintCRUD());
		setScanSubList(src.isScanSubList());
		setPrintForeignKeyFullObject(src.isPrintForeignKeyFullObject());
		setPrintRootRef(src.isPrintRootRef());
		setPrintObjectNamesNotRefs(src.isPrintObjectNamesNotRefs());
		setHideWorkflowFields(src.isHideWorkflowFields());
		setAdditionalFieldFilter(src.getAdditionalFieldFilter());
		setHideCreationUser(src.isHideCreationUser());
		jsonObjectWriterMap = src.jsonObjectWriterMap;
		
		//DO NOT COPY THE LIST START And COUNT They apply on the first level only
		//---------------------------------
		//setListStart(src.getListStart());
		//setListCount(src.getListCount());
		//---------------------------------
		
		masterObjectsPrinted = new ArrayList<String>(src.masterObjectsPrinted);
	}
	
	public B01JsonBuilder(StringBuffer buffer){
		this.buffer = buffer;
	}
	
	public void dispose() {
		jsonObjectWriterMap = null;
	}
	
	public boolean firstIndicator_IsCurrentFirst(){
		return firstIndicator.size() > 0 ? firstIndicator.get(firstIndicator.size()-1) : true;
	}
	
	public boolean firstIndicator_SetCurrentFirst(boolean first){
		return firstIndicator.size() > 0 ? firstIndicator.set(firstIndicator.size()-1, first) : true;
	}

	public boolean firstIndicator_AddLevel(){
		return firstIndicator.add(true);
	}

	public boolean firstIndicator_RemoveLevel(){
		return firstIndicator.remove(firstIndicator.size()-1);
	}

	public void beginObject_InValue(){
		append("{");
		firstIndicator_AddLevel();
	}

	public void beginObject(){
		if(!firstIndicator_IsCurrentFirst()){
			append(",");
		}else{
			firstIndicator_SetCurrentFirst(false);
		}
		append("{");
		firstIndicator_AddLevel();
	}

	public void endObject(){
		append("}");
		firstIndicator_RemoveLevel();
	}

	public void beginList(){
		append("[");
		firstIndicator_AddLevel();
	}

	public void endList(){
		append("]");
		firstIndicator_RemoveLevel();
	}

	public void append(String str){
		if(buffer != null) buffer.append(str);
	}

	public void appendKey(String key){
		if(buffer != null){
			if(!firstIndicator_IsCurrentFirst()){
				append(",");
			}else{
				firstIndicator_SetCurrentFirst(false);
			}
			buffer.append("\"");
			buffer.append(key);
			buffer.append("\":");
		}
	}

	public void appendValue(String value){
		if(buffer != null){
			buffer.append(quote(value));
			
//			buffer.append("\"");
//			buffer.append(value);
//			buffer.append("\"");
		}
	}
	
	public void appendNullValue(){
		if(buffer != null){
			buffer.append("null");
		}
	}

	public void appendKeyValue(String key, int value){
		if(buffer != null){
			appendKey(key);
			buffer.append(value);
		}
	}
	
	public void appendKeyValue(String key, boolean value){
		if(buffer != null){
			appendKey(key);
			buffer.append(value ? "true" : "false");
		}
	}
	
	public void appendKeyValue(String key, long value){
		if(buffer != null){
			appendKey(key);
			buffer.append(value);
		}
	}
		
	public void appendKeyValue(String key, double value){
		if(buffer != null){
			appendKey(key);
			buffer.append(value);
		}
	}
	
	public void appendKeyValue(String key, String value){
		if(buffer != null){
			appendKey(key);
			appendValue(value);
		}
	}
	
	public void appendKeyValueIfNotNull(String key, String value){
		if(buffer != null && key != null && value != null){
			appendKey(key);
			appendValue(value);
		}
	}
	
	public void appendKeyValueCheckNull(String key, String value){
		if(buffer != null){
			appendKey(key);
			if(value == null) {
				appendValue("");
			} else {
				appendValue(value);
			}
		}
	}
	
	@Override
	public String toString(){
		return buffer != null ? buffer.toString() : "";
	}

	public boolean isModifiedOnly() {
		return modifiedPropertiesOnly;
	}

	public void setModifiedOnly(boolean modifiedOnly) {
		this.modifiedPropertiesOnly = modifiedOnly;
	}

	public boolean isPrintObjectNamesNotRefs() {
		return printObjectNamesNotRefs;
	}

	public void setPrintObjectNamesNotRefs(boolean printObjectNamesNotRefs) {
		this.printObjectNamesNotRefs = printObjectNamesNotRefs;
	}

	public boolean isScanSubList() {
		return scanSubList;
	}

	public void setScanSubList(boolean scanSubList) {
		this.scanSubList = scanSubList;
	}

	public boolean isPrintRootRef() {
		return printRootRef;
	}

	public void setPrintRootRef(boolean printRootRef) {
		this.printRootRef = printRootRef;
	}

	public boolean isPrintCRUD() {
		return printCRUD;
	}

	public boolean setPrintCRUD(boolean printStatus) {
		boolean backupValue = this.printCRUD;
		this.printCRUD = printStatus;
		return backupValue;
	}

	public boolean isHideWorkflowFields() {
		return hideWorkflowFields;
	}

	public void setHideWorkflowFields(boolean hideWorkflowFields) {
		this.hideWorkflowFields = hideWorkflowFields;
	}

	public boolean isPrintForeignKeyFullObject() {
		return printForeignKeyFullObject;
	}

	public void setPrintForeignKeyFullObject(boolean printForeignKeyFullObject) {
		this.printForeignKeyFullObject = printForeignKeyFullObject;
	}
	
	public void pushMasterObject(String masterObject) {
		masterObjectsPrinted.add(masterObject);
	}
	
	public boolean containsMasterObject(String masterObject) {
		return masterObjectsPrinted.contains(masterObject);
	}

	public boolean isPrintOrderField() {
		return printOrderField;
	}

	public void setPrintOrderField(boolean printOrderField) {
		this.printOrderField = printOrderField;
	}
	
	public boolean isPrintDepricatedField() {
		return printDepricatedField;
	}

	public void setPrintDepricatedField(boolean printDepricatedField) {
		this.printDepricatedField = printDepricatedField;
	}

	public int getListStart() {
		return listStart;
	}

	public void setListStart(int listStart) {
		this.listStart = listStart;
	}

	public int getListCount() {
		return listCount;
	}

	public void setListCount(int listCount) {
		this.listCount = listCount;
	}
	
	public String quote(String string) {
		if(string == null || string.length() == 0){
			return "\"\"";
		}

		char b;
		char c = 0;
		int i;
		int len = string.length();
		StringBuffer sb = new StringBuffer(len + 4);
		String t;

		sb.append('"');
		for(i = 0; i < len; i += 1){
			b = c;
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				if(b == '<'){
					sb.append('\\');
				}
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if(c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')){
					t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				}else{
					sb.append(c);
				}
			}
		}
		sb.append('"');
		return sb.toString();
	}

	public boolean includeField(String tableName, String fieldName) {
		boolean include = true;
		JSONFieldFilter filter = getFieldFilter();
		if(filter != null) {
			include = filter.includeField(tableName, fieldName);
		}
		
		filter = getAdditionalFieldFilter();
		if (include && filter != null) {
			include = filter.includeField(tableName, fieldName);
		}
		return include;
	}
	
	private static JSONFieldFilter fieldFilter = null;
	public static JSONFieldFilter getFieldFilter() {
		return fieldFilter;
	}
	
	public static void setFieldFilter(JSONFieldFilter filter) {
		fieldFilter = filter;
	}
	
	public JSONFieldFilter getAdditionalFieldFilter() {
		return additionalFieldFilter;
	}
	
	public void setAdditionalFieldFilter(JSONFieldFilter filter) {
		additionalFieldFilter = filter;
	}

	
	public JSONObjectFilter getObjectFilter() {
		return objectFilter;
	}

	public void setObjectFilter(JSONObjectFilter objectFilter) {
		this.objectFilter = objectFilter;
	}

	public boolean isHideCreationUser() {
		return hideCreationUser;
	}

	public void setHideCreationUser(boolean hideCreationUser) {
		this.hideCreationUser = hideCreationUser;
	}

	public JSONObjectWriter getJsonObjectWriter(String key) {
		return jsonObjectWriterMap != null ? jsonObjectWriterMap.get(key) : null;
	}

	public void putJsonObjectWriter(String key, JSONObjectWriter jsonObjectWriter) {
		if(jsonObjectWriterMap == null) {
			jsonObjectWriterMap = new HashMap<String, JSONObjectWriter>();
		}
		jsonObjectWriterMap.put(key, jsonObjectWriter);
	}
}
