/*
 * Created on 27 feb. 2004
 */
package com.foc.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.admin.UserSession;
import com.foc.business.company.CompanyDesc;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.WorkflowDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.property.FProperty;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class SQLFilter {
	private boolean            ownerOfTemplate = true;
	private FocObject          focObjTemplate  = null;
  private FocObject          masterObject    = null;
  private int                filterFields    = 0;
  private ArrayList<Integer> selectedFields  = null;
  private StringBuffer       additionalWhere = null;
  private SQLJoinMap         joinMap         = null;
  private boolean            filterByCompany = (Globals.getApp() != null && Globals.getApp().isWebServer()) ? false : true;
  
//  private int offset      = -1;
//  private int offsetCount = -1;
  private ListPagination pagination = null;
  private String orderBy  = null;
  
  public static final String KEY_FILTER_BUTTON_ADDITINAL_WHERE = "FIL_BUT";
  public static final String KEY_MASTER_FIELD_ADDITINAL_WHERE  = "MST_FLD";
  public static final String KEY_NO_KEY_ADDITINAL_WHERE        = "NO_KEY";
  public static final String KEY_FOREIGN_KEY_ADDITINAL_WHERE   = "FRGN_KEY";
  
  private HashMap<String, String> additionalWhereMap = null;
  private HashMap<String, String> additionalWhereMap_ForJoinAlias = null;
  
  //private int aliasCount = 1;
  //BTOADD
  /*
  private HashMap joinHashMap = null; //String nom de la table, Join Object ok
  
  public void addJoin(table, Join)  ok
  
  create the class Join in the foc.db sqlFilter  ok
  class Join{ ok
    private String linkRequest = ""; ok
    
    get ok
    set ok
  }
  
  In the buildRequest for select
    If the SQLFilter has additional joins -> We include the storage name before all fields
    In the From we add the new tables
    In the where we add a section saying AND (cond_for_join1 and cond_for_join2 and cond_for_join3)
  
  To create the join condition:
    we should ask the FField to do it.
    All FFields alreaady implement 
      1- getFocDesc() which returns the new table (desc) to link to
      2- have to implement :CREATE LINK CONDITION WITCH USES GETfOCdESC to buikd the MAINTABLE.MASTER FIELD = SECTABLE.REF 
  */
  //ETOADD  

  public static final int FILTER_ON_NOTHING          = 0;
  public static final int FILTER_ON_IDENTIFIER       = 1;
  public static final int FILTER_ON_KEY              = 2;
  public static final int FILTER_ON_ALL              = 3;
  public static final int FILTER_ON_SELECTED         = 4;
  public static final int FILTER_ON_KEY_EXCLUDE_SITE = 5;
  
  public SQLFilter(FocObject focObjTemplate, int filterFields) {
    this.focObjTemplate = focObjTemplate;
    this.filterFields = filterFields;
    this.joinMap = null;
    if(filterFields == FILTER_ON_SELECTED){
      selectedFields = new ArrayList<Integer>();
    }
  }
  
  public void dispose(){
    if(focObjTemplate != null){
      if(ownerOfTemplate) focObjTemplate.dispose();
      focObjTemplate = null;
    }
    
    masterObject = null;
    if(selectedFields != null){
      selectedFields.clear();
      selectedFields = null;
    }
    
    if(additionalWhere != null){      
      additionalWhere = null;
    }
    
    if(additionalWhereMap != null){
      additionalWhere = null;
    }

   	additionalWhereMap_ForJoinAlias = null;
    
    if (pagination != null) {
    	pagination.dispose();
    	pagination = null;
    }
  }
  
  public void setFilterFields(int filterFields) {
    this.filterFields = filterFields;
  }
  
  public void addSelectedField(int field){
    if(selectedFields == null){
      selectedFields = new ArrayList<Integer>();
    }
    selectedFields.add(Integer.valueOf(field));       
  }

  public void resetSelectedFields(){
    if(selectedFields != null){
      selectedFields.clear();
    }       
  }

  public FocObject getObjectTemplate() {
    return focObjTemplate;
  }

  public void setObjectTemplate(FocObject focObjTemplate) {
    this.focObjTemplate = focObjTemplate;
  }
  
  /**
   * @return
   */
  public FocObject getMasterObject() {
    return masterObject;
  }

  /**
   * @param object
   */
  public void setMasterObject(FocObject object) {
    masterObject = object;
  }
  
/*  public SQLJoin addJoin (SQLJoin join){
    if(joinHashMap == null){
      joinHashMap = new HashMap<String, SQLJoin>();
    }
    SQLJoin newJoin = joinHashMap.get(join.getKey());
    if(newJoin == null){
      newJoin = join;
      String newTableName = join.getNewTableName();
      String newAlias = "T"+aliasCount++;
      joinHashMap.put(newJoin.getKey(), newJoin);      
      newJoin.setNewAlias(newAlias);
      addTableToMap(newTableName, newAlias);
    }
    return newJoin;
  }*/
  
  public boolean hasJoinMap(){
    return joinMap != null && joinMap.size() > 0;
  }
  
  public SQLJoinMap getJoinMap(){
    if (joinMap == null){
      joinMap = new SQLJoinMap();
    }
    return joinMap;
  }
  
  private boolean addFieldToWhere(int provider, StringBuffer sql, String fldName, String sqlValue, boolean isFirst, boolean withWhere) {
    //String value = objProp.getString();
    boolean errorAddingField = true;
    boolean valueNotNull = true;// (fieldID == FField.REF_FIELD_ID) ?
                                // value.compareTo("0") != 0 :
                                // value.compareTo("") != 0;

    if (valueNotNull) {
      if (isFirst) {
        if(withWhere){
        	sql.append(" WHERE (");
        }else{
        	sql.append(" (");
        }
      } else {
        sql.append(" and (");
      }
      
      String fieldName = fldName;
      if(    provider == DBManager.PROVIDER_ORACLE
      		|| provider == DBManager.PROVIDER_POSTGRES
      		|| provider == DBManager.PROVIDER_H2) fieldName = "\""+fieldName+"\"";
      if(hasJoinMap() && !fieldName.contains(".")){
      	fieldName = getJoinMap().getMainTableAlias()+"."+fldName; 
      }
      
      sql.append(fieldName);
      sql.append("=");
      sql.append(sqlValue);
      if (!isFirst) {
        sql.append(")");
      }
      errorAddingField = false;
    }
    return errorAddingField;
  }
  
  private boolean addFieldToWhere(StringBuffer sql, FocObject template, String fldName, int fieldID, boolean isFirst, boolean withWhere) {
    FProperty objProp = template.getFocProperty(fieldID);
    //String value = objProp.getString();
    String sqlValue = objProp.getSqlString();
    return addFieldToWhere(
    		template.getThisFocDesc() != null ? template.getThisFocDesc().getProvider() : DBManager.PROVIDER_MYSQL,
    		sql, fldName, sqlValue, isFirst, withWhere);
  }

  public boolean addWhereToRequest_WithoutWhere(StringBuffer requestBuffer, FocDesc requestFocDesc) {
  	return addWhereToRequest(requestBuffer, requestFocDesc, false, true);
  }
  
//  public boolean addWhereToRequest(StringBuffer requestBuffer, FocDesc requestFocDesc) {
//  	return addWhereToRequest(requestBuffer, requestFocDesc, true, true);
//  }
  
  public boolean addWhereToRequest(StringBuffer requestBuffer, FocDesc requestFocDesc, boolean withWhere, boolean includeCompanyConditionIfApplicable) {
  	boolean atLeastOneFieldAdded = false;
    if (requestBuffer != null) {
      boolean isFirst = true;

      // Building Where on Template fields
      // ---------------------------------
      FocObject focObj = getObjectTemplate();

      if (focObj != null) {
        // We start with the idetifier property field
        if (filterFields == FILTER_ON_IDENTIFIER) {
          FProperty idProp = focObj.getIdentifierProperty();
          FField idField = (idProp != null) ? idProp.getFocField() : null;
          //This is to prevent useless requests with where ref=0
          if (idField != null) {
            boolean errorAdding = addFieldToWhere(requestBuffer, focObj, idField.getName(), idField.getID(), isFirst, withWhere);
            isFirst = isFirst && errorAdding;
            atLeastOneFieldAdded = atLeastOneFieldAdded || !errorAdding;
          }
        }

        // If the identifier is not added we work on the key fields
        if (filterFields == FILTER_ON_KEY || filterFields == FILTER_ON_KEY_EXCLUDE_SITE) {
        	boolean excludeSite = filterFields == FILTER_ON_KEY_EXCLUDE_SITE;
          FocFieldEnum enumer = new FocFieldEnum(requestFocDesc, focObjTemplate, FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_DB);
          while (enumer.hasNext()) {
            enumer.next();
            FFieldPath path = enumer.getFieldPath();
            if(path != null){
              FProperty prop = enumer.getProperty();
              if(prop != null){
              	boolean excludeThisFieldInWhere = false;
              	
          	    if(excludeSite && requestFocDesc instanceof IWorkflowDesc){
          	      WorkflowDesc workflowDesc = ((IWorkflowDesc) requestFocDesc).iWorkflow_getWorkflowDesc();
          	      if(workflowDesc != null){
          	        int siteFieldID_1 = workflowDesc.getFieldID_Site_1();
          	        int siteFieldID_2 = workflowDesc.getFieldID_Site_2();
          	        excludeThisFieldInWhere = siteFieldID_1 == path.get(0);
          	        excludeThisFieldInWhere = excludeThisFieldInWhere || siteFieldID_2 == path.get(0);
          	      }
          	    } 
              	
              	if(!excludeThisFieldInWhere) {
	              	boolean errorAdding = addFieldToWhere(requestFocDesc.getProvider(), requestBuffer, enumer.getFieldCompleteName(requestFocDesc), prop.getSqlString(), isFirst, withWhere);
	              	//boolean errorAdding = addFieldToWhere(sql, focObj, enumer.getFieldCompleteName(sql.getFocDesc()), focField.getID(), isFirst);
	              	isFirst = isFirst && errorAdding;
	              	atLeastOneFieldAdded = atLeastOneFieldAdded || !errorAdding;
              	}
              }
            }
            
            /*
            FField focField = (FField) enumer.next();
            boolean errorAdding = addFieldToWhere(sql, focObj, enumer.getFieldCompleteName(sql.getFocDesc()), focField.getID(), isFirst);
            isFirst = isFirst && errorAdding;
            atLeastOneFieldAdded = !errorAdding;
            */
          }
        }
        
        if (filterFields == FILTER_ON_SELECTED) {
          for(int i=0; i<selectedFields.size(); i++){
            int fieldId = ((Integer)selectedFields.get(i)).intValue();
            FField focField = focObjTemplate.getThisFocDesc().getFieldByID(fieldId);
            if(focField != null){
              //Here we scan all the fields and find the ones with father focField to put a where on them
              FocFieldEnum enumer = new FocFieldEnum(requestFocDesc, focObjTemplate, FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
              while (enumer.hasNext()) {
                enumer.next();
                FFieldPath path = enumer.getFieldPath();
                if(path != null){
                  int dbRootFieldId = path.get(0);
                  if(dbRootFieldId == fieldId){
                    FProperty prop = enumer.getProperty();
                    boolean errorAdding = addFieldToWhere(requestFocDesc.getProvider(), requestBuffer, enumer.getFieldCompleteName(requestFocDesc), prop.getSqlString(), isFirst, withWhere);
                    //boolean errorAdding = addFieldToWhere(sql, focObj, enumer.getFieldCompleteName(sql.getFocDesc()), focField.getID(), isFirst);
                    isFirst = isFirst && errorAdding;
                    atLeastOneFieldAdded = atLeastOneFieldAdded || !errorAdding;
                  }
                }
              }              
            }
          }
        }
      }

      // Building Where on Master fields
      // -------------------------------
      FocObject masterObj = getMasterObject();
      if (masterObj != null) {
        FField focSlaveField = (FField) requestFocDesc.getFieldByID(FField.MASTER_REF_FIELD_ID);
        if(focSlaveField != null){
          FProperty masterIdentifier = masterObj.getIdentifierProperty();
          FField focMasterField = masterObj.getThisFocDesc().getIdentifierField();
          boolean errorAdding = addFieldToWhere(requestBuffer, masterObj, focSlaveField.getName(), focMasterField.getID(), isFirst, withWhere);
          isFirst = isFirst && errorAdding;
          atLeastOneFieldAdded = atLeastOneFieldAdded || !errorAdding;
        }
      }

      if (!isFirst && atLeastOneFieldAdded) {
      	requestBuffer.append(")");
      }
      
      StringBuffer additionalWhere = getAdditionalWhere();
      if(additionalWhere != null && additionalWhere.length() > 0){
        if(atLeastOneFieldAdded){
        	requestBuffer.append(" and (");
        }else{
        	if(withWhere){
        		requestBuffer.append(" WHERE (");
        	}else{
        		requestBuffer.append(" (");
        	}
        }
        requestBuffer.append(additionalWhere.toString());
        requestBuffer.append(")");
        atLeastOneFieldAdded = true;
      }
      
      if(includeCompanyConditionIfApplicable && requestFocDesc.isByCompany() && isFilterByCompany()){
      	FField companyField = requestFocDesc.getFieldByID(FField.FLD_COMPANY);
        String fieldName = companyField.getDBName();
        
        if(requestFocDesc.getProvider() == DBManager.PROVIDER_POSTGRES) {
        	fieldName = "\""+fieldName+"\"";
        }
        
        if(hasJoinMap() && !fieldName.contains(".")){
        	fieldName = getJoinMap().getMainTableAlias()+"."+fieldName; 
        }
      	String expression = CompanyDesc.getCompanyFilter_IfNeeded(fieldName, companyField.isMandatory());
      	if(!Utils.isStringEmpty(expression)) {
	        if(atLeastOneFieldAdded){
	        	requestBuffer.append(" and (");
	        }else{
	        	if(withWhere){
	        		requestBuffer.append(" WHERE (");
	        	}else{
	        		requestBuffer.append(" (");
	        	}
	        }
	        requestBuffer.append(expression);
	        requestBuffer.append(")");
	        atLeastOneFieldAdded = true;
      	}        
        
        if(requestFocDesc.workflow_IsWorkflowSubject() && 
        		(UserSession.getInstanceForThread() == null || !UserSession.getInstanceForThread().isSimulation())){
        	requestBuffer.append("and(");
          fieldName = WorkflowDesc.FNAME_SIMULATION;
          if(DBManager.provider_FieldNamesBetweenSpeachmarks(requestFocDesc.getProvider())) {
          	fieldName = "\"" + fieldName + "\"";
          }
          if(hasJoinMap() && !fieldName.contains(".")){
          	fieldName = getJoinMap().getMainTableAlias()+"."+fieldName; 
          }
        	requestBuffer.append(fieldName);
        	requestBuffer.append("<1)");
        }
      }
    }

    return atLeastOneFieldAdded;
  }

  public void copy_SelectedFieldsValues_From_Template_To_Object(FocObject obj){
    if(filterFields == FILTER_ON_SELECTED && focObjTemplate != null && obj != null){
      for(int i=0; i<selectedFields.size(); i++){
        int fieldId = ((Integer)selectedFields.get(i)).intValue();
        obj.getFocProperty(fieldId).copy(focObjTemplate.getFocProperty(fieldId));
      }
    }
  }  
  
  public StringBuffer getAdditionalWhere() {
    StringBuffer res = new StringBuffer();
    boolean firstCondition = true;
    if(additionalWhere != null && additionalWhere.length() > 0){
      res.append("( ");
      res.append("( "+ additionalWhere+" ) ");
      firstCondition = false;
    }
    if(additionalWhereMap != null){
      Iterator iter = additionalWhereMap.keySet().iterator();
      while(iter != null && iter.hasNext()){
        String condition = (String)additionalWhereMap.get(iter.next());
        if(condition != null){
          if(!firstCondition){
            res.append("and ");
          }else{
            res.append(" ( ");
            firstCondition = false;
          }
          res.append("( "+condition +") ");
        }
      }
    }
    if(res.length() > 0){
      res.append(") ");
    }
    return res;
  }

  public void setAdditionalWhere(StringBuffer buff) {
    additionalWhere = buff;
  }
  
  public void putAdditionalWhere(String key, String additionnalWhere){
    if(additionalWhereMap == null){
      additionalWhereMap = new HashMap<String, String>();
    }
    additionalWhereMap.put(key, additionnalWhere);
  }

  public void removeAdditionalWhere(String key){
    if(additionalWhereMap != null){
    	additionalWhereMap.remove(key);
    }
  }

  public String getAdditionalWhere(String key){
    return additionalWhereMap != null ? additionalWhereMap.get(key):null;
  }
  
  @Deprecated
  public void addAdditionalWhere(StringBuffer additionalWhere) {
    if(this.additionalWhere == null){
      this.additionalWhere = new StringBuffer(additionalWhere);
    }else{
      this.additionalWhere.append(additionalWhere);  
    }
  }

  public void putAliasWhere(String alias, String additionnalWhere){
    if(additionalWhereMap_ForJoinAlias == null){
      additionalWhereMap_ForJoinAlias = new HashMap<String, String>();
    }
    additionalWhereMap_ForJoinAlias.put(alias, additionnalWhere);
  }

  public void removeAliasWhere(String alias){
    if(additionalWhereMap_ForJoinAlias != null){
    	additionalWhereMap_ForJoinAlias.remove(alias);
    }
  }

  public String getAliasWhere(String alias){
    return additionalWhereMap_ForJoinAlias != null ? additionalWhereMap_ForJoinAlias.get(alias):null;
  }

	public boolean isFilterByCompany(){
		return filterByCompany;
	}
	
	public void setFilterByCompany(boolean filterByCompany){
		this.filterByCompany = filterByCompany;
	}
	
  /**
	 * @return the ownerOfTemplate
	 */
	public boolean isOwnerOfTemplate() {
		return ownerOfTemplate;
	}

	/**
	 * @param ownerOfTemplate the ownerOfTemplate to set
	 */
	public void setOwnerOfTemplate(boolean ownerOfTemplate) {
		this.ownerOfTemplate = ownerOfTemplate;
	}

	public void addOrderBy(StringBuffer requestBuffer) {
		if (requestBuffer != null && !Utils.isStringEmpty(orderBy)) {
			requestBuffer.append(" ORDER BY ");
			requestBuffer.append(orderBy);
		}
	}
	
	public int getOffset() {
		return pagination != null ? pagination.getOffset() : -1;
	}

	public void setOffset(int offset, int offsetCount) {
		if(pagination == null) pagination = new ListPagination();
		pagination.setOffset(offset);
		pagination.setOffsetCount(offsetCount);
	}

	public int getOffsetCount() {
		return pagination != null ? pagination.getOffsetCount() : -1;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public ListPagination getPagination(boolean createIfNeeded) {
		if (pagination == null && createIfNeeded) {
			pagination = new ListPagination();
		}
		return pagination;
	}
}
