/*
 * Created on Jan 9, 2006
 */
package com.foc.join;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;

/**
 * @author 01Barmaja
 */
public class JoinUsingObjectField extends Join{
  private int     objectFieldID         = FField.NO_FIELD_ID;
  private boolean objectFieldIsInSource = true;

  public JoinUsingObjectField(TableAlias sourceTableAlias, int objectFieldID){
  	this(sourceTableAlias, objectFieldID, true);
  }
  
  public JoinUsingObjectField(TableAlias sourceTableAlias, int objectFieldID, boolean objectFieldIsInSource){
    super(sourceTableAlias);
    this.objectFieldID         = objectFieldID;
    this.objectFieldIsInSource = objectFieldIsInSource;
  }
  
  public int fillRequestDescWithJoinFields_Internal(FocRequestDesc reqDesc, int firstJoinFieldID) {
    FocRequestField reqFld = new FocRequestField(firstJoinFieldID++, getTargetAlias(), FField.REF_FIELD_ID);
    reqDesc.addField(reqFld);
    
    return firstJoinFieldID;
  }
  
  public String getLinkCondition(){
    String ret = null;
    try{
      if(getSourceAlias() != null && getTargetAlias() != null){
        FocDesc      srcDesc  = getSourceAlias().getFocDesc();
        FocDesc      tarDesc  = getTargetAlias().getFocDesc();
        int provider = srcDesc.getProvider();
        
        if(objectFieldIsInSource){
          FField objField = (FField)srcDesc.getFieldByID(objectFieldID);
          
          if(tarDesc.getWithReference() && objField != null){
          	if(DBManager.provider_FieldNamesBetweenSpeachmarks(provider)){
          		ret = getSourceAlias().getAlias()+".\""+objField.getDBName()+"\"="+getTargetAlias().getAlias()+".\""+tarDesc.getRefFieldName()+"\"";
          	}else{
          		ret = getSourceAlias().getAlias()+"."+objField.getDBName()+"="+getTargetAlias().getAlias()+"."+tarDesc.getRefFieldName();
          	}
          }
        }else{
          FField objField = (FField)tarDesc.getFieldByID(objectFieldID);

          if(srcDesc.getWithReference() && objField != null){
          	if(DBManager.provider_FieldNamesBetweenSpeachmarks(provider)){
          		ret = getTargetAlias().getAlias()+".\""+objField.getDBName()+"\"="+getSourceAlias().getAlias()+".\""+srcDesc.getRefFieldName()+"\"";
          	}else{
          		ret = getTargetAlias().getAlias()+"."+objField.getDBName()+"="+getSourceAlias().getAlias()+"."+srcDesc.getRefFieldName();
          	}
          }
        }
      }
    }catch(Exception e){
      Globals.logException(e);
    }
    return ret;
  }
  
  public String getUpdateCondition(){
    return "";
  }
}
