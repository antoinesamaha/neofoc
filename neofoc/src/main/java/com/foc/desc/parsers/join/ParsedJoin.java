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
package com.foc.desc.parsers.join;

import java.util.ArrayList;

import org.xml.sax.Attributes;

import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.annotations.model.FocJoin;
import com.foc.annotations.model.FocJoinField;
import com.foc.db.SQLJoin;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FReferenceField;
import com.foc.desc.parsers.ParsedFocDesc;
import com.foc.desc.parsers.xml.FXMLDesc;
import com.foc.desc.parsers.xml.XMLFocDescParser;
import com.foc.join.FocRequestField;
import com.foc.join.Join;
import com.foc.join.JoinUsingObjectField;
import com.foc.join.TableAlias;
import com.foc.util.Utils;

public class ParsedJoin implements FXMLDesc {
	private int    order   = -1;
	private String alias   = null;
	private String table   = null;
	private String type    = null;
	private String on      = null;
	private String where   = null;
	private boolean isPrimaryKey = false;
	
	private String otherAlias = null;
	private String otherFieldName = null;
	private String thisFieldName  = null;

//	private XMLFocDescParser focDescParser = null;
	private ParsedFocDesc focDesc = null;
	private ArrayList<ParsedRequestField> array = null;
	
	private TableAlias tableAlias = null;
	
	public ParsedJoin(ParsedFocDesc focDesc, FocJoin joinAnnotation){
		this.focDesc = focDesc;
  	table        = joinAnnotation.table();
  	alias        = joinAnnotation.alias();
  	on           = joinAnnotation.on();
  	type         = joinAnnotation.type();
  	where        = joinAnnotation.where();
  	addLogicalDeleteToWhereIfNeeded(table, alias);
  	isPrimaryKey = joinAnnotation.isPrimaryKey();
  	if(joinAnnotation.fields() != null) {
  		for(FocJoinField field : joinAnnotation.fields()) {
  			ParsedRequestField reqField = new ParsedRequestField(field);
  			addRequestFied(reqField);
  		}
  	}
  	
  	parseOn();
	}
	
	public ParsedJoin(ParsedFocDesc focDesc, Attributes att){
		this.focDesc = focDesc;
  	table       = XMLFocDescParser.getString(att, ATT_JOIN_TABLE);
  	alias       = XMLFocDescParser.getString(att, ATT_JOIN_ALIAS);
  	on          = XMLFocDescParser.getString(att, ATT_JOIN_ON);
  	type        = XMLFocDescParser.getString(att, ATT_JOIN_TYPE);
  	where       = XMLFocDescParser.getString(att, ATT_JOIN_WHERE);
  	addLogicalDeleteToWhereIfNeeded(table, alias);
  	isPrimaryKey = XMLFocDescParser.getBoolean(att, ATT_JOIN_IS_PRIMARY_KEY, false);
  	
  	parseOn();
	}
	
	private void addLogicalDeleteToWhereIfNeeded(String table, String alias){
		if(!Utils.isStringEmpty(table) && Globals.getApp() != null) {
			FocDesc desc = Globals.getApp().getFocDescByName(table);
			if(desc != null && desc.isLogicalDeleteEnabled()) {
				String name = alias + "." + FField.LOGICAL_DELETE_FIELD_NAME;
				name = FField.adaptFieldNameToProvider(desc.getProvider(), name);
				String whereClause = "(" + name + " = 0 OR " + name + " IS NULL )";
				if(!Utils.isStringEmpty(where)) where += " AND ";
				if(where == null) where = "";
				where += whereClause;
			}
		}
	}
	
	public ParsedFocDesc getFocDesc(){
  	return focDesc;
	}
	
	public FocDesc getThisFocDesc(){
		return Globals.getApp().getFocDescByName(table);
	}

	public FField getThisField(){
		FocDesc focDesc = getThisFocDesc();
		FField field = focDesc.getFieldByName(getThisFieldName());
		return field;
	}

	public FocDesc getOtherFocDesc(){
		ParsedJoin otherJoin = getFocDesc().getJoin(otherAlias);
		return otherJoin != null ? otherJoin.getThisFocDesc() : null;
	}

	public FField getOtherField(){
		FocDesc focDesc = getOtherFocDesc();
		FField field = focDesc.getFieldByName(getOtherFieldName());
		if(field == null){
			field = focDesc.getFieldByName(getOtherFieldName());
		}
		return field;
	}

	public TableAlias getTableAlias(){
		if(tableAlias == null){
			FocDesc focDesc = getThisFocDesc();
			tableAlias = new TableAlias(getAlias(), focDesc);
			tableAlias.setOrder(getOrder());
			
			if(!Utils.isStringEmpty(otherAlias) && !Utils.isStringEmpty(otherFieldName) && !Utils.isStringEmpty(thisFieldName)){
				ParsedJoin otherJoin = getFocDesc().getJoin(otherAlias);
				if(otherJoin != null && otherJoin.getTableAlias() != null){
					boolean fieldInSource = true;
					FField fld = getOtherField();
					if(fld instanceof FReferenceField){
						fld = getThisField();
						if(fld == null) fld = getThisField();
						fieldInSource = false;
					}
					if(fld != null){
					  Join join = new JoinUsingObjectField(otherJoin.getTableAlias(), fld.getID(), fieldInSource);
					  
					  if(type != null){
						  if(type.equals("left")){
						  	join.setType(SQLJoin.JOIN_TYPE_LEFT);
						  }else if(type.equals("right")){
						  	join.setType(SQLJoin.JOIN_TYPE_RIGHT);
						  }
					  }
					  if(!Utils.isStringEmpty(getWhere())){
					  	join.setAdditionalWhere(getWhere());
					  	// we can't because the clause of the row 139 wouldn't allow us to enter in this code
					  }
					  
					  tableAlias.addJoin(join);
					}else{
						if(getFocDesc() != null){
							Globals.logString("MODEL ERROR -------------------------");
							Globals.logString("MODEL ERROR : In Table "+getFocDesc().getStorageName()+" Reference field not found for table Alias:"+otherAlias+" for Table Name "+ otherJoin.getTable());
							Globals.logString("-------------------------------------");							
						}
					}
				}else{
					if(otherJoin == null){
						if(getFocDesc() != null){
							Globals.logString("MODEL ERROR -------------------------");
							Globals.logString("MODEL ERROR : In Table "+getFocDesc().getStorageName()+" Join not found for :"+otherAlias);
							Globals.logString("-------------------------------------");							
						}
					}
				}
			}

			for(int f=0; f<array.size(); f++){
				ParsedRequestField xmlReqFld = array.get(f);
				
				if(xmlReqFld.getSourceFieldName().equals("*")){
					FocFieldEnum fieldEnum = new FocFieldEnum(getThisFocDesc(), FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
			    while(fieldEnum != null && fieldEnum.hasNext()){
			    	FField fld = (FField) fieldEnum.next();
			    	if(fld != null && fld.isDBResident() && !(fld instanceof FBlobStringField)){
			    		int fldID = 0;
			    		if(isPrimaryKey() && fld.getID() == FField.REF_FIELD_ID){
			    			fldID = FField.REF_FIELD_ID;
			    		}else{
			    			fldID = getFocDesc().nextFldID();
			    		}
			    		FocRequestField reqFld = new FocRequestField(fldID, tableAlias, fld.getID());
			    		if(getFocDesc() != null && getFocDesc().getFocRequestDesc() != null) getFocDesc().getFocRequestDesc().addField(reqFld);
			    	}
			    }
				}else{
					if(getThisFocDesc() == null){
						Globals.showNotification("Could not find Table", "Table name : "+table, IFocEnvironment.TYPE_ERROR_MESSAGE);
					}else{
						FField fld = getThisFocDesc().getFieldByName(xmlReqFld.getSourceFieldName());
						if(fld == null){
							Globals.showNotification("Could not find Field", getThisFocDesc().getStorageName()+"."+xmlReqFld.getSourceFieldName(), IFocEnvironment.TYPE_ERROR_MESSAGE);
						}else{
							if(getFocDesc() != null && getFocDesc().getFocRequestDesc() != null){
								int fieldID = 0;
								
								if(fld.getID() == FField.REF_FIELD_ID && isPrimaryKey()) {
									fieldID = FField.REF_FIELD_ID;
								} else {
									fieldID = getFocDesc().nextFldID();
								}
								
								FocRequestField reqFld = new FocRequestField(fieldID, tableAlias, fld.getID());
								getFocDesc().getFocRequestDesc().addField(reqFld);
								
				    		String groupByFormula = xmlReqFld.getGroupByFormula();
				    		reqFld.setGroupByFormula(groupByFormula);
				    		
				    		String groupByFormulaAdditionalField = xmlReqFld.getGroupByFormula_AdditionalFields();
				    		reqFld.setGroupByFormula_AdditionalField(groupByFormulaAdditionalField);
							}
						}
					}
				}
			}
		}		
		return tableAlias;
	}
	
	public void addRequestFied(ParsedRequestField requestField){
		if(array == null){
			array = new ArrayList<ParsedRequestField>();
		}
		array.add(requestField);
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getSide() {
		return type;
	}

	public void setSide(String side) {
		this.type = side;
	}

	public String getOn() {
		return on;
	}
	
	public String getWhere() {
		return where;
	}

	public void parseOn() {
		if(!Utils.isStringEmpty(on)){
			int indexOfEqual = on.indexOf("=");
			if(indexOfEqual > 0){
				String before = on.substring(0, indexOfEqual);
				String after = on.substring(indexOfEqual+1, on.length());
			
				int indexOfDotBefore = before.indexOf(".");
				int indexOfDotAfter  = after.indexOf(".");
				
				if(indexOfDotBefore > 0 && indexOfDotAfter > 0){
					String beforeAlias = before.substring(0, indexOfDotBefore);
					String beforeField = before.substring(indexOfDotBefore+1, before.length());
					
					String afterAlias  = after.substring(0, indexOfDotAfter);
					String afterField  = after.substring(indexOfDotAfter+1, after.length());
					
					if(beforeAlias.equals(alias)){
						otherAlias = afterAlias;
						otherFieldName = afterField;
						thisFieldName  = beforeField;
					}else if(afterAlias.equals(alias)){
						otherAlias = beforeAlias;
						otherFieldName = beforeField;
						thisFieldName  = afterField;						
					}
				}
			}
		}
	}

	public String getOtherAlias() {
		return otherAlias;
	}

	public String getThisFieldName() {
		return thisFieldName;
	}

	public String getOtherFieldName() {
		return otherFieldName;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}
}
