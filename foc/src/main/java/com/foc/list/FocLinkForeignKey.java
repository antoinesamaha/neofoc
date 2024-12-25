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
package com.foc.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.db.SQLFilter;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;

public class FocLinkForeignKey extends FocLink {
  private ArrayList<Integer> foreignKeyFieldList = null;
  private boolean transactionalWithChildren = false;

  public FocLinkForeignKey(FocDesc slaveDesc, ArrayList<Integer> foreignKeyFieldList, boolean transactionalWithChildren) {
    super(null, slaveDesc);
    this.foreignKeyFieldList = foreignKeyFieldList;
    init(transactionalWithChildren);
  }
  
  public FocLinkForeignKey(FocDesc slaveDesc, int uniqueForeignKey, boolean transactionalWithChildren){
    super(null, slaveDesc);
    setUniqueForeignKey(uniqueForeignKey);
    init(transactionalWithChildren);
  }
  
  private void init(boolean transactionalWithChildren){
    setTransactionalWithChildren(transactionalWithChildren);
  }
  
  public void setUniqueForeignKey(int uniqueForeignKey){
  	if(foreignKeyFieldList == null){
  		foreignKeyFieldList = new ArrayList<Integer>();
  	}
  	if(foreignKeyFieldList != null){
  		if(foreignKeyFieldList.size() == 0){
  			foreignKeyFieldList.add(uniqueForeignKey);
  		}else{
  			foreignKeyFieldList.set(0, uniqueForeignKey);
  		}
  	}
  }
  
  public void setTransactionalWithChildren(boolean transactional){
    this.transactionalWithChildren = transactional;
  }
  
  public boolean isTransactionalWithChildren(){
    return transactionalWithChildren;
  }
  
  public int getUniqueForeignKeyFieldID(){
    int uniqueForeignKeyFieldID = -1;
    if(foreignKeyFieldList != null && foreignKeyFieldList.size() == 1){
      uniqueForeignKeyFieldID = foreignKeyFieldList.get(0);
    }
    return uniqueForeignKeyFieldID;
  }
  
  private String getAdditionalWhereForForeignKey(FocList list){
    StringBuffer additionalWhere = new StringBuffer();
    boolean isFirstCondition = true;
    if(foreignKeyFieldList != null){
      HashMap<Integer , FocObject> listForeignObjectsMap = list.getForeignObjectsMap();
      if(listForeignObjectsMap != null){
        for(int i = 0; i < foreignKeyFieldList.size(); i++){
          int foreignKeyFieldID = foreignKeyFieldList.get(i);
          //Getting the masterRef
          int masterRef = -1;
          FocObject object = listForeignObjectsMap.get(foreignKeyFieldID);
          masterRef = object != null ? object.getReference().getInteger() : -1;
          //--------------------
          
          //Getting the foreign Key Field name
          String foreignKeyName = null;
          FField foreignField = getSlaveDesc().getFieldByID(foreignKeyFieldID);
          if(FObjectField.class.isInstance(foreignField)){
            foreignKeyName = ((FObjectField)foreignField).getDBName();
          }else{
            foreignKeyName = foreignField.getName();
          }
          //------------------------
          
          if(DBManager.provider_FieldNamesBetweenSpeachmarks(getSlaveDesc().getProvider()) && foreignKeyName != null){
          	foreignKeyName = "\""+foreignKeyName+"\"";
          }
          
          //Bulding additional where
          if(masterRef != -1 && foreignKeyName != null){
            if(isFirstCondition){
              isFirstCondition = false;
            }else{
              additionalWhere.append("and ");
            }
            additionalWhere.append(foreignKeyName + " = " + masterRef+" ");
          }
          //-------------------
        }
      }
    }
    return String.valueOf(additionalWhere);
  }

  @Override
  public void adaptSQLFilter(FocList list, SQLFilter filter) {
    if (list != null && filter != null) {
      String additionalWhere = getAdditionalWhereForForeignKey(list); 
      filter.putAdditionalWhere(SQLFilter.KEY_FOREIGN_KEY_ADDITINAL_WHERE, additionalWhere);
      filter.setMasterObject(list.getMasterObject());
    }
  }

  @Override
  public void copy(FocList targetList, FocList sourceList) {
  	if(foreignKeyFieldList != null){
	  	int fldToExclude[] = new int[foreignKeyFieldList.size()];
	  	for(int i=0; i<foreignKeyFieldList.size(); i++){
	  		fldToExclude[i] = foreignKeyFieldList.get(i);
	  	}
	  	super.copyDefault(targetList, sourceList, true, fldToExclude);
  	}
  }

  /*
  @Override
  public boolean deleteDB(FocList focList) {
    //XX ADAPT sql filter and delete 
    SQLFilter filter = focList.getFilter();
    String oldWhere = filter.getAdditionalWhere(SQLFilter.KEY_FOREIGN_KEY_ADDITINAL_WHERE);
    if(oldWhere == null){
      filter.putAdditionalWhere(SQLFilter.KEY_FOREIGN_KEY_ADDITINAL_WHERE, getAdditionalWhereForForeignKey(focList));
    }
    SQLDelete delete = new SQLDelete(getSlaveDesc(),filter);
    delete.execute();
    return false;
  }*/
  
  
  /*
  public boolean canDeleteDB(FocList focList) {
  	boolean can = true;
  	FocDesc focDesc = focList.getFocDesc();
  	if(focDesc != null){
  		FocFieldEnum enumeration = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
  		if(enumeration.hasNext()){
  			can = canDeleteDB_ElementByElement(focList);
  		}
  	}
    return can;
  }

  public boolean deleteDB(FocList focList) {
  	FocDesc focDesc = focList.getFocDesc();
  	boolean deleted = false;
  	if(focDesc != null){
  		FocFieldEnum enumeration = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
  		if(enumeration.hasNext()){
  			deleted = deleteDB_ElementByElement(focList);
  		}else{
  			deleted = deleteDB_AllTheListInOneTime(focList);
  		}
  	}
    return deleted;
  }

  private boolean canDeleteDB_ElementByElement(FocList focList){
  	boolean can = true;
  	Iterator iter = focList.focObjectIterator();
    while(iter != null && iter.hasNext() && can){
      FocObject obj = (FocObject) iter.next();
      if(obj != null) can = obj.canDelete(null);
    }
    return can;
  }

  private boolean deleteDB_ElementByElement(FocList focList){
  	boolean deleted = false;
  	Iterator iter = focList.focObjectIterator();
    while(iter != null && iter.hasNext()){
      FocObject obj = (FocObject) iter.next();
      if(obj != null) obj.delete();
    }
    deleted = true;
    return deleted;
  }
  
  private boolean deleteDB_AllTheListInOneTime(FocList focList){
  	//return !Globals.getApp().getDataSource().focList_Delete(focList);
  	
  	boolean deleted = false;
  	SQLFilter filter = focList.getFilter();
    String oldWhere = filter.getAdditionalWhere(SQLFilter.KEY_FOREIGN_KEY_ADDITINAL_WHERE);
    if(oldWhere == null){
      filter.putAdditionalWhere(SQLFilter.KEY_FOREIGN_KEY_ADDITINAL_WHERE, getAdditionalWhereForForeignKey(focList));
    }
    SQLDelete delete = new SQLDelete(getSlaveDesc(),filter);
    try{
    	delete.execute();
    }catch(Exception e){
    	Globals.logException(e);
    }
    deleted = true;
    return deleted;
  }
  */
  
  @Override
  public boolean disposeList(FocList list) {
    list.dispose();
    return true;
  }

  @Override
  public FocDesc getLinkTableDesc() {
    return null;
  }

  @Override
  public FocObject getSingleTableDisplayObject(FocList list) {
    return null;
  }
  
  public boolean isPossibleToLoadList(FocList list){
    boolean possible = true;
    HashMap<Integer, FocObject> foreignObjectsMap = list.getForeignObjectsMap();
    Iterator iter = foreignObjectsMap.keySet().iterator();
    while (iter != null && iter.hasNext() && possible){
      int key = (Integer)iter.next();
      FocObject focObj = foreignObjectsMap.get(key);
      possible = possible && focObj != null && focObj.hasRealReference();
    }
    return possible;
  }

  @Override
  public boolean loadDB(FocList focList) {
  	return loadDB(focList, 0);
  }
  		
  @Override
  public boolean loadDB(FocList focList, long refToUpdateIncementally) {
    boolean bool = false;
    if(isPossibleToLoadList(focList)){
    	//we need to adpate the filter an other time because maybe at creation 
    	//the master had ref = 0 (the master is newly created and do not have a referene yet) 
    	//so the filter condition is : "where m_ref = 0"
    	//so we make adaptSQLFilter now(before loading because now the master object have a real ref)
    	//so the filter condition will becom "where m_ref = the real ref"
    	adaptSQLFilter(focList, focList.getFilter());
      bool = loadDBDefault(focList, refToUpdateIncementally);
    }
    return bool ;
  }

  @Override
  public boolean saveDB(FocList focList) {
    return false;
  }

	@Override
	public boolean deleteDB(FocList focList) {
		return Globals.getApp().getDataSource().focList_Delete(focList);
	}
}
