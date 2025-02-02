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

import com.foc.desc.FocObject;
import com.foc.util.FocIterator;

public abstract class FocListIterator extends FocIterator{
  
  public abstract boolean treatElement(FocListElement element, FocObject focObj);

  public FocListIterator() {
    super();
  }

  public FocListIterator(int nbrOfUserObjects) {
    super(nbrOfUserObjects);
  }
  
  public FocListIterator(Object userObj1, Object userObj2, Object userObj3, Object userObj4) {
    super(userObj1, userObj2, userObj3, userObj4);
  }

  public FocListIterator(Object userObj1, Object userObj2, Object userObj3) {
    super(userObj1, userObj2, userObj3);
  }

  public FocListIterator(Object userObj1, Object userObj2) {
    super(userObj1, userObj2);
  }

  public FocListIterator(Object userObj1) {
    super(userObj1);
  }

  public boolean treatElement(Object object){
    FocListElement element = (FocListElement)object;
    FocObject focObject = (FocObject)element.getFocObject();
    return treatElement(element, focObject);
  }
}
