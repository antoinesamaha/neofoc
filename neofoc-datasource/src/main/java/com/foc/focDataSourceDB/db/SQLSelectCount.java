/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import com.foc.db.SQLFilter;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.list.FocList;

/**
 * @author 01Barmaja
 */
public class SQLSelectCount extends SQLSelect {

	public SQLSelectCount(FocList initialList, FocDesc focDesc, SQLFilter filter) {
		super(initialList, focDesc, filter);
	}
	
	protected void addTableFieldsToSelect(FocDesc focDesc, StringBuffer fieldsCommaSeparated, String tableAlias){
		super.addTableFieldsToSelect(focDesc, fieldsCommaSeparated, tableAlias);
		
    String fieldNameString = FField.REF_FIELD_NAME;
    if(tableAlias != null && !tableAlias.isEmpty()){
    	fieldNameString = tableAlias + "." + fieldNameString;
    }else{
    	fieldNameString = FField.adaptFieldNameToProvider(focDesc.getProvider(), fieldNameString);
    }
	
    fieldsCommaSeparated.append("COUNT(");
    fieldsCommaSeparated.append(fieldNameString);
    fieldsCommaSeparated.append(")");
	}
	
}