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
package com.foc.access;

import java.util.ArrayList;
import java.util.Iterator;

import com.foc.Globals;

/**
 * @author 01Barmaja
 */
public abstract class AccessSubject extends AccessConsole {
  public    abstract boolean commitStatusToDatabase();
  public    abstract void    undoStatus();
  public    abstract void    childStatusModification(AccessSubject childSubject);
  public    abstract void    childStatusUndo(AccessSubject childSubject);
  public    abstract void    childValidated(AccessSubject childSubject, char initialStatusFlags);
  public    abstract void    doBackup();
  protected abstract void    statusModification(int statusModified);
  public    abstract boolean isContentValid(boolean displayMessage);
	
  private AccessSubject fatherSubject = null;
  private char          flags         = 0;

  private static final char FLG_DESACTIVIAT_SUBJECT_NOTIFICATION =   1;
  private static final char FLG_FORCE_SELF_CONTROLER             =   2;
  private static final char FLG_TRANSACTIONAL_WITH_CHILDREN      =   4;
  private static final char FLG_MODIFIED                         =   8;
  private static final char FLG_DELETED                          =  16;
  private static final char FLG_CREATED                          =  32;
  private static final char FLG_DELETION_EXECUTED                =  64;
  private static final char FLG_DB_RESIDENT_INHERITED            = 128;
  private static final char FLG_DB_RESIDENT                      = 256;
  private static final char FLG_DELETED_AFTER_CREATION           = 512;//This means that the item was new and we deleted it

  //private AccessControl control    = null;  
  /*
  private boolean       desactivateSubjectNotifications = false;
  private boolean       forceSelfControler = false;
  private boolean       transactionalWithChildren = false;
  private boolean       modified         = false;
  private boolean       deleted          = false;
  private boolean       created          = false;
  private boolean       deletionExecuted = false;
  private int           dbResident = DB_RESIDENT_INHERITED;
  
  private final static int DB_RESIDENT_INHERITED = 0;
  private final static int DB_RESIDENT_TRUE      = 1; 
  private final static int DB_RESIDENT_FALSE     = 2;
  */
  
  public final static int STATUS_CREATED  = 1;
  public final static int STATUS_MODIFIED = 2;  
  public final static int STATUS_DELETED  = 3;

  public AccessSubject(AccessControl control) {
    super();
    flags = (char)(flags | FLG_DB_RESIDENT_INHERITED);
    //this.control = control;
  }

  public AccessSubject() {
    super();
    flags = (char)(flags | FLG_DB_RESIDENT_INHERITED);    
    //this.control = Globals.getDefaultAccessControl();
  }
  
  public void dispose(){
    setFatherSubject(null);
    fatherSubject = null;
    super.dispose();
    
    /*
    if(control == null){
      control = null;
    }
    */
  }  
  
  public int getHashCodeAsPointer(){
    return super.hashCode();
  }
  
  public boolean isModified() {
  	return isModified(flags);  	
  }
  
  public static boolean isModified(char flg){
  	return (flg & FLG_MODIFIED) != 0;
  }

  public boolean isDeleted() {
  	return isDeleted(flags);
  }
  
  public static boolean isDeleted(char flg){
  	return (flg & FLG_DELETED) != 0 || (flg & FLG_DELETED_AFTER_CREATION) != 0;
  }

  public boolean isDeletedAfterCreation() {
  	return (flags & FLG_DELETED_AFTER_CREATION) != 0;  	
  }

  public char getCreatedModifiedDeletedFlag(){
  	return flags;
  }
  
  public boolean isCreated() {
  	return isCreated(flags);
  }

  public static boolean isCreated(char flg) {
  	return (flg & FLG_CREATED) != 0;  	
  }
  
  public void setModified(boolean modified) {
    if (modified != isModified()) {      
      if (!isCreated() && !isDeleted()) {
        /*if( modified && this instanceof FocObject && ((FocObject)this).getReference() != null && ((FocObject)this).getReference().getInteger() == 156 ){
          int debug = 0;
        }*/
        
        if(modified){
          flags = (char)(flags | FLG_MODIFIED);
        }else{
          flags = (char)(flags & ~FLG_MODIFIED);
        }      	
        statusModificationInternal(STATUS_MODIFIED);
      }
    }
  } 

  public void setDeleted(boolean deleted) {
    if (deleted != isDeleted()) {
      if(deleted){
        flags = (char)(flags | FLG_DELETED);
      	if(isCreated()){
      		flags = (char)(flags | FLG_DELETED_AFTER_CREATION);
      	}
      }else{
        flags = (char)(flags & ~FLG_DELETED);
        flags = (char)(flags & ~FLG_DELETED_AFTER_CREATION);
      }
      if(deleted){
        flags = (char)(flags & ~FLG_CREATED);
        flags = (char)(flags & ~FLG_MODIFIED);
      }
      statusModificationInternal(STATUS_DELETED);
    }
  }

  public void setCreated(boolean created) {
    if (created != isCreated()) {
      if(created){
        flags = (char)(flags | FLG_CREATED);
      }else{
        flags = (char)(flags & ~FLG_CREATED);
      }      	
      if(created){
      	flags = (char)(flags & ~FLG_DELETED);
      	flags = (char)(flags & ~FLG_DELETED_AFTER_CREATION);
      	flags = (char)(flags & ~FLG_MODIFIED);      	
      }
      statusModificationInternal(STATUS_CREATED);
    }
  }

  public void forceControler(boolean force) {
    if(force){
      flags = (char)(flags | FLG_FORCE_SELF_CONTROLER);
    }else{
      flags = (char)(flags & ~FLG_FORCE_SELF_CONTROLER);
    }      	
  }

  public boolean isForceControler() {
  	return (flags & FLG_FORCE_SELF_CONTROLER) != 0;
  }
  
  public boolean isControler() {
    return fatherSubject == null || fatherSubject == this || isForceControler();
  }

  public boolean isTransactionalWithChildren(){
    return (flags & FLG_TRANSACTIONAL_WITH_CHILDREN) != 0;
  	//return false;
  }
  
  public void setTransactionalWithChildren(boolean transactionalWithChildren){
    if(transactionalWithChildren){
      flags = (char)(flags | FLG_TRANSACTIONAL_WITH_CHILDREN);
    }else{
      flags = (char)(flags & ~FLG_TRANSACTIONAL_WITH_CHILDREN);
    }
  }
  
  public boolean validate_FromTheValidationPanel(boolean checkValidity) {
  	return validate(checkValidity, false);
  }
  
  public boolean validate(boolean checkValidity, boolean callFromValidationPanel){
    boolean valid = !checkValidity || isContentValidWithPropagation();
    if(valid){
      if(!isCreated()) {
        if(!this.isModified()){
          //Globals.logString("! IN VALIDATION WE ARE IN THE OLD METHOD THAT SET MODIFIED !");
          //Globals.logString("! IN VALIDATION THE OLD METHOD WOULD HAVE SET MODIFIED !");
        }
        //this.setModified(true);
      }
      //TEMPREF 2012 - Begin
      //Switch the commit with the child validation
      char statusFlags = getCreatedModifiedDeletedFlag();
      if(isControler()){
        valid = !commitStatusToDatabaseWithPropagation();
      }
      //else{//AntoineS 2013-06-10 added so that the JV error will not popup twice!
	      if(fatherSubject != null && fatherSubject != this) {
	        fatherSubject.childValidated(this, statusFlags);
	      }
//      }
      //TEMPREF 2012 - End
    }
    return valid;
  }

  public void cancel() {
    undoStatusWithPropagation();
  }

  public void resetStatus() {
    setCreated(false);
    setDeleted(false);
    setModified(false);
  }

  public void resetStatusWithPropagation() {
    this.resetStatus();
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null) {
        subject.resetStatusWithPropagation();
      }
    }
  }

  public boolean isContentValidWithPropagation(){
    boolean isContentValid = isContentValid(true);
    
    if(isContentValid){
      Iterator iter = newSubjectIterator();
      while(iter != null && iter.hasNext() && isContentValid){
        AccessSubject subject = (AccessSubject) iter.next();
        if (subject != null && !subject.isDeleted()) {
          isContentValid = subject.isContentValid(true);
        }
      }
    }
    return isContentValid;
  }
  
  @Override
  public boolean needValidationWithPropagation() {
  	boolean need = false;
  	if(isDbResident()){
	    need = isCreated() || isModified() || (isDeleted() && !isDeletionExecuted()); 
	    need = need || super.needValidationWithPropagation();
  	}
  	return need;
  }
  
  public void undoStatusWithPropagation() {
    if (fatherSubject != null) {
      fatherSubject.childStatusUndo(this);
    }
    undoStatus();
    resetStatus();    

    /* WE CANNOT DO THIS BECAUSE WE GET A CONCURRENCY EXCEPTION SEE EXPLANATION BELLOW
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next(); 
      if (subject != null) {
        subject.undoStatusWithPropagation();
      }
    }*/

    //In the following we want to scan the subjects and call the undoStatusWithPropagation
    //We cannot do this directly through the subjects iterator because:
    //In the case of a create that we are undoing, the subject will call a remove from father 
    //and modify the iteration procedure, this will generate a concurrency exception.
    //This is why we are putting all subjects in a temporary item, then we are scanning 
    //this temporary item to undoStatus
    
    //Scan the subjects and fill a temporary array
    ArrayList<AccessSubject> arrayOfSubjects = new ArrayList<AccessSubject>();
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next(); 
      if (subject != null) {
        arrayOfSubjects.add(subject);
      }
    }
    
    //Scan the temporary array and call the undo status with propagation    
    for(int i=0; i<arrayOfSubjects.size(); i++){
      AccessSubject subject = (AccessSubject) arrayOfSubjects.get(i); 
      if (subject != null) {
        subject.undoStatusWithPropagation();
      }
    }
  }
  
  private void statusModificationInternal(int statusModified){
    statusModification(statusModified);
    childStatusModificationPropagation();
  }
  
  private void childStatusModificationPropagation() {
    if (fatherSubject != null && fatherSubject != this && !isDesactivateSubjectNotifications()) {
      fatherSubject.childStatusModification(this);
      //fatherSubject.childStatusModificationPropagation();
    }
  }

  public boolean commitStatusToDatabaseWithPropagation() {
  	boolean error = false; 
    if(isTransactionalWithChildren()){
    	if(Globals.getApp() == null || Globals.getApp().getDataSource() == null){
    		Globals.getApp().getDataSource().transaction_setShouldSurroundWithTransactionIfRequest();
    	}
    	Globals.getApp().getDataSource().transaction_setShouldSurroundWithTransactionIfRequest();
      //Globals.getDBManager().beginTransaction();
    }
    try{
	    error = commitStatusToDatabase() || error;
	
	    Iterator iter = newSubjectIterator();
	    while(iter != null && iter.hasNext()){
	      AccessSubject subject = (AccessSubject) iter.next();
	      if (subject != null) {
	      	error = subject.commitStatusToDatabaseWithPropagation() || error;
	        if(subject.isDeletionExecuted()){
	        	iter.remove();
	          subject.setFatherSubject(null);
	        }
	      }
	    }
    }catch(Exception e){
    	Globals.logException(e);
    }
    
    if(isTransactionalWithChildren()){
    	Globals.getApp().getDataSource().transaction_SeeIfShouldCommit();
    	//Globals.getDBManager().transaction_SeeIfShouldCommit();
      //Globals.getDBManager().commitTransaction();
    	//Globals.getDBManager().transaction_SetRequestGather(false);
    }
  	resetStatus();
  	return error;
  }

  public void doBackupWithPropagation(){
    doBackup();

    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null) {
        subject.doBackup();
      }
    }
  }
  
  /**
   * @param fatherSubject
   *          The fatherSubject to set.
   */
  public void setFatherSubject(AccessSubject fatherSubject) {
    if (this.fatherSubject != fatherSubject) {
      if (this.fatherSubject != null) {
        this.fatherSubject.removeSubject(this);
        this.fatherSubject = null;
      }
      if (fatherSubject != null) {
        this.fatherSubject = fatherSubject;
        this.fatherSubject.addSubject(this);
      }
    }
  }

  /**
   * @return Returns the desactivateNotifications.
   */
  public boolean isDesactivateSubjectNotifications() {
  	return (flags & FLG_DESACTIVIAT_SUBJECT_NOTIFICATION) != 0;  	
  }

  /**
   * @param desactivateNotifications
   *          The desactivateNotifications to set.
   */
  public void setDesactivateSubjectNotifications(boolean desactivateNotifications) {
    if(desactivateNotifications){
      flags = (char)(flags | FLG_DESACTIVIAT_SUBJECT_NOTIFICATION);
    }else{
      flags = (char)(flags & ~FLG_DESACTIVIAT_SUBJECT_NOTIFICATION);
    }
  }
  
  /**
   * @return Returns the fatherSubject.
   */
  public AccessSubject getFatherSubject() {
    return fatherSubject;
  }
  
	public boolean isDbResident() {
		boolean retValue = true;
		
		if((flags & FLG_DB_RESIDENT_INHERITED) != 0){
			if(getFatherSubject() != null && getFatherSubject() != this){
				retValue = getFatherSubject().isDbResident();
			}
		}else if((flags & FLG_DB_RESIDENT) != 0){
			retValue = true;
		}else{
			retValue = false;
		}
		return retValue;
	}
	
	public void setDbResident(boolean dbResident) {
    if(dbResident){
      flags = (char)(flags | FLG_DB_RESIDENT);
    }else{
      flags = (char)(flags & ~FLG_DB_RESIDENT);
    }
    flags = (char)(flags & ~FLG_DB_RESIDENT_INHERITED);
	}

	public boolean isDeletionExecuted() {
		return (flags & FLG_DELETION_EXECUTED) != 0;
  }
	
  public void setDeletionExecuted(boolean deletionExecuted) {
    if(deletionExecuted){
      flags = (char)(flags | FLG_DELETION_EXECUTED);
    }else{
      flags = (char)(flags & ~FLG_DELETION_EXECUTED);
    }
  }
}
