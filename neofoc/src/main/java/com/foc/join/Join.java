/*
 * Created on Jan 9, 2006
 */
package com.foc.join;

import com.foc.db.DBManager;
import com.foc.db.SQLJoin;

/**
 * @author 01Barmaja
 */
public abstract class Join {
  private TableAlias sourceTableAlias = null;
  private TableAlias targetTableAlias = null;
  private int firstJoinFieldID = 0;
  private int type = SQLJoin.JOIN_TYPE_NONE;
  private String additionalWhere = null;
  
  public abstract String getLinkCondition();
  public abstract String getUpdateCondition();
  protected abstract int fillRequestDescWithJoinFields_Internal(FocRequestDesc desc, int firstJoinFieldID);
  
  public int fillRequestDescWithJoinFields(FocRequestDesc reqDesc, int firstJoinFieldID) {
    setFirstJoinFieldID(firstJoinFieldID);
    return fillRequestDescWithJoinFields_Internal(reqDesc, firstJoinFieldID);
  }
  
  public Join(TableAlias sourceTableAlias){
    this.sourceTableAlias = sourceTableAlias; 
  }
  
  public void dispose(){
  	sourceTableAlias = null;
  	targetTableAlias = null;
  }
  
  public TableAlias getSourceAlias(){
    return sourceTableAlias;
  }
  
  public TableAlias getTargetAlias(){
    return targetTableAlias;
  } 
  
  public void setTargetAlias(TableAlias targetTableAlias) {
    this.targetTableAlias = targetTableAlias;
  }
  
  public int getFirstJoinFieldID() {
    return firstJoinFieldID;
  }
  
  public void setFirstJoinFieldID(int firstJoinFieldID) {
    this.firstJoinFieldID = firstJoinFieldID;
  }
  
	public String getAdditionalWhere() {
		return additionalWhere;
	}
	
	public void setAdditionalWhere(String additionalWhere) {
		this.additionalWhere = additionalWhere;
	}
  
  public String getSQLString(){
  	StringBuffer str = new StringBuffer();
  	switch(type){
  	case SQLJoin.JOIN_TYPE_RIGHT:
  		str.append(" RIGHT JOIN ");
  		break;
  	case SQLJoin.JOIN_TYPE_LEFT:
  		str.append(" LEFT JOIN ");  		
  		break;
  	case SQLJoin.JOIN_TYPE_NONE:
  		str.append(" JOIN ");
  		break;
  	}
  	
  	if(DBManager.provider_FieldNamesBetweenSpeachmarks(targetTableAlias.getFocDesc().getProvider())){
  		str.append("\""+targetTableAlias.getFocDesc().getStorageName_ForSQL()+"\"");
  	}else{
  		str.append(targetTableAlias.getFocDesc().getStorageName_ForSQL());
  	}
  	
  	str.append(' ');
  	str.append(targetTableAlias.getAlias());
  	str.append(" ON ");
  	str.append(getLinkCondition());
  	if(getLinkCondition() == null){
  		getLinkCondition();
  	}
  	if(getAdditionalWhere() != null){
  		str.append(" AND ");
  		str.append(getAdditionalWhere());
  	}
  	return str.toString();
  }
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}

