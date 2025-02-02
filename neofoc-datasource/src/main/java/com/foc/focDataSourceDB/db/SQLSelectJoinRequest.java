/*
 * Created on 27 fevr. 2004
 */
package com.foc.focDataSourceDB.db;

import java.util.*;

import com.foc.db.SQLFilter;
import com.foc.desc.*;
import com.foc.join.*;
import com.foc.list.*;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class SQLSelectJoinRequest extends SQLSelect {
  private FocRequestDesc requestDesc = null;
  
  public SQLSelectJoinRequest(FocObject focObject, FocRequestDesc requestDesc, SQLFilter filter) {
    super(focObject, requestDesc.getFocDesc(), filter);
    this.requestDesc = requestDesc ;
  }
  
  public SQLSelectJoinRequest(FocList initialList, FocRequestDesc requestDesc, SQLFilter filter) {
    super(initialList, requestDesc.getFocDesc(), filter);
    this.requestDesc = requestDesc ;
  }
  
  @Override
  public boolean buildRequest(){
  	return super.buildRequest();
  }
  
  public void addFrom() {
    request.append(" FROM ");

    TableAlias rootAlias = requestDesc.getRootTableAlias();
    request.append(addTableNameSurroundings(rootAlias.getFocDesc().getStorageName_ForSQL())+" "+rootAlias.getAlias());
    
    Collection<TableAlias> collec = requestDesc.newAliasArray();
    if(collec != null) {
	    for(TableAlias alias : collec) {
	//    Iterator iter = requestDesc.newAliasIterator();
	//    while(iter != null && iter.hasNext()){
	//      TableAlias alias = (TableAlias) iter.next();
	      if(alias != null && rootAlias != alias){
	      	for(int i=0; i<alias.getJoinCount(); i++){
		      	Join join = alias.getJoin(i);
		      	request.append(" ");
		      	request.append(join.getSQLString());
		      	if (join.getTargetAlias() != null && join.getTargetAlias().getAlias() != null) {
		      		String aliasWhere = filter.getAliasWhere(join.getTargetAlias().getAlias());
		      		if (!Utils.isStringEmpty(aliasWhere)) {
		      			request.append(" AND ");
		      			request.append(aliasWhere);
		      		}
		      	}
	      	}
	      }
	    }
    }
  }
  
  public boolean addWhere() {
    //int length = request.length();
    boolean b = super.addWhere();

    if(!b){
    	/*
      boolean withAnd = request.length() != length;
      if(withAnd){
        append(" AND (");
      }else{
        append(" WHERE ");
      }
      
      String linkCond = requestDesc.getLinkCondition();
      append(linkCond);
      
      if(withAnd){
        append(")");
      }
      */
    }
    return b;
  }
  
}
