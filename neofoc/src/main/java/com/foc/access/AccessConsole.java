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

import java.util.*;

import com.foc.shared.dataStore.IFocData;

/**
 * @author 01Barmaja
 */
public class AccessConsole implements IFocData {
  private HashMap<Integer, AccessSubject> childrenSubjects = null;
  private ArrayList<AccessConsole> disposeDependentObjectsArray = null;
 
  public AccessConsole() {
    super();
  }

  public void dispose(){
    if(childrenSubjects != null){
      Iterator iter = newSubjectIterator();
      while(iter != null && iter.hasNext()){
        AccessSubject subject = (AccessSubject) iter.next();
        if (subject != null) {
          iter.remove();
        }
      }
      childrenSubjects = null;
    }
    disposeDisposeDependentObjects();
  }
  
  private void disposeDisposeDependentObjects(){
  	if(disposeDependentObjectsArray != null){
  		Iterator<AccessConsole> iter = disposeDependentObjectsArray.iterator();
  		while(iter != null && iter.hasNext()){
  			AccessConsole console = iter.next();
  			if(console != null){
  				console.dispose();
  			}
  		}
  		disposeDependentObjectsArray.clear();
  		disposeDependentObjectsArray = null;
  	}
  }
  
  private ArrayList<AccessConsole> getDisposeDepenedentObjectsArray(){
  	if(disposeDependentObjectsArray == null){
  		disposeDependentObjectsArray = new ArrayList<AccessConsole>();
  	}
  	return disposeDependentObjectsArray;
  }
  
  public void addDisposeDependentObject(AccessConsole disposeDependentObject){
  	if(disposeDependentObject != null){
  		getDisposeDepenedentObjectsArray().add(disposeDependentObject);
  	}
  }
  
  public void removeDisposeDependentObject(AccessConsole disposeDependentObject){
  	if(disposeDependentObject != null && disposeDependentObjectsArray != null){
  		disposeDependentObjectsArray.remove(disposeDependentObject);
  	}
  }
  
  /*  
  public int subjectNumber() {
    return childrenSubjects != null ? childrenSubjects.size() : 0;
  }

  public AccessSubject subjectAt(int i) {
    return (childrenSubjects != null) ? (AccessSubject) childrenSubjects.get(i) : null;
  }
  */
  
  public Iterator newSubjectIterator(){
    return childrenSubjects != null ? childrenSubjects.values().iterator() : null;
  }
  
  public void addSubject(AccessSubject subject) {
    if (childrenSubjects == null) {
      childrenSubjects = new HashMap<Integer, AccessSubject>();
    }
    //childrenSubjects.put(subject, subject);
    childrenSubjects.put(Integer.valueOf(subject.getHashCodeAsPointer()), subject);
  }

  public void removeSubject(AccessSubject subject) {
    if (childrenSubjects != null) {
      //childrenSubjects.remove(subject);
      childrenSubjects.remove(Integer.valueOf(subject.getHashCodeAsPointer()));
    }
  }
  
  public boolean validate(boolean checkValidity) {
  	return validate(checkValidity, false);
  }
  
  public boolean checkValidityWithPropagation(){
  	boolean validate = true;
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext() && validate){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null) {
        validate = subject.isContentValidWithPropagation();
      }
    }
    return validate;
  }
  
  public boolean validate(boolean checkValidity, boolean callFromValidationPanel) {
    boolean validate = true;
    
    //BGuiLock
    if(checkValidity){
    	validate = checkValidityWithPropagation();//20150904 just placed the code in a separate method
    }
    //EGuiLock
    
    if(validate){
      Iterator iter = newSubjectIterator();
      while(iter != null && iter.hasNext()){
        AccessSubject subject = (AccessSubject) iter.next();
        if(subject != null){
        	if(callFromValidationPanel){
        		subject.validate_FromTheValidationPanel(true);
        	}else{
        		subject.validate(true);
        	}
        }
      }
    }
    
    return validate;
  }
  
  public boolean needValidationWithPropagation(){
    boolean res = false;
    
    if(childrenSubjects != null && childrenSubjects.size()!=0){
      Iterator iter = newSubjectIterator();
      while(iter != null && iter.hasNext() && !res){
        AccessSubject subject = (AccessSubject) iter.next();
        res = subject.needValidationWithPropagation();
      }
    }
    return res;
  }

  public void cancel() {
    //Globals.logString("Cancel :"+toString());
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null) {
        subject.cancel();
      }
    }
  }

  @Override
  public boolean iFocData_isValid() {
    return true;
  }

  @Override
  public boolean iFocData_validate() {//return true if error
    return !validate(true);
  }

  @Override
  public void iFocData_cancel() {
    cancel();
  }

  @Override
  public IFocData iFocData_getDataByPath(String path) {
    return null;
  }

  @Override
  public Object iFocData_getValue() {
    return null;
  }
}
