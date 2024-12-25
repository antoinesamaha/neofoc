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
package com.foc.db;

public class SQLJoin {
  private String newAlias     = null ;//New alias to point to this new table
  private String newTableName = null;//New table name
  private int    type         = JOIN_TYPE_NONE;
  private String prevAlias    = null ;
  private String sourceField  = "";
  private String targetField  = "";
  
  private String additionalWhere = null;
  private int    provider        = DBManager.PROVIDER_MYSQL;
  
  public static String MAIN_TABLE_ALIAS = "M";

  public static final int JOIN_TYPE_NONE  = 0;
  public static final int JOIN_TYPE_LEFT  = 1;
  public static final int JOIN_TYPE_RIGHT = 2;
  
  public SQLJoin(int provider, String newTableName, String prevAlias, String sourceField, String targetField){
  	this.provider     = provider;
    this.newTableName = newTableName;
    this.prevAlias    = prevAlias;
    this.sourceField  = sourceField;
    this.targetField  = targetField;
  }

  public String getKey() {
    return prevAlias+"|"+sourceField+"|"+newTableName+"|"+targetField;
  }
  
  public String getLinkCondition() {
  	if(provider == DBManager.PROVIDER_ORACLE || provider == DBManager.PROVIDER_H2 || provider == DBManager.PROVIDER_POSTGRES){
  		return prevAlias+".\""+sourceField+"\"="+newAlias+".\""+targetField+"\"";
  	}else{
  		return prevAlias+"."+sourceField+"="+newAlias+"."+targetField;
  	}
  }

  public String getSQLString(){
  	StringBuffer str = new StringBuffer();
  	switch(type){
  	case JOIN_TYPE_RIGHT:
  		str.append(" RIGHT JOIN ");
  		break;
  	case JOIN_TYPE_LEFT:
  		str.append(" LEFT JOIN ");  		
  		break;
  	case JOIN_TYPE_NONE:
  		str.append(" JOIN ");
  		break;
  	}
  	
  	str.append(getNewTableName());
  	str.append(' ');
  	str.append(getNewAlias());
  	str.append(" ON ");
  	str.append(getLinkCondition());
  	if(getAdditionalWhere() != null){
  		str.append(" AND (");
  		str.append(getAdditionalWhere());
  		str.append(")");
  	}
  	return str.toString();
  }
  
  public String getNewAlias() {
    return newAlias;
  }

  public void setNewAlias(String newAlias) {
    this.newAlias = newAlias;
  }

  public String getNewTableName() {
    return newTableName;
  }

  public String getPrevAlias() {
    return prevAlias;
  }

  public void setPrevAlias(String prevAlias) {
    this.prevAlias = prevAlias;
  }

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAdditionalWhere() {
		return additionalWhere;
	}

	public void setAdditionalWhere(String additionalWhere) {
		this.additionalWhere = additionalWhere;
	}
}
