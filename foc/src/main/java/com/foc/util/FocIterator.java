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
package com.foc.util;

public abstract class FocIterator implements IFocIterator{
  public final static int DEFAULT_NBR_USER_OBJECTS = 3;
  
  private Object userObjects[] = null;

  public FocIterator(){
  	super();
  }
  
  public FocIterator(int nbrOfUserObjects){ 
    super();
    userObjects = new Object[nbrOfUserObjects];
  }

  public FocIterator(Object userObj1){
    super();
    userObjects = new Object[1];
    setObject(0, userObj1);  
  }

  public FocIterator(Object userObj1, Object userObj2){
    super();
    userObjects = new Object[2];
    setObject(0, userObj1);
    setObject(1, userObj2);
  }

  public FocIterator(Object userObj1, Object userObj2, Object userObj3){
    super();
    userObjects = new Object[3];
    setObject(0, userObj1);
    setObject(1, userObj2);
    setObject(2, userObj3);
  }
  
  public FocIterator(Object userObj1, Object userObj2, Object userObj3, Object userObj4){
    super();
    userObjects = new Object[4];
    setObject(0, userObj1);
    setObject(1, userObj2);
    setObject(2, userObj3);
    setObject(3, userObj4);
  }
  
  public Object getObject(int i){
    return (userObjects != null) ? userObjects[i] : null;
  }

  public void setObject(int i, Object object){
    userObjects[i] = object;
  }
}
